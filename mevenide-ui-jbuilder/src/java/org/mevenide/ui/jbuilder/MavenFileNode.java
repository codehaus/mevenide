/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.jbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.maven.project.Dependency;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.project.io.ProjectReader;
import com.borland.jbuilder.node.JBProject;
import com.borland.jbuilder.node.XMLFileNode;
import com.borland.jbuilder.paths.PathSet;
import com.borland.jbuilder.paths.ProjectPathSet;
import com.borland.primetime.PrimeTime;
import com.borland.primetime.node.DuplicateNodeException;
import com.borland.primetime.node.FileNode;
import com.borland.primetime.node.Node;
import com.borland.primetime.node.Project;
import com.borland.primetime.vfs.Url;

/**
 * <p>Title: maven.xml Project view file node</p>
 * <p>Description: This class represents the project view file node that
 * contains the maven.xml file. It derives from XMLFileNode to override the
 * default behavior for XML files, but basically only interceps calls if the
 * XML file is *not* named "maven.xml".</p>
 * <p>It seems that JBuilder has an undocumented mechanism to avoid having to
 * revert to tricks like this one, using a setXmlHandler method on XMLFileNode,
 * but as it is not documented I prefer not to use it.</p>
 * <p>Copyright: Copyright (c) 2004 Jahia Ltd</p>
 * <p>Company: Jahia Ltd</p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenFileNode extends XMLFileNode {

    boolean mavenFile = false;
    private ArrayList goalNodes = new ArrayList();
    private ArrayList childNodes = new ArrayList();
    private long lastModifTime = Long.MAX_VALUE;

    public MavenFileNode (Project project, Node parent, Url url)
        throws DuplicateNodeException {
        super(project, parent, url);

        if ("maven.xml".equalsIgnoreCase(url.getName())) {
            mavenFile = true;
            refreshGoals();
            refreshDependencies();
        }

    }

    private void refreshDependencies () {
        File parentFile = getUrl().getFileObject().getParentFile();
        File projectFile = new File(parentFile, "project.xml");
        if (projectFile.exists()) {
            ArrayList mavenDependencyUrls = new ArrayList();
            try {
                ProjectReader projReader = ProjectReader.getReader();
                org.apache.maven.project.Project projectPOM = projReader.read(
                    projectFile);
                Iterator dependencyIter = projectPOM.getDependencies().iterator();
                String userHome = System.getProperty("user.home");
                String repositoryPath = userHome + File.separator + ".maven" +
                    File.separator + "repository";

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
            JBProject jbProject = (JBProject) getProject();
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

    private void refreshGoals () {
        System.out.println("Building goals list...");
        File mavenFile = getUrl().getFileObject();
        lastModifTime = mavenFile.lastModified();
        goalNodes.clear();
        childNodes.clear();
        initMavenFileNode(getProject(), getUrl());
        System.out.println("Goals list completed.");
    }

    private void refreshIfChanged () {
        long curModifTime = getUrl().getFileObject().lastModified();
        if (curModifTime > lastModifTime) {
            refreshGoals();
            refreshDependencies();
        }
    }

    public boolean isMavenFile () {
        return mavenFile;
    }

    private void initMavenFileNode (Project project, Url url) {
        try {
            IGoalsGrabber projectGoalGrabber = GoalsGrabbersManager.
                getGoalsGrabber(url.getFileObject().toString());

            buildGoalNodes(project, projectGoalGrabber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildGoalNodes (Project project,
                                 IGoalsGrabber goalsGrabber) {
        String[] plugins = goalsGrabber.getPlugins();
        for (int i = 0; i < plugins.length; i++) {
            System.out.println("plugin=" + plugins[i]);
            MavenCollectionNode pluginNode = new MavenCollectionNode(project, this,
                plugins[i]);
            childNodes.add(pluginNode);
            String[] goals = goalsGrabber.getGoals(plugins[i]);
            for (int j = 0; j < goals.length; j++) {
                System.out.println("  goal=" + goals[j]);
                MavenGoalNode newNode = null;
                if (goals[j].equalsIgnoreCase("(default)")) {
                    // in the case of the default goal, the full goal name
                    // is actually the plugin name
                    newNode = new MavenGoalNode(project, this, goals[j],
                                                "", plugins[i]);
                } else {
                    newNode = new MavenGoalNode(project, this, goals[j],
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

    public boolean hasDisplayChildren () {
        if (!mavenFile) {
            return super.hasDisplayChildren();
        }
        refreshIfChanged();
        if (childNodes.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Node[] getDisplayChildren () {
        if (!mavenFile) {
            return super.getDisplayChildren();
        }
        refreshIfChanged();
        return (Node[]) childNodes.toArray(new Node[childNodes.size()]);
    }

    public Node[] getGoalNodes () {
        if (!mavenFile) {
            return super.getDisplayChildren();
        }
        return (Node[]) goalNodes.toArray(new Node[goalNodes.size()]);
    }

    public static void initOpenTool (byte major, byte minor) {
        if (PrimeTime.isVerbose()) {
            System.out.println("Registering Maven project file XML node");
        }
        Class existingXMLClass = FileNode.findFileNodeClass("xml", false);
        if (PrimeTime.isVerbose()) {
            System.out.println("Existing XML class handler=" +
                               existingXMLClass.getName());
        }
        FileNode.registerFileNodeClass("xml", "XML file", MavenFileNode.class,
                                       XMLFileNode.ICON);
    }

}
