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
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.borland.jbuilder.node.XMLFileNode;
import com.borland.primetime.PrimeTime;
import com.borland.primetime.node.DuplicateNodeException;
import com.borland.primetime.node.FileNode;
import com.borland.primetime.node.Node;
import com.borland.primetime.node.Project;
import com.borland.primetime.vfs.Url;

/**
 * <p>Title: project.xml Project view file node</p>
 * <p>Description: This class represents the project view file node that
 * contains the project.xml file. It derives from XMLFileNode to override the
 * default behavior for XML files, but basically only interceps calls if the
 * XML file is *not* named "project.xml".</p>
 * <p>It seems that JBuilder has an undocumented mechanism to avoid having to
 * revert to tricks like this one, using a setXmlHandler method on XMLFileNode,
 * but as it is not documented I prefer not to use it.</p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenFileNode extends XMLFileNode {

    boolean mavenFile = false;
    private ArrayList goalNodes = new ArrayList();
    private ArrayList childNodes = new ArrayList();
    private long lastModifTime = Long.MAX_VALUE;
    IFileNodeWorker fileNodeWorker = null;
    Class fileNodeWorkerClass = null;
    private static FilteringClassLoader filteringClassLoader = null;

    public MavenFileNode (Project project, Node parent, Url url)
        throws DuplicateNodeException {
        super(project, parent, url);
        ArrayList urls = new ArrayList();

        String sysClassPath = System.getProperty("java.class.path");
        StringTokenizer tokens = new StringTokenizer(sysClassPath, File.pathSeparator);
        while (tokens.hasMoreTokens()) {
            String curPath = tokens.nextToken();
            File pathFile = new File(curPath);
            try {
                if ((pathFile.toURL().toString().indexOf(".maven/repository") == -1) &&
                    (pathFile.toURL().toString().indexOf("mevenide-ui-jbuilder") == -1)) {
                    //System.out.println("Skipping JAR : " + pathFile.toURL());
                    continue;
                }
                //System.out.println("Adding path : " + pathFile.toURL());
                urls.add(pathFile.toURL());
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            }
        }
        if (filteringClassLoader == null) {
            filteringClassLoader = new FilteringClassLoader((URL[]) urls.toArray(new URL[urls.size()]));
        }
        try {
            fileNodeWorkerClass = filteringClassLoader.loadClass(
                "org.mevenide.ui.jbuilder.FileNodeWorker");
            fileNodeWorker = (IFileNodeWorker) fileNodeWorkerClass.newInstance();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }

        if ("project.xml".equalsIgnoreCase(url.getName())) {
            mavenFile = true;
            refreshGoals();
            refreshDependencies(getUrl(), getProject());
        }

    }

    private void refreshDependencies(Url url, Project project) {
        ClassLoader curContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(filteringClassLoader);
        fileNodeWorker.refreshDependencies(url, project);
        Thread.currentThread().setContextClassLoader(curContextClassLoader);
    }


    private void refreshGoals () {
        if (PrimeTime.isVerbose()) {
            System.out.println("Building goals list...");
        }
        File mavenFile = getUrl().getFileObject();
        lastModifTime = mavenFile.lastModified();
        goalNodes.clear();
        childNodes.clear();
        initMavenFileNode(this, getProject(), getUrl(), goalNodes, childNodes);
        if (PrimeTime.isVerbose()) {
            System.out.println("Goals list completed.");
        }
    }

    private void initMavenFileNode(MavenFileNode mavenFileNode, Project project, Url url, ArrayList goalNodes, ArrayList childNodes) {
        ClassLoader curContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(filteringClassLoader);
        fileNodeWorker.initMavenFileNode(mavenFileNode, project, url, goalNodes, childNodes);
        Thread.currentThread().setContextClassLoader(curContextClassLoader);
    }

    private void refreshIfChanged () {
        long curModifTime = getUrl().getFileObject().lastModified();
        if (curModifTime > lastModifTime) {
            refreshGoals();
            refreshDependencies(getUrl(), getProject());
        }
    }

    public boolean isMavenFile () {
        return mavenFile;
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

    public void doSave()
        throws java.lang.Exception {
        super.doSave();
        // if the project file has changed, we refresh the goal and dependencies
        // in the project.
        refreshIfChanged();
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
