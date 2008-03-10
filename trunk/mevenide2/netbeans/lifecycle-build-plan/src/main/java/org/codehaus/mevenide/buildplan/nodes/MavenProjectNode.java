/*
 *  Copyright 2008 Anuradha.
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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class MavenProjectNode extends AbstractNode {

    private MavenProject nmp;
    private String[] tasks;

    public MavenProjectNode(MavenProject nmp, String... tasks) {
        super(Children.LEAF);
        this.nmp = nmp;
        this.tasks = tasks;
        setDisplayName(nmp.getName() + " (" + nmp.getPackaging() + ")");
        setShortDescription(nmp.getDescription());
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/Maven2Icon.gif");
    }

    @Override
    public Action[] getActions(boolean bool) {
        return new Action[]{new AbstractAction(NbBundle.getMessage(MavenProjectNode.class, "LBL_Show_BuildPlan", getDisplayName())) {

                public void actionPerformed(ActionEvent e) {
                    new BuildPlanView(nmp, tasks).open();
                }
            }
                };
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction(NbBundle.getMessage(MavenProjectNode.class, "LBL_Show_BuildPlan", getDisplayName())) {

            public void actionPerformed(ActionEvent e) {
                new BuildPlanView(nmp, tasks).open();
            }
        };
    }
}
