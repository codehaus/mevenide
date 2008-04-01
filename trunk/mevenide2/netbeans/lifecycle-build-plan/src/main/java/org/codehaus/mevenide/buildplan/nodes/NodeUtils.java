/*
 *  Copyright 2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.buildplan.nodes;

import java.awt.Image;
import java.util.List;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class NodeUtils {

    private NodeUtils() {
    }

    public static Node createLoadingNode() {
        AbstractNode nd = new AbstractNode(Children.LEAF) {

            @Override
            public Image getIcon(int arg0) {
                return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/wait.gif");
            }
        };
        nd.setName("Loading"); //NOI18N

        nd.setDisplayName(NbBundle.getMessage(NodeUtils.class, "Node_Loading"));
        return nd;
    }

    public static Children createBuildPlanChildren(final BuildPlanView view,
            final List<MavenProject> mps) {
        final Children.Array array = new Children.Array();

        try {
            ReactorManager rm = new ReactorManager(mps, ReactorManager.FAIL_FAST);
            List<MavenProject> sortedProjects = rm.getSortedProjects();
            for (MavenProject mp : sortedProjects) {
                array.add(new Node[]{new LifecycleNode(view, mp)
                        });
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return array;
    }
    
}
