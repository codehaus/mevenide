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
package org.mevenide.netbeans.project.customizer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.netbeans.project.dependencies.DependencyNode;
import org.mevenide.netbeans.project.dependencies.DependencyPanel;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.resolver.DefaultsResolver;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class OverridePanel extends JPanel implements ExplorerManager.Provider, ProjectPanel {
    private static final Log logger = LogFactory.getLog(DependenciesPanel.class);
    
    private MavenProject project;
    private ExplorerManager manager;
    private DependencyPanel currentCustomizer;
    private BeanTreeView btv;
    private OriginChange originChange;
    private OriginChange originChange2;
    
    /** Creates new form CustomGoalsPanel */
    public OverridePanel(MavenProject proj, boolean editable) {
        project = proj;
        initComponents();
        GridBagConstraints fillConstraints = new GridBagConstraints();
        fillConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fillConstraints.gridheight = GridBagConstraints.REMAINDER;
        fillConstraints.fill = GridBagConstraints.BOTH;
        fillConstraints.weightx = 1.0;
        fillConstraints.weighty = 1.0;
        
        manager = new ExplorerManager();
        btv = new BeanTreeView();    // Add the BeanTreeView
        btv.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION );
        btv.setPopupAllowed( false );
        btv.setRootVisible( false );
        btv.setDefaultActionAllowed( false );
        pnlDeps.add(btv, fillConstraints);
        manager.addPropertyChangeListener( new ManagerChangeListener() );
        
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.gridx = 1;
//        constraints.gridy = 0;
//        constraints.anchor = GridBagConstraints.NORTHWEST;
//        constraints.weightx = 0.1;
//        constraints.insets = new java.awt.Insets(6, 6, 0, 0);
//        originChange = LocationComboFactory.createPropertiesChange(project);
        originChange.setChangeObserver(new OriginChange.ChangeObserver() {
            public void actionSelected(String action) {
                if (OriginChange.ACTION_RESET_TO_DEFAULT.equals(action)) {
                    // assuming the correct default value is not-override..
                    System.out.println("overriding to default");
                    cbOverride.setSelected(false);
                }
            }
        });
        cbOverride.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent event) {
                if (originChange.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED ||
                    originChange.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
                        // assume the default placement is build..
                        // maybe have configurable or smartish later..
                        originChange.setAction(OriginChange.ACTION_DEFINE_IN_BUILD);
                }
            }
        });
        setFieldsEditable(editable);
    }
    
    public void setFieldsEditable(boolean editable) {
        txtOverrideValue.setEditable(editable);
        originChange2.getComponent().setEnabled(editable);
    }  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        cbOverride = new javax.swing.JCheckBox();
        pnlDeps = new javax.swing.JPanel();
        pnlSingleDep = new javax.swing.JPanel();
        lblOverrideValue = new javax.swing.JLabel();
        txtOverrideValue = new javax.swing.JTextField();
        originChange2 = LocationComboFactory.createPropertiesChange(project);
        btnJarOverrideLoc = btnJarOverrideLoc = (JButton)originChange2.getComponent();
        originChange = LocationComboFactory.createPropertiesChange(project);
        btnOverrideLoc = (JButton)originChange.getComponent();

        setLayout(new java.awt.GridBagLayout());

        cbOverride.setText("Override");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(cbOverride, gridBagConstraints);

        pnlDeps.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(pnlDeps, gridBagConstraints);

        pnlSingleDep.setLayout(new java.awt.GridBagLayout());

        lblOverrideValue.setText("Artifact");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlSingleDep.add(lblOverrideValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlSingleDep.add(txtOverrideValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlSingleDep.add(btnJarOverrideLoc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(pnlSingleDep, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnOverrideLoc, gridBagConstraints);

    }//GEN-END:initComponents
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public void addNotify() {
        super.addNotify();
    }
    
    private void selectFirstNode() {
        Children ch = manager.getRootContext().getChildren();
        if ( ch != null ) {
            Node nodes[] = ch.getNodes();
            
            if ( nodes != null && nodes.length > 0 ) {
                try {
                    manager.setSelectedNodes( new Node[] { nodes[0] } );
                }
                catch ( PropertyVetoException e ) {
                    // No node will be selected
                }
            }
        }
    }
    
    /** Listens to selection change and shows the customizers as
     *  panels
     */
    
    private class ManagerChangeListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getSource() != manager) {
                return;
            }
            
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node nodes[] = manager.getSelectedNodes();
                if ( nodes == null || nodes.length <= 0 ) {
                    return;
                }
                DependencyNode node = (DependencyNode)nodes[0];
                setDependency(node.getDependency());
                
                return;
            }
        }
    }
    
    private Node createRootNode(Project proj) {
        Node root = new AbstractNode(new DepRootChildren(proj));
        root.setName("root invisible");
        return root;
    }
    
    public void setDependency(Dependency dependency) {
    }    
    
    public Project copyProject(Project project) {
        //TODO
        return null;
    }
    
    public String getValidityMessage() {
        return "";
    }
    
    public boolean isInValidState() {
        return true;
    }
    
    public void setProject(Project proj, boolean resolve) {
        manager.setRootContext(createRootNode(proj));
        btv.expandAll();
        selectFirstNode();
        int loc = project.getPropertyLocator().getPropertyLocation("maven.jar.override");
        String value = project.getPropertyResolver().getResolvedValue("maven.jar.override");
        if ("true".equalsIgnoreCase(value)) {
            cbOverride.setSelected(true);
        }
        originChange.setSelectedLocationID(loc);
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
    }
    
    private class DepRootChildren extends Children.Keys {
        private Project project;
        public DepRootChildren(Project proj) {
            super();
            project = proj;
        }
        
        public void addNotify() {
            List depend = project.getDependencies();
            if (depend != null) {
                setKeys(depend);
            } else {
                setKeys(Collections.EMPTY_LIST);
            }
            
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object obj) {
            return new Node[] { new DependencyNode((Dependency)obj, OverridePanel.this.project)};
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnJarOverrideLoc;
    private javax.swing.JButton btnOverrideLoc;
    private javax.swing.JCheckBox cbOverride;
    private javax.swing.JLabel lblOverrideValue;
    private javax.swing.JPanel pnlDeps;
    private javax.swing.JPanel pnlSingleDep;
    private javax.swing.JTextField txtOverrideValue;
    // End of variables declaration//GEN-END:variables
    
}
