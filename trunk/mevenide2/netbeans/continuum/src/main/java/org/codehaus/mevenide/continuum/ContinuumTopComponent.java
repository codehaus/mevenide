/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.continuum;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.model.project.ProjectDependency;
import org.apache.maven.continuum.model.project.ProjectDeveloper;
import org.apache.maven.continuum.model.scm.ScmResult;
import org.codehaus.mevenide.continuum.options.SingleServer;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays the continuum servers and the projects in them...
 */
final class ContinuumTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;

    private static ContinuumTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/codehaus/mevenide/continuum/ContinuumServer.png";

    private static final String PREFERRED_ID = "ContinuumTopComponent";

    private ExplorerManager manager;
    private PropertyChangeListener nodeListener;
    private Node lastNode;
    
    private ContinuumTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ContinuumTopComponent.class, "CTL_ContinuumTopComponent"));
        setToolTipText(NbBundle.getMessage(ContinuumTopComponent.class, "HINT_ContinuumTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
        manager = new ExplorerManager();
        nodeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (ProjectNode.PROPERTY_COMPLETE_RELOAD.equals(evt.getPropertyName())) {
                    Node[] nds = manager.getSelectedNodes();
                    Project proj = (Project)nds[0].getLookup().lookup(Project.class);
                    populateProjectPanel(proj);
                }
            }
        };
        
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    setActivatedNodes(manager.getSelectedNodes());
                    Node[] nds = manager.getSelectedNodes();
                    if (lastNode != null) {
                        lastNode.removePropertyChangeListener(nodeListener);
                    }
                    if (nds.length == 1) {
                        Project proj = (Project)nds[0].getLookup().lookup(Project.class);
                        if (proj != null) {
                            pnlDetails.setVisible(true);
                            lastNode = nds[0];
                            lastNode.addPropertyChangeListener(nodeListener);
                            populateProjectPanel(proj);
                        } else {
                            pnlDetails.setVisible(false);
                        }
                    } else {
                        pnlDetails.setVisible(false);
                    }
                }
            }
        });
        BeanTreeView view = new BeanTreeView();
        view.setRootVisible(false);
        view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jPanel1.add(view);
        pnlDetails.setVisible(false);
        jButton1.setEnabled(false);
        jButton1.setVisible(false);
        jButton2.setEnabled(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        pnlDetails = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        spGeneral = new javax.swing.JScrollPane();
        pnlGeneral = new javax.swing.JPanel();
        lblArtifactId = new javax.swing.JLabel();
        txtArtifactId = new javax.swing.JTextField();
        lblGroupId = new javax.swing.JLabel();
        txtGroupId = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        lblState = new javax.swing.JLabel();
        lblStateValue = new javax.swing.JLabel();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblBuildNumber = new javax.swing.JLabel();
        lblBuildNumberValue = new javax.swing.JLabel();
        pnlDevelopers = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstDevelopers = new javax.swing.JList();
        pnlDependencies = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstDependencies = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jButton1.setText("Refresh");
        add(jButton1, new java.awt.GridBagConstraints());

        jButton2.setText("Add Server...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        add(jButton2, new java.awt.GridBagConstraints());

        jPanel1.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(jPanel1, gridBagConstraints);

        pnlDetails.setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        spGeneral.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pnlGeneral.setLayout(new java.awt.GridBagLayout());

        lblArtifactId.setText("ArtifactId :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblArtifactId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        pnlGeneral.add(txtArtifactId, gridBagConstraints);

        lblGroupId.setText("GroupId :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblGroupId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        pnlGeneral.add(txtGroupId, gridBagConstraints);

        lblVersion.setText("Version :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblVersion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        pnlGeneral.add(txtVersion, gridBagConstraints);

        lblName.setText("Name :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        pnlGeneral.add(txtName, gridBagConstraints);

        lblDescription.setText("Description :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        pnlGeneral.add(txtDescription, gridBagConstraints);

        lblState.setText("State :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblState, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblStateValue, gridBagConstraints);

        lblUrl.setText("Url :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblUrl, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        pnlGeneral.add(txtUrl, gridBagConstraints);

        lblBuildNumber.setText("Build Number :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblBuildNumber, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlGeneral.add(lblBuildNumberValue, gridBagConstraints);

        spGeneral.setViewportView(pnlGeneral);

        jTabbedPane1.addTab("General", spGeneral);

        pnlDevelopers.setLayout(new java.awt.BorderLayout());

        lstDevelopers.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstDevelopers);

        pnlDevelopers.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Developers", pnlDevelopers);

        pnlDependencies.setLayout(new java.awt.BorderLayout());

        lstDependencies.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstDependencies);

        pnlDependencies.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Dependencies", pnlDependencies);

        jTabbedPane1.addTab("Build Definitions", jPanel5);

        pnlDetails.add(jTabbedPane1, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(pnlDetails, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        SingleServer ss = new SingleServer();
        DialogDescriptor dd = new DialogDescriptor(ss, "Add Continuum server");
        dd.setOptions(new Object [] {
            NotifyDescriptor.OK_OPTION,
            NotifyDescriptor.CANCEL_OPTION
        });
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            ContinuumSettings.getDefault().addServer(ss.getURL());
//            ContinuumSettings.getDefault().addOutputServer(ss.getOutputURL()());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblArtifactId;
    private javax.swing.JLabel lblBuildNumber;
    private javax.swing.JLabel lblBuildNumberValue;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblGroupId;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblState;
    private javax.swing.JLabel lblStateValue;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JList lstDependencies;
    private javax.swing.JList lstDevelopers;
    private javax.swing.JPanel pnlDependencies;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlDevelopers;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JScrollPane spGeneral;
    private javax.swing.JTextField txtArtifactId;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables

    private void populateProjectPanel(Project proj) {
        txtArtifactId.setText(proj.getArtifactId());
        txtGroupId.setText(proj.getGroupId());
        txtName.setText(proj.getName());
        txtVersion.setText(proj.getVersion());
        txtDescription.setText(proj.getDescription());
        List dependencies = proj.getDependencies();
        DefaultListModel model = new DefaultListModel();
        if (dependencies != null) {
            Iterator it = dependencies.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                ProjectDependency dep = (ProjectDependency)obj;
                model.addElement(dep.getGroupId() + ":" + dep.getArtifactId() + ":" + dep.getVersion());
            }
        }
        lstDependencies.setModel(model);
        model = new DefaultListModel();
        List developers = proj.getDevelopers();
        if (developers != null) {
            Iterator it = developers.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                ProjectDeveloper dep = (ProjectDeveloper)obj;
                model.addElement(dep.getScmId() + " - " + dep.getName() + " (" + dep.getEmail() + ")");
            }
        }
        lstDevelopers.setModel(model);
        int state = proj.getState();
        String stateStr = "";
        boolean running = false;
        if (state == 6 || state == 7 || state == 8 ) {
            running = true;
            state = proj.getOldState();
        }
        if (state == 1) {
            stateStr = "Never built before";
        }
        if (state == 2) {
            stateStr = "Last Build successful.";
        }
        if (state == 3 || state == 4) {
            stateStr = "Last Build failed.";
            ScmResult res = proj.getCheckoutResult();
            if (res != null) {
                //TODO.. add more details
            }
        }
        if (running) {
            stateStr = stateStr + " (Now running)";
        }
        lblStateValue.setText("<html><b>" + stateStr + "</b></html>");
        lblBuildNumberValue.setText("" + proj.getBuildNumber() + " " + proj.getLatestBuildId());
        txtUrl.setText(proj.getUrl());
    }
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ContinuumTopComponent getDefault() {
        if (instance == null) {
            instance = new ContinuumTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ContinuumTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ContinuumTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find Continuum component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ContinuumTopComponent) {
            return (ContinuumTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public void componentOpened() {
        startLoading();
    }

    public void componentClosed() {
    }

    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    private void startLoading() {
        Children.Keys children = new RootChildren();
        AbstractNode nd = new AbstractNode(children);
        manager.setRootContext(nd);
    }

    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return ContinuumTopComponent.getDefault();
        }
    }
    
    private static class RootChildren extends Children.Keys {

        private PropertyChangeListener listener;
        
        RootChildren() {
            listener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (ContinuumSettings.PROP_SERVERS.equals(evt.getPropertyName())) {
                        reloadKeys();
                    }
                }
            };
        }
        
        protected Node[] createNodes(Object object) {
            return new Node[] { new ServerNode((String)object) };
        }

        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }

        protected void addNotify() {
            super.addNotify();
            reloadKeys();
            ContinuumSettings.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(listener, ContinuumSettings.getDefault()));
        }
        
        void reloadKeys() {
            setKeys(ContinuumSettings.getDefault().getServers());
        }
        
    }

}
