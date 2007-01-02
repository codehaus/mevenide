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

import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
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
import javax.swing.ImageIcon;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.GoalsProvider;
import org.codehaus.mevenide.netbeans.TextValueCompleter;
import org.codehaus.mevenide.netbeans.execute.BeanRunConfig;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.customizer.ActionMappings;
import org.codehaus.mevenide.netbeans.customizer.PropertySplitter;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.StringUtils;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author  mkleint
 */
public class RunGoalsPanel extends javax.swing.JPanel {
    
    private int gridRow = 0;
    private List<NetbeansActionMapping> historyMappings;
    private int historyIndex = 0;
    private TextValueCompleter goalcompleter;
    
    /** Creates new form RunGoalsPanel */
    public RunGoalsPanel() {
        initComponents();
        historyMappings = new ArrayList<NetbeansActionMapping>();
        btnPrev.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/back.png")));
        btnNext.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/forward.png")));
        
        //maybe do lazy..
        goalcompleter = new TextValueCompleter(Collections.EMPTY_LIST, txtGoals, " ");
        GoalsProvider provider = Lookup.getDefault().lookup(GoalsProvider.class);
        if (provider != null) {
            Set<String> strs = provider.getAvailableGoals();
            try {
                List<String> phases = EmbedderFactory.getProjectEmbedder().getLifecyclePhases();
                strs.addAll(phases);
            } catch (Exception e) {
                // oh wel just ignore..
                e.printStackTrace();
            }
            goalcompleter.setValueList(strs);
        }
    }

    public void addNotify() {
        super.addNotify();
        txtGoals.requestFocus();
    }
    
