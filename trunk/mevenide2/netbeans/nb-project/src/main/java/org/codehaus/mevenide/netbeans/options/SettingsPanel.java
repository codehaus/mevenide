/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.Settings;
import org.openide.filesystems.FileUtil;

/**
 * The visual panel that displays in the Options dialog. Some properties
 * are written to the settings file, some into the Netbeans settings..
 * @author  mkleint
 */
public class SettingsPanel extends javax.swing.JPanel {
    private boolean changed;
    private ActionListener listener;
    
    /** Creates new form SettingsPanel */
    public SettingsPanel() {
        initComponents();
        cbDebug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cbDebug.isSelected()) {
                    cbErrors.setEnabled(false);
                    cbErrors.putClientProperty("wasSelected", Boolean.valueOf(cbErrors.isSelected()));
                    cbErrors.setSelected(true);
                } else {
                    cbErrors.setEnabled(true);
                    cbErrors.setSelected(((Boolean)cbErrors.getClientProperty("wasSelected")).booleanValue());
                }
            }
        });
        initValues();
        listener = new ActionListenerImpl();
        cbDebug.addActionListener(listener);
        cbOffline.addActionListener(listener);
        cbErrors.addActionListener(listener);
        cbPluginRegistry.addActionListener(listener);
        rbChecksumLax.addActionListener(listener);
        rbChecksumNone.addActionListener(listener);
        rbChecksumStrict.addActionListener(listener);
        rbFailEnd.addActionListener(listener);
        rbFailFast.addActionListener(listener);
        rbFailNever.addActionListener(listener);
        rbNoPluginUpdate.addActionListener(listener);
        rbPluginNone.addActionListener(listener);
        rbPluginUpdate.addActionListener(listener);
    }
    
    private void initValues() {
        cbDebug.setSelected(false);
        cbErrors.setSelected(false);
        cbOffline.setSelected(false);
        rbPluginNone.setSelected(true);
        cbPluginRegistry.setSelected(false);
        rbFailFast.setSelected(true);
        rbChecksumNone.setSelected(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        bgChecksums = new javax.swing.ButtonGroup();
        bgPlugins = new javax.swing.ButtonGroup();
        bgFailure = new javax.swing.ButtonGroup();
        cbOffline = new javax.swing.JCheckBox();
        cbDebug = new javax.swing.JCheckBox();
        cbErrors = new javax.swing.JCheckBox();
        pnlChecksums = new javax.swing.JPanel();
        rbChecksumStrict = new javax.swing.JRadioButton();
        rbChecksumLax = new javax.swing.JRadioButton();
        rbChecksumNone = new javax.swing.JRadioButton();
        pnlPlugins = new javax.swing.JPanel();
        rbPluginUpdate = new javax.swing.JRadioButton();
        rbNoPluginUpdate = new javax.swing.JRadioButton();
        rbPluginNone = new javax.swing.JRadioButton();
        pnlFail = new javax.swing.JPanel();
        rbFailFast = new javax.swing.JRadioButton();
        rbFailEnd = new javax.swing.JRadioButton();
        rbFailNever = new javax.swing.JRadioButton();
        cbPluginRegistry = new javax.swing.JCheckBox();
        lblLocalRepository = new javax.swing.JLabel();
        txtLocalRepository = new javax.swing.JTextField();
        btnLocalRepository = new javax.swing.JButton();

        cbOffline.setText("Work Offline");
        cbOffline.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbOffline.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbOffline.setOpaque(false);

        cbDebug.setText("Produce Debug Output");
        cbDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDebug.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDebug.setOpaque(false);

        cbErrors.setText("Produce Exception Error Messages");
        cbErrors.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbErrors.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbErrors.setOpaque(false);

        pnlChecksums.setBorder(javax.swing.BorderFactory.createTitledBorder("Checksum policy"));
        pnlChecksums.setOpaque(false);
        bgChecksums.add(rbChecksumStrict);
        rbChecksumStrict.setText("Strict (Fail)");
        rbChecksumStrict.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbChecksumStrict.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbChecksumStrict.setOpaque(false);

        bgChecksums.add(rbChecksumLax);
        rbChecksumLax.setText("Lax (Warn only)");
        rbChecksumLax.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbChecksumLax.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbChecksumLax.setOpaque(false);

        bgChecksums.add(rbChecksumNone);
        rbChecksumNone.setText("No Global policy");
        rbChecksumNone.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbChecksumNone.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbChecksumNone.setOpaque(false);

        org.jdesktop.layout.GroupLayout pnlChecksumsLayout = new org.jdesktop.layout.GroupLayout(pnlChecksums);
        pnlChecksums.setLayout(pnlChecksumsLayout);
        pnlChecksumsLayout.setHorizontalGroup(
            pnlChecksumsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlChecksumsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlChecksumsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbChecksumNone)
                    .add(rbChecksumStrict)
                    .add(rbChecksumLax))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        pnlChecksumsLayout.setVerticalGroup(
            pnlChecksumsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlChecksumsLayout.createSequentialGroup()
                .add(rbChecksumNone)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbChecksumStrict)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbChecksumLax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pnlPlugins.setBorder(javax.swing.BorderFactory.createTitledBorder("Plugin Update Policy"));
        pnlPlugins.setOpaque(false);
        bgPlugins.add(rbPluginUpdate);
        rbPluginUpdate.setText("Check For Updates");
        rbPluginUpdate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbPluginUpdate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbPluginUpdate.setOpaque(false);

        bgPlugins.add(rbNoPluginUpdate);
        rbNoPluginUpdate.setText("Supress Checking");
        rbNoPluginUpdate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbNoPluginUpdate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbNoPluginUpdate.setOpaque(false);

        bgPlugins.add(rbPluginNone);
        rbPluginNone.setText("No Global policy");
        rbPluginNone.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbPluginNone.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbPluginNone.setOpaque(false);

        org.jdesktop.layout.GroupLayout pnlPluginsLayout = new org.jdesktop.layout.GroupLayout(pnlPlugins);
        pnlPlugins.setLayout(pnlPluginsLayout);
        pnlPluginsLayout.setHorizontalGroup(
            pnlPluginsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPluginsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPluginsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbPluginNone)
                    .add(rbPluginUpdate)
                    .add(rbNoPluginUpdate))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        pnlPluginsLayout.setVerticalGroup(
            pnlPluginsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPluginsLayout.createSequentialGroup()
                .add(rbPluginNone)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbPluginUpdate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbNoPluginUpdate)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFail.setBorder(javax.swing.BorderFactory.createTitledBorder("Multiproject build fail policy"));
        pnlFail.setOpaque(false);
        bgFailure.add(rbFailFast);
        rbFailFast.setText("Stop at first failure");
        rbFailFast.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbFailFast.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbFailFast.setOpaque(false);

        bgFailure.add(rbFailEnd);
        rbFailEnd.setText("Fail at the end");
        rbFailEnd.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbFailEnd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbFailEnd.setOpaque(false);

        bgFailure.add(rbFailNever);
        rbFailNever.setText("Never fail");
        rbFailNever.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbFailNever.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbFailNever.setOpaque(false);

        org.jdesktop.layout.GroupLayout pnlFailLayout = new org.jdesktop.layout.GroupLayout(pnlFail);
        pnlFail.setLayout(pnlFailLayout);
        pnlFailLayout.setHorizontalGroup(
            pnlFailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFailLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlFailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbFailFast)
                    .add(rbFailEnd)
                    .add(rbFailNever))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        pnlFailLayout.setVerticalGroup(
            pnlFailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFailLayout.createSequentialGroup()
                .add(rbFailFast)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbFailEnd)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbFailNever)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cbPluginRegistry.setText("Use plugin registry");
        cbPluginRegistry.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbPluginRegistry.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbPluginRegistry.setOpaque(false);

        lblLocalRepository.setText("Local Repository :");

        btnLocalRepository.setText("Browse...");
        btnLocalRepository.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocalRepositoryActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(cbDebug)
                                .add(cbErrors, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(cbOffline))
                            .add(pnlFail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlPlugins, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(pnlChecksums, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(cbPluginRegistry)
                    .add(layout.createSequentialGroup()
                        .add(lblLocalRepository)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtLocalRepository, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLocalRepository)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(cbOffline)
                        .add(34, 34, 34)
                        .add(cbDebug)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbErrors))
                    .add(pnlChecksums, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlPlugins, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlFail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbPluginRegistry)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblLocalRepository)
                    .add(btnLocalRepository)
                    .add(txtLocalRepository, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(108, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnLocalRepositoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocalRepositoryActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle("Select Local Repository Location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(false);
        String path = txtLocalRepository.getText();
        if (path.trim().length() == 0) {
            path = new File(System.getProperty("user.home"), ".m2").getAbsolutePath();
        }
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectDir = chooser.getSelectedFile();
            txtLocalRepository.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
        }
    }//GEN-LAST:event_btnLocalRepositoryActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgChecksums;
    private javax.swing.ButtonGroup bgFailure;
    private javax.swing.ButtonGroup bgPlugins;
    private javax.swing.JButton btnLocalRepository;
    private javax.swing.JCheckBox cbDebug;
    private javax.swing.JCheckBox cbErrors;
    private javax.swing.JCheckBox cbOffline;
    private javax.swing.JCheckBox cbPluginRegistry;
    private javax.swing.JLabel lblLocalRepository;
    private javax.swing.JPanel pnlChecksums;
    private javax.swing.JPanel pnlFail;
    private javax.swing.JPanel pnlPlugins;
    private javax.swing.JRadioButton rbChecksumLax;
    private javax.swing.JRadioButton rbChecksumNone;
    private javax.swing.JRadioButton rbChecksumStrict;
    private javax.swing.JRadioButton rbFailEnd;
    private javax.swing.JRadioButton rbFailFast;
    private javax.swing.JRadioButton rbFailNever;
    private javax.swing.JRadioButton rbNoPluginUpdate;
    private javax.swing.JRadioButton rbPluginNone;
    private javax.swing.JRadioButton rbPluginUpdate;
    private javax.swing.JTextField txtLocalRepository;
    // End of variables declaration//GEN-END:variables
    
    public void setValues(Settings sett) {
        changed = false;
        cbOffline.setSelected(sett.isOffline());
        cbPluginRegistry.setSelected(MavenExecutionSettings.getDefault().isUsePluginRegistry());
        txtLocalRepository.setText(sett.getLocalRepository());
        cbErrors.setSelected(MavenExecutionSettings.getDefault().isShowErrors());
        cbErrors.putClientProperty("wasSelected", Boolean.valueOf(cbErrors.isSelected()));
        cbDebug.setSelected(MavenExecutionSettings.getDefault().isShowDebug());
        String failureBehaviour = MavenExecutionSettings.getDefault().getFailureBehaviour();
        if (MavenExecutionRequest.REACTOR_FAIL_FAST.equals(failureBehaviour)) {
            rbFailFast.setSelected(true);
        } else if (MavenExecutionRequest.REACTOR_FAIL_AT_END.equals(failureBehaviour)) {
            rbFailEnd.setSelected(true);
        } else if (MavenExecutionRequest.REACTOR_FAIL_NEVER.equals(failureBehaviour)) {
            rbFailNever.setSelected(true);
        }
        String checksums = MavenExecutionSettings.getDefault().getChecksumPolicy();
        if (MavenExecutionRequest.CHECKSUM_POLICY_WARN.equals(checksums)) {
            rbChecksumLax.setSelected(true);
        } else if (MavenExecutionRequest.CHECKSUM_POLICY_FAIL.equals(checksums)) {
            rbChecksumStrict.setSelected(true);
        } else {
            rbChecksumNone.setSelected(true);
        }
        Boolean updates = MavenExecutionSettings.getDefault().getPluginUpdatePolicy();
        if (Boolean.TRUE.equals(updates)) {
            rbPluginUpdate.setSelected(true);
        } else if (Boolean.FALSE.equals(updates)) {
            rbNoPluginUpdate.setSelected(true);
        } else {
            rbPluginNone.setSelected(true);
        }
    }
    
    public void applyValues(Settings sett) {
        sett.setOffline(cbOffline.isSelected());
        String locrepo = txtLocalRepository.getText().trim();
        if (locrepo.length() == 0) {
            locrepo = null;
        }
        sett.setLocalRepository(locrepo);
        
        MavenExecutionSettings.getDefault().setUsePluginRegistry(cbPluginRegistry.isSelected());
        MavenExecutionSettings.getDefault().setShowDebug(cbDebug.isSelected());
        MavenExecutionSettings.getDefault().setShowErrors(cbErrors.isSelected());
        String checksums = null;
        checksums = rbChecksumStrict.isSelected() ? MavenExecutionRequest.CHECKSUM_POLICY_FAIL : checksums;
        checksums = rbChecksumLax.isSelected() ? MavenExecutionRequest.CHECKSUM_POLICY_WARN : checksums;
        MavenExecutionSettings.getDefault().setChecksumPolicy(checksums);
        
        Boolean updates = null;
        updates = rbPluginUpdate.isSelected() ? Boolean.TRUE : updates;
        updates = rbNoPluginUpdate.isSelected() ? Boolean.FALSE : updates;
        MavenExecutionSettings.getDefault().setPluginUpdatePolicy(updates);
        
        String failureBehaviour = MavenExecutionRequest.REACTOR_FAIL_FAST;
        failureBehaviour = rbFailEnd.isSelected() ? MavenExecutionRequest.REACTOR_FAIL_AT_END : failureBehaviour;
        failureBehaviour = rbFailNever.isSelected() ? MavenExecutionRequest.REACTOR_FAIL_NEVER : failureBehaviour;
        MavenExecutionSettings.getDefault().setFailureBehaviour(failureBehaviour);
        changed = false;
    }
    
    boolean hasValidValues() {
        return true;
    }
    
    boolean hasChangedValues() {
        return changed;
    }
    
    private class ActionListenerImpl implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            changed = true;
        }
        
    }
}
