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

package org.codehaus.mevenide.netbeans.newproject;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.TreeSelectionModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.TreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  mkleint
 */
public class ChooseArchetypePanel extends javax.swing.JPanel implements ExplorerManager.Provider, Runnable {
    private ExplorerManager manager;

    private ChooseWizardPanel wizardPanel;
    /** Creates new form ChooseArchetypePanel */
    public ChooseArchetypePanel(ChooseWizardPanel wizPanel) {
        initComponents();
        this.wizardPanel = wizPanel;
        TreeView tv = new BeanTreeView();
        manager = new ExplorerManager();
        pnlView.add(tv, BorderLayout.CENTER);
        tv.setBorder(jScrollPane1.getBorder());
        AbstractNode loading = new AbstractNode(Children.LEAF);
        tv.setDefaultActionAllowed(false);
        tv.setPopupAllowed(false);
        tv.setRootVisible(false);
        tv.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        loading.setName("loading");
        loading.setDisplayName("Loading...");
        Children childs = new Children.Array();
        childs.add(new Node[] {loading});
        AbstractNode root = new AbstractNode(childs);
        manager.setRootContext(root);
        RequestProcessor.getDefault().post(this);
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateDescription();
                wizardPanel.fireChangeEvent();
            }
        });
        updateDescription();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHint = new javax.swing.JLabel();
        pnlView = new javax.swing.JPanel();
        btnCustom = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        lblHint.setLabelFor(pnlView);
        lblHint.setText("Maven Archetypes:");

        pnlView.setLayout(new java.awt.BorderLayout());

        btnCustom.setText("Custom...");
        btnCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomActionPerformed(evt);
            }
        });

        taDescription.setBackground(new java.awt.Color(238, 238, 238));
        taDescription.setColumns(20);
        taDescription.setEditable(false);
        taDescription.setRows(5);
        jScrollPane1.setViewportView(taDescription);

        jLabel1.setLabelFor(taDescription);
        jLabel1.setText("Description:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(lblHint)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap(424, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(pnlView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCustom))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(lblHint)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(pnlView, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1))
                    .add(btnCustom))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomActionPerformed
        CustomArchetypePanel panel = new CustomArchetypePanel();
        DialogDescriptor dd = new DialogDescriptor(panel, "Specify archetype details");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            Childs childs = (Childs)manager.getRootContext().getChildren();
            Archetype arch = new Archetype();
            arch.setArtifactId(panel.getArtifactId());
            arch.setGroupId(panel.getGroupId());
            arch.setVersion(panel.getVersion().length() == 0 ? "LATEST" : panel.getVersion()); //NOI18N
            arch.setName("Custom archetype - " + panel.getArtifactId());
            childs.addArchetype(arch);
            //HACK - the added one will be last..
            Node[] list =  getExplorerManager().getRootContext().getChildren().getNodes();
            try {
                getExplorerManager().setSelectedNodes(new Node[] {list[list.length - 1]});
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnCustomActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHint;
    private javax.swing.JPanel pnlView;
    private javax.swing.JTextArea taDescription;
    // End of variables declaration//GEN-END:variables
    
    

    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public void run() {
        Lookup.Result<ArchetypeProvider> res = Lookup.getDefault().lookup(new Lookup.Template<ArchetypeProvider>(ArchetypeProvider.class));
        List<Archetype> archetypes = new ArrayList<Archetype>();
        for (ArchetypeProvider provider : res.allInstances()) {
            Iterator it2 = provider.getArchetypes().iterator();
            for (Archetype ar : provider.getArchetypes()) {
                if (!archetypes.contains(ar)) {
                    archetypes.add(ar);
                }
            }
        }
        Childs childs = new Childs(archetypes);
        AbstractNode root = new AbstractNode(childs);
        manager.setRootContext(root);
        try {
            manager.setSelectedNodes(new Node[] {root.getChildren().getNodes()[0]});
        } catch (PropertyVetoException e) {
        }
    }

    void read(WizardDescriptor wizardDescriptor) {
    }

    void store(WizardDescriptor d) {
        if (manager.getSelectedNodes().length > 0) {
            d.putProperty("archetype", manager.getSelectedNodes()[0].getValue("archetype"));
        }
    }

    void validate(WizardDescriptor wizardDescriptor) {
    }

    boolean valid(WizardDescriptor wizardDescriptor) {
        boolean isSelected = manager.getSelectedNodes().length > 0;
        return isSelected;
    }
    private void updateDescription() {
        Node[] nds = manager.getSelectedNodes();
        if (nds.length > 0) {
            Archetype arch = (Archetype)((AbstractNode)nds[0]).getValue("archetype");
            taDescription.setText("Name: " + (arch.getName() != null ? arch.getName() : arch.getArtifactId()) + 
                                  "\nDescription: " + arch.getDescription() + 
                                  "\n\nGroupId: " + arch.getGroupId() + 
                                  "\nArtifactId: " + arch.getArtifactId() + 
                                  "\nVersion: " + arch.getVersion());
        } else {
            taDescription.setText("<No template selected>");
        }
    }
    
    private static class Childs extends Children.Keys {
        private List<Archetype> keys;
        public Childs(List<Archetype> keys) {
            this.keys = keys;
        }
        public void addNotify() {
            setKeys(keys);
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        public void addArchetype(Archetype arch) {
            keys.add(arch);
            setKeys(keys);
            refresh();
        }
        
        public Node[] createNodes(Object key) {
            Archetype arch = (Archetype)key;
            AbstractNode nd = new AbstractNode(Children.LEAF);
            String dn = arch.getName() == null ? arch.getArtifactId() : arch.getName();
            nd.setName(dn);
            nd.setDisplayName(dn);
            nd.setIconBaseWithExtension("org/codehaus/mevenide/netbeans/Maven2Icon.gif");
            nd.setValue("archetype", arch);
            return new Node[] { nd };
        }
    }
}
