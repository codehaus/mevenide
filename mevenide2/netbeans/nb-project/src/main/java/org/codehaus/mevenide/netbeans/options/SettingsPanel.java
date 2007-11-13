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
import java.io.StringReader;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.maven.archiva.indexer.RepositoryIndexException;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.Settings;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.indexer.MavenIndexSettings;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.customizer.ActionMappings;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.execute.NbGlobalActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * The visual panel that displays in the Options dialog. Some properties
 * are written to the settings file, some into the Netbeans settings..
 * @author  mkleint
 */
public class SettingsPanel extends javax.swing.JPanel {
    private static final String CP_SELECTED = "wasSelected"; //NOI18N
    private boolean changed;
    private boolean valid;
    private ActionListener listener;
    private DocumentListener docList;
    private MavenOptionController controller;
    
    /** Creates new form SettingsPanel */
    SettingsPanel(MavenOptionController controller) {
        initComponents();
        this.controller = controller;
        cbDebug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cbDebug.isSelected()) {
                    cbErrors.setEnabled(false);
                    cbErrors.putClientProperty(CP_SELECTED, Boolean.valueOf(cbErrors.isSelected())); 
                    cbErrors.setSelected(true);
                } else {
                    cbErrors.setEnabled(true);
                    cbErrors.setSelected(((Boolean)cbErrors.getClientProperty(CP_SELECTED)).booleanValue());
                }
            }
        });
        docList = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                documentChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                documentChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                documentChanged(e);
            }
        };
        initValues();
        listener = new ActionListenerImpl();
        cbDebug.addActionListener(listener);
        cbOffline.addActionListener(listener);
        cbErrors.addActionListener(listener);
        cbPluginRegistry.addActionListener(listener);
        cbSnapshots.addActionListener(listener);
        rbChecksumLax.addActionListener(listener);
        rbChecksumNone.addActionListener(listener);
        rbChecksumStrict.addActionListener(listener);
        rbFailEnd.addActionListener(listener);
        rbFailFast.addActionListener(listener);
        rbFailNever.addActionListener(listener);
        rbNoPluginUpdate.addActionListener(listener);
        rbPluginNone.addActionListener(listener);
        rbPluginUpdate.addActionListener(listener);
        comIndex.addActionListener(listener);
    }

    
    private void initValues() {
        cbDebug.setSelected(false);
        cbErrors.setSelected(false);
        cbOffline.setSelected(false);
        rbPluginNone.setSelected(true);
        cbPluginRegistry.setSelected(false);
        rbFailFast.setSelected(true);
        rbChecksumNone.setSelected(true);
        comIndex.setSelectedIndex(0);
        cbSnapshots.setSelected(true);
        cbUseCommandLine.setSelected(false);
    }
    
    private void documentChanged(DocumentEvent e) {
        changed = true;
        boolean oldvalid = valid;
        if (txtCommandLine.getText().trim().length() > 0) {
            File fil = new File(txtCommandLine.getText());
            if (fil.exists() && new File(fil, "bin" + File.separator + "mvn").exists()) { //NOI18N
                valid = true;
            } else {
                valid = false;
            }
        } else {
            valid = true;
        }
        if (oldvalid != valid) {
            controller.firePropChange(MavenOptionController.PROP_VALID, Boolean.valueOf(oldvalid), Boolean.valueOf(valid));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        lblIndex = new javax.swing.JLabel();
        comIndex = new javax.swing.JComboBox();
        btnIndex = new javax.swing.JButton();
        cbSnapshots = new javax.swing.JCheckBox();
        lblCommandLine = new javax.swing.JLabel();
        txtCommandLine = new javax.swing.JTextField();
        btnCommandLine = new javax.swing.JButton();
        cbUseCommandLine = new javax.swing.JCheckBox();
        btnGoals = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(cbOffline, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.cbOffline.text")); // NOI18N
        cbOffline.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbOffline.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbOffline.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(cbDebug, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.cbDebug.text")); // NOI18N
        cbDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDebug.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDebug.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(cbErrors, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.cbErrors.text")); // NOI18N
        cbErrors.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbErrors.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbErrors.setOpaque(false);

        pnlChecksums.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.pnlChecksums.border.title"))); // NOI18N
        pnlChecksums.setOpaque(false);

        bgChecksums.add(rbChecksumStrict);
        org.openide.awt.Mnemonics.setLocalizedText(rbChecksumStrict, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbChecksumStrict.text")); // NOI18N
        rbChecksumStrict.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbChecksumStrict.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbChecksumStrict.setOpaque(false);

        bgChecksums.add(rbChecksumLax);
        org.openide.awt.Mnemonics.setLocalizedText(rbChecksumLax, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbChecksumLax.text")); // NOI18N
        rbChecksumLax.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbChecksumLax.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbChecksumLax.setOpaque(false);

        bgChecksums.add(rbChecksumNone);
        org.openide.awt.Mnemonics.setLocalizedText(rbChecksumNone, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbChecksumNone.text")); // NOI18N
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
                .addContainerGap(52, Short.MAX_VALUE))
        );
        pnlChecksumsLayout.setVerticalGroup(
            pnlChecksumsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlChecksumsLayout.createSequentialGroup()
                .add(rbChecksumNone)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbChecksumStrict)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbChecksumLax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pnlPlugins.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.pnlPlugins.border.title"))); // NOI18N
        pnlPlugins.setOpaque(false);

        bgPlugins.add(rbPluginUpdate);
        org.openide.awt.Mnemonics.setLocalizedText(rbPluginUpdate, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbPluginUpdate.text")); // NOI18N
        rbPluginUpdate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbPluginUpdate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbPluginUpdate.setOpaque(false);

        bgPlugins.add(rbNoPluginUpdate);
        org.openide.awt.Mnemonics.setLocalizedText(rbNoPluginUpdate, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbNoPluginUpdate.text")); // NOI18N
        rbNoPluginUpdate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbNoPluginUpdate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbNoPluginUpdate.setOpaque(false);

        bgPlugins.add(rbPluginNone);
        org.openide.awt.Mnemonics.setLocalizedText(rbPluginNone, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbPluginNone.text")); // NOI18N
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

        pnlFail.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.pnlFail.border.title"))); // NOI18N
        pnlFail.setOpaque(false);

        bgFailure.add(rbFailFast);
        org.openide.awt.Mnemonics.setLocalizedText(rbFailFast, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbFailFast.text")); // NOI18N
        rbFailFast.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbFailFast.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbFailFast.setOpaque(false);

        bgFailure.add(rbFailEnd);
        org.openide.awt.Mnemonics.setLocalizedText(rbFailEnd, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbFailEnd.text")); // NOI18N
        rbFailEnd.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbFailEnd.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbFailEnd.setOpaque(false);

        bgFailure.add(rbFailNever);
        org.openide.awt.Mnemonics.setLocalizedText(rbFailNever, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.rbFailNever.text")); // NOI18N
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
                    .add(rbFailEnd)
                    .add(rbFailNever)
                    .add(rbFailFast))
                .addContainerGap(142, Short.MAX_VALUE))
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

        org.openide.awt.Mnemonics.setLocalizedText(cbPluginRegistry, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.cbPluginRegistry.text")); // NOI18N
        cbPluginRegistry.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbPluginRegistry.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbPluginRegistry.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(lblLocalRepository, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.lblLocalRepository.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnLocalRepository, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.btnLocalRepository.text")); // NOI18N
        btnLocalRepository.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocalRepositoryActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblIndex, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.lblIndex.text")); // NOI18N

        comIndex.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Once a week", "Once a day", "On every startup", "Never" }));

        org.openide.awt.Mnemonics.setLocalizedText(btnIndex, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.btnIndex.text")); // NOI18N
        btnIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndexActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cbSnapshots, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.cbSnapshots.text")); // NOI18N
        cbSnapshots.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSnapshots.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(lblCommandLine, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.lblCommandLine.text")); // NOI18N

        txtCommandLine.setText(org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.txtCommandLine.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnCommandLine, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.btnCommandLine.text")); // NOI18N
        btnCommandLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommandLineActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cbUseCommandLine, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.cbUseCommandLine.text")); // NOI18N
        cbUseCommandLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbUseCommandLine.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(btnGoals, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "SettingsPanel.btnGoals.text")); // NOI18N
        btnGoals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoalsActionPerformed(evt);
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
                            .add(pnlFail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cbErrors, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .add(btnGoals)
                            .add(cbOffline)
                            .add(cbDebug)
                            .add(cbPluginRegistry))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlPlugins, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(pnlChecksums, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblLocalRepository)
                            .add(lblIndex))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtLocalRepository, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .add(comIndex, 0, 261, Short.MAX_VALUE)
                            .add(cbSnapshots))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(btnLocalRepository)
                            .add(btnIndex)))
                    .add(layout.createSequentialGroup()
                        .add(lblCommandLine)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbUseCommandLine)
                            .add(layout.createSequentialGroup()
                                .add(txtCommandLine, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                                .add(6, 6, 6)
                                .add(btnCommandLine)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnCommandLine, btnIndex, btnLocalRepository}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCommandLine)
                    .add(txtCommandLine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnCommandLine))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbUseCommandLine)
                .add(25, 25, 25)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(btnGoals)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbOffline)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbDebug)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbErrors)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbPluginRegistry))
                    .add(pnlChecksums, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlPlugins, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlFail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblLocalRepository)
                    .add(btnLocalRepository)
                    .add(txtLocalRepository, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnIndex)
                    .add(lblIndex)
                    .add(comIndex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSnapshots)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndexActionPerformed
        btnIndex.setEnabled(false);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                LocalRepositoryIndexer ind = LocalRepositoryIndexer.getInstance();
                try {
                    ind.updateIndex();
                } catch (RepositoryIndexException ex) {
                    ex.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            btnIndex.setEnabled(true);
                        }
                    });
                }
            }
        });
    }//GEN-LAST:event_btnIndexActionPerformed
    
    private void btnLocalRepositoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocalRepositoryActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(org.openide.util.NbBundle.getMessage(SettingsPanel.class, "TIT_Select"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(false);
        String path = txtLocalRepository.getText();
        if (path.trim().length() == 0) {
            path = new File(System.getProperty("user.home"), ".m2").getAbsolutePath(); //NOI18N
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

    private void btnCommandLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommandLineActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(org.openide.util.NbBundle.getMessage(SettingsPanel.class, "TIT_Select2"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(false);
        String path = txtCommandLine.getText();
        if (path.trim().length() == 0) {
            path = new File(System.getProperty("user.home")).getAbsolutePath(); //NOI18N
        }
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectDir = chooser.getSelectedFile();
            txtCommandLine.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
        }
        
    }//GEN-LAST:event_btnCommandLineActionPerformed

    private void btnGoalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoalsActionPerformed
        NbGlobalActionGoalProvider provider = null;
        for (AdditionalM2ActionsProvider prov : Lookup.getDefault().lookupAll(AdditionalM2ActionsProvider.class)) {
            if (prov instanceof NbGlobalActionGoalProvider) {
                provider = (NbGlobalActionGoalProvider)prov;
            }
        }
        assert provider != null;
        try {
            ActionToGoalMapping mappings = new NetbeansBuildActionXpp3Reader().read(new StringReader(provider.getRawMappingsAsString()));
            ActionMappings panel = new ActionMappings(mappings);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(SettingsPanel.class, "TIT_Global"));
            Object retVal = DialogDisplayer.getDefault().notify(dd);
            if (retVal == DialogDescriptor.OK_OPTION) {
                FileObject dir = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Projects/org-codehaus-mevenide-netbeans"); //NOI18N
                CustomizerProviderImpl.writeNbActionsModel(dir, mappings);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnGoalsActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgChecksums;
    private javax.swing.ButtonGroup bgFailure;
    private javax.swing.ButtonGroup bgPlugins;
    private javax.swing.JButton btnCommandLine;
    private javax.swing.JButton btnGoals;
    private javax.swing.JButton btnIndex;
    private javax.swing.JButton btnLocalRepository;
    private javax.swing.JCheckBox cbDebug;
    private javax.swing.JCheckBox cbErrors;
    private javax.swing.JCheckBox cbOffline;
    private javax.swing.JCheckBox cbPluginRegistry;
    private javax.swing.JCheckBox cbSnapshots;
    private javax.swing.JCheckBox cbUseCommandLine;
    private javax.swing.JComboBox comIndex;
    private javax.swing.JLabel lblCommandLine;
    private javax.swing.JLabel lblIndex;
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
    private javax.swing.JTextField txtCommandLine;
    private javax.swing.JTextField txtLocalRepository;
    // End of variables declaration//GEN-END:variables
    
    public void setValues(Settings sett) {
        changed = false;
        cbOffline.setSelected(sett.isOffline());
        cbPluginRegistry.setSelected(MavenExecutionSettings.getDefault().isUsePluginRegistry());
        txtLocalRepository.setText(sett.getLocalRepository());
        cbErrors.setSelected(MavenExecutionSettings.getDefault().isShowErrors());
        cbErrors.putClientProperty(CP_SELECTED, Boolean.valueOf(cbErrors.isSelected()));
        cbDebug.setSelected(MavenExecutionSettings.getDefault().isShowDebug());
        cbUseCommandLine.setSelected(MavenExecutionSettings.getDefault().isUseCommandLine());
        
        txtCommandLine.getDocument().removeDocumentListener(docList);
        File command = MavenExecutionSettings.getDefault().getCommandLinePath();
        txtCommandLine.setText(command != null ? command.getAbsolutePath() : ""); //NOI18N
        txtCommandLine.getDocument().addDocumentListener(docList);
        
        cbSnapshots.setSelected(MavenIndexSettings.getDefault().isIncludeSnapshots());
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
        comIndex.setSelectedIndex(MavenIndexSettings.getDefault().getIndexUpdateFrequency());
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
        MavenExecutionSettings.getDefault().setUseCommandLine(cbUseCommandLine.isSelected());
        String cl = txtCommandLine.getText().trim();
        if (cl.length() == 0) {
            cl = null;
        }
        //MEVENIDE-553
        File command = cl != null ? new File(cl) : null;
        if (command != null && command.exists()) {
            MavenExecutionSettings.getDefault().setCommandLinePath(command);
        } else {
            MavenExecutionSettings.getDefault().setCommandLinePath(null);
        }
        
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
        MavenIndexSettings.getDefault().setIndexUpdateFrequency(comIndex.getSelectedIndex());
        MavenIndexSettings.getDefault().setIncludeSnapshots(cbSnapshots.isSelected());
        changed = false;
    }
    
    boolean hasValidValues() {
        return valid;
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
