/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.mevenide.netbeans.project.wizards;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/** First panel in the NewProject wizard. Used for filling in
 * name, and directory of the project.
 *
 * @author Petr Hrebejk
 */
public class CreateProjectVisual extends JPanel implements DocumentListener {
    
    private CreateProjectPanel panel;
    
    public CreateProjectVisual(CreateProjectPanel pnl) {
        this.panel = pnl;
        initComponents();
        txtProjectName.getDocument().addDocumentListener( this );
        txtProjectDir.getDocument().addDocumentListener( this );
        
        
        setName("Name and Location");
        putClientProperty("NewProjectWizard_Title", "New Maven project");
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        return validateBasics(wizardDescriptor);
    }
    
    void read(WizardDescriptor d) {
        String val = (String)d.getProperty("artifactID");
        txtProjectName.setText(val != null ? val : "");
        val = (String)d.getProperty("projectDir");
        txtProjectDir.setText(val != null ? val : "");
        val = (String)d.getProperty("groupID");
        txtGroupId.setText(val != null ? val : "");
        val = (String)d.getProperty("version");
        txtVersion.setText(val != null ? val : "");
        val = (String)d.getProperty("packageName");
        txtPackageName.setText(val != null ? val : "");
    }
    
    void store(WizardDescriptor d) {
        d.putProperty("artifactID", txtProjectName.getText());
        d.putProperty("projectDir", txtProjectDir.getText());
        d.putProperty("groupID", txtGroupId.getText());
        d.putProperty("packageName", txtPackageName.getText());
        d.putProperty("version", txtVersion.getText());
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        locationContainer = new javax.swing.JPanel();
        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        lblProjectDir = new javax.swing.JLabel();
        txtProjectDir = new javax.swing.JTextField();
        lblFolder = new javax.swing.JLabel();
        txtFolder = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        optionsContainer = new javax.swing.JPanel();
        lblGroupId = new javax.swing.JLabel();
        txtGroupId = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblPackageName = new javax.swing.JLabel();
        txtPackageName = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        locationContainer.setLayout(new java.awt.GridBagLayout());

        lblProjectName.setLabelFor(txtProjectName);
        lblProjectName.setText("Project Name (Artifact ID):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        locationContainer.add(lblProjectName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        locationContainer.add(txtProjectName, gridBagConstraints);

        lblProjectDir.setLabelFor(txtProjectDir);
        lblProjectDir.setText("Project Location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        locationContainer.add(lblProjectDir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        locationContainer.add(txtProjectDir, gridBagConstraints);

        lblFolder.setLabelFor(txtFolder);
        lblFolder.setText("Project Folder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        locationContainer.add(lblFolder, gridBagConstraints);

        txtFolder.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        locationContainer.add(txtFolder, gridBagConstraints);

        btnBrowse.setText("Browse...");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 0);
        locationContainer.add(btnBrowse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(locationContainer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 0);
        add(jSeparator1, gridBagConstraints);

        optionsContainer.setLayout(new java.awt.GridBagLayout());

        lblGroupId.setLabelFor(txtGroupId);
        lblGroupId.setText("Project Group ID :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        optionsContainer.add(lblGroupId, gridBagConstraints);

        txtGroupId.setMinimumSize(new java.awt.Dimension(100, 28));
        txtGroupId.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        optionsContainer.add(txtGroupId, gridBagConstraints);

        lblVersion.setLabelFor(txtVersion);
        lblVersion.setText("Version :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        optionsContainer.add(lblVersion, gridBagConstraints);

        txtVersion.setMinimumSize(new java.awt.Dimension(100, 28));
        txtVersion.setPreferredSize(new java.awt.Dimension(150, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        optionsContainer.add(txtVersion, gridBagConstraints);

        lblPackageName.setLabelFor(txtPackageName);
        lblPackageName.setText("Package Name :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        optionsContainer.add(lblPackageName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        optionsContainer.add(txtPackageName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(optionsContainer, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Project Location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String path = txtProjectDir.getText();
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) { //NOI18N
            File projectDir = chooser.getSelectedFile();
            txtProjectDir.setText( projectDir.getAbsolutePath() );
        }
        panel.fireChangeEvent();
    }//GEN-LAST:event_btnBrowseActionPerformed
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        txtProjectName.requestFocus();
    }
    
    boolean validateBasics(WizardDescriptor wizardDescriptor) {
        if (txtProjectName.getText().length() == 0 ) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", "Project Name is not valid folder name.");
            return false; // Display name not specified
        }
        File destFolder = new File( txtFolder.getText() );
        File[] kids = destFolder.listFiles();
        if ( destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", "Project Folder already exists and is not empty.");
            return false;
        }
        wizardDescriptor.putProperty( "WizardPanel_errorMessage", "" );
        return true;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFolder;
    private javax.swing.JLabel lblGroupId;
    private javax.swing.JLabel lblPackageName;
    private javax.swing.JLabel lblProjectDir;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JPanel locationContainer;
    private javax.swing.JPanel optionsContainer;
    private javax.swing.JTextField txtFolder;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtPackageName;
    private javax.swing.JTextField txtProjectDir;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables
    
    public void changedUpdate( DocumentEvent e ) {
        updateTexts( e );
    }
    
    public void insertUpdate( DocumentEvent e ) {
        updateTexts( e );
    }
    
    public void removeUpdate( DocumentEvent e ) {
        updateTexts( e );
    }
    
    /** Handles changes in the Project name and project directory
     */
    private void updateTexts(DocumentEvent e) {
        Document doc = e.getDocument();
        if (doc == txtProjectName.getDocument() || doc == txtProjectDir.getDocument()) {
            String projectName = txtProjectName.getText();
            String projectFolder = txtProjectDir.getText();
            txtFolder.setText(projectFolder + File.separatorChar + projectName);
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
}
