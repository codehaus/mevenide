package org.codehaus.mevenide.idea.model;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.PluginDocument;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModelUtils {
    private static final Logger LOG = Logger.getLogger(ModelUtils.class);

    public static MavenProjectDocument loadMavenProjectDocument(ActionContext context, VirtualFile vFile) {
        assert vFile.getName().equalsIgnoreCase(PluginConstants.MAVEN_POM_FILENAME);

        PsiFile psiFile = PsiManager.getInstance(context.getPluginProject()).findFile(vFile);
        if ( psiFile == null ) {
            return null;
        }

        MavenProjectDocument mavenProjectDocument = new MavenProjectDocumentImpl(psiFile);
        loadPlugins(mavenProjectDocument, context.getProjectPluginSettings().getMavenRepository());
        context.getPomDocumentList().add(mavenProjectDocument);

        return mavenProjectDocument;
    }

    public static void loadPlugins(MavenProjectDocument mavenProjectDocument, String mavenRepository) {
        ProjectDocument.Project project = mavenProjectDocument.getProject();
        if ( project == null ) {
            return;
        }

        List<ProjectDocument.Plugin> pomPluginList = project.getBuild().getPlugins().getPlugins();

        if ( pomPluginList.size() == 0 ){
            LOG.warn("Project does not contain any customized plugins");
            return;
        }

//        LOG.warn("Number of plugins: " + pomPluginList.size());

        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        for (ProjectDocument.Plugin pomPlugin : pomPluginList) {
            String groupId = pomPlugin.getGroupId().getStringValue();

            if (StringUtils.isEmpty(groupId)) {
                groupId = "org.apache.maven.plugins";
            }

            String artifactId = pomPlugin.getArtifactId().getStringValue();
            String version = pomPlugin.getVersion().getStringValue();
            String mostRecentVersion;

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

                if (StringUtils.isEmpty(version) &&!directoryList.isEmpty()) {
                    mostRecentVersion = directoryList.get(directoryList.size() - 1);
                } else {
                    mostRecentVersion = version;
                }

                pluginDirectory = pluginDirectory + System.getProperty("file.separator") + mostRecentVersion;

                File pluginJarArchive = new File(pluginDirectory + System.getProperty("file.separator") + artifactId
                                                 + "-" + mostRecentVersion + ".jar");

                LOG.debug("Adding plugin: " + pluginJarArchive.getAbsolutePath() + " to POM");

                MavenPluginDocument pluginDocument = createMavenPluginDocument(pluginJarArchive.getPath(), false);

                if (pluginDocument != null) {
                    mavenProjectDocument.addPlugin(pluginDocument);
                }
            }
        }
    }

    public static MavenPluginDocument createMavenPluginDocument(String path, boolean isManuallyAdded) {
        PluginDocument pluginDocument;

        try {
            ZipFile jarArchive = new ZipFile(path);
            ZipEntry entry = jarArchive.getEntry(PluginConstants.MAVEN_PLUGIN_DESCRIPTOR);
            if (entry != null) {
                pluginDocument = PluginDocument.Factory.parse(jarArchive.getInputStream(entry));
            } else {
                return null;
//                throw new IdeaMavenPluginException("Selected archive is not a Maven 2 plugin!");
            }
        } catch (IOException e) {
            return null;
        }

        Set<PluginGoal> pluginGoalList = new LinkedHashSet<PluginGoal>();

        for (PluginDocument.Mojo mojo : pluginDocument.getPlugin().getMojos().getMojoList()) {
            PluginGoal pluginGoal = new PluginGoal();

            pluginGoal.setPluginPrefix(pluginDocument.getPlugin().getGoalPrefix());
            pluginGoal.setGoal(mojo.getGoal());
            pluginGoalList.add(pluginGoal);
        }

        MavenPluginDocument mavenPluginDocument = new MavenPluginDocumentImpl(pluginDocument);

        mavenPluginDocument.setPluginGoalList(pluginGoalList);
        mavenPluginDocument.setPluginPath(path);
        mavenPluginDocument.setMemberOfPom(!isManuallyAdded);

        return mavenPluginDocument;
    }
}
