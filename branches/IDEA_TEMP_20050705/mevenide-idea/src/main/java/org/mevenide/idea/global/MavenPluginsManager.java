package org.mevenide.idea.global;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.idea.project.model.DefaultGoalInfo;
import org.mevenide.idea.project.model.DefaultPluginInfo;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.model.PluginInfo;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @author Arik
 */
public class MavenPluginsManager extends AbstractProjectComponent {
    /**
     * The name of the Maven property that denotes the location of the plugins JAR files.
     */
    private static final String PLUGINS_DIR = "maven.plugin.dir";
    private static final String POM_ID_XPATH = "project/id";
    private static final String POM_GROUP_ID_XPATH = "project/groupId";
    private static final String POM_ARTIFACT_ID_XPATH = "project/artifactId";
    private static final String POM_VERSION_XPATH = "project/currentVersion";
    private static final String POM_NAME_XPATH = "project/name";
    private static final String POM_DESC_XPATH = "project/description";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Cache for loaded plugin descriptors.
     */
    private PluginInfo[] plugins = null;

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
        MavenManager.getInstance().addPropertyChangeListener(
                "mavenHome",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        synchronized (this) {
                            plugins = null;
                        }
                    }
                });

        PropertiesManager.getInstance().addPropertiesListener(
                new PropertiesListener() {
                    public void propertiesChanged(PropertiesEvent pEvent) {
                        synchronized (this) {
                            plugins = null;
                        }
                    }
                });
    }

    public PluginInfo getPlugin(final String pId) {
        final PluginInfo[] plugins = getPlugins();
        for (PluginInfo plugin : plugins) {
            if (plugin.getId().equals(pId))
                return plugin;
        }

        return null;
    }

    public PluginInfo getPlugin(final String pGroupId, final String pArtifactId) {
        final PluginInfo[] plugins = getPlugins();
        for (PluginInfo plugin : plugins) {
            if (plugin.getGroupId().equals(pGroupId) && plugin.getArtifactId().equals(pArtifactId))
                return plugin;
        }

        return null;
    }

    /**
     * Parses all plugins in the Maven installation.
     *
     * @return list of plugins found in the maven installation
     */
    public PluginInfo[] getPlugins() {
        synchronized (this) {
            if (plugins != null)
                return plugins;

            plugins = loadPlugins();
            return plugins;
        }
    }

    private PluginInfo[] loadPlugins() {
        final VirtualFileManager vfMgr = VirtualFileManager.getInstance();

        final PropertiesManager propMgr = PropertiesManager.getInstance();
        final String pluginsDirName = propMgr.getProperty(PLUGINS_DIR);
        if (pluginsDirName == null || pluginsDirName.trim().length() == 0)
            return new PluginInfo[0];

        final String url = "file://" + pluginsDirName.replace(File.separatorChar, '/');
        final VirtualFile pluginsDir = vfMgr.findFileByUrl(url);
        if (pluginsDir == null || !pluginsDir.isValid() || !pluginsDir.isDirectory())
            return new PluginInfo[0];

        final Set<PluginInfo> plugins = new HashSet<PluginInfo>(30);
        final VirtualFile[] children = pluginsDir.getChildren();
        for (VirtualFile pluginFile : children) {
            if (!"jar".equalsIgnoreCase(pluginFile.getExtension()))
                continue;

            final PluginInfo plugin = parsePlugin(pluginFile);
            if (plugin != null)
                plugins.add(plugin);
        }

        return plugins.toArray(new PluginInfo[plugins.size()]);
    }

    /**
     * Parses the given plugin JAR and extracts general plugin information and available goals.
     *
     * @param pPluginJar the plugin JAR file
     *
     * @return plugin descriptor
     */
    private PluginInfo parsePlugin(final VirtualFile pPluginJar) {
        if (pPluginJar == null || !pPluginJar.isValid())
            return null;

        final VirtualFileManager vfMgr = VirtualFileManager.getInstance();
        final DefaultPluginInfo plugin = new DefaultPluginInfo();

        //
        //parse plugin POM
        //
        final String pomUrl = "jar://" + pPluginJar.getPath() + "!/project.xml";
        final VirtualFile pluginPom = vfMgr.findFileByUrl(pomUrl);
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
        final String jellyUrl = "jar://" + pPluginJar.getPath() + "!/plugin.jelly";
        final VirtualFile jellyFile = vfMgr.findFileByUrl(jellyUrl);
        if (jellyFile != null && jellyFile.isValid())
            plugin.setScriptFile(jellyFile);
        else
            return null;

        final XmlFile jellyPsi = PsiUtils.findXmlFile(project, jellyFile);
        final XmlTagPath goalsPath = new XmlTagPath(jellyPsi, "project/goal");
        final XmlTag[] goalTags = goalsPath.getAllTags();
        final GoalInfo[] goals = new GoalInfo[goalTags.length];
        for (int i = 0; i < goalTags.length; i++) {
            XmlTag tag = goalTags[i];
            final String name = tag.getAttributeValue("name");
            final String desc = tag.getAttributeValue("description");
            final String preReqsValue = tag.getAttributeValue("prereqs");
            final String[] preReqs = preReqsValue == null ? EMPTY_STRING_ARRAY : preReqsValue.split(
                    ",");

            final DefaultGoalInfo goal = new DefaultGoalInfo();
            goal.setName(name);
            goal.setDescription(desc);
            goal.setPlugin(plugin);
            goal.setPrereqs(preReqs);
            goals[i] = goal;
        }
        plugin.setGoals(goals);

        return plugin;
    }

    public static MavenPluginsManager getInstance(final Project pProject) {
        return pProject.getComponent(MavenPluginsManager.class);
    }
}
