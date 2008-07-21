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

package org.netbeans.modules.maven.customizer;

import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.customizer.ModelHandle;

/**
 *
 * @author  mkleint
 */
public class LibrariesPanel extends javax.swing.JPanel {
    
    private ScopedDependenciesPanel compile;
    private ScopedDependenciesPanel run;
    private ScopedDependenciesPanel test;
    
    /** Creates new form LibrariesPanel */
    public LibrariesPanel(ModelHandle hand, NbMavenProjectImpl orig) {
        initComponents();
        test = new ScopedDependenciesPanel(ScopedDependenciesPanel.TEST, orig, hand);
        run = new ScopedDependenciesPanel(ScopedDependenciesPanel.RUN, orig, hand);
        compile = new ScopedDependenciesPanel(ScopedDependenciesPanel.COMPILE, orig, hand);
        pnlTest.add(test);
        pnlRun.add(run);
        pnlCompile.add(compile);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlCompile = new javax.swing.JPanel();
        pnlRun = new javax.swing.JPanel();
        pnlTest = new javax.swing.JPanel();
        cbTransitive = new javax.swing.JCheckBox();

        pnlCompile.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.pnlCompile.TabConstraints.tabTitle"), pnlCompile); // NOI18N

        pnlRun.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.pnlRun.TabConstraints.tabTitle"), pnlRun); // NOI18N

        pnlTest.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.pnlTest.TabConstraints.tabTitle"), pnlTest); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbTransitive, org.openide.util.NbBundle.getMessage(LibrariesPanel.class, "LibrariesPanel.cbTransitive.text")); // NOI18N
        cbTransitive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbTransitive.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbTransitive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTransitiveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(cbTransitive)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTransitive))
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void cbTransitiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTransitiveActionPerformed
    // TODO add your handling code here:
    compile.setTransitive(cbTransitive.isSelected());
    run.setTransitive(cbTransitive.isSelected());
    test.setTransitive(cbTransitive.isSelected());
    
}//GEN-LAST:event_cbTransitiveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbTransitive;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnlCompile;
    private javax.swing.JPanel pnlRun;
    private javax.swing.JPanel pnlTest;
    // End of variables declaration//GEN-END:variables
    
}