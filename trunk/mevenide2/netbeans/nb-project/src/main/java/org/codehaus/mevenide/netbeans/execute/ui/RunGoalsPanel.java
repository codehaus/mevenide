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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.openide.util.Utilities;

/**
 *
 * @author  mkleint
 */
public class RunGoalsPanel extends javax.swing.JPanel {
    
    private int gridRow = 0;
    private List propertyList;
    private List historyMappings;
    private int historyIndex = 0;
    
    /** Creates new form RunGoalsPanel */
    public RunGoalsPanel() {
        initComponents();
        propertyList = new ArrayList();
        historyMappings = new ArrayList();
        btnPrev.setIcon(new ImageIcon(Utilities.loadImage("/org/codehaus/mevenide/netbeans/execute/back.png")));
        btnNext.setIcon(new ImageIcon(Utilities.loadImage("/org/codehaus/mevenide/netbeans/execute/forward.png")));
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
            addPropertyPanel(key, mapp.getProperties().getProperty(key));
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
        Iterator it = propertyList.iterator();
        while (it.hasNext()) {
            PropertyPanel panl = (PropertyPanel)it.next();
            if (!panl.isRemoved()) {
                mapp.getProperties().setProperty(panl.getPropertyKey(), panl.getPropertyValue());
            }
        }
        
        mapp.getActivatedProfiles().clear();
        mapp.getActivatedProfiles().addAll(lstProfiles.getSelectedValues() != null 
                ? Arrays.asList(lstProfiles.getSelectedValues())
                : Collections.EMPTY_LIST);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        lstProfiles = new javax.swing.JList();
        cbOffline = new javax.swing.JCheckBox();
        cbDebug = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlProperties = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();

        lblGoals.setText("Goals:");

        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Activate Profiles"));
        jScrollPane1.setViewportView(lstProfiles);

        cbOffline.setText("Build Offline");
        cbOffline.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbOffline.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbDebug.setText("Debug Output");
        cbDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDebug.setMargin(new java.awt.Insets(0, 0, 0, 0));

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(lblGoals)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtGoals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cbDebug)
                                    .add(cbOffline))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 101, Short.MAX_VALUE)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 179, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnPrev)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnNext)
                    .add(btnPrev))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblGoals)
                    .add(txtGoals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(cbOffline, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbDebug))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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
        readMapping((NetbeansActionMapping)historyMappings.get(historyIndex));
        btnPrev.setEnabled(historyIndex != 0);
        btnNext.setEnabled(historyIndex != (historyMappings.size() - 1));
    }
    
    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        moveHistory(-1);
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addPropertyPanel("", "");
    }//GEN-LAST:event_btnAddActionPerformed
    
    private void addPropertyPanel(String key, String value) {
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
        pnl.requestFocusInWindow();
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JCheckBox cbDebug;
    private javax.swing.JCheckBox cbOffline;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JList lstProfiles;
    private javax.swing.JPanel pnlProperties;
    private javax.swing.JTextField txtGoals;
    // End of variables declaration//GEN-END:variables
}