    private String createSpaceSeparatedList(List list) {
        String str = "";
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                str = str + elem + " ";
            }
        }
        return str;
    }
    
    
    public void readMapping(NetbeansActionMapping mapp, MavenProject project, Set profiles, ActionToGoalMapping historyMappings)  {
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
        txtGoals.setText(createSpaceSeparatedList(config.getGoals()));
        if (config.getProperties() != null) {
            StringBuffer buf = new StringBuffer();
            Iterator it = config.getProperties().keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                buf.append(key).append("=").append(config.getProperties().getProperty(key)).append("\n");
            }
            taProperties.setText(buf.toString());
            if (buf.toString().matches(".*maven\\.test\\.skip\\s*=\\s*true\\s*.*")) {
                cbSkipTests.setSelected(true);
            }
        } else {
            taProperties.setText("");
        }
        txtProfiles.setText(createSpaceSeparatedList(config.getActivatedProfiles()));
        setUpdateSnapshots(config.isUpdateSnapshots());
        setOffline(config.isOffline() != null ? config.isOffline().booleanValue() : false);
        setRecursive(config.isRecursive());
        setShowDebug(config.isShowDebug());
    }
    
    private void readMapping(NetbeansActionMapping mapp) {
        txtGoals.setText(createSpaceSeparatedList(mapp.getGoals()));
        if (mapp.getProperties() != null) {
            StringBuffer buf = new StringBuffer();
            Iterator it = mapp.getProperties().keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                buf.append(key).append("=").append(mapp.getProperties().getProperty(key)).append("\n");
            }
            taProperties.setText(buf.toString());
            if (buf.toString().matches(".*maven\\.test\\.skip\\s*=\\s*true\\s*.*")) {
                cbSkipTests.setSelected(true);
            }
        } else {
            taProperties.setText("");
        }
        txtProfiles.setText(createSpaceSeparatedList(mapp.getActivatedProfiles()));
    }
    
    public void applyValues(NetbeansActionMapping mapp) {
        StringTokenizer tok = new StringTokenizer(txtGoals.getText().trim());
        List lst = new ArrayList();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        mapp.setGoals(lst.size() > 0 ? lst : null);
        
        PropertySplitter split = new PropertySplitter(taProperties.getText());
        String token = split.nextPair();
        Properties props = new Properties();
        while (token != null) {
            String[] prp = StringUtils.split(token, "=", 2); //NOI18N
            if (prp.length == 2) {
                props.setProperty(prp[0], prp[1]);
            }
            token = split.nextPair();
        }
        if (cbSkipTests.isSelected()) {
            props.setProperty(ActionMappings.PROP_SKIP_TEST, "true"); //NOI18N
        }
        mapp.setProperties(props);
        
        tok = new StringTokenizer(txtProfiles.getText().trim());
        lst = new ArrayList();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        mapp.setActivatedProfiles(lst);
        
    }
    
    public void applyValues(BeanRunConfig rc) {
        StringTokenizer tok = new StringTokenizer(txtGoals.getText().trim());
        List lst = new ArrayList();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        rc.setGoals(lst.size() > 0 ? lst : Collections.singletonList("install"));
        tok = new StringTokenizer(txtProfiles.getText().trim());
        lst = new ArrayList();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        rc.setActivatedProfiles(lst);
        
        PropertySplitter split = new PropertySplitter(taProperties.getText());
        String token = split.nextPair();
        Properties props = new Properties();
        while (token != null) {
            String[] prp = StringUtils.split(token, "=", 2); //NOI18N
            if (prp.length == 2) {
                props.setProperty(prp[0], prp[1]);
            }
            token = split.nextPair();
        }
        if (cbSkipTests.isSelected()) {
            props.setProperty(ActionMappings.PROP_SKIP_TEST, "true"); //NOI18N
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

        lblGoals = new javax.swing.JLabel();
        txtGoals = new javax.swing.JTextField();
        lblProfiles = new javax.swing.JLabel();
        txtProfiles = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taProperties = new javax.swing.JTextArea();
        cbRecursive = new javax.swing.JCheckBox();
        cbOffline = new javax.swing.JCheckBox();
        cbDebug = new javax.swing.JCheckBox();
        cbUpdateSnapshots = new javax.swing.JCheckBox();
        cbSkipTests = new javax.swing.JCheckBox();
        btnNext = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(lblGoals, "Goals:");

        org.openide.awt.Mnemonics.setLocalizedText(lblProfiles, "&Profiles:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "P&roperties:");

        taProperties.setColumns(20);
        taProperties.setRows(5);
        jScrollPane1.setViewportView(taProperties);

        org.openide.awt.Mnemonics.setLocalizedText(cbRecursive, "Recursive (with Modules)");
        cbRecursive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbRecursive.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(cbOffline, "Build Offline");
        cbOffline.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbOffline.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(cbDebug, "Show Debug Output");
        cbDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDebug.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(cbUpdateSnapshots, "Update Snapshots");
        cbUpdateSnapshots.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbUpdateSnapshots.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(cbSkipTests, "Skip Tests");
        cbSkipTests.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSkipTests.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSkipTests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSkipTestsActionPerformed(evt);
            }
        });

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
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblGoals)
                            .add(lblProfiles)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                            .add(txtGoals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                            .add(txtProfiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbRecursive)
                            .add(cbOffline))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbDebug)
                            .add(cbUpdateSnapshots)))
                    .add(cbSkipTests)
                    .add(layout.createSequentialGroup()
                        .add(btnPrev)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblGoals)
                    .add(txtGoals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProfiles)
                    .add(txtProfiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbRecursive)
                    .add(cbUpdateSnapshots))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbOffline)
                    .add(cbDebug))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSkipTests)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnPrev)
                    .add(btnNext))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbSkipTestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSkipTestsActionPerformed
        String current = taProperties.getText();
        if (current.contains(ActionMappings.PROP_SKIP_TEST)) {
            taProperties.setText(current.replaceAll(".*maven\\.test\\.skip\\s*=\\s*[a-z]*\\s*.*", "maven.test.skip=" + (cbSkipTests.isSelected() ? "true" : "false")));
        } else if (cbSkipTests.isSelected()) {
            taProperties.setText(taProperties.getText() + "\nmaven.test.skip=true");
        }
        
    }//GEN-LAST:event_cbSkipTestsActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        moveHistory(1);
    }//GEN-LAST:event_btnNextActionPerformed

    private void moveHistory(int step) {
        historyIndex = historyIndex + step;
        readMapping(historyMappings.get(historyIndex));
        btnPrev.setEnabled(historyIndex != 0);
        btnNext.setEnabled(historyIndex != (historyMappings.size() - 1));
    }
    
    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        moveHistory(-1);
    }//GEN-LAST:event_btnPrevActionPerformed
    
    
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
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JCheckBox cbDebug;
    private javax.swing.JCheckBox cbOffline;
    private javax.swing.JCheckBox cbRecursive;
    private javax.swing.JCheckBox cbSkipTests;
    private javax.swing.JCheckBox cbUpdateSnapshots;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JLabel lblProfiles;
    private javax.swing.JTextArea taProperties;
    private javax.swing.JTextField txtGoals;
    private javax.swing.JTextField txtProfiles;
    // End of variables declaration//GEN-END:variables
}
