/*
 * AddDependencyUI.java
 *
 * Created on December 26, 2007, 11:25 AM
 */
package org.codehaus.mevenide.repository.dependency.ui;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.repository.NodeUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G (theanuradha-at-netbeans.org)
 */
public class AddDependencyUI extends javax.swing.JPanel implements ExplorerManager.Provider {

    private ExplorerManager explorerManager = new ExplorerManager();
    private final JButton addButton;

    /** Creates new form AddDependencyUI */
    public AddDependencyUI(String libDef) {
        initComponents();
        lblDescription.setText(NbBundle.getMessage(AddDependencyUI.class, "LBL_Description", libDef));//NOI18N
        addButton = new JButton(NbBundle.getMessage(AddDependencyUI.class, "BTN_Add"));//NOI18N
        addButton.setEnabled(false);
        final List<NbMavenProject> openProjects =getOpenProjects();
        Children children = new Children.Keys<NbMavenProject>() {

            @Override
            protected Node[] createNodes(NbMavenProject nmp) {
                return new Node[]{new OpenProjectNode(nmp)};
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(openProjects);
            }
        };

        AbstractNode openProjectsNode = new AbstractNode(children){

            @Override
            public Image getIcon(int arg0) {
                return NodeUtils.getTreeFolderIcon(false);
            }

            @Override
            public Image getOpenedIcon(int arg0) {
                return NodeUtils.getTreeFolderIcon(true);
            }
        
        };
        openProjectsNode.setDisplayName(NbBundle.getMessage(AddDependencyUI.class, "LBL_OpenProjects"));//NOI18N
        explorerManager.setRootContext(openProjectsNode);
        BeanTreeView beanTreeView = (BeanTreeView) jScrollPane1;
        beanTreeView.setPopupAllowed(false);
        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent arg0) {
                if (arg0.getPropertyName().equals("selectedNodes")) {//NOI18N
                    Node[] selectedNodes = explorerManager.getSelectedNodes();
                    boolean enable=false;
                    for (Node node : selectedNodes) {
                        if (node instanceof OpenProjectNode) {
                          enable=true;
                          break;

                        }
                    }
                    addButton.setEnabled(enable);
                   
                }
            }
        });
    }

    public JButton getAddButton() {
        return addButton;
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
        lblDescription = new javax.swing.JLabel();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));

        lblDescription.setText(org.openide.util.NbBundle.getMessage(AddDependencyUI.class, "LBL_Description")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    // End of variables declaration//GEN-END:variables
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public List<NbMavenProject> getSelectedMavenProjects() {
        List<NbMavenProject> mavenProjects = new ArrayList<NbMavenProject>();
        Node[] selectedNodes = explorerManager.getSelectedNodes();
        for (Node node : selectedNodes) {
            if (node instanceof OpenProjectNode) {
                OpenProjectNode opn = (OpenProjectNode) node;
                mavenProjects.add(opn.project);
            }
        }

        return mavenProjects;
    }
    public  List<NbMavenProject> getOpenProjects() {
        List<NbMavenProject> mavenProjects = new ArrayList<NbMavenProject>();
        //get all open projects
        Project[] prjs = OpenProjects.getDefault().getOpenProjects();

        for (Project project : prjs) {
            //varify is maven project 
            NbMavenProject mavProj = project.getLookup().lookup(NbMavenProject.class);
            if(mavProj!=null)
                mavenProjects.add(mavProj);
        }

        return mavenProjects;

    }
    public static class OpenProjectNode extends AbstractNode {

        private NbMavenProject project;
        private ProjectInformation pi;

        public OpenProjectNode(NbMavenProject project) {
            super(Children.LEAF);
            this.project = project;
            pi = ProjectUtils.getInformation(project);
        }

        @Override
        public Image getIcon(int arg0) {
            return Utilities.icon2Image(pi.getIcon());
        }

        @Override
        public String getDisplayName() {
            return pi.getDisplayName();
        }
    }
}
