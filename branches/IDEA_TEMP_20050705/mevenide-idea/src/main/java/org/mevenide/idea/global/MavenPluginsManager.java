package org.mevenide.idea.global;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
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
import org.mevenide.idea.project.properties.PropertiesEvent;
import org.mevenide.idea.project.properties.PropertiesListener;
import org.mevenide.idea.project.properties.PropertiesManager;
import org.mevenide.idea.project.goals.DefaultPluginGoal;
import org.mevenide.idea.project.goals.DefaultPluginGoalContainer;
import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PluginGoalContainer;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @author Arik
 * @todo raise event when plugin cache is refreshed
 */
public class MavenPluginsManager extends AbstractProjectComponent
        implements VirtualFilePointerListener {
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

    private VirtualFilePointer pluginsFilePointer = null;

    /**
     * Cache for loaded plugin descriptors.
     */
    private PluginGoalContainer[] plugins = null;

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this component instance will be registered for
     */
    public MavenPluginsManager(final Project pProject) {
        super(pProject);
    }

    @Override
    public void initComponent() {
        final VirtualFilePointerManager vfpMgr = VirtualFilePointerManager.getInstance();

        MavenManager.getInstance().addPropertyChangeListener(
                "mavenHome",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        synchronized (this) {
                            plugins = null;
                            if (pluginsFilePointer != null)
                                vfpMgr.kill(pluginsFilePointer);
                            pluginsFilePointer = createPluginsFilePointer();
                        }
                    }
                });

        final PropertiesManager propsMg = PropertiesManager.getInstance(project);
        propsMg.addPropertiesListener(
                new PropertiesListener() {
                    public void propertiesChanged(PropertiesEvent pEvent) {
                        synchronized (this) {
                            plugins = null;
                            if (pluginsFilePointer != null)
                                vfpMgr.kill(pluginsFilePointer);
                            pluginsFilePointer = createPluginsFilePointer();
                        }
                    }
                });

        pluginsFilePointer = createPluginsFilePointer();
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
            if (plugins == null)
                try {
                    plugins = loadPlugins();
                }
                catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    plugins = new PluginGoalContainer[0];
                }
            return plugins;
        }
    }

    /**
     * Internal. Does nothing.
     *
     * @param pointers files about to change validity
     */
    public void beforeValidityChanged(VirtualFilePointer[] pointers) {
    }

    /**
     * Internal. Refreshes plugins cache if needed.
     *
     * @param pointers changed files
     */
    public void validityChanged(VirtualFilePointer[] pointers) {
        for (VirtualFilePointer pointer : pointers) {
            if (pointer != pluginsFilePointer)
                continue;

            synchronized (this) {
                if (pointer.isValid())
                    getPlugins();
                else
                    plugins = null;
            }
        }
    }

    /**
     * Loads and parses all available plugins in the user's cache directory.
     *
     * @return available plugins
     * @throws IOException if an error occurs
     */
    private PluginGoalContainer[] loadPlugins() throws IOException {
        if (pluginsFilePointer == null || !pluginsFilePointer.isValid())
            return new PluginGoalContainer[0];

        final VirtualFile pluginsFile = pluginsFilePointer.getFile();
        if (pluginsFile == null || !pluginsFile.isValid() || pluginsFile.isDirectory())
            return new PluginGoalContainer[0];

        final VirtualFile pluginsDir = pluginsFile.getParent();
        if (pluginsDir == null || !pluginsDir.isValid() || !pluginsDir.isDirectory())
            return new PluginGoalContainer[0];

        final Properties props = new Properties();
        final byte[] data = pluginsFile.contentsToByteArray();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        props.load(stream);

        final Set<PluginGoalContainer> plugins = new HashSet<PluginGoalContainer>(30);
        final Set<Map.Entry<Object, Object>> entries = props.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            final String pluginName = entry.getValue().toString();
            final String artifactId = entry.getKey().toString();

            final VirtualFile pluginDir = pluginsDir.findChild(artifactId);
            if (pluginDir == null || !pluginDir.isValid() || !pluginDir.isDirectory()) {
                LOG.warn("Could not load plugin '" + pluginName + "'");
                continue;
            }

            final PluginGoalContainer plugin = parsePlugin(pluginDir);
            if (plugin != null)
                plugins.add(plugin);
        }

        return plugins.toArray(new PluginGoalContainer[plugins.size()]);
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
        if (pluginPom != null && pluginPom.isValid())
            plugin.setPomFile(pluginPom);
        else
            return null;

        final XmlFile pomPsi = PsiUtils.findXmlFile(project, pluginPom);
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
        if (jellyFile != null && jellyFile.isValid())
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

    private VirtualFilePointer createPluginsFilePointer() {
        final PropertiesManager propsMg = PropertiesManager.getInstance(project);
        final String pluginsDirName = propsMg.getProperty(CACHE_DIR_PROPERTY);
        if (pluginsDirName == null || pluginsDirName.trim().length() == 0)
            return null;

        final StringBuilder url = new StringBuilder("file://");
        url.append(pluginsDirName.replace(File.separatorChar, '/'));
        if (url.charAt(url.length() - 1) != '/')
            url.append('/');
        url.append("/artifactIdToPlugin.cache");

        final VirtualFilePointerManager vfpMgr = VirtualFilePointerManager.getInstance();
        return vfpMgr.create(url.toString(), this);
    }

    public static MavenPluginsManager getInstance(final Project pProject) {
        return pProject.getComponent(MavenPluginsManager.class);
    }
}
