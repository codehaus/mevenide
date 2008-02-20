/*
 * CreateCustomIndexUI.java
 *
 * Created on January 29, 2008, 2:40 PM
 */
package org.codehaus.mevenide.repository.local;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;

/**
 *
 * @author  Anuradha
 */
public class CreateCustomIndexUI extends javax.swing.JPanel {

    private static File lastFolder = new File(System.getProperty("user.home")); //NOI18N

    /** Creates new form CreateCustomIndexUI */
    public CreateCustomIndexUI() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnDoIndex = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtRepoId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtRepoDir = new javax.swing.JTextField();
        btnRepoDirBrowse = new javax.swing.JButton();
        btnIndexDirBrowse = new javax.swing.JButton();
        txtIndeDir = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        btnDoIndex.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_EXPORT", new Object[] {})); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_REPO_ID", new Object[] {})); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_REPO_FOLDER", new Object[] {})); // NOI18N

        btnRepoDirBrowse.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_BROWSE", new Object[] {})); // NOI18N
        btnRepoDirBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepoDirBrowseActionPerformed(evt);
            }
        });

        btnIndexDirBrowse.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_BROWSE", new Object[] {})); // NOI18N
        btnIndexDirBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndexDirBrowseActionPerformed(evt);
            }
        });

        jLabel4.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_INDEX_OUTPUT", new Object[] {})); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 153));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(CreateCustomIndexUI.class, "LBL_Header", new Object[] {})); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtIndeDir, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                            .add(txtRepoDir, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                            .add(txtRepoId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, btnRepoDirBrowse)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, btnIndexDirBrowse)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(jLabel1)
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtRepoId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(txtRepoDir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnRepoDirBrowse))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(btnIndexDirBrowse)
                    .add(txtIndeDir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnRepoDirBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepoDirBrowseActionPerformed
JFileChooser chooser = new JFileChooser(lastFolder);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose Repository Dir");

        chooser.setMultiSelectionEnabled(false);
        if (txtRepoDir.getText().trim().length() > 0) {
            File fil = new File(txtRepoDir.getText().trim());
            if (fil.exists()) {
                chooser.setSelectedFile(fil);
            }
        }
        int ret = chooser.showDialog(SwingUtilities.getWindowAncestor(this), "Select");
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtRepoDir.setText(chooser.getSelectedFile().getAbsolutePath());
            txtRepoDir.requestFocusInWindow();
        }
}//GEN-LAST:event_btnRepoDirBrowseActionPerformed

private void btnIndexDirBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndexDirBrowseActionPerformed
JFileChooser chooser = new JFileChooser(lastFolder);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose Directory to put Index ");

        chooser.setMultiSelectionEnabled(false);
        if (txtIndeDir.getText().trim().length() > 0) {
            File fil = new File(txtIndeDir.getText().trim());
            if (fil.exists()) {
                chooser.setSelectedFile(fil);
            }
        }
        int ret = chooser.showDialog(SwingUtilities.getWindowAncestor(this), "Select");
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtIndeDir.setText(chooser.getSelectedFile().getAbsolutePath());
            txtIndeDir.requestFocusInWindow();
        }
}//GEN-LAST:event_btnIndexDirBrowseActionPerformed
public JButton getIndexButton(){
 return btnDoIndex;
}
public void doIndex(){
RepositoryUtil.getDefaultRepositoryIndexer().indexRepo(txtRepoId.getText().trim(), new File(txtRepoDir.getText().trim()), new File(txtIndeDir.getText().trim()));

}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDoIndex;
    private javax.swing.JButton btnIndexDirBrowse;
    private javax.swing.JButton btnRepoDirBrowse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField txtIndeDir;
    private javax.swing.JTextField txtRepoDir;
    private javax.swing.JTextField txtRepoId;
    // End of variables declaration//GEN-END:variables

}