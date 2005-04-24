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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
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
    }
    
    public DeployPanel(MavenProject proj) {
        this();
        
        project = proj;
        Set containers = CargoServerRegistry.getInstance().getContainers();
        if (containers != null) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            Iterator it = containers.iterator();
            while (it.hasNext()) {
                Container cont = (Container)it.next();
                model.addElement(new ContainerWrapper(cont));
            }
            comContainer.setModel(model);
        }
        String buildDir = project.getPropertyResolver().getResolvedValue("maven.war.build.dir");
        String name = project.getPropertyResolver().getResolvedValue("maven.war.final.name");
        if (name != null && buildDir != null) {
            File fil = new File(buildDir, name);
            txtFile.setText(fil.toString());
            String context = project.getContext().getPOMContext().getFinalProject().getArtifactId();
            if (context == null) {
                context = project.getContext().getPOMContext().getFinalProject().getId();
            }
            txtContext.setText(context);
        } else {
            //TODO what's wrong?
        }
        
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

    private String getBaseUrl() {
        Container cont = ((ContainerWrapper)comContainer.getSelectedItem()).getContainer();
        String hostname = cont.getConfiguration().getPropertyValue(GeneralPropertySet.HOSTNAME);
        String port = cont.getConfiguration().getPropertyValue(ServletPropertySet.PORT);
        return "http://" + hostname + ":" + port;
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
        txtUrl = new javax.swing.JTextField();
        btnUrl = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblContainer.setText("Select Container :");
        add(lblContainer, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(comContainer, gridBagConstraints);

        lblFile.setText("War :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(lblFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(txtFile, gridBagConstraints);

        lblContext.setText("Context :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(lblContext, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(txtContext, gridBagConstraints);

        cbOpenInBrowser.setText("Open in Browser");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(cbOpenInBrowser, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(txtUrl, gridBagConstraints);

        btnUrl.setText("Select...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnUrl, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
//GEN-FIRST:event_comContainerActionPerformed
//GEN-LAST:event_comContainerActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUrl;
    private javax.swing.JCheckBox cbOpenInBrowser;
    private javax.swing.JComboBox comContainer;
    private javax.swing.JLabel lblContainer;
    private javax.swing.JLabel lblContext;
    private javax.swing.JLabel lblFile;
    private javax.swing.JTextField txtContext;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextField txtUrl;
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
    
    
    private class List implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == comContainer) {
                txtUrl.setText(getBaseUrl());
            }
            if (e.getSource() == btnUrl) {
                
            }
        }
        
    }
 
}
