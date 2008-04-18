/*
 * Copyright 2008 Mevenide Team
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.configurations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author  mkleint
 */
public class NewConfigurationPanel extends javax.swing.JPanel {

    public String getConfigurationId() {
        return txtId.getText();
    }

    public void setConfigurationId(String configurationId) {
        txtId.setText(configurationId);
    }

    public boolean isShared() {
        return cbShared.isSelected();
    }

    public void setShared(boolean shared) {
        cbShared.setSelected(shared);
    }
    /** Creates new form NewConfigurationPanel */
    public NewConfigurationPanel() {
        initComponents();
    }
    
    public void setProfiles(List<String> profiles) {
        String val = "";
        for (String prf : profiles) {
            val = val + prf;
        }
        txtActivate.setText(val);
    }

    public List<String> getProfiles() {
        String val = txtActivate.getText().trim();
        String[] splitted = val.split(" ,");
        List<String> toRet = new ArrayList<String>();
        for (String s : splitted) {
            if (s.trim().length() > 0) {
                toRet.add(s.trim());
            }
        }
        return toRet;
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        cbShared = new javax.swing.JCheckBox();
        lblActivate = new javax.swing.JLabel();
        lblHint = new javax.swing.JLabel();
        txtActivate = new javax.swing.JTextField();

        lblId.setText(org.openide.util.NbBundle.getMessage(NewConfigurationPanel.class, "NewConfigurationPanel.lblId.text")); // NOI18N

        cbShared.setText(org.openide.util.NbBundle.getMessage(NewConfigurationPanel.class, "NewConfigurationPanel.cbShared.text")); // NOI18N

        lblActivate.setLabelFor(txtActivate);
        lblActivate.setText(org.openide.util.NbBundle.getMessage(NewConfigurationPanel.class, "NewConfigurationPanel.lblActivate.text")); // NOI18N

        lblHint.setText(org.openide.util.NbBundle.getMessage(NewConfigurationPanel.class, "NewConfigurationPanel.lblHint.text")); // NOI18N
        lblHint.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(lblId)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbShared)
                            .add(txtId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(lblActivate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtActivate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblHint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblId)
                    .add(txtId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cbShared)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblActivate)
                    .add(txtActivate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(lblHint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbShared;
    private javax.swing.JLabel lblActivate;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblId;
    private javax.swing.JTextField txtActivate;
    private javax.swing.JTextField txtId;
    // End of variables declaration//GEN-END:variables

}