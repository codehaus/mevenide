package org.mevenide.idea.global;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Application;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.mevenide.idea.project.goals.DefaultPluginGoal;
import org.mevenide.idea.project.goals.DefaultPluginGoalContainer;
import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PluginGoalContainer;
import org.mevenide.idea.project.properties.PropertiesManager;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.FileUtils;

/**
 * @author Arik
 */
public class MavenPluginsManager extends AbstractProjectComponent {
    /**
     * The name of the Maven property that denotes the location of the plugins JAR files.
     */
    private static final String CACHE_DIR_PROPERTY = "maven.plugin.unpacked.dir";
    private static final String POM_ID_XPATH = "project/id";
    private static final String POM_GROUP_ID_XPATH = "project/groupId";
    private static final String POM_ARTIFACT_ID_XPATH = "project/artifactId";
    private static final String POM_VERSION_XPATH = "project/currentVersion";
    private static final String POM_NAME_XPATH = "project/name";
    private static final String POM_DESC_XPATH = "project/description";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private VirtualFile pluginsCacheFile = null;

    /**
     * Cache for loaded plugin descriptors.
     */
    private PluginGoalContainer[] plugins = new PluginGoalContainer[0];
    private final MavenHomeListener MAVEN_HOME_LISTENER = new MavenHomeListener();

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this component instance will be registered for
     */
    public MavenPluginsManager(final Project pProject) {
        super(pProject);
    }

    @Override
    public void projectOpened() {
        final MavenManager mvnMgr = MavenManager.getInstance();
        mvnMgr.addPropertyChangeListener("mavenHome", MAVEN_HOME_LISTENER);
    }

    @Override
    public void projectClosed() {
        final MavenManager mvnMgr = MavenManager.getInstance();
        mvnMgr.removePropertyChangeListener("mavenHome", MAVEN_HOME_LISTENER);
    }

    public PluginGoalContainer getPlugin(final String pId) {
        synchronized (this) {
            final PluginGoalContainer[] plugins = getPlugins();
            for (PluginGoalContainer plugin : plugins) {
                if (plugin.getId().equals(pId))
                    return plugin;
            }

            return null;
        }
    }

    public PluginGoalContainer getPlugin(final String pGroupId, final String pArtifactId) {
        synchronized (this) {
            final PluginGoalContainer[] plugins = getPlugins();
            for (PluginGoalContainer plugin : plugins) {
                if (plugin.getGroupId().equals(pGroupId) && plugin.getArtifactId().equals(
                        pArtifactId))
                    return plugin;
            }

            return null;
        }
    }

    /**
     * Parses all plugins in the Maven installation.
     *
     * @return list of plugins found in the maven installation
     */
    public PluginGoalContainer[] getPlugins() {
        synchronized (this) {
            boolean reload = true;
            final VirtualFile cacheFile = getPluginsCacheFile();
            if(pluginsCacheFile == cacheFile)
                reload = false;
            else if(pluginsCacheFile != null && pluginsCacheFile.equals(cacheFile))
                reload = false;

            if (reload)
                refresh(cacheFile);

            return plugins;
        }
    }

    public void refresh() {
        refresh(getPluginsCacheFile());
    }

    private void refresh(final VirtualFile pPluginsCacheFile) {
        synchronized (this) {
            final PluginGoalContainer[] oldPlugins = plugins;
            try {
                LOG.trace("Refreshing maven plugins cache");
                final Runnable pluginLoader = new Runnable() {
                    public void run() {
                        try {
                            plugins = loadPlugins(pPluginsCacheFile);
                        }
                        catch (IOException e) {
                            LOG.error(e.getMessage(), e);
                            plugins = new PluginGoalContainer[0];
                        }
                    }
                };
                final Application app = ApplicationManager.getApplication();
                app.runProcessWithProgressSynchronously(
                        pluginLoader,
                        "Loading plugins",
                        false,
                        project);
            }
            finally {
                pluginsCacheFile = pPluginsCacheFile;
                changeSupport.firePropertyChange("plugins", oldPlugins, plugins, false);
            }
        }
    }

