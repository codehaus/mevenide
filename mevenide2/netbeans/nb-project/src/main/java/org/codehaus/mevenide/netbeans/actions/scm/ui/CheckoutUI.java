/*
 * CheckoutUI.java
 *
 * Created on January 2, 2008, 11:54 AM
 */
package org.codehaus.mevenide.netbeans.actions.scm.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Scm;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class CheckoutUI extends javax.swing.JPanel {

    private static File lastFolder = new File(System.getProperty("user.home")); //NOI18N
    private final JButton checkoutButton;
    private Scm scm;
    private Artifact artifact;

    /** Creates new form CheckoutUI */
    public CheckoutUI(Artifact artifact, Scm scm) {
        this.scm = scm;
        this.artifact = artifact;
        StringBuffer buffer = new StringBuffer();
        buffer.append("<b>");//NOI18N
        buffer.append(artifact.getArtifactId());
        buffer.append("</b>");//NOI18N
        buffer.append(":");//NOI18N
        buffer.append("<b>");//NOI18N
        buffer.append(artifact.getVersion().toString());
        buffer.append("</b>");//NOI18N
        initComponents();
        lblDescription.setText(org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_Description", buffer.toString())); // NOI18N
        checkoutButton = new JButton(NbBundle.getMessage(CheckoutUI.class, "BTN_Checkout"));//NOI18N
        //checkoutButton.setEnabled(false);//TODO validate 
        load();
    }

    private void load() {
        if (scm.getConnection() != null) {
            defaultConnection.setSelected(true);
            txtUrl.setText(scm.getConnection());


        } else {
            defaultConnection.setEnabled(false);

        }
        if (scm.getDeveloperConnection() != null) {


        } else {

            developerConnection.setEnabled(false);


        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblDescription = new javax.swing.JLabel();
        lblConnection = new javax.swing.JLabel();
        defaultConnection = new javax.swing.JRadioButton();
        developerConnection = new javax.swing.JRadioButton();
        lblLocalFolderDescription = new javax.swing.JLabel();
        txtFolder = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();
        lblLocalFolder = new javax.swing.JLabel();
        lblAuthenticationDescription = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblActhenticationHint = new javax.swing.JLabel();
        chkPrintDebugInfo = new javax.swing.JCheckBox();
        txtUrl = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(lblDescription, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_Description")); // NOI18N

        lblConnection.setForeground(new java.awt.Color(0, 0, 102));
        org.openide.awt.Mnemonics.setLocalizedText(lblConnection, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_Connection")); // NOI18N

        buttonGroup1.add(defaultConnection);
        org.openide.awt.Mnemonics.setLocalizedText(defaultConnection, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_DefaultConnection")); // NOI18N
        defaultConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultConnectionActionPerformed(evt);
            }
        });

        buttonGroup1.add(developerConnection);
        org.openide.awt.Mnemonics.setLocalizedText(developerConnection, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_DeveloperConnection")); // NOI18N
        developerConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                developerConnectionActionPerformed(evt);
            }
        });

        lblLocalFolderDescription.setForeground(new java.awt.Color(0, 0, 102));
        org.openide.awt.Mnemonics.setLocalizedText(lblLocalFolderDescription, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_LocalFolderDescription")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnFile, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "BTN_Browse")); // NOI18N
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblLocalFolder, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_LocalFolder")); // NOI18N

        lblAuthenticationDescription.setForeground(new java.awt.Color(0, 0, 102));
        org.openide.awt.Mnemonics.setLocalizedText(lblAuthenticationDescription, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_AuthenticationDescription")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblUser, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_User")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblPassword, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_Password")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblActhenticationHint, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_ActhenticationHint")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkPrintDebugInfo, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "LBL_PrintDebugInfo")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "CheckoutUI.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CheckoutUI.class, "CheckoutUI.jLabel2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkPrintDebugInfo)
                    .add(lblConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 258, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(defaultConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(developerConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 442, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 450, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lblLocalFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 366, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnFile))
                    .add(lblLocalFolderDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 297, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblAuthenticationDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 297, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblUser)
                            .add(lblPassword))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 36, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtPassword)
                            .add(txtUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 172, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblActhenticationHint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 266, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblConnection)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(developerConnection)
                    .add(defaultConnection))
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 16, Short.MAX_VALUE)
                .add(lblLocalFolderDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnFile)
                    .add(txtFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblLocalFolder))
                .add(18, 18, 18)
                .add(lblAuthenticationDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUser)
                    .add(lblActhenticationHint)
                    .add(txtUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPassword)
                    .add(txtPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(chkPrintDebugInfo)
                .add(18, 18, 18))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        JFileChooser chooser = new JFileChooser(lastFolder);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(NbBundle.getMessage(CheckoutUI.class, "TIT_Choose"));

        chooser.setMultiSelectionEnabled(false);
        if (txtFolder.getText().trim().length() > 0) {
            File fil = new File(txtFolder.getText().trim());
            if (fil.exists()) {
                chooser.setSelectedFile(fil);
            }
        }
        int ret = chooser.showDialog(SwingUtilities.getWindowAncestor(this), NbBundle.getMessage(CheckoutUI.class, "LBL_Select"));
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtFolder.setText(chooser.getSelectedFile().getAbsolutePath());
            txtFolder.requestFocusInWindow();
        }
    }//GEN-LAST:event_btnFileActionPerformed

    private void defaultConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultConnectionActionPerformed
        if (defaultConnection.isEnabled()) {
            txtUrl.setText(scm.getConnection());
        }
    }//GEN-LAST:event_defaultConnectionActionPerformed

    private void developerConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_developerConnectionActionPerformed
        if (developerConnection.isEnabled()) {
            txtUrl.setText(scm.getDeveloperConnection());
        }
    }//GEN-LAST:event_developerConnectionActionPerformed

    public RunConfig getRunConfig() {

        return new RunConfig() {

            public File getExecutionDirectory() {
                File file = new File(txtFolder.getText());
                return file;
            }

            public NbMavenProject getProject() {
                return null;
            }

            public List<String> getGoals() {

                List<String> goals = new ArrayList<String>();
                goals.add("scm:checkout");//NOI18N
                return goals;
            }

            public String getExecutionName() {
                return getTaskDisplayName();
            }

            public String getTaskDisplayName() {
                return NbBundle.getMessage(CheckoutUI.class, "LBL_Checkout", artifact.getArtifactId() + " : " + artifact.getVersion().toString());
            }

            public Properties getProperties() {
                Properties properties = new Properties();
                String path = txtFolder.getText();
               
                properties.put("checkoutDirectory", path);//NOI18N
                properties.put("connectionUrl", txtUrl.getText());//NOI18N

                if (txtUser.getText().trim().length() != 0) {
                    properties.put("username", txtUser.getText());//NOI18N
                    properties.put("password ", new String(txtPassword.getPassword()));//NOI18N
                }
                return properties;
            }

            public boolean isShowDebug() {
                return chkPrintDebugInfo.isSelected();
            }

            public boolean isShowError() {
                return chkPrintDebugInfo.isSelected();
            }

            public Boolean isOffline() {
                return false;
            }

            public void setOffline(Boolean bool) {
            //
            }

            public boolean isRecursive() {
                return false;
            }

            public boolean isUpdateSnapshots() {
                return false;
            }

            public List getActivatedProfiles() {
                return Collections.EMPTY_LIST;
            }

            public boolean isInteractive() {
                return true;
            }
        };


    }

    public JButton getCheckoutButton() {
        return checkoutButton;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFile;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkPrintDebugInfo;
    private javax.swing.JRadioButton defaultConnection;
    private javax.swing.JRadioButton developerConnection;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblActhenticationHint;
    private javax.swing.JLabel lblAuthenticationDescription;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblLocalFolder;
    private javax.swing.JLabel lblLocalFolderDescription;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTextField txtFolder;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
