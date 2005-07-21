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


package org.mevenide.netbeans.j2ee.deploy;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class NbDeployPanel extends javax.swing.JPanel {
    
    /** Creates new form NbDeployPanel */
    public NbDeployPanel(String selected) {
        initComponents();
        loadModel(selected);
    }
    
    private void loadModel(String selectedId) {
        String[] ids = Deployment.getDefault().getServerInstanceIDs();
        Collection col = new ArrayList();
        Wrapper selected = null;
        for (int i = 0; i < ids.length; i++) {
            Wrapper wr = new Wrapper(ids[i], 
                             Deployment.getDefault().getServerInstanceDisplayName(ids[i]));
            col.add(wr);
            if (selectedId.equals(ids[i])) {
                selected = wr;
            }
            
        }
        comServer.setModel(new DefaultComboBoxModel(col.toArray()));
        comServer.setSelectedItem(selected);
    }
    
    public String getSelectedServer() {
        return ((Wrapper)comServer.getSelectedItem()).getId();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblServer = new javax.swing.JLabel();
        comServer = new javax.swing.JComboBox();
        lblPage = new javax.swing.JLabel();
        txtPage = new javax.swing.JTextField();
        btnPage = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblServer.setText("Server Instance :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblServer, gridBagConstraints);

        comServer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(comServer, gridBagConstraints);

        lblPage.setText("Page :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(lblPage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(txtPage, gridBagConstraints);

        btnPage.setText("Select...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(btnPage, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPage;
    private javax.swing.JComboBox comServer;
    private javax.swing.JLabel lblPage;
    private javax.swing.JLabel lblServer;
    private javax.swing.JTextField txtPage;
    // End of variables declaration//GEN-END:variables
    
    private class Wrapper {
        private String dn;
        private String id;
        
        public Wrapper(String serverid, String name) {
            id = serverid;
            dn = name;
        }
        
        public String getId() {
            return id;
        }
        
        public String toString() {
            return dn;
        }
                
    }
}
