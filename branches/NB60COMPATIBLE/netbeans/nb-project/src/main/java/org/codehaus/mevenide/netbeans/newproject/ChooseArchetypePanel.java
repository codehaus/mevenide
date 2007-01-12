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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.TreeSelectionModel;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.TreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  mkleint
 */
public class ChooseArchetypePanel extends javax.swing.JPanel implements ExplorerManager.Provider, Runnable {
    private ExplorerManager manager;

    private ChooseWizardPanel wizardPanel;
    private static final String PROP_ARCHETYPE = "archetype"; //NOI18N
    TreeView tv;
    /** Creates new form ChooseArchetypePanel */
    public ChooseArchetypePanel(ChooseWizardPanel wizPanel) {
        initComponents();
        this.wizardPanel = wizPanel;
        tv = new BeanTreeView();
        manager = new ExplorerManager();
        pnlView.add(tv, BorderLayout.CENTER);
        tv.setBorder(jScrollPane1.getBorder());
        AbstractNode loading = new AbstractNode(Children.LEAF);
        tv.setDefaultActionAllowed(false);
        tv.setPopupAllowed(false);
        tv.setRootVisible(false);
        tv.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        loading.setName("loading"); //NOI18N
        loading.setDisplayName(org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "LBL_Loading"));
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
    
    public void addNotify() {
        super.addNotify();
        tv.requestFocusInWindow();
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
        btnRemove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        lblHint.setLabelFor(pnlView);
        org.openide.awt.Mnemonics.setLocalizedText(lblHint, org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "LBL_MavenArchetype")); // NOI18N

        pnlView.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnCustom, org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "LBL_AddArchetype")); // NOI18N
        btnCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnRemove, org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "LBL_RemoveArchetype")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        taDescription.setBackground(new java.awt.Color(238, 238, 238));
        taDescription.setColumns(20);
        taDescription.setEditable(false);
        taDescription.setRows(5);
        jScrollPane1.setViewportView(taDescription);

        jLabel1.setLabelFor(taDescription);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "LBL_Description")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "TIT_CreateProjectStep")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel2)
            .add(lblHint)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(pnlView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btnCustom)
                    .add(btnRemove)))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnCustom, btnRemove}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(btnCustom)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemove))
                    .add(pnlView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
    Node[] nds = getExplorerManager().getSelectedNodes();
    if (nds.length != 0) {
        Archetype arch = (Archetype) nds[0].getValue(PROP_ARCHETYPE);
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(ChooseArchetypePanel.class, "Q_RemoveArch", arch.getArtifactId()), 
                NotifyDescriptor.YES_NO_OPTION);
        Object ret = DialogDisplayer.getDefault().notify(nd);
        if (ret != NotifyDescriptor.YES_OPTION) {
            return;
        }
        try {
            List<StandardArtifactIndexRecord> rec = CustomQueries.getRecords(arch.getGroupId(), arch.getArtifactId(), arch.getVersion());
            for (StandardArtifactIndexRecord record : rec) {
                LocalRepositoryIndexer.getInstance().getDefaultIndex().deleteRecords(rec);
            }
            File path = new File(EmbedderFactory.getProjectEmbedder().getLocalRepositoryDirectory(),
                    arch.getGroupId().replace('.', File.separatorChar) + File.separatorChar + arch.getArtifactId() 
                  + File.separatorChar + arch.getVersion());
            if (path.exists()) {
                path.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((Childs)getExplorerManager().getRootContext().getChildren()).removeArchetype(arch);
    }
}//GEN-LAST:event_btnRemoveActionPerformed

    private void btnCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomActionPerformed
        CustomArchetypePanel panel = new CustomArchetypePanel();
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "TIT_Archetype_details"));
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            Childs childs = (Childs)manager.getRootContext().getChildren();
            Archetype arch = new Archetype();
            arch.setArtifactId(panel.getArtifactId());
            arch.setGroupId(panel.getGroupId());
            arch.setVersion(panel.getVersion().length() == 0 ? "LATEST" : panel.getVersion()); //NOI18N
            
            arch.setName(org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "LBL_Custom", panel.getArtifactId()));
            if (panel.getRepository().length() != 0) {
                arch.setRepository(panel.getRepository());
            }
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
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
            d.putProperty(PROP_ARCHETYPE, manager.getSelectedNodes()[0].getValue(PROP_ARCHETYPE));
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
            Archetype arch = (Archetype)((AbstractNode)nds[0]).getValue(PROP_ARCHETYPE);
            taDescription.setText(NbBundle.getMessage(ChooseArchetypePanel.class, "MSG_Description", 
                    new Object[] {
                            (arch.getName() != null ? arch.getName() : arch.getArtifactId()),
                             arch.getDescription() == null ? "" : arch.getDescription(),
                             arch.getGroupId(),
                             arch.getArtifactId(),
                             arch.getVersion()
                    }));
            btnRemove.setEnabled(arch.deletable);
        } else {
            taDescription.setText(org.openide.util.NbBundle.getMessage(ChooseArchetypePanel.class, "MSG_NoTemplate"));
            btnRemove.setEnabled(false);
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
        
        public void removeArchetype(Archetype arch) {
            keys.remove(arch);
            setKeys(keys);
            refresh();
        }
        
        public Node[] createNodes(Object key) {
            Archetype arch = (Archetype)key;
            AbstractNode nd = new AbstractNode(Children.LEAF);
            String dn = arch.getName() == null ? arch.getArtifactId() : arch.getName();
            nd.setName(dn);
            nd.setDisplayName(dn);
            nd.setIconBaseWithExtension("org/codehaus/mevenide/netbeans/Maven2Icon.gif"); //NOI18N
            nd.setValue(PROP_ARCHETYPE, arch);
            return new Node[] { nd };
        }
    }
}
