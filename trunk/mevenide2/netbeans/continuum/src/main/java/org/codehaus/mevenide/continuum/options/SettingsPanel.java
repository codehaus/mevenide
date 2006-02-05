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

package org.codehaus.mevenide.continuum.options;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * visual panel for setting the continuum servers.
 * @author  mkleint
 */
public class SettingsPanel extends javax.swing.JPanel {
    
    /** Creates new form SettingsPanel */
    public SettingsPanel() {
        initComponents();
        checkButtons();
        jList1.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                checkButtons();
            }
        });
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createTitledBorder("Apache Continuum servers"));
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(jScrollPane1, gridBagConstraints);

        btnAdd.setText("Add...");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(btnAdd, gridBagConstraints);

        btnEdit.setText("Edit...");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(btnEdit, gridBagConstraints);

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(btnRemove, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int selected = jList1.getSelectedIndex();
        if (selected != -1) {
            ((DefaultListModel)jList1.getModel()).removeElementAt(selected);
            changed = true;
        }
        
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        
        
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        SingleServer ss = new SingleServer();
        DialogDescriptor dd = new DialogDescriptor(ss, "Add Continuum XML-RPC server");
        dd.setOptions(new Object [] {
           NotifyDescriptor.OK_OPTION, 
           NotifyDescriptor.CANCEL_OPTION
        });
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            ((DefaultListModel)jList1.getModel()).addElement(new ServerOutputPair(ss.getURL(), ss.getOutputURL()));
            changed = true;
        }
        
    }//GEN-LAST:event_btnAddActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;

    private boolean changed;
    // End of variables declaration//GEN-END:variables
    
    void setServers(String[] servers, String[] outputs) {
        changed= false;
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < servers.length; i++) {
            model.addElement(new ServerOutputPair(servers[i], outputs.length > i ? outputs[i] : null));
        }
        jList1.setModel(model);
    }
    String[] getServers() {
        DefaultListModel mod = (DefaultListModel)jList1.getModel();
        ServerOutputPair[] ser = new ServerOutputPair[mod.getSize()];
        String[] toRet = new String[mod.getSize()];
        mod.copyInto(ser);
        for (int i = 0; i < ser.length; i++) {
            toRet[i] = ser[i].getRpc();
        }
        return toRet;
    }
    
    private void checkButtons() {
        int selected = jList1.getSelectedIndex();
        btnRemove.setEnabled(selected != -1);
        btnEdit.setEnabled(selected != -1);
    }

    String[] getOutputs() {
        DefaultListModel mod = (DefaultListModel)jList1.getModel();
        ServerOutputPair[] ser = new ServerOutputPair[mod.getSize()];
        String[] toRet = new String[mod.getSize()];
        mod.copyInto(ser);
        for (int i = 0; i < ser.length; i++) {
            toRet[i] = ser[i].getOutput();
        }
        return toRet;
    }

    boolean isChanged() {
        return changed;
    }
    
    private class ServerOutputPair {

        private String output;

        private String rpc;
        ServerOutputPair(String rpc, String output) {
            this.rpc = rpc;
            this.output = output;
        }
        
        String getOutput() {
            return output;
        }
        
        String getRpc() {
            return rpc;
        }
        
        public String toString() {
            return rpc;
        }
    }
}
