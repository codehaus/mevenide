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

package org.codehaus.mevenide.netbeans.execute.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.execute.BeanRunConfig;
import org.codehaus.mevenide.netbeans.execute.RunConfig;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.openide.util.Utilities;

/**
 *
 * @author  mkleint
 */
public class RunGoalsPanel extends javax.swing.JPanel {
    
    private int gridRow = 0;
    private List<PropertyPanel> propertyList;
    private List<NetbeansActionMapping> historyMappings;
    private int historyIndex = 0;
    
    /** Creates new form RunGoalsPanel */
    public RunGoalsPanel() {
        initComponents();
        propertyList = new ArrayList<PropertyPanel>();
        historyMappings = new ArrayList<NetbeansActionMapping>();
        btnPrev.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/back.png")));
        btnNext.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/forward.png")));
    }

    public void addNotify() {
        super.addNotify();
        txtGoals.requestFocus();
    }
    
    public void readMapping(NetbeansActionMapping mapp, MavenProject project, Set profiles, ActionToGoalMapping historyMappings)  {
        DefaultListModel model = new DefaultListModel();
        Iterator it = profiles.iterator();
        while (it.hasNext()) {
            String  prof = (String)it.next();
            model.addElement(prof);
        }
        lstProfiles.setModel(model);
        this.historyMappings.clear();
        this.historyMappings.addAll(historyMappings.getActions());
        this.historyMappings.add(mapp);
        historyIndex = this.historyMappings.size();
        moveHistory(-1);
    }
    
