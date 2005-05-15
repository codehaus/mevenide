/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.netbeans.project.dependencies.DependencyEditor;
import org.mevenide.netbeans.project.dependencies.DependencyNode;
import org.mevenide.netbeans.project.dependencies.RepositoryUtilities;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependenciesPanel extends JPanel implements ExplorerManager.Provider, ProjectPanel {
    
    private MavenProject project;
    private ExplorerManager manager;
    private BeanTreeView btv;
    private boolean initialized = false;
    private boolean isResolvingValues = false;
    private OriginChange ocDummyDependency;
    private OriginChange ocDummyOverride;
    private List values = new ArrayList();
    private DependencyPOMChange currentDep;
    private DependencyPOMChange emptyDep;
    private List overrideValues = new ArrayList();
    
    /** Creates new form CustomGoalsPanel */
    public DependenciesPanel(MavenProject proj, boolean editable) {
        project = proj;
        ocDummyDependency = LocationComboFactory.createPOMChange(project, false);
        ocDummyOverride = LocationComboFactory.createPropertiesChange(project);
        initComponents();
        //TODO - just temporary, in future have some override support
        tbDep.setVisible(false);
        
        ButtonGroup grp = new ButtonGroup();
        grp.add(rbVersion);
        grp.add(rbPath);
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
        setFieldsEditable(editable);
        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String url = txtURL.getText().trim();
                url = project.getPropertyResolver().resolveString(url);
                if (url.startsWith("http://")) {
                    try {
                        URL link = new URL(url);
                        HtmlBrowser.URLDisplayer.getDefault().showURL(link);
                    } catch (MalformedURLException exc) {
                        NotifyDescriptor error = new NotifyDescriptor.Message("Is not a valid URL.", NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(error);
                    }
                    
                }
            }
        });
        emptyDep = new DependencyPOMChange("", new HashMap(), 
                IPropertyLocator.LOCATION_NOT_DEFINED, createFieldMap(), 
                ocDummyOverride, new HashMap(), false);
        
    }
    
    public void setFieldsEditable(boolean editable) {
        txtArtifactID.setEditable(editable);
        txtGroupId.setEditable(editable);
        txtVersion.setEditable(editable);
        txtType.setEditable(editable);
        txtURL.setEditable(editable);
        txtJar.setEditable(editable);
        lstProperties.setEnabled(editable);
    }  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlDeps = new javax.swing.JPanel();
        tbDep = new javax.swing.JTabbedPane();
        pnlOverrides = new javax.swing.JPanel();
        rbVersion = new javax.swing.JRadioButton();
        rbPath = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        btnOverride = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        pnlSingleDep = new javax.swing.JPanel();
        ocDummyDependency = LocationComboFactory.createPOMChange(project, true);
        btnLoc = (JButton)ocDummyDependency.getComponent();
        lblArtifactId = new javax.swing.JLabel();
        txtArtifactID = new javax.swing.JTextField();
        lblGroupId = new javax.swing.JLabel();
        txtGroupId = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblType = new javax.swing.JLabel();
        txtType = new javax.swing.JTextField();
        lblJar = new javax.swing.JLabel();
        txtJar = new javax.swing.JTextField();
        lblURL = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        btnView = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstProperties = new javax.swing.JList();
        lblJavadocSrc = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        pnlDeps.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(pnlDeps, gridBagConstraints);

        tbDep.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tbDep.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        pnlOverrides.setLayout(new java.awt.GridBagLayout());

        rbVersion.setText("Version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlOverrides.add(rbVersion, gridBagConstraints);

        rbPath.setText("Artifact Path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlOverrides.add(rbPath, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlOverrides.add(jTextField1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlOverrides.add(jTextField2, gridBagConstraints);

        jButton1.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
        pnlOverrides.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlOverrides.add(btnOverride, gridBagConstraints);

        tbDep.addTab("Overrides", pnlOverrides);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(tbDep, gridBagConstraints);

        btnAdd.setText("Add...");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnAdd, gridBagConstraints);

        btnEdit.setText("Edit...");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnEdit, gridBagConstraints);

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnRemove, gridBagConstraints);

        btnCheck.setText("Doc&Src check");
        btnCheck.setToolTipText("Contacts remote repositories requesting download of javadoc and source jars for the dependency.");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnCheck, gridBagConstraints);

        pnlSingleDep.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(btnLoc, gridBagConstraints);

        lblArtifactId.setText("Artifact");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(lblArtifactId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(txtArtifactID, gridBagConstraints);

        lblGroupId.setText("Group");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(lblGroupId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
        pnlSingleDep.add(txtGroupId, gridBagConstraints);

        lblVersion.setText("Version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(lblVersion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(txtVersion, gridBagConstraints);

        lblType.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(lblType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
        pnlSingleDep.add(txtType, gridBagConstraints);

        lblJar.setLabelFor(txtJar);
        lblJar.setText("Jar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(lblJar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(txtJar, gridBagConstraints);

        lblURL.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(lblURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlSingleDep.add(txtURL, gridBagConstraints);

        btnView.setText("View...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 3, 0, 3);
        pnlSingleDep.add(btnView, gridBagConstraints);

        lstProperties.setMinimumSize(new java.awt.Dimension(10, 50));
        jScrollPane1.setViewportView(lstProperties);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 6, 3);
        pnlSingleDep.add(jScrollPane1, gridBagConstraints);

        lblJavadocSrc.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 6);
        pnlSingleDep.add(lblJavadocSrc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(pnlSingleDep, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        if (currentDep != null) {
            final IContentProvider prov = currentDep.getChangedContent();
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    IRepositoryReader[] readers = RepositoryUtilities.createRemoteReaders(project.getPropertyResolver());
                    for (int i = 0; i < readers.length; i++) {
                        final RepoPathElement docEl = new RepoPathElement(readers[i], null,
                                prov.getValue("groupId"),
                                "javadoc.jar",
                                prov.getValue("version"),
                                prov.getValue("artifactId"),
                                "javadoc.jar");
                        final RepoPathElement srcEl = new RepoPathElement(readers[i], null, 
                                prov.getValue("groupId"),
                                "src.jar",
                                prov.getValue("version"),
                                prov.getValue("artifactId"),
                                "src.jar");
                        File localRepo = new File(project.getLocFinder().getMavenLocalRepository());
                        File destinationFile = new File(URI.create(localRepo.toURI().toString() + srcEl.getRelativeURIPath()));
                        if (!destinationFile.exists() || destinationFile.getName().indexOf("SNAPSHOT") >= 0) {
                            try {
                                RepositoryUtilities.downloadArtifact(project.getLocFinder(),
                                        project.getPropertyResolver(),
                                        srcEl);
                            } catch (FileNotFoundException exc) {
                                // well can happen, definitely if having multiple repositories
                            } catch (Exception exc) {
                                StatusDisplayer.getDefault().setStatusText("Error downloading " + destinationFile.getName() + " : " + exc.getLocalizedMessage());
                            }
                        }
                        localRepo = new File(project.getLocFinder().getMavenLocalRepository());
                        destinationFile = new File(URI.create(localRepo.toURI().toString() + docEl.getRelativeURIPath()));
                        if (!destinationFile.exists() || destinationFile.getName().indexOf("SNAPSHOT") >= 0) {
                            try {
                                RepositoryUtilities.downloadArtifact(project.getLocFinder(),
                                        project.getPropertyResolver(),
                                        docEl);
                            } catch (FileNotFoundException exc) {
                                // well can happen, definitely if having multiple repositories
                            } catch (Exception exc) {
                                StatusDisplayer.getDefault().setStatusText("Error downloading " + destinationFile.getName() + " : " + exc.getLocalizedMessage());
                            }
                        }
                        ((DepRootChildren)manager.getRootContext().getChildren()).doRefresh();
                    }
                    
                }
            });
        }
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        if (currentDep != null) {
            values.remove(currentDep);
            if (values.size() == 0) {
                btnLoc.setVisible(false);
            }
            ((DepRootChildren)manager.getRootContext().getChildren()).doRefresh();
            setDependency(manager.getSelectedNodes());
        }
        
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        DependencyEditor ed = new DependencyEditor(project, currentDep);
        DialogDescriptor dd = new DialogDescriptor(ed, "title");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            HashMap props = ed.getProperties();
            currentDep.setNewValues(ed.getValues(), props);
            ((DepRootChildren)manager.getRootContext().getChildren()).doRefresh();
            setDependency(manager.getSelectedNodes());
            MavenSettings.getDefault().checkDependencyProperties(props.keySet());
        }

    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        DependencyPOMChange change = new DependencyPOMChange(
                      "pom.dependencies.dependency", 
                       new HashMap(), 0, createFieldMap(), 
                       ocDummyDependency, 
                       new HashMap(), false);
        
        DependencyEditor ed = new DependencyEditor(project, change);
        DialogDescriptor dd = new DialogDescriptor(ed, "title");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            HashMap props = ed.getProperties();
            change.setNewValues(ed.getValues(), props);
            values.add(change);
            if (values.size() == 1) {
                btnLoc.setVisible(true);
            }
            ((DepRootChildren)manager.getRootContext().getChildren()).doRefresh();
            MavenSettings.getDefault().checkDependencyProperties(props.keySet());
//            setDependency(manager.getSelectedNodes());
        }
        
    }//GEN-LAST:event_btnAddActionPerformed
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public void addNotify() {
        super.addNotify();
        if (!initialized) {
            initialized = true;
            populateChangeInstances();
        }
    }
    
    
    private void populateChangeInstances() {    
        Project[] projs =  project.getContext().getPOMContext().getProjectLayers();
        for (int i = 0; i < projs.length; i++) {
            int location = i;
            List deps = projs[i].getDependencies();
            
            if (deps != null && deps.size() > 0) {
                Iterator it = deps.iterator();
                while (it.hasNext()) {
                    Dependency dep = (Dependency)it.next();
                    HashMap vals = new HashMap();
                    vals.put("artifactId", dep.getArtifactId());
                    vals.put("groupId", dep.getGroupId());
                    vals.put("version", dep.getVersion());
                    vals.put("type", dep.getType());
                    vals.put("jar", dep.getJar());
                    vals.put("url", dep.getUrl());
                    HashMap props = new HashMap();
                    Map map = dep.resolvedProperties();
                    if (map != null) {
                        Iterator it2 = map.entrySet().iterator();
                        while (it2.hasNext()) {
                            Map.Entry ent = (Map.Entry)it2.next();
                            if (ent.getValue() != null && ent.getValue().toString().trim().length() > 0) {
                                props.put(ent.getKey(), ent.getValue());
                            }   
                        }
                    }                    
                    DependencyPOMChange change = new DependencyPOMChange(
                                        "pom.dependencies.dependency", 
                                        vals, location, createFieldMap(), 
                                        ocDummyDependency, 
                                        props, false);
                    values.add(change);
                    String overrideProp = "maven.jar." + dep.getArtifactId();
                }
            }
        }
        if (values.size() == 0) {
            btnLoc.setVisible(false);
        }
        manager.setRootContext(createRootNode());
        selectFirstNode();
    } 
    
    private HashMap createFieldMap() {
        HashMap fields = new HashMap();
        fields.put("artifactId", txtArtifactID); //NOI18N
        fields.put("groupId", txtGroupId); //NOI18N
        fields.put("version", txtVersion); //NOI18N
        fields.put("type", txtType); //NOI18N
        fields.put("jar", txtJar); //NOI18N
        fields.put("url", txtURL); //NOI18N
        return fields;
    }    
    
     public void setResolveValues(boolean resolve) {
        isResolvingValues = resolve;
        resolveDependency(resolve, currentDep);
//        // nothing selected -> disable
        btnRemove.setEnabled(!resolve);
        btnAdd.setEnabled(!resolve);
        btnEdit.setEnabled(!resolve);
        
    }
   
     private void resolveDependency(boolean resolve, DependencyPOMChange chng) {
         if (chng != null) {
             if (resolve) {
                 IContentProvider prov = chng.getChangedContent();
                 HashMap resolved = new HashMap();
                 String value = prov.getValue("artifactId"); //NOI18N
                 if (value != null) {
                     resolved.put("artifactId", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("groupId"); //NOI18N
                 if (value != null) {
                    resolved.put("groupId", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("version"); //NOI18N
                 if (value != null) {
                    resolved.put("version", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("type"); //NOI18N
                 if (value != null) {
                     resolved.put("type", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("jar"); //NOI18N
                 if (value != null) {
                     resolved.put("jar", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("url"); //NOI18N
                 if (value != null) {
                     resolved.put("url", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 chng.setResolvedValues(resolved);
             } else {
                 chng.resetToNonResolvedValue();
             }
         }
     }
    
    public List getChanges() {
        // dependencies are special. as each of them can be defined in any file
        // it's easier to put all inside as changed and let the writer resolve the actual changes.
        if (!initialized) {
            initialized = true;
            populateChangeInstances();
        }
        return values;
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
                setDependency(manager.getSelectedNodes());
            }
        }
    }
    
    private Node createRootNode() {
        Node root = new AbstractNode(new DepRootChildren());
        root.setName("root invisible");
        return root;
    }
    private String getValue(String value, boolean resolve) {
        if (resolve) {
            return project.getPropertyResolver().resolveString(value);
        }
        return value;
    }        
    
    public void setDependency(Node[] nodes) {
        boolean javadoc = false;
        boolean source = false;
        lblJavadocSrc.setText("");
        if (currentDep != null) {
            currentDep.detachListeners();
        }
        if ( nodes == null || nodes.length <= 0 ) {
            currentDep = null;
            DefaultListModel model = new DefaultListModel();
            lstProperties.setModel(model);
            emptyDep.resetToNonResolvedValue();
            
            btnRemove.setEnabled(false);
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnCheck.setEnabled(false);
            return;
        }
        Node node = nodes[0];
        DependencyPOMChange chan = (DependencyPOMChange)node.getLookup().lookup(DependencyPOMChange.class);
        if (chan != null) {
            chan.resetToNonResolvedValue();
            chan.attachListeners();
            DefaultListModel model = new DefaultListModel();
            List props = chan.getChangedContent().getProperties();
            if (props != null) {
                Iterator it = props.iterator();
                while (it.hasNext()) {
                    String ent = (String)it.next();
                    int index  = ent.indexOf(':');
                    if (index > 0) {
                        model.addElement(ent.substring(0, index) + "=" + ent.substring(index + 1));
                    }
                }
            }
            lstProperties.setModel(model);
            if (node instanceof DependencyNode) {
                DependencyNode depNode = (DependencyNode)node;
                javadoc = depNode.hasJavadocInRepository();
                source = depNode.hasSourceInRepository();
            }
            currentDep = chan;
            btnRemove.setEnabled(!isResolvingValues);
            btnAdd.setEnabled(!isResolvingValues);
            btnEdit.setEnabled(!isResolvingValues);
            btnCheck.setEnabled(javadoc == false || source == false);
        } else {
            currentDep = null;
        }
        if (javadoc && source) {
            lblJavadocSrc.setText("Javadoc and Sources are available.");
        } else if (javadoc) {
            lblJavadocSrc.setText("Javadoc for dependency available.");
        } else if (source) {
            lblJavadocSrc.setText("Sources for dependency available.");
        } 
    }
    
    
    
    public String getValidityMessage() {
        return "";
    }
    
    public boolean isInValidState() {
        return true;
    }
    
    
    public void setValidateObserver(ProjectValidateObserver observer) {
    }
    
    private class DepRootChildren extends Children.Keys {
        
        public void addNotify() {
            List depend = DependenciesPanel.this.values;
            if (depend != null) {
                setKeys(new ArrayList(depend));
            } else {
                setKeys(Collections.EMPTY_LIST);
            }
            
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        public void doRefresh() {
            setKeys(new ArrayList(DependenciesPanel.this.values));
            Node[] nds = getNodes();
            for (int i = 0; i < nds.length; i++) {
                if (nds[i] instanceof DependencyNode) {
                    ((DependencyNode)nds[i]).refreshNode();
                }
            }
        }
        
        protected Node[] createNodes(Object obj) {
            DependencyPOMChange chng = (DependencyPOMChange)obj;
            IContentProvider provider = chng.getChangedContent();
            //TODO - pass correct context into node
            Lookup look = Lookups.fixed(new Object[] {
                        chng,
                        chng.getChangedContent()
            });
            return new Node[] { new DependencyNode(provider, DependenciesPanel.this.project, look)};
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnOverride;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnView;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lblArtifactId;
    private javax.swing.JLabel lblGroupId;
    private javax.swing.JLabel lblJar;
    private javax.swing.JLabel lblJavadocSrc;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblURL;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JList lstProperties;
    private javax.swing.JPanel pnlDeps;
    private javax.swing.JPanel pnlOverrides;
    private javax.swing.JPanel pnlSingleDep;
    private javax.swing.JRadioButton rbPath;
    private javax.swing.JRadioButton rbVersion;
    private javax.swing.JTabbedPane tbDep;
    private javax.swing.JTextField txtArtifactID;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtJar;
    private javax.swing.JTextField txtType;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables
    
}
