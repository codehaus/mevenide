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
package org.mevenide.netbeans.project.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;


import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DescriptionPanel extends JPanel implements ProjectPanel {
    private static Log logger = LogFactory.getLog(DescriptionPanel.class);

    private ProjectValidateObserver valObserver;
    private MavenProject project;
   
    private OriginChange ocLogo;
    private OriginChange ocUrl;
    private OriginChange ocInceptionYear;
    private OriginChange ocShortDescription;
    private OriginChange ocDescription;
    
    private HashMap changes;
    private boolean initialized;
    
    /** Creates new form BasicsPanel */
    public DescriptionPanel(MavenProject proj) {
	project = proj;
        changes = new HashMap();
        initComponents();
        valObserver = null;
        //TODO add listeners for immediatePropagation stuff.
        setName("Description");
        btnUrl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String url = txtUrl.getText().trim();
                url = project.getPropertyResolver().resolveString(url);
                if (url.startsWith("http://")) {
                    try {
                        URL link = new URL(url);
                        HtmlBrowser.URLDisplayer.getDefault().showURL(link);
                    } catch (MalformedURLException exc) {
                        NotifyDescriptor error = new NotifyDescriptor.Message("Is not a valid URL.", NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(error);
                    }
                }
            }
        });
        initialized = false;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblInceptionYear = new javax.swing.JLabel();
        txtInceptionYear = new javax.swing.JTextField();
        ocInceptionYear = LocationComboFactory.createPOMChange(project, false);
        btnInceptionYear = (JButton)ocInceptionYear.getComponent();
        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        ocUrl = LocationComboFactory.createPOMChange(project, false);
        btnUrlLoc = (JButton)ocUrl.getComponent();
        btnUrl = new javax.swing.JButton();
        lblLogo = new javax.swing.JLabel();
        txtLogo = new javax.swing.JTextField();
        ocLogo = LocationComboFactory.createPOMChange(project, false);
        btnLogo = (JButton)ocLogo.getComponent();
        lblShortDescription = new javax.swing.JLabel();
        txtShortDescription = new javax.swing.JTextField();
        ocShortDescription = LocationComboFactory.createPOMChange(project, false);
        btnShortDescription = (JButton)ocShortDescription.getComponent();
        lblDescription = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        ocDescription = LocationComboFactory.createPOMChange(project, true);
        btnDescription = (JButton)ocDescription.getComponent();

        setLayout(new java.awt.GridBagLayout());

        lblInceptionYear.setLabelFor(txtInceptionYear);
        lblInceptionYear.setText(org.openide.util.NbBundle.getMessage(DescriptionPanel.class, "DescPanel.lblInceptionYear.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblInceptionYear, gridBagConstraints);

        txtInceptionYear.setMinimumSize(new java.awt.Dimension(50, 28));
        txtInceptionYear.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtInceptionYear, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(btnInceptionYear, gridBagConstraints);

        lblUrl.setLabelFor(txtUrl);
        lblUrl.setText(org.openide.util.NbBundle.getMessage(DescriptionPanel.class, "DescPanel.lblUrl.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblUrl, gridBagConstraints);

        txtUrl.setMinimumSize(new java.awt.Dimension(50, 26));
        txtUrl.setPreferredSize(new java.awt.Dimension(50, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtUrl, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(btnUrlLoc, gridBagConstraints);

        btnUrl.setText(org.openide.util.NbBundle.getMessage(DescriptionPanel.class, "BasicPanel.btnUrl.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(btnUrl, gridBagConstraints);

        lblLogo.setLabelFor(txtLogo);
        lblLogo.setText(org.openide.util.NbBundle.getMessage(DescriptionPanel.class, "DescPanel.lblLogo.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblLogo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtLogo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(btnLogo, gridBagConstraints);

        lblShortDescription.setLabelFor(txtShortDescription);
        lblShortDescription.setText(org.openide.util.NbBundle.getMessage(DescriptionPanel.class, "DescPanel.lblShortDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblShortDescription, gridBagConstraints);

        txtShortDescription.setMinimumSize(new java.awt.Dimension(100, 26));
        txtShortDescription.setPreferredSize(new java.awt.Dimension(100, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtShortDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        add(btnShortDescription, gridBagConstraints);

        lblDescription.setLabelFor(taDescription);
        lblDescription.setText(org.openide.util.NbBundle.getMessage(DescriptionPanel.class, "DescPanel.lblDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblDescription, gridBagConstraints);

        taDescription.setMinimumSize(new java.awt.Dimension(200, 100));
        spDescription.setViewportView(taDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(spDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(btnDescription, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDescription;
    private javax.swing.JButton btnInceptionYear;
    private javax.swing.JButton btnLogo;
    private javax.swing.JButton btnShortDescription;
    private javax.swing.JButton btnUrl;
    private javax.swing.JButton btnUrlLoc;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblInceptionYear;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblShortDescription;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField txtInceptionYear;
    private javax.swing.JTextField txtLogo;
    private javax.swing.JTextField txtShortDescription;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables

    
    public void addNotify() {
        super.addNotify();
        if (!initialized) {
            initialized = true;
            populateChangeInstances();
        }
    }    
    
   private void populateChangeInstances() {
        createPOMChangeInstance("inceptionYear", txtInceptionYear, ocInceptionYear);
        createPOMChangeInstance("logo", txtLogo, ocLogo);
        createPOMChangeInstance("url", txtUrl, ocUrl);
        createPOMChangeInstance("shortDescription", txtShortDescription, ocShortDescription);
        createPOMChangeInstance("description", taDescription, ocDescription);
   }

   private void createPOMChangeInstance(String propName, JTextComponent field, OriginChange oc) {
       String key = "pom." + propName; //NOI18N
       String value = project.getProjectWalker().getValue(key);
       int location = project.getProjectWalker().getLocation(key);
       if (value == null) {
           value = "";
       } 
       changes.put(key, new TextComponentPOMChange(key, value, location, field, oc));
   }        
   
     public void setResolveValues(boolean resolve) {
        assignValue("inceptionYear", resolve);
        assignValue("logo", resolve);
        assignValue("url", resolve);
        assignValue("shortDescription", resolve);
        assignValue("description", resolve);
    }
     
   private void assignValue(String actionName, boolean resolve) {
       String key = "pom." + actionName; //NOI18N
       TextComponentPOMChange change = (TextComponentPOMChange)changes.get(key);
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
            MavenChange change = (MavenChange)it.next();
            if (change.hasChanged()) {
                toReturn.add(change);
            }
        }
        return toReturn;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
    }
    
    /**
     * returns 0 for ok, otherwise a integer code.
     */
    private int doValidateCheck() {
        return  0;
    }
    
    public boolean isInValidState() {
        return true;
    }
    
    public String getValidityMessage() {
        return "";
    }
}
