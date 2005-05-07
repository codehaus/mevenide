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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.mevenide.netbeans.cargo.AddContainerAction;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.mevenide.netbeans.project.MavenProject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DeployPanel extends JPanel {
    private MavenProject project;
    /** Creates new form DeployPanel */
    public DeployPanel() {
        initComponents();
        List listener = new List();
        comContainer.addActionListener(listener);
        btnUrl.addActionListener(listener);
        btnAddContainer.addActionListener(listener);
        txtContext.getDocument().addDocumentListener(listener);
        txtWebpage.getDocument().addDocumentListener(listener);
        setPreferredSize(new Dimension(500, 180));
    }
    
    public DeployPanel(MavenProject proj) {
        this();
        
        project = proj;
    }
    
    private void refreshFields() {
        File war = project.getWar();
        if (war != null) {
            txtFile.setText(war.toString());
            String context = project.getContext().getPOMContext().getFinalProject().getArtifactId();
            if (context == null) {
                context = project.getContext().getPOMContext().getFinalProject().getId();
            }
            if (txtContext.getText() == null || txtContext.getText().trim().length() == 0) {
                // set just for the first time.
                txtContext.setText(context);
            }
        } else {
            //TODO what's wrong?
            txtFile.setText("");
        }
        
    }
    
    private void reloadAvailableContainers() {
        Collection containers = CargoServerRegistry.getInstance().getContainers();
        if (containers != null) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            Iterator it = containers.iterator();
            while (it.hasNext()) {
                Container cont = (Container)it.next();
//                if (CargoServerRegistry.getInstance().supportsDynamicDeployment(cont)) {
                    model.addElement(new ContainerWrapper(cont));
//                }
            }
            comContainer.setModel(model);
        }
    }
    
    public void addNotify() {
        super.addNotify();
        reloadAvailableContainers();
        if (comContainer.getSelectedItem() == null 
                && comContainer.getModel().getSize() > 0) {
            System.out.println("force selection");
            comContainer.setSelectedIndex(0);
            txtUrl.setText(getBaseUrl());
        }
        refreshFields();
    }
    
    
    public Container getSelectedContainer() {
        return ((ContainerWrapper)comContainer.getSelectedItem()).getContainer();
    }
    
    public String getDeployable() {
        return txtFile.getText();
    }
    
    public String getContext() {
        return txtContext.getText();
    }

    public String getBaseUrl() {
        Container cont = ((ContainerWrapper)comContainer.getSelectedItem()).getContainer();
        String hostname = cont.getConfiguration().getPropertyValue(GeneralPropertySet.HOSTNAME);
        String port = cont.getConfiguration().getPropertyValue(ServletPropertySet.PORT);
        boolean hasContext = txtContext.getText().trim().length() > 0;
        boolean hasPage = txtWebpage.getText().trim().length() > 0;
        return "http://" + hostname + ":" + port + 
                (hasContext ? "/" : "") + txtContext.getText() + 
                (hasPage ? "/" : "") + txtWebpage.getText();
    }
    
    public boolean showInBrowser() {
        return cbOpenInBrowser.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblContainer = new javax.swing.JLabel();
        comContainer = new javax.swing.JComboBox();
        lblFile = new javax.swing.JLabel();
        txtFile = new javax.swing.JTextField();
        lblContext = new javax.swing.JLabel();
        txtContext = new javax.swing.JTextField();
        cbOpenInBrowser = new javax.swing.JCheckBox();
        txtWebpage = new javax.swing.JTextField();
        btnUrl = new javax.swing.JButton();
        btnAddContainer = new javax.swing.JButton();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblWebpage = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblContainer.setText("Select Container :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblContainer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(comContainer, gridBagConstraints);

        lblFile.setText("War :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtFile, gridBagConstraints);

        lblContext.setText("Context :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblContext, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtContext, gridBagConstraints);

        cbOpenInBrowser.setText("Open in Browser");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(cbOpenInBrowser, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(txtWebpage, gridBagConstraints);

        btnUrl.setText("Select...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(btnUrl, gridBagConstraints);

        btnAddContainer.setText("Add Container...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(btnAddContainer, gridBagConstraints);

        lblUrl.setText("URL :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(lblUrl, gridBagConstraints);

        txtUrl.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(txtUrl, gridBagConstraints);

        lblWebpage.setText("Page :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblWebpage, gridBagConstraints);

        lblInfo.setText("<html>Maven projects use Cargo APIs (http://cargo.codehaus.org) for deployment. Please refer to Cargo's website for detailed information about supported J2EE containers and features supported for given container. </html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblInfo, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
//GEN-FIRST:event_comContainerActionPerformed
//GEN-LAST:event_comContainerActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddContainer;
    private javax.swing.JButton btnUrl;
    private javax.swing.JCheckBox cbOpenInBrowser;
    private javax.swing.JComboBox comContainer;
    private javax.swing.JLabel lblContainer;
    private javax.swing.JLabel lblContext;
    private javax.swing.JLabel lblFile;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblWebpage;
    private javax.swing.JTextField txtContext;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtWebpage;
    // End of variables declaration//GEN-END:variables
    
    private static class ContainerWrapper  {
        private Container container;
        public ContainerWrapper(Container cont) {
            container = cont;
        }
        
        public Container getContainer() {
            return container;
        }
        
        public String toString() {
            return container.getName();
        }
    }
    
    
    private class List implements ActionListener, DocumentListener {
        
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == comContainer) {
                update();
            }
            if (e.getSource() == btnUrl) {
                
            }
            if (e.getSource() == btnAddContainer) {
                new AddContainerAction().actionPerformed(e);
                reloadAvailableContainers();
            }
        }
    
        private void update() {
            txtUrl.setText(getBaseUrl());
        }
        
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
        
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
        
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
        
    }
 
}
