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

import java.util.ArrayList;
import java.util.Hashtable;

import com.borland.jbuilder.node.JBProject;
import com.borland.primetime.PrimeTime;
import com.borland.primetime.build.BuildAction;
import com.borland.primetime.build.BuildProcess;
import com.borland.primetime.build.BuilderManager;
import com.borland.primetime.build.CodeGenerator;
import com.borland.primetime.build.Phase;
import com.borland.primetime.node.FileNode;
import com.borland.primetime.node.Node;
import com.borland.primetime.node.Project;
import com.borland.primetime.properties.MapProperty;
import com.borland.primetime.properties.PropertyPageFactory;
import com.borland.primetime.vfs.Url;

/**
 * <p>Title: Maven Builder</p>
 * <p>Description: This class is responsible for generating the build tasks
 * for a JBuilder project so that we can process the goals selected by
 * the user</p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenBuilder extends CodeGenerator {

    private static String MAVEN_BUILD_TARGET = "MavenBuildTarget";

    public static final String CATEGORY = "runtime";
    public static final MapProperty MAIN_CLASS = new MapProperty(
        CATEGORY, "opentools.mainclass",
        "org.apache.catalina.startup.Bootstrap");

    private Hashtable mavenNodeTasks;
    private boolean buildingProject;
    private boolean buildingMavenFile;

    public static void initOpenTool (byte majorVersion, byte minorVersion) {
        if (majorVersion != PrimeTime.CURRENT_MAJOR_VERSION) {
            return;
        }
        if (PrimeTime.isVerbose()) {
            System.out.println("Initializing Maven Builder");
        }

        BuilderManager.registerBuilder(new MavenBuilder());
    }

    public static BuildAction getBuildAction (Project project, String actionKey) {

        int separatorPos = actionKey.indexOf(";");
        if (separatorPos >= 0) {
            String mavenFileName = actionKey.substring(0, separatorPos);
            String fullyQualifiedGoalName = actionKey.substring(separatorPos +
                1);
            if (fullyQualifiedGoalName.endsWith(";"))
                fullyQualifiedGoalName = fullyQualifiedGoalName.substring(0,
                    fullyQualifiedGoalName.length());
            Url projectDirNodeUrl = project.getUrl().getParent();
            Url mavenFileUrl = projectDirNodeUrl.getRelativeUrl(mavenFileName);
            FileNode filenode = project.findNode(mavenFileUrl);
            if (filenode != null && isMavenNode(filenode)) {
                MavenFileNode mavenFileNode = (MavenFileNode) filenode;
                Node goalNodes[] = mavenFileNode.getGoalNodes();

                // now let's find the configured goal in the list of
                // current goals.
                for (int i = 0; i < goalNodes.length; i++) {
                    MavenGoalNode mavenGoalNode = (MavenGoalNode) goalNodes[i];
                    if (mavenGoalNode.getFullyQualifiedGoalName().equals(
                        fullyQualifiedGoalName)) {
                        // found it, let's return it
                        MavenGoalAction mavenGoalAction = new MavenGoalAction(
                            mavenGoalNode);
                        return mavenGoalAction;
                    }
                }

            }
        }

        // if we reach this point, it means we have not successfully found
        // the configured goal.
        return null;
    }

    public BuildAction[] getMappableTargets (Node node) {

        ArrayList buildActionList = new ArrayList();
        if (isMavenNode(node)) {
            MavenFileNode mavenFileNode = (MavenFileNode) node;
            Node goalNodes[] = mavenFileNode.getGoalNodes();
            for (int i = 0; i < goalNodes.length; i++) {
                MavenGoalNode mavenGoalNode = (MavenGoalNode) goalNodes[i];
                buildActionList.add(new MavenGoalAction(mavenGoalNode));
            }

        }
        return (BuildAction[]) buildActionList.toArray(new BuildAction[
            buildActionList.size()]);
    }

    public boolean isMakeable (Node node) {
        if (! (node.getProject() instanceof JBProject)) {
            return false;
        } else {
            return (node instanceof MavenGoalNode) || isMavenNode(node);
        }
    }

    public boolean isCleanable (Node node) {
        return false;
    }

    public void beginUpdateBuildProcess (BuildProcess buildprocess) {
        mavenNodeTasks = new Hashtable();
        buildingProject = false;
        buildingMavenFile = false;
    }

    public void updateBuildProcess (BuildProcess buildprocess, Node node) {
        if (node instanceof Project) {
            buildingProject = true;
            return;
        } else if (isMavenNode(node)) {
            buildingMavenFile = true;
        }
        if (isMakeable(node) && (!buildingProject)) {
            MavenBuildTask mavenBuildTask = getNodeBuildTask(buildprocess, node);
            if ( (node instanceof MavenGoalNode) &&
                !buildingMavenFile) {
                mavenBuildTask.addGoal( ( (MavenGoalNode) node).
                                       getFullyQualifiedGoalName());
            }
        }
    }

    public void endUpdateBuildProcess (BuildProcess buildprocess) {
        mavenNodeTasks = null;
    }

    public PropertyPageFactory getPageFactory (Object topic) {
        return null;
    }
    /*
    public PropertyPageFactory getPageFactory (Object topic) {
        if (topic instanceof MavenFileNode) {
            final MavenFileNode mavenFileNode = (MavenFileNode) topic;
            if (mavenFileNode.isMavenFile()) {
                return new PropertyPageFactory("Maven") {
                    public PropertyPage createPropertyPage () {
                        return new MavenPropertyPage(mavenFileNode);
                    }
                };
            } else {
                return null;
            }
        }
        return null;
    }
    */

    static private boolean isMavenNode (Node node) {
        if (node instanceof MavenFileNode) {
            MavenFileNode mavenFileNode = (MavenFileNode) node;
            return mavenFileNode.isMavenFile();
        } else {
            return false;
        }
    }

    private MavenBuildTask getNodeBuildTask (BuildProcess buildProcess,
                                             Node curNode) {

        MavenFileNode mavenFileNode = null;
        if (curNode instanceof MavenGoalNode) {
            mavenFileNode = ( (MavenGoalNode) curNode).getMavenNode();
        } else {
            mavenFileNode = (MavenFileNode) curNode;
        }

        MavenBuildTask mavenNodeTask = (MavenBuildTask) mavenNodeTasks.
            get(mavenFileNode);
        if (mavenNodeTask != null) {
            // we already have a task for this node, let's return it.
            return mavenNodeTask;
        } else {
            // no build task existing for the current Maven file node, let's
            // create one
            MavenBuildTask mavenBuildTask = (MavenBuildTask) buildProcess.
                createTask(org.mevenide.ui.jbuilder.MavenBuildTask.class,
                           MAVEN_BUILD_TARGET);
            mavenBuildTask.setMavenFileNode(mavenFileNode);

            // we must setup a dependency otherwise the task will never be
            // executed by JBuilder.
            buildProcess.addDependency(MAVEN_BUILD_TARGET,
                                       Phase.POST_COMPILE_PHASE);

            // ok now let's store this in the mavenFileNode task table
            mavenNodeTasks.put(mavenFileNode, mavenBuildTask);
            return mavenBuildTask;
        }
    }

}