    public void readConfig(RunConfig config) {
        historyMappings.clear();
        btnNext.setVisible(false);
        btnPrev.setVisible(false);
        List lst = config.getGoals();
        String value = "";
        if (lst != null) {
            Iterator it = lst.iterator();
            while (it.hasNext()) {
                String goal = (String) it.next();
                value = value + goal + " ";
            }
        }
        txtGoals.setText(value);
        if (config.getProperties() != null) {
        Iterator it = config.getProperties().keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                addPropertyPanel(key, config.getProperties().getProperty(key), false);
                if ("maven.test.skip".equals(key)) {
                    cbSkipTests.setSelected(true);
                }
            }
        }
        DefaultListSelectionModel sel = new DefaultListSelectionModel();
        sel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        for (int i = 0; i < lstProfiles.getModel().getSize(); i++) {
            if (config.getActiveteProfiles().contains(lstProfiles.getModel().getElementAt(i))) {
                sel.addSelectionInterval(i, i);
            }
        }
        lstProfiles.setSelectionModel(sel);
        setUpdateSnapshots(config.isUpdateSnapshots());
        setOffline(config.isOffline() != null ? config.isOffline().booleanValue() : false);
        setRecursive(config.isRecursive());
        setShowDebug(config.isShowDebug());
    }
    
    private void readMapping(NetbeansActionMapping mapp) {
        List lst = mapp.getGoals();
        String value = "";
        if (lst != null) {
            Iterator it = lst.iterator();
            while (it.hasNext()) {
                String goal = (String) it.next();
                value = value + goal + " ";
            }
        }
        txtGoals.setText(value);
        Iterator it = mapp.getProperties().keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            addPropertyPanel(key, mapp.getProperties().getProperty(key), false);
            if ("maven.test.skip".equals(key)) {
                cbSkipTests.setSelected(true);
            }
        }
        DefaultListSelectionModel sel = new DefaultListSelectionModel();
        sel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        for (int i = 0; i < lstProfiles.getModel().getSize(); i++) {
            if (mapp.getActivatedProfiles().contains(lstProfiles.getModel().getElementAt(i))) {
                sel.addSelectionInterval(i, i);
            }
        }
        lstProfiles.setSelectionModel(sel);
    }
    
    public void applyValues(NetbeansActionMapping mapp) {
        StringTokenizer tok = new StringTokenizer(txtGoals.getText().trim());
        List lst = new ArrayList();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        mapp.setGoals(lst.size() > 0 ? lst : null);
        
        mapp.getProperties().clear();
        for (PropertyPanel panl : propertyList) {
            if (!panl.isRemoved()) {
                mapp.getProperties().setProperty(panl.getPropertyKey(), panl.getPropertyValue());
            }
        }
        
        mapp.getActivatedProfiles().clear();
        mapp.getActivatedProfiles().addAll(lstProfiles.getSelectedValues() != null 
                ? Arrays.asList(lstProfiles.getSelectedValues())
                : Collections.EMPTY_LIST);
    }
    
    public void applyValues(BeanRunConfig rc) {
        rc.setActiveteProfiles(lstProfiles.getSelectedValues() != null 
                ? Arrays.asList(lstProfiles.getSelectedValues())
                : Collections.EMPTY_LIST);
        StringTokenizer tok = new StringTokenizer(txtGoals.getText().trim());
        List lst = new ArrayList();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        rc.setGoals(lst.size() > 0 ? lst : Collections.singletonList("install"));
        Properties props = new Properties();
        for (PropertyPanel panl : propertyList) {
            if (!panl.isRemoved()) {
                props.setProperty(panl.getPropertyKey(), panl.getPropertyValue());
            }
        }
        rc.setProperties(props);
        rc.setRecursive(isRecursive());
        rc.setShowDebug(isShowDebug());
        rc.setUpdateSnapshots(isUpdateSnapshots());
        rc.setOffline(Boolean.valueOf(isOffline()));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblGoals = new javax.swing.JLabel();
        txtGoals = new javax.swing.JTextField();
        cbRecursive = new javax.swing.JCheckBox();
        cbOffline = new javax.swing.JCheckBox();
        cbDebug = new javax.swing.JCheckBox();
        cbUpdateSnapshots = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstProfiles = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlProperties = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        cbSkipTests = new javax.swing.JCheckBox();

        lblGoals.setText("Goals:");

        cbRecursive.setText("Recursive (with Modules)");
        cbRecursive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbRecursive.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbOffline.setText("Build Offline");
        cbOffline.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbOffline.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbDebug.setText("Show Debug Output");
        cbDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDebug.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbUpdateSnapshots.setText("Update Snapshots");
        cbUpdateSnapshots.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbUpdateSnapshots.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Activate Profiles"));
        jScrollPane1.setViewportView(lstProfiles);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Properties"));
        pnlProperties.setLayout(new java.awt.GridBagLayout());

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 100;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnlProperties.add(btnAdd, gridBagConstraints);

        jScrollPane2.setViewportView(pnlProperties);

        btnNext.setToolTipText("Get next entry");
        btnNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnPrev.setToolTipText("Get previous entry");
        btnPrev.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        cbSkipTests.setText("Skip Tests");
        cbSkipTests.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSkipTests.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSkipTests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSkipTestsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(lblGoals)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 393, Short.MAX_VALUE)
                        .add(btnPrev)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnNext))
                    .add(txtGoals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbSkipTests)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbDebug)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbRecursive, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbUpdateSnapshots)
                            .add(cbOffline))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblGoals)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btnNext)
                        .add(btnPrev)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtGoals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(cbRecursive)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbDebug)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbSkipTests))
                    .add(layout.createSequentialGroup()
                        .add(cbOffline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbUpdateSnapshots))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbSkipTestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSkipTestsActionPerformed
        if (cbSkipTests.isSelected()) {
            for (PropertyPanel elem : propertyList) {
                if ("maven.test.skip".equals(elem.getPropertyKey())) {
                    elem.readdToView();
                    elem.setPropertyValue("true");
                    return;
                }
            }
            addPropertyPanel("maven.test.skip", "true", false);
        } else {
            for (PropertyPanel elem : propertyList) {
                if ("maven.test.skip".equals(elem.getPropertyKey())) {
                    elem.removeFromView();
                }
            }
        }
        
    }//GEN-LAST:event_cbSkipTestsActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        moveHistory(1);
    }//GEN-LAST:event_btnNextActionPerformed

    private void moveHistory(int step) {
        Component[] comps = pnlProperties.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof PropertyPanel) {
                pnlProperties.remove(comps[i]);
            }
        }
        pnlProperties.invalidate();
        pnlProperties.revalidate();
        pnlProperties.repaint();
        gridRow = 0;
        propertyList.clear();
        historyIndex = historyIndex + step;
        readMapping(historyMappings.get(historyIndex));
        btnPrev.setEnabled(historyIndex != 0);
        btnNext.setEnabled(historyIndex != (historyMappings.size() - 1));
    }
    
    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        moveHistory(-1);
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addPropertyPanel("", "", true);
    }//GEN-LAST:event_btnAddActionPerformed
    
    private void addPropertyPanel(String key, String value, boolean reqFocus) {
        PropertyPanel pnl = new PropertyPanel();
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = gridRow;
        cons.gridwidth = 2;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.anchor = GridBagConstraints.NORTHWEST;
        pnlProperties.add(pnl, cons);
        propertyList.add(pnl);
        pnl.setPropertyKeyValue(key, value);
        revalidate();
        repaint();
        gridRow++;
        if (reqFocus) {
            pnl.requestFocusInWindow();
        }
    }
    
    public boolean isOffline() {
        return cbOffline.isSelected();
    }
    
    public boolean isShowDebug() {
        return cbDebug.isSelected();
    }

    public void setOffline(boolean b) {
        cbOffline.setSelected(b);
    }

    public void setShowDebug(boolean b) {
        cbDebug.setSelected(b);
    }
    
    public void setUpdateSnapshots(boolean b) {
        cbUpdateSnapshots.setSelected(b);
    }
    
    public void setSkipTests(boolean b) {
        cbSkipTests.setSelected(b);
    }
    
    public void setRecursive(boolean b) {
        cbRecursive.setSelected(b);
    }
    
    public boolean isSkipTests() {
        return cbSkipTests.isSelected();
    }
    
    public boolean isRecursive() {
        return cbRecursive.isSelected();
    }
    
    public boolean isUpdateSnapshots() {
        return cbUpdateSnapshots.isSelected();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JCheckBox cbDebug;
    private javax.swing.JCheckBox cbOffline;
    private javax.swing.JCheckBox cbRecursive;
    private javax.swing.JCheckBox cbSkipTests;
    private javax.swing.JCheckBox cbUpdateSnapshots;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JList lstProfiles;
    private javax.swing.JPanel pnlProperties;
    private javax.swing.JTextField txtGoals;
    // End of variables declaration//GEN-END:variables
}
