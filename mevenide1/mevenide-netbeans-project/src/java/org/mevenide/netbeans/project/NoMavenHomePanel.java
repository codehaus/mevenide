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


package org.mevenide.netbeans.project;

import java.awt.Dimension;
import javax.swing.JFileChooser;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class NoMavenHomePanel extends javax.swing.JPanel {
    
    /** Creates new form NoMavenHomePanel */
    public NoMavenHomePanel() {
        initComponents();
        setPreferredSize(new Dimension(450, 300));
    }
    
    public String getMavenHome() {
        return txtHome.getText();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblHint1 = new javax.swing.JLabel();
        lblHome = new javax.swing.JLabel();
        txtHome = new javax.swing.JTextField();
        btnHome = new javax.swing.JButton();
        lblHint2 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblHint1.setText("<html>You don't define the MAVEN_HOME environment property. That means that you probably didn't install Maven or installed it incorrectly. The mevenide plugin requires Maven present in order to run the build.  Other UI functionality may also be limited.</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(lblHint1, gridBagConstraints);

        lblHome.setText("Maven Home Directory :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblHome, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtHome, gridBagConstraints);

        btnHome.setText("Browse...");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(btnHome, gridBagConstraints);

        lblHint2.setText("<html>If you have Maven installed, please point to it's base installation directory. This value will thereafter be used when invoked from Netbeans. You can later change the location in Tools/Options dialog.<p>\nIf you don't have it installed, please download it from http://maven.apache.org, install it and restart Netbeans.</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(lblHint2, gridBagConstraints);

        }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
// TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (JFileChooser.APPROVE_OPTION == chooser.showDialog(this, "Select")) {
            txtHome.setText("" + chooser.getSelectedFile());
        }
    }//GEN-LAST:event_btnHomeActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHome;
    private javax.swing.JLabel lblHint1;
    private javax.swing.JLabel lblHint2;
    private javax.swing.JLabel lblHome;
    private javax.swing.JTextField txtHome;
    // End of variables declaration//GEN-END:variables
    
}
