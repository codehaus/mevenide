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

package org.codehaus.mevenide.netbeans.nodes;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.netbeans.TextValueCompleter;

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
        populateGroupId();
        artifactCompleter = new TextValueCompleter(Collections.EMPTY_LIST, txtArtifactId);
        versionCompleter = new TextValueCompleter(Collections.EMPTY_LIST, txtVersion);
        txtGroupId.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                populateArtifact();
                populateVersion();
            }
        });
        txtArtifactId.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                populateVersion();
            }
        });
        
        okButton = new JButton("Ok");
        
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
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
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
        lblGroupId.setText("GroupId:"); // NOI18N

        lblArtifactId.setLabelFor(txtArtifactId);
        lblArtifactId.setText("ArtifactId:"); // NOI18N

        lblVersion.setLabelFor(txtVersion);
        lblVersion.setText("Version:"); // NOI18N

        lblScope.setLabelFor(comScope);
        lblScope.setText("Scope:"); // NOI18N

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
        try {
            List<String> lst = CustomQueries.enumerateGroupIds();
            groupCompleter = new TextValueCompleter(lst, txtGroupId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void populateArtifact() {
        try {
            List<String> lst = CustomQueries.getArtifacts(txtGroupId.getText().trim());
            artifactCompleter.setValueList(lst);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void populateVersion() {
        try {
            List<StandardArtifactIndexRecord> lst = CustomQueries.getVersions(txtGroupId.getText().trim(), txtArtifactId.getText().trim());
            List<String> vers = new ArrayList<String>();
            for (StandardArtifactIndexRecord rec : lst) {
                if (!vers.contains(rec.getVersion())) {
                    vers.add(rec.getVersion());
                }
            }
            Collections.sort(vers);
            versionCompleter.setValueList(vers);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
}
