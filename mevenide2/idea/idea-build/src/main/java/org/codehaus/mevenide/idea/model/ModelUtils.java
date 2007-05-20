package org.codehaus.mevenide.idea.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.PluginDocument;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModelUtils {
    private static final Logger LOG = Logger.getLogger(ModelUtils.class);

    public static MavenProjectDocument loadMavenProjectDocument(Project project, VirtualFile vFile) {
        return new MavenProjectDocumentImpl(PsiManager.getInstance(project).findFile(vFile));
    }

    public static void loadPlugins(MavenProjectDocument mavenProjectDocument, String mavenRepository) {
        ProjectDocument.Project project = mavenProjectDocument.getProject();
        if (project == null) {
            return;
        }

        for (ProjectDocument.Plugin pomPlugin : project.getBuild().getPlugins().getPlugins()) {
            String path = findPluginPath(mavenRepository, pomPlugin.getGroupId().getStringValue(), pomPlugin.getArtifactId().getStringValue(), pomPlugin.getVersion().getStringValue());
            if (path != null) {
                try {
                    MavenPluginDocument pluginDocument = createMavenPluginDocument(path);
                    if (pluginDocument != null) {
                        mavenProjectDocument.addPlugin(pluginDocument);
                    }
                } catch (IdeaMavenPluginException e) {
                    LOG.warn(e.getMessage());
                }
            }
        }
        addAdditionalPlugin(mavenRepository, mavenProjectDocument, "org.apache.maven.plugins", "maven-site-plugin", null);
    }

    private static void addAdditionalPlugin(String mavenRepository, MavenProjectDocument mavenProjectDocument, String groupId, String artifactId, String version) {
        /*
        ProjectDocument.Project project = mavenProjectDocument.getProject();
        for (ProjectDocument.Plugin pomPlugin : project.getBuild().getPlugins().getPlugins()) {
            String existingGroupId = pomPlugin.getGroupId().getStringValue();
            String existingArtifactId = pomPlugin.getArtifactId().getStringValue();
            // don't add the plugin in case it already was specified in the POM!
            if (existingGroupId == null) {
                existingGroupId = "org.apache.maven.plugins";
            }
            if (existingGroupId.equals(groupId) && existingArtifactId != null && existingArtifactId.equals(artifactId)) {
                return;
            }

        }  */
        String path = findPluginPath(mavenRepository, groupId, artifactId, version);
        if (path != null) {
            try {
                MavenPluginDocument pluginDocument = createMavenPluginDocument(path);
                if (pluginDocument != null) {
                    mavenProjectDocument.addPlugin(pluginDocument);
                }
            } catch (IdeaMavenPluginException e) {
                LOG.warn(e.getMessage());
            }
        }
    }

    public static String findPluginPath(String mavenRepository, String groupId, String artifactId, String version) {
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        if (StringUtils.isEmpty(groupId)) {
            groupId = "org.apache.maven.plugins";
        }
        groupId = StringUtils.replace(groupId, ".", System.getProperty("file.separator"));

        String pluginDirectory = mavenRepository + System.getProperty("file.separator") + groupId
                + System.getProperty("file.separator") + artifactId;
        VirtualFile pluginDirectoryAsFile = localFileSystem.findFileByIoFile(new File(pluginDirectory));

        if (pluginDirectoryAsFile == null) {
            groupId = "org.codehaus.mojo";
            pluginDirectory = mavenRepository + System.getProperty("file.separator") + groupId
                    + System.getProperty("file.separator") + artifactId;
            pluginDirectoryAsFile = localFileSystem.findFileByIoFile(new File(pluginDirectory));
        }

        if ((pluginDirectoryAsFile != null) && pluginDirectoryAsFile.isDirectory()) {
            VirtualFile[] availableVersions = pluginDirectoryAsFile.getChildren();
            List<String> directoryList = new ArrayList<String>();

            for (VirtualFile availableVersion : availableVersions) {
                if (availableVersion.isDirectory()) {
                    directoryList.add(availableVersion.getName());
                }
            }

            Collections.sort(directoryList);

            final String mostRecentVersion;
            if (StringUtils.isEmpty(version) && !directoryList.isEmpty()) {
                mostRecentVersion = directoryList.get(directoryList.size() - 1);
            } else {
                mostRecentVersion = version;
            }

            pluginDirectory = pluginDirectory + System.getProperty("file.separator") + mostRecentVersion;

            File pluginJarArchive = new File(pluginDirectory + System.getProperty("file.separator") + artifactId
                    + "-" + mostRecentVersion + ".jar");

            LOG.debug("Adding plugin: " + pluginJarArchive.getAbsolutePath() + " to POM");
            return pluginJarArchive.getPath();
        }
        return null;
    }

    public static MavenPluginDocument createMavenPluginDocument(String path) throws IdeaMavenPluginException {
        PluginDocument pluginDocument;

        try {
            ZipFile jarArchive = new ZipFile(path);
            ZipEntry entry = jarArchive.getEntry(PluginConstants.MAVEN_PLUGIN_DESCRIPTOR);
            if (entry != null) {
                pluginDocument = PluginDocument.Factory.parse(jarArchive.getInputStream(entry));
            } else {
                throw new IdeaMavenPluginException("Selected archive is not a Maven 2 plugin!");
            }
        } catch (IOException e) {
            return null;
        }

        return new MavenPluginDocumentImpl(pluginDocument, path);
    }
}
