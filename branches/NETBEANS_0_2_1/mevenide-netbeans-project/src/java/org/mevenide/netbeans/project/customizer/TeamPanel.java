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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Developer;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class TeamPanel extends JPanel implements ProjectPanel {
    private static Log logger = LogFactory.getLog(TeamPanel.class);
    
    private boolean propagate;
    private ProjectValidateObserver valObserver;
    private Contributor current;
    private Listener listener;
    private MavenProject project;
    private DefaultListModel contribModel;
    private DefaultListModel develModel;
    private boolean doResolve = false;
    
    /** Creates new form BasicsPanel */
    public TeamPanel(boolean propagateImmediately, boolean enable, MavenProject proj) {
        initComponents();
        propagate = propagateImmediately;
        project = proj;
        valObserver = null;
        contribModel = new DefaultListModel();
        develModel = new DefaultListModel();
        //TODO add listeners for immediatePropagation stuff.
        setName(NbBundle.getMessage(TeamPanel.class, "TeamPanel.name"));
        setEnableFields(enable);
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
    }
    
    public void setEnableFields(boolean enable) {
        txtEmail.setEditable(enable);
        txtName.setEditable(enable);
        txtID.setEditable(enable);
        txtOrganization.setEditable(enable);
        txtTimezone.setEditable(enable);
        txtURL.setEditable(enable);
        btnAdd.setEnabled(enable);
        btnEdit.setEnabled(enable);
        btnRemove.setEnabled(enable);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblTeam = new javax.swing.JLabel();
        comTeam = new javax.swing.JComboBox();
        spTeam = new javax.swing.JScrollPane();
        lstTeam = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblTeam, gridBagConstraints);

        comTeam.setActionCommand("comTeam");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(comTeam, gridBagConstraints);

        spTeam.setPreferredSize(new java.awt.Dimension(300, 131));
        spTeam.setViewportView(lstTeam);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.2;
        add(spTeam, gridBagConstraints);

        btnAdd.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.btnAdd.text"));
        btnAdd.setActionCommand("btnAdd");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnAdd, gridBagConstraints);

        btnEdit.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.btnEdit.text"));
        btnEdit.setActionCommand("btnEdit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnEdit, gridBagConstraints);

        btnRemove.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.btnRemove.text"));
        btnRemove.setActionCommand("btnRemove");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        add(btnRemove, gridBagConstraints);

        lblName.setLabelFor(txtName);
        lblName.setText(org.openide.util.NbBundle.getMessage(TeamPanel.class, "ListsPanel.lblName.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        add(txtName, gridBagConstraints);

        lblID.setLabelFor(txtID);
        lblID.setText("ID :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(txtID, gridBagConstraints);

        lblEmail.setLabelFor(txtEmail);
        lblEmail.setText("Email :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblEmail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
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
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(txtOrganization, gridBagConstraints);

        lblURL.setLabelFor(txtURL);
        lblURL.setText("URL :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(txtURL, gridBagConstraints);

        lblTimezone.setLabelFor(txtTimezone);
        lblTimezone.setText("TZ :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblTimezone, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(txtTimezone, gridBagConstraints);

        btnView.setText("View...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnView, gridBagConstraints);

    }//GEN-END:initComponents
    
    public void addNotify() {
        super.addNotify();
        listener = new Listener();
        btnAdd.addActionListener(listener);
        btnEdit.addActionListener(listener);
        btnRemove.addActionListener(listener);
        lstTeam.addListSelectionListener(listener);
        comTeam.addActionListener(listener);
    }
    
    public void removeNotify() {
        super.removeNotify();
        btnAdd.removeActionListener(listener);
        btnEdit.removeActionListener(listener);
        btnRemove.removeActionListener(listener);
        lstTeam.removeListSelectionListener(listener);
        comTeam.removeActionListener(listener);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
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
    
     public void setResolveValues(boolean resolve) {
//TODO        setEnableFields(!resolve);                
        doResolve = resolve;
        Project proj = project.getOriginalMavenProject();
        List contrib = proj.getContributors();
        List devel = proj.getDevelopers();
        comTeam.removeAllItems();
        develModel.removeAllElements();
        if (devel != null) {
            Iterator it = devel.iterator();
            while (it.hasNext()) {
                Developer dev = (Developer)it.next();
                // need to copy and use fresh instances, for easier rollback
                Developer modelDevel = new Developer();
                modelDevel.setName(dev.getName());
                modelDevel.setId(dev.getId());
                modelDevel.setEmail(dev.getEmail());
                modelDevel.setOrganization(dev.getOrganization());
                modelDevel.setTimezone(dev.getTimezone());
                modelDevel.setUrl(dev.getUrl());
                develModel.addElement(modelDevel);
            }
        }
        comTeam.addItem(new ComboWrapper("Developers", develModel));
        contribModel.removeAllElements();
        if (contrib != null) {
            Iterator it = contrib.iterator();
            while (it.hasNext()) {
                Contributor dev = (Contributor)it.next();
                // need to copy and use fresh instances, for easier rollback
                Contributor modelDevel = new Developer();
                modelDevel.setName(dev.getName());
                modelDevel.setId(dev.getId());
                modelDevel.setEmail(dev.getEmail());
                modelDevel.setOrganization(dev.getOrganization());
                modelDevel.setTimezone(dev.getTimezone());
                modelDevel.setUrl(dev.getUrl());
                contribModel.addElement(modelDevel);
            }
        }
        comTeam.addItem(new ComboWrapper("Contributors", contribModel));
//        comTeam.setSelectedIndex(0);
        lstTeam.setModel(develModel);
        fillValues(null);
        // nothing selected -> disable
        btnRemove.setEnabled(false);
        btnEdit.setEnabled(false);
    }
    
    private String getValue(String value, boolean resolve) {
        if (resolve) {
            return project.getPropertyResolver().resolveString(value);
        }
        return value;
    }    
    
    private void fillValues(Contributor contrib) {
        if (contrib == null) {
            txtName.setText("");
            txtID.setText("");
            txtEmail.setText("");
            txtOrganization.setText("");
            txtTimezone.setText("");
            txtURL.setText("");
        } else {
            txtName.setText(contrib.getName() == null ? "" : getValue(contrib.getName(), doResolve));
            txtID.setText(contrib.getId() == null ? "" : getValue(contrib.getId(), doResolve));
            txtEmail.setText(contrib.getEmail() == null ? "" : getValue(contrib.getEmail(), doResolve));
            txtOrganization.setText(contrib.getOrganization() == null ? "" : getValue(contrib.getOrganization(), doResolve));
            txtTimezone.setText(contrib.getTimezone() == null ? "" : getValue(contrib.getTimezone(), doResolve));
            txtURL.setText(contrib.getUrl() == null ? "" : getValue(contrib.getUrl(), doResolve));
        }
        
    }
    
    public List getChanges() {
        return Collections.EMPTY_LIST;
        //        // when copying over, we will discard the current instances in the project with our local fresh ones.
        //        // I hope that is ok, and the mailing lists don't have custom properties.
        //        DefaultListModel model = (DefaultListModel)lstLists.getModel();
        //        ArrayList list = new ArrayList(model.size() + 5);
        //        Enumeration en = model.elements();
        //        while (en.hasMoreElements()) {
        //            Object obj = en.nextElement();
        //            list.add(obj);
        //        }
        //        project.setMailingLists(list);
//        return project;
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
        int retCode = doValidateCheck();
        String message = "";
        //        // initially and when nothing is selected don't show message.
        //        // when adding the currentList should be non-null
        //        if (retCode == 1 && current != null) {
        //            message = NbBundle.getMessage(TeamPanel.class, "ListsPanel.error1.text");
        //        }
        return message;
    }
    
    private Contributor assign(Contributor cont) {
        //        logger.debug("Listener called");
        //        ProjectValidateObserver obs = valObserver;
        //        if (obs != null) {
        //            obs.resetValidState(isInValidState(), getValidityMessage());
        //        }
        //        if (doValidateCheck() == 0) {
        //            list.setName(txtName.getText());
        //            list.setArchive(txtArchive.getText());
        //            list.setSubscribe(txtSubscribe.getText());
        //            list.setUnsubscribe(txtUnsubscribe.getText());
        //            return list;
        //        }
        return null;
    }
    
    /**
     * action listener for buttons and list selection..
     */
    private class Listener implements ActionListener, ListSelectionListener {
        
        public void actionPerformed(ActionEvent e) {
            if ("btnRemove".equals(e.getActionCommand())) //NOI18N
            {
                //                DefaultListModel model = (DefaultListModel)lstLists.getModel();
                //                int index = lstLists.getSelectedIndex();
                //                model.removeElementAt(index);
                //                while (index >= model.size()) {
                //                    index = index - 1;
                //                }
                //                if (index > -1) {
                //                    lstLists.setSelectedIndex(index);
                //                }
            }
            if ("btnEdit".equals(e.getActionCommand())) //NOI18N
            {
                //                if (currentList != null) {
                //                    assignList(currentList);
                //                } else {
                //                    logger.debug("Something wrong, no currentList selected when editing"); //NOI18N
                //                }
            }
            if ("btnAdd".equals(e.getActionCommand())) //NOI18N
            {
                //                currentList = new MailingList();
                //                currentList = assignList(currentList);
                //                if (currentList != null) {
                //                    DefaultListModel model = (DefaultListModel)lstTeam.getModel();
                //                    model.addElement(currentList);
                //                    lstLists.setSelectedValue(currentList, true);
                //                }
            }
            if ("comTeam".equals(e.getActionCommand())) //NOI18N
            {
                DefaultListModel model = ((ComboWrapper)comTeam.getSelectedItem()).getModel();
                lstTeam.setModel(model);
                if (model.size() > 0) {
                    lstTeam.setSelectedIndex(0);
                }
            }
        }
        
        public void valueChanged(ListSelectionEvent e) {
            if (lstTeam.getSelectedIndex() == -1) {
                current = null;
                btnRemove.setEnabled(false);
                btnEdit.setEnabled(false);
            } else {
                current = (Contributor)lstTeam.getSelectedValue();
                //TEMP                btnRemove.setEnabled(true);
                //TEMP                btnEdit.setEnabled(true);
            }
            fillValues(current);
        }
    }
    
    private class ComboWrapper {
        String title;
        DefaultListModel model;
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
    
}