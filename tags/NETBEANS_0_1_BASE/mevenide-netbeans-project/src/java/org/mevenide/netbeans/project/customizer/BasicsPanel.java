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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class BasicsPanel extends JPanel implements ProjectPanel {
    private static Log logger = LogFactory.getLog(BasicsPanel.class);
    
    private boolean propagate;
    private ProjectValidateObserver valObserver;
    private DocumentListener listener;
    private MavenProject project;
    
    /** Creates new form BasicsPanel */
    public BasicsPanel(boolean propagateImmediately, boolean enable, MavenProject proj) {
        initComponents();
	project = proj;
        propagate = propagateImmediately;
        valObserver = null;
        //TODO add listeners for immediatePropagation stuff.
        setName(NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.name"));
        setEnableFields(enable);
        btnUrl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String url = txtUrl.getText().trim();
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

    }
    
    public void setEnableFields(boolean enable) {
        txtArtifactID.setEditable(enable);
        txtCurrentVersion.setEditable(enable);
        txtGroupID.setEditable(enable);
        txtInceptionYear.setEditable(enable);
        txtLogo.setEditable(enable);
        txtName.setEditable(enable);
        txtPackage.setEditable(enable);
        txtShortDescription.setEditable(enable);
        txtUrl.setEditable(enable);
        taDescription.setEditable(enable);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblArtifactID = new javax.swing.JLabel();
        txtArtifactID = new javax.swing.JTextField();
        lblGroupID = new javax.swing.JLabel();
        txtGroupID = new javax.swing.JTextField();
        lblCurrentVersion = new javax.swing.JLabel();
        txtCurrentVersion = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblPackage = new javax.swing.JLabel();
        txtPackage = new javax.swing.JTextField();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblLogo = new javax.swing.JLabel();
        txtLogo = new javax.swing.JTextField();
        lblInceptionYear = new javax.swing.JLabel();
        txtInceptionYear = new javax.swing.JTextField();
        lblShortDescription = new javax.swing.JLabel();
        txtShortDescription = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        btnUrl = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblArtifactID.setLabelFor(txtArtifactID);
        lblArtifactID.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.lblArtifactID.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblArtifactID, gridBagConstraints);

        txtArtifactID.setMinimumSize(new java.awt.Dimension(30, 26));
        txtArtifactID.setPreferredSize(new java.awt.Dimension(30, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        add(txtArtifactID, gridBagConstraints);

        lblGroupID.setLabelFor(txtGroupID);
        lblGroupID.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.lblGroupID.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblGroupID, gridBagConstraints);

        txtGroupID.setMinimumSize(new java.awt.Dimension(30, 26));
        txtGroupID.setPreferredSize(new java.awt.Dimension(30, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtGroupID, gridBagConstraints);

        lblCurrentVersion.setLabelFor(txtCurrentVersion);
        lblCurrentVersion.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.lblCurrentVersion.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblCurrentVersion, gridBagConstraints);

        txtCurrentVersion.setMinimumSize(new java.awt.Dimension(30, 26));
        txtCurrentVersion.setPreferredSize(new java.awt.Dimension(30, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtCurrentVersion, gridBagConstraints);

        lblName.setLabelFor(txtName);
        lblName.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.lblName.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblName, gridBagConstraints);

        txtName.setMinimumSize(new java.awt.Dimension(30, 26));
        txtName.setPreferredSize(new java.awt.Dimension(30, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 0);
        add(txtName, gridBagConstraints);

        lblPackage.setLabelFor(txtPackage);
        lblPackage.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.lblPackage.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblPackage, gridBagConstraints);

        txtPackage.setMinimumSize(new java.awt.Dimension(50, 26));
        txtPackage.setPreferredSize(new java.awt.Dimension(50, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtPackage, gridBagConstraints);

        lblUrl.setLabelFor(txtUrl);
        lblUrl.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "DescPanel.lblUrl.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblUrl, gridBagConstraints);

        txtUrl.setMinimumSize(new java.awt.Dimension(50, 26));
        txtUrl.setPreferredSize(new java.awt.Dimension(50, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtUrl, gridBagConstraints);

        lblLogo.setLabelFor(txtLogo);
        lblLogo.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "DescPanel.lblLogo.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblLogo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtLogo, gridBagConstraints);

        lblInceptionYear.setLabelFor(txtInceptionYear);
        lblInceptionYear.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "DescPanel.lblInceptionYear.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblInceptionYear, gridBagConstraints);

        txtInceptionYear.setMinimumSize(new java.awt.Dimension(50, 28));
        txtInceptionYear.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtInceptionYear, gridBagConstraints);

        lblShortDescription.setLabelFor(txtShortDescription);
        lblShortDescription.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "DescPanel.lblShortDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblShortDescription, gridBagConstraints);

        txtShortDescription.setMinimumSize(new java.awt.Dimension(100, 26));
        txtShortDescription.setPreferredSize(new java.awt.Dimension(100, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(txtShortDescription, gridBagConstraints);

        lblDescription.setLabelFor(taDescription);
        lblDescription.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "DescPanel.lblDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblDescription, gridBagConstraints);

        taDescription.setMinimumSize(new java.awt.Dimension(200, 100));
        spDescription.setViewportView(taDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(spDescription, gridBagConstraints);

        btnUrl.setText(org.openide.util.NbBundle.getMessage(BasicsPanel.class, "BasicPanel.btnUrl.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnUrl, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUrl;
    private javax.swing.JLabel lblArtifactID;
    private javax.swing.JLabel lblCurrentVersion;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblGroupID;
    private javax.swing.JLabel lblInceptionYear;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPackage;
    private javax.swing.JLabel lblShortDescription;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField txtArtifactID;
    private javax.swing.JTextField txtCurrentVersion;
    private javax.swing.JTextField txtGroupID;
    private javax.swing.JTextField txtInceptionYear;
    private javax.swing.JTextField txtLogo;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPackage;
    private javax.swing.JTextField txtShortDescription;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
     
     public void setResolveValues(boolean resolve) {
//TODO        setEnableFields(!resolve);
        Project proj = project.getOriginalMavenProject();
        txtName.setText(proj.getName() == null ? "" : getValue(proj.getName(), resolve));
        txtCurrentVersion.setText(proj.getCurrentVersion() == null ? "" : getValue(proj.getCurrentVersion(), resolve));
        txtArtifactID.setText(proj.getArtifactId() == null ? "" : getValue(proj.getArtifactId(), resolve));
        txtGroupID.setText(proj.getGroupId() == null ? "" : getValue(proj.getGroupId(), resolve));
        txtPackage.setText(proj.getPackage() == null ? "" : getValue(proj.getPackage(), resolve));
        txtInceptionYear.setText(proj.getInceptionYear() == null ? "" : getValue(proj.getInceptionYear(), resolve));
        txtShortDescription.setText(proj.getShortDescription() == null ? "" : getValue(proj.getShortDescription(), resolve));
        txtUrl.setText(proj.getUrl() == null ? "" : getValue(proj.getUrl(), resolve));
        txtLogo.setText(proj.getLogo() == null ? "" : getValue(proj.getLogo(), resolve));
        taDescription.setText(proj.getDescription() == null ? "" : getValue(proj.getDescription(), resolve));
    }
    
    private String getValue(String value, boolean resolve) {
        if (resolve) {
            return project.getPropertyResolver().resolveString(value);
        }
        return value;
    }
    
    public List getChanges() {
        return Collections.EMPTY_LIST;
//        project.setName(txtName.getText());
//        project.setArtifactId(txtArtifactID.getText());
//        project.setGroupId(txtGroupID.getText());
//        project.setCurrentVersion(txtCurrentVersion.getText());
//        project.setPackage(txtPackage.getText());
//        project.setDescription(taDescription.getText());
//        project.setShortDescription(txtShortDescription.getText());
//        project.setInceptionYear(txtInceptionYear.getText());
//        project.setUrl(txtUrl.getText());
//        project.setLogo(txtLogo.getText());
//        return project;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
        if (listener == null) {
            listener = new ValidateListener();
            txtArtifactID.getDocument().addDocumentListener(listener);
            txtGroupID.getDocument().addDocumentListener(listener);
            txtPackage.getDocument().addDocumentListener(listener);
        }
    }
    
    private void doValidate() {
        logger.debug("Listener called");
        ProjectValidateObserver obs = valObserver;
        if (obs != null) {
            obs.resetValidState(isInValidState(), getValidityMessage());
        }
    }
    
    /**
     * returns 0 for ok, otherwise a integer code.
     */
    private int doValidateCheck() {
        if (Math.min(txtArtifactID.getText().trim().length(),
        txtGroupID.getText().trim().length()) == 0) {
            return 1;
        }
        if (txtPackage.getText().trim().length() > 0) {
            boolean matches = txtPackage.getText().matches("[a-zA-Z0-9\\.]*"); //NOI18N
            if (!matches) {
                return 2;
            } else {
                if (txtPackage.getText().startsWith(".") || txtPackage.getText().endsWith(".")) {
                    return 2;
                }
            }
        }
        return  0;
    }
    
    public boolean isInValidState() {
        return doValidateCheck() == 0;
    }
    
    public String getValidityMessage() {
        int retCode = doValidateCheck();
        String message = "";
        if (retCode == 1) {
            message = NbBundle.getMessage(BasicsPanel.class, "BasicsPanel.error1.text");
        }
        if (retCode == 2) {
            message = "Badly formed package name";
        }
        return message;
    }
    
    /**
     * attach to the fields that are validated.
     */
    private class ValidateListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            doValidate();
        }
        
        public void insertUpdate(DocumentEvent e) {
            doValidate();
        }
        
        public void removeUpdate(DocumentEvent e) {
            doValidate();
        }
    }
}
