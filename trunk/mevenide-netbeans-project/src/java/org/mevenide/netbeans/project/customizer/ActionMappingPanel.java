/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.netbeans.project.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.properties.IPropertyLocator;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ActionMappingPanel extends JPanel implements ProjectPanel {
    
    private ProjectValidateObserver valObserver;
    private MavenProject project;
    
    private OriginChange ocBuild;
    private OriginChange ocClean;    
    private OriginChange ocJavadoc;    
    private OriginChange ocRebuild;    
    private OriginChange ocTest;    
    private OriginChange ocTestSingle;
    private OriginChange ocMultiBuild;
    private OriginChange ocMultiClean;
    
    private HashMap changes;
    
    /** Creates new form BasicsPanel */
    public ActionMappingPanel(MavenProject proj) {
        project = proj;
        changes = new HashMap();
        initComponents();
        valObserver = null;
        setName("Goal-IDE Action Mapping");

 //       attachOriginListeners();
        populateChangeInstances();
    }
    
    public void setEnableFields(boolean enable) {
//        txtBuild.setEditable(enable);
//        txtClean.setEditable(enable);
//        txtJavadoc.setEditable(enable);
//        txtRebuild.setEditable(enable);
//        txtTest.setEditable(enable);
//        txtTestSingle.setEditable(enable);
//        txtMultiBuild.setEditable(enable);
//        txtMultiClean.setEditable(enable);
        
        txtBuild.setEditable(true);
        txtClean.setEditable(true);
        txtJavadoc.setEditable(true);
        txtRebuild.setEditable(true);
        txtTest.setEditable(true);
        txtTestSingle.setEditable(true);
        txtMultiBuild.setEditable(true);
        txtMultiClean.setEditable(true);
        
    }
    
//    private void attachOriginListeners() {
//        DocListener list = new DocListener(ocBuild, ActionProvider.COMMAND_BUILD, txtBuild);
//        txtBuild.getDocument().addDocumentListener(list);
//        ocBuild.setChangeObserver(list);
//        list = new DocListener(ocClean, ActionProvider.COMMAND_CLEAN, txtClean);
//        txtClean.getDocument().addDocumentListener(list);
//        ocClean.setChangeObserver(list);        
//        list = new DocListener(ocRebuild, ActionProvider.COMMAND_REBUILD, txtRebuild);
//        txtRebuild.getDocument().addDocumentListener(list);
//        ocRebuild.setChangeObserver(list);        
//        list = new DocListener(ocJavadoc, "javadoc", txtJavadoc); //NOI18N
//        txtJavadoc.getDocument().addDocumentListener(list);
//        ocJavadoc.setChangeObserver(list);        
//        list = new DocListener(ocMultiBuild, ActionProviderImpl.COMMAND_MULTIPROJECTBUILD, txtMultiBuild);
//        txtMultiBuild.getDocument().addDocumentListener(list);
//        ocMultiBuild.setChangeObserver(list);        
//        list = new DocListener(ocMultiClean, ActionProviderImpl.COMMAND_MULTIPROJECTCLEAN, txtMultiClean);
//        txtMultiClean.getDocument().addDocumentListener(list);
//        ocMultiClean.setChangeObserver(list);        
//        list = new DocListener(ocTest, ActionProvider.COMMAND_TEST, txtTest);
//        txtTest.getDocument().addDocumentListener(list);
//        ocTest.setChangeObserver(list);        
//        list = new DocListener(ocTestSingle, ActionProvider.COMMAND_TEST_SINGLE, txtTestSingle);
//        txtTestSingle.getDocument().addDocumentListener(list);
//        ocTestSingle.setChangeObserver(list);        
//    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblBuild = new javax.swing.JLabel();
        txtBuild = new javax.swing.JTextField();
        ocBuild = LocationComboFactory.createPropertiesChange(project);
        btnBuild = (JButton)ocBuild.getComponent();
        lblClean = new javax.swing.JLabel();
        txtClean = new javax.swing.JTextField();
        ocClean = LocationComboFactory.createPropertiesChange(project);
        btnClean = (JButton)ocClean.getComponent();
        lblRebuild = new javax.swing.JLabel();
        txtRebuild = new javax.swing.JTextField();
        ocRebuild = LocationComboFactory.createPropertiesChange(project);
        btnRebuild = (JButton)ocRebuild.getComponent();
        lblJavadoc = new javax.swing.JLabel();
        txtJavadoc = new javax.swing.JTextField();
        ocJavadoc = LocationComboFactory.createPropertiesChange(project);
        btnJavadoc = (JButton)ocJavadoc.getComponent();
        lblTest = new javax.swing.JLabel();
        txtTest = new javax.swing.JTextField();
        ocTest = LocationComboFactory.createPropertiesChange(project);
        btnTest = (JButton)ocTest.getComponent();
        lblTestSingle = new javax.swing.JLabel();
        txtTestSingle = new javax.swing.JTextField();
        ocTestSingle = LocationComboFactory.createPropertiesChange(project);
        btnTestSingle = (JButton)ocTestSingle.getComponent();
        lblMultiBuild = new javax.swing.JLabel();
        txtMultiBuild = new javax.swing.JTextField();
        ocMultiBuild = LocationComboFactory.createPropertiesChange(project);
        btnMultiBuild = (JButton)ocMultiBuild.getComponent();
        lblMultiClean = new javax.swing.JLabel();
        txtMultiClean = new javax.swing.JTextField();
        ocMultiClean = LocationComboFactory.createPropertiesChange(project);
        btnMultiClean = (JButton)ocMultiClean.getComponent();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        lblBuild.setLabelFor(txtBuild);
        lblBuild.setText("Build :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblBuild, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtBuild, gridBagConstraints);

        btnBuild.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnBuild, gridBagConstraints);

        lblClean.setLabelFor(txtClean);
        lblClean.setText("Clean :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblClean, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtClean, gridBagConstraints);

        btnClean.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnClean, gridBagConstraints);

        lblRebuild.setLabelFor(txtRebuild);
        lblRebuild.setText("Rebuild :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblRebuild, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtRebuild, gridBagConstraints);

        btnRebuild.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnRebuild, gridBagConstraints);

        lblJavadoc.setLabelFor(txtJavadoc);
        lblJavadoc.setText("Javadoc :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblJavadoc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtJavadoc, gridBagConstraints);

        btnJavadoc.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnJavadoc, gridBagConstraints);

        lblTest.setLabelFor(txtTest);
        lblTest.setText("Test :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblTest, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtTest, gridBagConstraints);

        btnTest.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnTest, gridBagConstraints);

        lblTestSingle.setLabelFor(txtTestSingle);
        lblTestSingle.setText("Test Single :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblTestSingle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtTestSingle, gridBagConstraints);

        btnTestSingle.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnTestSingle, gridBagConstraints);

        lblMultiBuild.setLabelFor(txtMultiBuild);
        lblMultiBuild.setText("Multi Project Build :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblMultiBuild, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        add(txtMultiBuild, gridBagConstraints);

        btnMultiBuild.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        add(btnMultiBuild, gridBagConstraints);

        lblMultiClean.setLabelFor(txtMultiClean);
        lblMultiClean.setText("Multi Project Clean :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblMultiClean, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        add(txtMultiClean, gridBagConstraints);

        btnMultiClean.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        add(btnMultiClean, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuild;
    private javax.swing.JButton btnClean;
    private javax.swing.JButton btnJavadoc;
    private javax.swing.JButton btnMultiBuild;
    private javax.swing.JButton btnMultiClean;
    private javax.swing.JButton btnRebuild;
    private javax.swing.JButton btnTest;
    private javax.swing.JButton btnTestSingle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBuild;
    private javax.swing.JLabel lblClean;
    private javax.swing.JLabel lblJavadoc;
    private javax.swing.JLabel lblMultiBuild;
    private javax.swing.JLabel lblMultiClean;
    private javax.swing.JLabel lblRebuild;
    private javax.swing.JLabel lblTest;
    private javax.swing.JLabel lblTestSingle;
    private javax.swing.JTextField txtBuild;
    private javax.swing.JTextField txtClean;
    private javax.swing.JTextField txtJavadoc;
    private javax.swing.JTextField txtMultiBuild;
    private javax.swing.JTextField txtMultiClean;
    private javax.swing.JTextField txtRebuild;
    private javax.swing.JTextField txtTest;
    private javax.swing.JTextField txtTestSingle;
    // End of variables declaration//GEN-END:variables
    
   private void populateChangeInstances() {
        createChangeInstance(ActionProvider.COMMAND_BUILD, txtBuild, ocBuild);
        createChangeInstance(ActionProvider.COMMAND_CLEAN, txtClean, ocClean);
        createChangeInstance(ActionProvider.COMMAND_REBUILD, txtRebuild, ocRebuild);
        createChangeInstance("javadoc", txtJavadoc, ocJavadoc); //NOI18N
        createChangeInstance(ActionProvider.COMMAND_TEST, txtTest, ocTest);
        createChangeInstance(ActionProvider.COMMAND_TEST_SINGLE, txtTestSingle, ocTestSingle);
        createChangeInstance(ActionProviderImpl.COMMAND_MULTIPROJECTBUILD, txtMultiBuild, ocMultiBuild);
        createChangeInstance(ActionProviderImpl.COMMAND_MULTIPROJECTCLEAN, txtMultiClean, ocMultiClean);
   }
   
   private void createChangeInstance(String actionName, JTextField field, OriginChange oc) {
       String key = "maven.netbeans.exec." + actionName; //NOI18N
       String value = project.getPropertyResolver().getValue(key);
       int location = project.getPropertyLocator().getPropertyLocation(key);
       if (value == null) {
           value = ActionProviderImpl.getDefaultGoalForAction(key);
           location = IPropertyLocator.LOCATION_DEFAULTS;
       } 
       changes.put(key, new TextFieldPropertyChange(key, value, location, field, oc));
   }

    public void setResolveValues(boolean resolve) {
        setEnableFields(!resolve);
        assignValue(ActionProvider.COMMAND_BUILD, resolve);
        assignValue(ActionProvider.COMMAND_CLEAN, resolve);
        assignValue(ActionProvider.COMMAND_REBUILD, resolve);
        assignValue("javadoc", resolve); //NOI18N
        assignValue(ActionProvider.COMMAND_TEST, resolve);
        assignValue(ActionProvider.COMMAND_TEST_SINGLE, resolve);
        assignValue(ActionProviderImpl.COMMAND_MULTIPROJECTBUILD, resolve);
        assignValue(ActionProviderImpl.COMMAND_MULTIPROJECTCLEAN, resolve);
   }
   
   
   private void assignValue(String actionName, boolean resolve) {
       String key = "maven.netbeans.exec." + actionName; //NOI18N
       TextFieldPropertyChange change = (TextFieldPropertyChange)changes.get(key);
       if (resolve) {
           String value = project.getPropertyResolver().resolveString(change.getNewValue());
           change.setResolvedValue(value);
       } else {
           change.resetToNonResolvedValue();
       }
   }
   
    public List getChanges() {
        List toReturn = new ArrayList();
        Iterator it = changes.values().iterator();
        while (it.hasNext()) {
            MavenPropertyChange change = (MavenPropertyChange)it.next();
            if (change.hasChanged()) {
                toReturn.add(change);
            }
        }
        return toReturn;
    }
    
    public boolean isInValidState() {
        return true;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
    }
    
    public String getValidityMessage() {
        return "";
    }
    
  
}
