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
import javax.swing.JPanel;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class RepositoryPanel extends JPanel implements ProjectPanel {
    
    private boolean propagate;
    private ProjectValidateObserver valObserver;
    private MavenProject project;
    /** Creates new form BasicsPanel */
    public RepositoryPanel(boolean propagateImmediately, boolean enable, MavenProject proj) {
        initComponents();
        project = proj;
        propagate = propagateImmediately;
        valObserver = null;
        //TODO add listeners for immediatePropagation stuff.
        setName("Repository");
        setEnableFields(enable);
        btnURL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String url = txtURL.getText().trim();
                url = project.getPropertyResolver().resolveString(url);
                if (url.startsWith("http://")) { //NOI18N
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
        txtConnection.setEnabled(enable);
        txtDevConnection.setEnabled(enable);
        txtGumpRepoID.setEnabled(enable);
        txtURL.setEnabled(enable);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JTextField();
        lblURL = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        lblDevConnection = new javax.swing.JLabel();
        txtDevConnection = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblGumpRepoID = new javax.swing.JLabel();
        txtGumpRepoID = new javax.swing.JTextField();
        btnURL = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblConnection.setLabelFor(txtConnection);
        lblConnection.setText("Connection:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblConnection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        add(txtConnection, gridBagConstraints);

        lblURL.setLabelFor(txtURL);
        lblURL.setText("URL :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(txtURL, gridBagConstraints);

        lblDevConnection.setLabelFor(txtDevConnection);
        lblDevConnection.setText("Developer Conn:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblDevConnection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(txtDevConnection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jSeparator1, gridBagConstraints);

        lblGumpRepoID.setLabelFor(txtGumpRepoID);
        lblGumpRepoID.setText("Gump Repository ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblGumpRepoID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(txtGumpRepoID, gridBagConstraints);

        btnURL.setText("View...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnURL, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnURL;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblDevConnection;
    private javax.swing.JLabel lblGumpRepoID;
    private javax.swing.JLabel lblURL;
    private javax.swing.JTextField txtConnection;
    private javax.swing.JTextField txtDevConnection;
    private javax.swing.JTextField txtGumpRepoID;
    private javax.swing.JTextField txtURL;
    // End of variables declaration//GEN-END:variables
    
    public void setProject(Project project) {
        Repository repo = project.getRepository();
        if (repo == null) {
            txtURL.setText("");
            txtConnection.setText("");
            txtDevConnection.setText("");
        } else {
            txtURL.setText(repo.getUrl() == null ? "" : repo.getUrl());
            txtConnection.setText(repo.getConnection() == null ? "" : repo.getConnection());
            txtDevConnection.setText(repo.getDeveloperConnection() == null ? "" : repo.getDeveloperConnection());
        }
        txtGumpRepoID.setText(project.getGumpRepositoryId() == null ? "" : project.getGumpRepositoryId());
    }
    
    public Project copyProject(Project project) {
        return project;
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
