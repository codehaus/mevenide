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

import java.util.List;

import javax.swing.JPanel;
import org.apache.maven.project.Organization;
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
public class OrgPanel extends JPanel implements ProjectPanel {
    
    private boolean propagate;
    private ProjectValidateObserver valObserver;
    private MavenProject project;
    /** Creates new form BasicsPanel */
    public OrgPanel(boolean propagateImmediately, boolean enable, MavenProject proj) {
        initComponents();
        project = proj;
        propagate = propagateImmediately;
        valObserver = null;
        //TODO add listeners for immediatePropagation stuff.
        setName(NbBundle.getMessage(OrgPanel.class, "OrgPanel.name"));
        setEnableFields(enable);
        btnURL.addActionListener(new ActionListener() {
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
        
    }
    
    public void setEnableFields(boolean enable) {
        txtDistAddress.setEditable(enable);
        txtDistDir.setEditable(enable);
        txtLogo.setEditable(enable);
        txtName.setEditable(enable);
        txtSiteAddress.setEditable(enable);
        txtSiteDir.setEditable(enable);
        txtURL.setEditable(enable);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblURL = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        lblLogo = new javax.swing.JLabel();
        txtLogo = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblSiteAddress = new javax.swing.JLabel();
        txtSiteAddress = new javax.swing.JTextField();
        lblSiteDir = new javax.swing.JLabel();
        txtSiteDir = new javax.swing.JTextField();
        lblDistAddress = new javax.swing.JLabel();
        txtDistAddress = new javax.swing.JTextField();
        lblDistDir = new javax.swing.JLabel();
        txtDistDir = new javax.swing.JTextField();
        btnURL = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblName.setLabelFor(txtName);
        lblName.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblName.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        add(txtName, gridBagConstraints);

        lblURL.setLabelFor(txtURL);
        lblURL.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblURL.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtURL, gridBagConstraints);

        lblLogo.setLabelFor(txtLogo);
        lblLogo.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblLogo.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblLogo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtLogo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(jSeparator1, gridBagConstraints);

        lblSiteAddress.setLabelFor(txtSiteAddress);
        lblSiteAddress.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblSiteAddress.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblSiteAddress, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtSiteAddress, gridBagConstraints);

        lblSiteDir.setLabelFor(txtSiteDir);
        lblSiteDir.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblSiteDir.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblSiteDir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtSiteDir, gridBagConstraints);

        lblDistAddress.setLabelFor(txtDistAddress);
        lblDistAddress.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblDistAddress.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblDistAddress, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtDistAddress, gridBagConstraints);

        lblDistDir.setLabelFor(txtDistDir);
        lblDistDir.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.lblSiteDist.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblDistDir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtDistDir, gridBagConstraints);

        btnURL.setText(org.openide.util.NbBundle.getMessage(OrgPanel.class, "OrgPanel.btnURL.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(btnURL, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnURL;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDistAddress;
    private javax.swing.JLabel lblDistDir;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSiteAddress;
    private javax.swing.JLabel lblSiteDir;
    private javax.swing.JLabel lblURL;
    private javax.swing.JTextField txtDistAddress;
    private javax.swing.JTextField txtDistDir;
    private javax.swing.JTextField txtLogo;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSiteAddress;
    private javax.swing.JTextField txtSiteDir;
    private javax.swing.JTextField txtURL;
    // End of variables declaration//GEN-END:variables
    
    public void setResolveValues(boolean resolve) {
//        setEnableFields(!resolve); 
        Project proj = project.getOriginalMavenProject();
        Organization org = proj.getOrganization();
        if (org == null) {
            txtURL.setText("");
            txtName.setText("");
            txtLogo.setText("");
        } else {
            txtURL.setText(org.getUrl() == null ? "" : getValue(org.getUrl(), resolve));
            txtName.setText(org.getName() == null ? "" : getValue(org.getName(), resolve));
            txtLogo.setText(org.getLogo() == null ? "" : getValue(org.getLogo(), resolve));
        }
        txtSiteAddress.setText(proj.getSiteAddress() == null ? "" : getValue(proj.getSiteAddress(), resolve));
        txtSiteDir.setText(proj.getSiteDirectory() == null ? "" : getValue(proj.getSiteDirectory(), resolve));
        txtDistDir.setText(proj.getDistributionDirectory() == null ? "" : getValue(proj.getDistributionDirectory(), resolve));
        txtDistAddress.setText(proj.getDistributionSite() == null ? "" : getValue(proj.getDistributionSite(), resolve));
    }
    
   private String getValue(String value, boolean resolve) {
       if (resolve) {
           return project.getPropertyResolver().resolveString(value);
       }
       return value;
   }
   
    public List getChanges() {
        return Collections.EMPTY_LIST;
//        project.setSiteAddress(txtSiteAddress.getText());
//        project.setSiteDirectory(txtSiteDir.getText());
//        project.setDistributionSite(txtDistAddress.getText());
//        project.setDistributionDirectory(txtDistDir.getText());
//        // see if one of the org fields is defined ..
//        int length = Math.max(
//        Math.max(txtLogo.getText().length(), txtURL.getText().length()),
//        txtName.getText().length());
//        Organization org = null;
//        if (length > 0) {
//            if (project.getOrganization() == null) {
//                org = new Organization();
//                project.setOrganization(org);
//            } else {
//                org = project.getOrganization();
//            }
//            org.setLogo(txtLogo.getText());
//            org.setName(txtName.getText());
//            org.setUrl(txtURL.getText());
//        } else {
//            // no field defined..
//            project.setOrganization(null);
//        }
//        return project;
    }
    
    public boolean isInValidState() {
        //TODO some checks..
        return true;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
    }
    
    public String getValidityMessage() {
        return "";
    }
    
}
