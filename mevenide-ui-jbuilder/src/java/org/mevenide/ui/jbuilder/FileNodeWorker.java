package org.mevenide.ui.jbuilder;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jahia Ltd</p>
 * <p>Company: Jahia Ltd</p>
 * @author not attributable
 * @version 1.0
 */

import org.apache.maven.project.Dependency;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.project.io.ProjectReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import com.borland.jbuilder.node.JBProject;
import com.borland.jbuilder.paths.ProjectPathSet;
import com.borland.jbuilder.paths.PathSet;
import com.borland.primetime.vfs.Url;
import com.borland.primetime.node.Project;
import com.borland.primetime.node.Node;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.properties.resolver.PropertyResolverFactory;
import org.mevenide.properties.IPropertyResolver;

public class FileNodeWorker {
    public FileNodeWorker() {
    }

    public void refreshDependencies (Url url, Project project) {
        File parentFile = url.getFileObject().getParentFile();
        File projectFile = new File(parentFile, "project.xml");
        if (projectFile.exists()) {
            ArrayList mavenDependencyUrls = new ArrayList();
            try {
                ProjectReader projReader = ProjectReader.getReader();
                org.apache.maven.project.Project projectPOM = projReader.read(
                    projectFile);
                Iterator dependencyIter = projectPOM.getDependencies().iterator();

                IQueryContext defaultQueryContext = DefaultQueryContext.getNonProjectContextInstance();
                PropertyResolverFactory propertyResolverFactory = PropertyResolverFactory.getFactory();
                IPropertyResolver iPropertyResolver = propertyResolverFactory.createContextBasedResolver(defaultQueryContext);
                String repositoryPath = iPropertyResolver.getResolvedValue("maven.repo.local");

                while (dependencyIter.hasNext()) {
                    Dependency curDependency = (Dependency) dependencyIter.next();
                    File jarFile = findDependencyJar(repositoryPath,
                        curDependency);
                    if (jarFile != null) {
                        Url jarUrl = new Url(jarFile);
                        mavenDependencyUrls.add(jarUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // now let's update the library file with the dependencies extracted
            // from the Maven project descriptor. We do not update the project,
            // only the library file, so it's the user's choice to insert the
            // library or not.
            JBProject jbProject = (JBProject) project;
            File jbProjectFile = jbProject.getUrl().getFileObject();
            File jbParentFile = jbProjectFile.getParentFile();
            File mavenLibraryFile = new File(jbParentFile,
                                             "MavenAutoUpdated.library");
            ProjectPathSet projectPathSet = jbProject.getPaths();
            // PathSet[] requiredLibs = projectPathSet.getRequired();
            PathSet mavenLibrary = projectPathSet.getLibrary("MavenAutoUpdated");
            Url[] mavenDependencies = (Url[]) mavenDependencyUrls.toArray(new
                Url[mavenDependencyUrls.size()]);
            mavenLibrary.setClassPath(mavenDependencies);
            mavenLibrary.setUrl(new Url(mavenLibraryFile));
            mavenLibrary.save();

            projectPathSet.reloadLibraries();
        }
    }

    private File findDependencyJar (String repositoryPath,
                                    Dependency curDependency) {
        String type = curDependency.getType();
        if (type == null) {
            type = "jar";
        }
        String groupId = curDependency.getGroupId();
        if (groupId == null) {
            groupId = curDependency.getArtifactId();
        }
        String jar = curDependency.getJar();
        if (jar == null) {
            jar = curDependency.getArtifactId() + "-" +
                curDependency.getVersion() + "." + type;
        }
        String pathToJar = repositoryPath + File.separator +
            curDependency.getGroupId() + File.separator + type + "s" +
            File.separator + jar;
        File jarFile = new File(pathToJar);
        if (jarFile.exists()) {
            return jarFile;
        } else {
            return null;
        }
    }

    public void initMavenFileNode (MavenFileNode mavenFileNode, Project project, Url url, ArrayList goalNodes, ArrayList childNodes) {
        try {
            IGoalsGrabber projectGoalGrabber = GoalsGrabbersManager.
                getGoalsGrabber(url.getFileObject().toString());

            buildGoalNodes(mavenFileNode, project, projectGoalGrabber, goalNodes, childNodes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildGoalNodes (MavenFileNode mavenFileNode, Project project,
                                 IGoalsGrabber goalsGrabber, ArrayList goalNodes, ArrayList childNodes) {
        String[] plugins = goalsGrabber.getPlugins();
        for (int i = 0; i < plugins.length; i++) {
            System.out.println("plugin=" + plugins[i]);
            MavenCollectionNode pluginNode = new MavenCollectionNode(project, mavenFileNode,
                plugins[i]);
            childNodes.add(pluginNode);
            String[] goals = goalsGrabber.getGoals(plugins[i]);
            for (int j = 0; j < goals.length; j++) {
                System.out.println("  goal=" + goals[j]);
                MavenGoalNode newNode = null;
                if (goals[j].equalsIgnoreCase("(default)")) {
                    // in the case of the default goal, the full goal name
                    // is actually the plugin name
                    newNode = new MavenGoalNode(project, mavenFileNode, goals[j],
                                                "", plugins[i]);
                } else {
                    newNode = new MavenGoalNode(project, mavenFileNode, goals[j],
                                                "", plugins[i] + ":" + goals[j]);
                }
                if (newNode != null) {
                    pluginNode.addChild(newNode);
                    goalNodes.add(newNode);
                } else {
                    System.out.println("WARNING, NULL MAVENNODE !");
                }
            }
        }
    }

}