    /**
     * Loads and parses all available plugins in the user's cache directory.
     *
     * @return available plugins
     * @throws IOException if an error occurs
     */
    private PluginGoalContainer[] loadPlugins(final VirtualFile pPluginsCacheFile) throws IOException {
        final ProgressIndicator prg = IDEUtils.getProgressIndicator();
        if (prg != null) {
            prg.setIndeterminate(true);
            prg.setText("Discovering available plugins...");
            prg.start();
        }

        int counter = 0;
        try {
            if (pPluginsCacheFile == null || !pPluginsCacheFile.isValid() || !FileUtils.exists(pPluginsCacheFile) || pPluginsCacheFile.isDirectory()) {
                if (prg != null) {
                    prg.setText("No available plugins found.");
                    prg.setFraction(1);
                }
                return new PluginGoalContainer[0];
            }

            final VirtualFile pluginsDir = pPluginsCacheFile.getParent();
            if (pluginsDir == null || !pluginsDir.isValid() || !FileUtils.exists(pluginsDir) || !pluginsDir.isDirectory()) {
                if (prg != null) {
                    prg.setText("No available plugins found.");
                    prg.setFraction(1);
                }
                return new PluginGoalContainer[0];
            }

            final Properties props = new Properties();
            final byte[] data = pPluginsCacheFile.contentsToByteArray();
            final ByteArrayInputStream stream = new ByteArrayInputStream(data);
            props.load(stream);

            if (prg != null) {
                prg.setText("Found " + props.size() + " plugins.");
                prg.setIndeterminate(false);
                prg.setFraction(0);
            }

            final Set<PluginGoalContainer> plugins = new HashSet<PluginGoalContainer>(30);
            final Set<Map.Entry<Object, Object>> entries = props.entrySet();

            final int pluginCount = entries.size();

            for (Map.Entry<Object, Object> entry : entries) {
                final String pluginName = entry.getValue().toString();
                final String artifactId = entry.getKey().toString();

                if (prg != null) {
                    prg.setText("Loading plugin '" + pluginName + "'");
                    prg.setFraction((double)counter / (double)pluginCount);
                    counter++;
                }

                final VirtualFile pluginDir = pluginsDir.findChild(artifactId);
                if (pluginDir == null || !pluginDir.isValid() || !FileUtils.exists(pluginDir) || !pluginDir.isDirectory()) {
                    if (prg != null)
                        prg.setText("Could not load plugin '" + pluginName + "'");
                    continue;
                }

                final PluginGoalContainer plugin = parsePlugin(pluginDir);
                if (plugin != null)
                    plugins.add(plugin);
            }

            return plugins.toArray(new PluginGoalContainer[plugins.size()]);
        }
        finally {
            if (prg != null) {
                prg.setText("Done - loaded " + counter + " plugin(s).");
                prg.setFraction(1);
            }
        }
    }

    /**
     * Parses the given plugin JAR and extracts general plugin information and available goals.
     *
     * @param pPluginDir the plugin directory
     *
     * @return plugin descriptor
     */
    private PluginGoalContainer parsePlugin(final VirtualFile pPluginDir) {
        final DefaultPluginGoalContainer plugin = new DefaultPluginGoalContainer();

        //
        //parse plugin POM
        //
        final VirtualFile pluginPom = pPluginDir.findChild("project.xml");
        if (pluginPom != null && pluginPom.isValid() && FileUtils.exists(pluginPom))
            plugin.setPomFile(pluginPom);
        else
            return null;

        final XmlFile pomPsi = PsiUtils.findXmlFile(project, pluginPom);
        if(pomPsi == null)
            return null;

        plugin.setId(new XmlTagPath(pomPsi, POM_ID_XPATH).getStringValue());
        plugin.setGroupId(new XmlTagPath(pomPsi, POM_GROUP_ID_XPATH).getStringValue());
        plugin.setArtifactId(new XmlTagPath(pomPsi, POM_ARTIFACT_ID_XPATH).getStringValue());
        plugin.setVersion(new XmlTagPath(pomPsi, POM_VERSION_XPATH).getStringValue());
        plugin.setName(new XmlTagPath(pomPsi, POM_NAME_XPATH).getStringValue());
        plugin.setDescription(new XmlTagPath(pomPsi, POM_DESC_XPATH).getStringValue());

        //
        //parse plugin Jelly script
        //
        final VirtualFile jellyFile = pPluginDir.findChild("plugin.jelly");
        if (jellyFile != null && jellyFile.isValid() && FileUtils.exists(jellyFile))
            plugin.setScriptFile(jellyFile);
        else
            return null;

        final XmlFile jellyPsi = PsiUtils.findXmlFile(project, jellyFile);
        final XmlTagPath goalsPath = new XmlTagPath(jellyPsi, "project/goal");
        final XmlTag[] goalTags = goalsPath.getAllTags();
        final PluginGoal[] goals = new PluginGoal[goalTags.length];
        for (int i = 0; i < goalTags.length; i++) {
            XmlTag tag = goalTags[i];
            final String name = tag.getAttributeValue("name");
            final String lcName = name.toLowerCase();
            if(lcName.equalsIgnoreCase("register") || lcName.equalsIgnoreCase("deregister"))
                continue;

            final String desc = tag.getAttributeValue("description");
            final String preReqsValue = tag.getAttributeValue("prereqs");
            final String[] preReqs = preReqsValue == null ? EMPTY_STRING_ARRAY : preReqsValue.split(
                    ",");

            final DefaultPluginGoal goal = new DefaultPluginGoal();
            goal.setName(name);
            goal.setDescription(desc);
            goal.setContainer(plugin);
            goal.setPrereqs(preReqs);
            goals[i] = goal;
        }
        plugin.setGoals(goals);

        return plugin;
    }

    private VirtualFile getPluginsCacheFile() {
        final PropertiesManager propsMgr = PropertiesManager.getInstance(project);
        final String cacheDir = propsMgr.getProperty(CACHE_DIR_PROPERTY);
        if (cacheDir == null || cacheDir.trim().length() == 0)
            return null;

        final StringBuilder buf = new StringBuilder(100);
        buf.append("file://").append(cacheDir.replace(File.separatorChar, '/'));
        if (buf.charAt(buf.length() - 1) != '/')
            buf.append('/');
        buf.append("artifactIdToPlugin.cache");
        return VirtualFileManager.getInstance().findFileByUrl(buf.toString());
    }

    public static MavenPluginsManager getInstance(final Project pProject) {
        return pProject.getComponent(MavenPluginsManager.class);
    }

    private class MavenHomeListener implements PropertyChangeListener {
        public void propertyChange(
                PropertyChangeEvent evt) {
            if (!evt.getPropertyName().equals(
                    "mavenHome"))
                return;

            refresh();
        }
    }
}
