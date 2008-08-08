/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.netbeans.modules.maven.nodes;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryQueries;
import org.netbeans.modules.maven.TextValueCompleter;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  mkleint
 */
public class AddDependencyPanel extends javax.swing.JPanel {

    private TextValueCompleter groupCompleter;
    private TextValueCompleter artifactCompleter;
    private TextValueCompleter versionCompleter;
    private JButton okButton;

    /** Creates new form AddDependencyPanel */
    public AddDependencyPanel() {
        initComponents();
        groupCompleter = new TextValueCompleter(Collections.EMPTY_LIST, txtGroupId);
        artifactCompleter = new TextValueCompleter(Collections.EMPTY_LIST, txtArtifactId);
        versionCompleter = new TextValueCompleter(Collections.EMPTY_LIST, txtVersion);
        txtGroupId.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                artifactCompleter.setLoading(true);
                versionCompleter.setLoading(true);
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        populateArtifact();
                        if (txtArtifactId.getText().trim().length() > 0) {
                            populateVersion();
                        }
                    }
                });
            }
        });
        
        txtArtifactId.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                versionCompleter.setLoading(true);
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        populateVersion();
                    }
                });
            }
        });

        okButton = new JButton(NbBundle.getMessage(AddDependencyPanel.class, "BTN_OK"));

        DocumentListener docList = new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                checkValidState();
            }

            public void insertUpdate(DocumentEvent e) {
                checkValidState();
            }

            public void removeUpdate(DocumentEvent e) {
                checkValidState();
            }
        };
        txtGroupId.getDocument().addDocumentListener(docList);
        txtVersion.getDocument().addDocumentListener(docList);
        txtArtifactId.getDocument().addDocumentListener(docList);
        checkValidState();
        groupCompleter.setLoading(true);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                populateGroupId();
            }
        });
        
    }

    public JButton getOkButton() {
        return okButton;
    }

    public String getGroupId() {
        return txtGroupId.getText().trim();
    }

    public String getArtifactId() {
        return txtArtifactId.getText().trim();
    }

    public String getVersion() {
        return txtVersion.getText().trim();
    }

    public String getScope() {
        return comScope.getSelectedItem().toString();
    }

    private void checkValidState() {
        if (txtGroupId.getText().trim().length() <= 0) {
            okButton.setEnabled(false);
            return;
        }
        if (txtArtifactId.getText().trim().length() <= 0) {
            okButton.setEnabled(false);
            return;
        }
        if (txtVersion.getText().trim().length() <= 0) {
            okButton.setEnabled(false);
            return;
        }
        okButton.setEnabled(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblGroupId = new javax.swing.JLabel();
        txtGroupId = new javax.swing.JTextField();
        lblArtifactId = new javax.swing.JLabel();
        txtArtifactId = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblScope = new javax.swing.JLabel();
        comScope = new javax.swing.JComboBox();

        lblGroupId.setLabelFor(txtGroupId);
        org.openide.awt.Mnemonics.setLocalizedText(lblGroupId, org.openide.util.NbBundle.getMessage(AddDependencyPanel.class, "LBL_GroupId")); // NOI18N

        lblArtifactId.setLabelFor(txtArtifactId);
        org.openide.awt.Mnemonics.setLocalizedText(lblArtifactId, org.openide.util.NbBundle.getMessage(AddDependencyPanel.class, "LBL_ArtifactId")); // NOI18N

        lblVersion.setLabelFor(txtVersion);
        org.openide.awt.Mnemonics.setLocalizedText(lblVersion, org.openide.util.NbBundle.getMessage(AddDependencyPanel.class, "LBL_Version")); // NOI18N

        lblScope.setLabelFor(comScope);
        org.openide.awt.Mnemonics.setLocalizedText(lblScope, org.openide.util.NbBundle.getMessage(AddDependencyPanel.class, "LBL_Scope")); // NOI18N

        comScope.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "compile", "runtime", "test", "provided" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblVersion)
                            .add(lblGroupId)
                            .add(lblArtifactId))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                            .add(txtArtifactId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                            .add(txtGroupId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(86, 86, 86)
                        .add(comScope, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblScope)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblGroupId)
                    .add(txtGroupId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblArtifactId)
                    .add(txtArtifactId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblVersion)
                    .add(txtVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblScope)
                    .add(comScope, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(187, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comScope;
    private javax.swing.JLabel lblArtifactId;
    private javax.swing.JLabel lblGroupId;
    private javax.swing.JLabel lblScope;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JTextField txtArtifactId;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables
    
    private void populateGroupId() {
        assert !SwingUtilities.isEventDispatchThread();
        final List<String> lst = new ArrayList<String>(RepositoryQueries.getGroups());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                groupCompleter.setValueList(lst);
            }
        });

    }

    private void populateArtifact() {
        assert !SwingUtilities.isEventDispatchThread();

        final List<String> lst = new ArrayList<String>(RepositoryQueries.getArtifacts(txtGroupId.getText().trim()));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                artifactCompleter.setValueList(lst);
            }
        });

    }

    private void populateVersion() {
        assert !SwingUtilities.isEventDispatchThread();

        List<NBVersionInfo> lst = RepositoryQueries.getVersions(txtGroupId.getText().trim(), txtArtifactId.getText().trim());
        final List<String> vers = new ArrayList<String>();
        for (NBVersionInfo rec : lst) {
            if (!vers.contains(rec.getVersion())) {
                vers.add(rec.getVersion());
            }
        }
        Collections.sort(vers);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                versionCompleter.setValueList(vers);
            }
        });

    }
}
