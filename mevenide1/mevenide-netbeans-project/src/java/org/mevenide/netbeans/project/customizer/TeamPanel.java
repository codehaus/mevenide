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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.maven.project.Contributor;
import org.apache.maven.project.Developer;
import org.mevenide.netbeans.api.customizer.ProjectPanel;
import org.mevenide.netbeans.api.customizer.ProjectValidateObserver;
import org.mevenide.netbeans.api.customizer.changes.ListModelPOMChange;
import org.mevenide.netbeans.api.customizer.changes.MultiTextComponentPOMChange;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.api.customizer.LocationComboFactory;
import org.mevenide.netbeans.api.customizer.OriginChange;
import org.mevenide.project.io.IContentProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 * panel to display and edit the contributor and developer pom elements.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class TeamPanel extends JPanel implements ProjectPanel {
    private final static Logger LOGGER = Logger.getLogger(TeamPanel.class.getName());
    
    private ProjectValidateObserver valObserver;
    private MultiTextComponentPOMChange currentContDev;
    private Listener listener;
    private MavenProject project;
    private OriginChange ocContDevel;
    private OriginChange ocDummyOC;
    private DefaultListModel developerModel;
    private DefaultListModel contributorModel;
    private ListModelPOMChange changeDevel;
    private ListModelPOMChange changeContrib;
    private ListModelPOMChange currentChange;
    private boolean isResolvingValues = false;
    private boolean initialized;
    
    /** Creates new form BasicsPanel */
    public TeamPanel(MavenProject proj) {
        project = proj;
        initComponents();
        valObserver = null;
        //TODO add listeners for immediatePropagation stuff.
        setName(NbBundle.getMessage(TeamPanel.class, "TeamPanel.name"));
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
        lstTeam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initialized = false;      
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblTeam = new javax.swing.JLabel();
        comTeam = new javax.swing.JComboBox();
        ocContDevel = LocationComboFactory.createPOMChange(project, true);
        btnLocation = (JButton)ocContDevel.getComponent();
        spTeam = new javax.swing.JScrollPane();
        lstTeam = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblID = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblOrganization = new javax.swing.JLabel();
        txtOrganization = new javax.swing.JTextField();
        lblURL = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        lblTimezone = new javax.swing.JLabel();
        txtTimezone = new javax.swing.JTextField();
        btnView = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblTeam.setLabelFor(lblName);
        lblTeam.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "TeamPanel.lblTeam.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(lblTeam, gridBagConstraints);

        comTeam.setActionCommand("comTeam");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        add(comTeam, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(btnLocation, gridBagConstraints);

        spTeam.setPreferredSize(new java.awt.Dimension(300, 131));
        spTeam.setViewportView(lstTeam);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(spTeam, gridBagConstraints);

        btnAdd.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.btnAdd.text"));
        btnAdd.setActionCommand("btnAdd");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnAdd, gridBagConstraints);

        btnRemove.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.btnRemove.text"));
        btnRemove.setActionCommand("btnRemove");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnRemove, gridBagConstraints);

        lblName.setLabelFor(txtName);
        lblName.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.lblName.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtName, gridBagConstraints);

        lblID.setLabelFor(txtID);
        lblID.setText("ID :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(lblID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtID, gridBagConstraints);

        lblEmail.setLabelFor(txtEmail);
        lblEmail.setText("Email :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblEmail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtEmail, gridBagConstraints);

        lblOrganization.setLabelFor(txtOrganization);
        lblOrganization.setText("Organization :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblOrganization, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtOrganization, gridBagConstraints);

        lblURL.setLabelFor(txtURL);
        lblURL.setText("URL :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtURL, gridBagConstraints);

        lblTimezone.setLabelFor(txtTimezone);
        lblTimezone.setText("TZ :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(lblTimezone, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtTimezone, gridBagConstraints);

        btnView.setText("View...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 0);
        add(btnView, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    public void addNotify() {
        super.addNotify();
        if (!initialized) {
            initialized = true;
            populateChangeInstances();
        }
        listener = new Listener();
        btnAdd.addActionListener(listener);
        btnRemove.addActionListener(listener);
        lstTeam.addListSelectionListener(listener);
        comTeam.addActionListener(listener);
        comTeam.setSelectedIndex(0);
        txtName.addFocusListener(listener);
    }
    
    public void removeNotify() {
        super.removeNotify();
        btnAdd.removeActionListener(listener);
        btnRemove.removeActionListener(listener);
        lstTeam.removeListSelectionListener(listener);
        comTeam.removeActionListener(listener);
        txtName.removeFocusListener(listener);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox comTeam;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOrganization;
    private javax.swing.JLabel lblTeam;
    private javax.swing.JLabel lblTimezone;
    private javax.swing.JLabel lblURL;
    private javax.swing.JList lstTeam;
    private javax.swing.JScrollPane spTeam;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOrganization;
    private javax.swing.JTextField txtTimezone;
    private javax.swing.JTextField txtURL;
    // End of variables declaration//GEN-END:variables

    private void populateChangeInstances() {    
        String key = "pom.developers"; //NOI18N
        int location = project.getProjectWalker().getLocation(key);
        ocDummyOC = LocationComboFactory.createPOMChange(project, true);
        List oldValues = new ArrayList();
        List orig = project.getOriginalMavenProject().getDevelopers();
        if (orig != null) {
            Iterator it = orig.iterator();
            while (it.hasNext()) {
                Developer mlist  = (Developer)it.next();
                HashMap vals = new HashMap();
                vals.put("id", mlist.getId());
                vals.put("name", mlist.getName());
                vals.put("email", mlist.getEmail());
                vals.put("organization", mlist.getOrganization());
                vals.put("timezone", mlist.getTimezone());
                vals.put("url", mlist.getUrl());
                //TODO remove roles hack
                List roles = new ArrayList();
                if (mlist.getRoles() != null) {
                    roles.addAll(mlist.getRoles());
                }
                vals.put("roles", roles);
                MultiTextComponentPOMChange change = new MultiTextComponentPOMChange(
                                                           "pom.developers.developer", 
                                                           vals, location, createFieldMap(true), 
                                                           ocDummyOC, false);
                
                oldValues.add(change);
            }
        }
        developerModel = new DefaultListModel();
        changeDevel = new ListModelPOMChange(key, oldValues, location, developerModel, ocContDevel, true);
        
        key = "pom.contributors"; //NOI18N
        location = project.getProjectWalker().getLocation(key);
        oldValues = new ArrayList();
        orig = project.getOriginalMavenProject().getContributors();
        if (orig != null) {
            Iterator it = orig.iterator();
            while (it.hasNext()) {
                Contributor mlist  = (Contributor)it.next();
                HashMap vals = new HashMap();
                vals.put("name", mlist.getName());
                vals.put("email", mlist.getEmail());
                vals.put("organization", mlist.getOrganization());
                vals.put("timezone", mlist.getTimezone());
                vals.put("url", mlist.getUrl());
                //TODO remove roles hack
                List roles = new ArrayList();
                if (mlist.getRoles() != null) {
                    roles.addAll(mlist.getRoles());
                }
                vals.put("roles", roles);
                MultiTextComponentPOMChange change = new MultiTextComponentPOMChange(
                                                           "pom.contributors.contributor", 
                                                           vals, location, createFieldMap(false), 
                                                           ocDummyOC, false);
                
                oldValues.add(change);
            }
        }
        contributorModel = new DefaultListModel();
//        lstLists.setModel(model);
        changeContrib = new ListModelPOMChange(key, oldValues, location, contributorModel, ocContDevel, true);
        
        lstTeam.setCellRenderer(new ListRenderer());
        changeContrib.startIgnoringChanges();
        changeDevel.startIgnoringChanges();
        DefaultComboBoxModel comModel = new DefaultComboBoxModel();
        comModel.addElement(new ComboWrapper("Developers", developerModel));
        comModel.addElement(new ComboWrapper("Contributors", contributorModel));
        comTeam.setModel(comModel);
    }   
    
   private HashMap createFieldMap(boolean isDeveloper) {
        HashMap fields = new HashMap();
        if (isDeveloper) {
            fields.put("id", txtID); //NOI18N
        }
        fields.put("name", txtName); //NOI18N
        fields.put("email", txtEmail); //NOI18N
        fields.put("organization", txtOrganization); //NOI18N
        fields.put("timezone", txtTimezone); //NOI18N
        fields.put("url", txtURL); //NOI18N
        return fields;
    }    
    
     public void setResolveValues(boolean resolve) {
        isResolvingValues = resolve;
        resolveOneCont(resolve, currentContDev);        
        // nothing selected -> disable
        btnRemove.setEnabled(false);
    }
    
 

    public List getChanges() {
        List toReturn = new ArrayList();
        // developers first..
        if (initialized) {
            boolean hasChanged = changeDevel.hasChanged();
            if (!hasChanged) {
                for (int i = 0; i < developerModel.size(); i++) {
                    MultiTextComponentPOMChange chng = (MultiTextComponentPOMChange)developerModel.get(i);
                    hasChanged = chng.hasChanged();
                    if (hasChanged) {
                        break;
                    }
                }
            }
            if (hasChanged) {
                toReturn.add(changeDevel);
            }
            //now check contributors
            hasChanged = changeContrib.hasChanged();
            if (!hasChanged) {
                for (int i = 0; i < contributorModel.size(); i++) {
                    MultiTextComponentPOMChange chng = (MultiTextComponentPOMChange)contributorModel.get(i);
                    hasChanged = chng.hasChanged();
                    if (hasChanged) {
                        break;
                    }
                }
            }
            if (hasChanged) {
                toReturn.add(changeContrib);
            }
        }
        return toReturn;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
    }
    
    /**
     * returns 0 for ok, otherwise a integer code.
     */
    private int doValidateCheck() {
        if (txtName.getText().trim().length() == 0) {
            return 1;
        }
        return  0;
    }
    
    public boolean isInValidState() {
        // is always valid, since we can continue, error messages only happen when the
        // attemp to add to list is done.. if it fails, it's not commited, thus the state is always valid.
        return true;
    }
    
    public String getValidityMessage() {
        String message = "";
        return message;
    }
 
    /**
     * action listener for buttons and list selection..
     */
    private class Listener implements ActionListener, ListSelectionListener, FocusListener {
        
        public void actionPerformed(ActionEvent e) {
            if ("btnRemove".equals(e.getActionCommand())) { //NOI18N
                int index = lstTeam.getSelectedIndex();
                DefaultListModel model = (DefaultListModel)lstTeam.getModel();
                model.removeElementAt(index);
                while (index >= model.size()) {
                    index = index - 1;
                }
                if (index > -1) {
                    lstTeam.setSelectedIndex(index);
                }
            }
            if ("btnAdd".equals(e.getActionCommand())) { //NOI18N
                MultiTextComponentPOMChange newOne = new MultiTextComponentPOMChange(
                                                           (isDeveloper() ? "pom.developers.developer" : "pom.contributors.contributor"), 
                                                           new HashMap(), currentChange.getOldLocation(), createFieldMap(isDeveloper()), 
                                                           ocDummyOC, false);
                if (currentContDev != null) {
                    currentContDev.detachListeners();
                }
                DefaultListModel model = (DefaultListModel)lstTeam.getModel();
                model.addElement(newOne);
                lstTeam.setSelectedValue(newOne, true);
                txtName.requestFocusInWindow();
            }
            if ("comTeam".equals(e.getActionCommand())) { //NOI18N 
                if (currentChange != null) {
                    currentChange.startIgnoringChanges();
                }
                DefaultListModel model = ((ComboWrapper)comTeam.getSelectedItem()).getModel();
                if (model == developerModel) {
                    currentChange = changeDevel;
                    lstTeam.setModel(model);
                    changeDevel.stopIgnoringChanges();
                } else if (model == contributorModel) {
                    currentChange = changeContrib;
                    lstTeam.setModel(model);
                    changeContrib.stopIgnoringChanges();
                } 
                if (model.size() > 0) {
                    lstTeam.setSelectedIndex(0);
                }
            }
        }
        
        public void valueChanged(ListSelectionEvent e) {
            if (currentContDev != null) {
                currentContDev.detachListeners();
                txtName.setText("");
                txtURL.setText("");
                txtEmail.setText("");
                txtID.setText("");
                txtOrganization.setText("");
                txtTimezone.setText("");
                
            }
            // repaint to show new values in list?
            lstTeam.repaint();
            if (lstTeam.getSelectedIndex() == -1) {
                currentContDev = null;
                btnRemove.setEnabled(false);
            } else {
                currentContDev = (MultiTextComponentPOMChange)lstTeam.getSelectedValue();
                if (!isDeveloper()) {
                    txtID.setText("");
                    txtID.setEditable(false);
                }
                resolveOneCont(isResolvingValues, currentContDev);
                currentContDev.attachListeners();
                btnRemove.setEnabled(true);
            }
        }

        public void focusGained(FocusEvent focusEvent) {
            // ignore
        }

        public void focusLost(FocusEvent focusEvent) {
            // when focus is lost on txtName, refresh the list..
            lstTeam.repaint();
        }
    }
    
    private boolean isDeveloper() {
        return ((ComboWrapper)comTeam.getSelectedItem()).getModel() == developerModel;
    }
    
     private void resolveOneCont(boolean resolve, MultiTextComponentPOMChange  chng) {
         if (chng != null) {
             if (resolve) {
                 IContentProvider prov = currentContDev.getChangedContent();
                 HashMap resolved = new HashMap();
                 String value = prov.getValue("name"); //NOI18N
                 if (value != null) {
                     resolved.put("name", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("id"); //NOI18N
                 if (value != null) {
                    resolved.put("id", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("timezone"); //NOI18N
                 if (value != null) {
                    resolved.put("timezone", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("url"); //NOI18N
                 if (value != null) {
                    resolved.put("url", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("email"); //NOI18N
                 if (value != null) {
                    resolved.put("email", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 value = prov.getValue("organization"); //NOI18N
                 if (value != null) {
                     resolved.put("organization", project.getPropertyResolver().resolveString(value)); //NOI18N
                 }
                 chng.setResolvedValues(resolved);
             } else {
                 chng.resetToNonResolvedValue();
             }
         }
     }    
    
    private class ComboWrapper {
        private String title;
        private DefaultListModel model;
        public ComboWrapper(String tit, DefaultListModel mod) {
            title = tit;
            model = mod;
        }
        
        public DefaultListModel getModel() {
            return model;
        }
        
        public String toString() {
            return title;
        }
    }
   
    /**
     * rendered which displays the name of the mailing list in the list..
     */
    private class ListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) 
        {
            MultiTextComponentPOMChange change = (MultiTextComponentPOMChange)value;
            String name = change.getValueFor("name"); //NOI18N
            if (name == null || name.trim().length() == 0) {
                name = isDeveloper() ? "<Developer with no name>" : "<Contributor with no name>";
            } else {
                name = project.getPropertyResolver().resolveString(name);
            }
            return  super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
        }
    }    
}
