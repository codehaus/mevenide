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
package org.codehaus.mevenide.buildplan.ui;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanView;
import org.codehaus.mevenide.buildplan.nodes.MavenProjectNode;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author  Anuradha
 */
public class BuildPlanViewUI extends javax.swing.JPanel implements ExplorerManager.Provider {

    private transient ExplorerManager explorerManager = new ExplorerManager();
    private BuildPlanView planView;
    private BeanTreeView treeView;

    /** Creates new form BuildPlanViewUI */
    public BuildPlanViewUI(BuildPlanView planView) {
        this.planView = planView;
        initComponents();
        treeView = (BeanTreeView) jScrollPane1;
        setName(planView.getProject().getName());
    //jScrollPane1.s
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public void buildNodeView() {
        Children.Array array = new Children.Array();
        AbstractNode node = new AbstractNode(array) {

            @Override
            public Image getIcon(int arg0) {
                return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/buildplangoals.png");
            }

            @Override
            public Image getOpenedIcon(int arg0) {
                return getIcon(arg0);
            }

            @Override
            public String getHtmlDisplayName() {
                return NbBundle.getMessage(BuildPlanViewUI.class,
                        "LBL_Buildplan_of_goals", new Object[]{getTasksAsString()});
            }
        };

        explorerManager.setRootContext(node);
        array.add(new Node[]{createLoadingNode()});

        treeView.expandNode(node);
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                Children.Array array = new Children.Array();
                List<MavenProject> mavenProjects = new ArrayList<MavenProject>();
                mavenProjects.add(planView.getProject());
                List collectedProjects = planView.getProject().getCollectedProjects();

                if (collectedProjects != null) {
                    mavenProjects.addAll(collectedProjects);
                }
                for (MavenProject mp : mavenProjects) {
                    array.add(new Node[]{new MavenProjectNode(mp, planView.getTasks())});
                }
                final AbstractNode node = new AbstractNode(array) {

                    @Override
                    public Image getIcon(int arg0) {
                        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/buildplangoals.png");
                    }

                    @Override
                    public Image getOpenedIcon(int arg0) {
                        return getIcon(arg0);
                    }

                    @Override
                    public String getHtmlDisplayName() {
                        return NbBundle.getMessage(BuildPlanViewUI.class,
                                "LBL_Buildplan_of_goals", new Object[]{getTasksAsString()});
                    }
                };
                explorerManager.setRootContext(node);
                treeView.expandNode(node);
            }
        });




    }

    private String getTasksAsString() {
        StringBuffer sb = new StringBuffer();
        for (String task : planView.getTasks()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(task);
        }

        return sb.toString();
    }

    public static Node createLoadingNode() {
        AbstractNode nd = new AbstractNode(Children.LEAF) {

            @Override
            public Image getIcon(int arg0) {
                return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/wait.gif");
            }
        };
        nd.setName("Loading"); //NOI18N

        nd.setDisplayName(NbBundle.getMessage(BuildPlanViewUI.class, "Node_Loading"));
        return nd;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
