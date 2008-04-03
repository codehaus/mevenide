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
package org.codehaus.mevenide.repository;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryIndexer;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;
import org.codehaus.mevenide.repository.register.RepositoryRegisterUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 * @author mkleint
 */
public final class M2RepositoryBrowserTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static M2RepositoryBrowserTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/codehaus/mevenide/repository/MavenRepoBrowser.png"; //NOI18N
    private static final String PREFERRED_ID = "M2RepositoryBrowserTopComponent"; //NOI18N
    private BeanTreeView btv;
    private ExplorerManager manager;

    private M2RepositoryBrowserTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(M2RepositoryBrowserTopComponent.class, "CTL_M2RepositoryBrowserTopComponent")); //NOI18N
        setToolTipText(NbBundle.getMessage(M2RepositoryBrowserTopComponent.class, "HINT_M2RepositoryBrowserTopComponent")); //NOI18N
        setIcon(Utilities.loadImage(ICON_PATH, true));
        btv = new BeanTreeView();
        btv.setRootVisible(false);
        manager = new ExplorerManager();
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));
        associateLookup(ExplorerUtils.createLookup(manager, map));
        pnlBrowse.add(btv, BorderLayout.CENTER);
        btnIndex.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/repository/refreshRepo.png"))); //NOI18N
        btnAddRepo.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/repository/AddRepo.png"))); //NOI18N
        btnFind.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/repository/FindInRepo.png"))); //NOI18N
        btnIndex.setText(null);
        btnAddRepo.setText(null);
        btnFind.setText(null);

//        RepositoryUtil.getDefaultRepositoryIndexer().addIndexChangeListener(new ChangeListener() {
//
//            public void stateChanged(ChangeEvent e) {
//                manager.setRootContext(createRootNode());
//            }
//        });
        hideFind();
    }

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    private void hideFind() {
        pnlFind.removeAll();
        pnlFind.setVisible(false);
        
        jSplitPane1.setDividerLocation(1.0);
        jSplitPane1.setEnabled(false);
    }
    
    private void showFind(List<NBVersionInfo> infos, DialogDescriptor dd) {
        FindResultsPanel pnl = new FindResultsPanel(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideFind();
            }
        }, dd);
        pnl.setResults(infos);
        pnlFind.add(pnl);
        pnlFind.setVisible(true);
        jSplitPane1.setEnabled(true);
        jSplitPane1.setDividerLocation(0.5);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlExplorer = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlBrowse = new javax.swing.JPanel();
        pnlFind = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnIndex = new javax.swing.JButton();
        btnAddRepo = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        pnlExplorer.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerSize(3);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlBrowse.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setTopComponent(pnlBrowse);

        pnlFind.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlFind);

        pnlExplorer.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(pnlExplorer, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);

        btnIndex.setText("Index");
        btnIndex.setToolTipText(org.openide.util.NbBundle.getMessage(M2RepositoryBrowserTopComponent.class, "LBL_REPO_Update_Indexes")); // NOI18N
        btnIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndexActionPerformed(evt);
            }
        });
        jToolBar1.add(btnIndex);

        btnAddRepo.setText("Add Repo");
        btnAddRepo.setToolTipText(org.openide.util.NbBundle.getMessage(M2RepositoryBrowserTopComponent.class, "LBL_Add_Repo", new Object[] {})); // NOI18N
        btnAddRepo.setFocusable(false);
        btnAddRepo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddRepo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddRepo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRepoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddRepo);

        btnFind.setText("Find");
        btnFind.setToolTipText(org.openide.util.NbBundle.getMessage(M2RepositoryBrowserTopComponent.class, "LBL_REPO_Find")); // NOI18N
        btnFind.setFocusable(false);
        btnFind.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFind.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFind);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents
    private void btnIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndexActionPerformed
        btnIndex.setEnabled(false);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                List<RepositoryInfo> infos = RepositoryPreferences.getInstance().getRepositoryInfos();
                for (RepositoryInfo ri : infos) {
                    RepositoryIndexer.indexRepo(ri);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        btnIndex.setEnabled(true);
                    }
                });
            }
        });

    }//GEN-LAST:event_btnIndexActionPerformed

private void btnAddRepoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRepoActionPerformed
    final RepositoryRegisterUI rrui = new RepositoryRegisterUI();
    DialogDescriptor dd = new DialogDescriptor(rrui, NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_ADD"));
    dd.setClosingOptions(new Object[]{
                rrui.getButton(),
                DialogDescriptor.CANCEL_OPTION
            });
    dd.setOptions(new Object[]{
                rrui.getButton(),
                DialogDescriptor.CANCEL_OPTION
            });
    Object ret = DialogDisplayer.getDefault().notify(dd);
    if (rrui.getButton() == ret) {
        final  RepositoryInfo info = rrui.getRepositoryInfo();
        RepositoryPreferences.getInstance().addOrModifyRepositoryInfo(info);
        manager.setRootContext(createRootNode());
        RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    RepositoryIndexer.indexRepo(info);
                }
            });
    }
        
}//GEN-LAST:event_btnAddRepoActionPerformed

private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
    hideFind();
    final FindInRepoPanel pnl = new FindInRepoPanel();
    final DialogDescriptor dd = new DialogDescriptor(pnl, "Find In Repositories");
    Object ret = DialogDisplayer.getDefault().notify(dd);
    if (ret == DialogDescriptor.OK_OPTION) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                final List<NBVersionInfo> infos = RepositoryQueries.find(pnl.getQuery());
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showFind(infos, dd);
                        }
                    });
            }
        });
    }
    
}//GEN-LAST:event_btnFindActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRepo;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnIndex;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlBrowse;
    private javax.swing.JPanel pnlExplorer;
    private javax.swing.JPanel pnlFind;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized M2RepositoryBrowserTopComponent getDefault() {
        if (instance == null) {
            instance = new M2RepositoryBrowserTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the M2RepositoryBrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized M2RepositoryBrowserTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING,
                    "Cannot find MyWindow component. It will not be located properly in the window system."); //NOI18N
            return getDefault();
        }
        if (win instanceof M2RepositoryBrowserTopComponent) {
            return (M2RepositoryBrowserTopComponent) win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING,
                "There seem to be multiple components with the '" + PREFERRED_ID + //NOI18N
                "' ID. That is a potential source of errors and unexpected behavior."); //NOI18N
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        manager.setRootContext(createRootNode());
    }

    @Override
    public void componentClosed() {
        manager.setRootContext(new AbstractNode(Children.LEAF));
    }

    @Override
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
    }

    @Override
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }



    @Override
    public boolean requestFocusInWindow() {

        return btv.requestFocusInWindow();
    }

    @Override
    public void requestFocus() {

        btv.requestFocus();
    }

    @Override
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    private Node createRootNode() {
        Children.Array array = new Children.Array();
        List<RepositoryInfo> infos = RepositoryPreferences.getInstance().getRepositoryInfos();
        for (RepositoryInfo ri : infos) {
            if (ri.isRemoteDownloadable() || ri.isLocal()) {
             array.add(new Node[]{new RepositoryNode(ri)});
            }
        }

        return new AbstractNode(array);
    }



    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return M2RepositoryBrowserTopComponent.getDefault();
        }
    }
}
