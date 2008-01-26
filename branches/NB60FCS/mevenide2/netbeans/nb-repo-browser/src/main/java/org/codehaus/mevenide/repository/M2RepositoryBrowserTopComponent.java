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
import java.io.Serializable;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;
import org.apache.maven.archiva.indexer.RepositoryIndexException;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.repository.search.SearchAction;
import org.codehaus.mevenide.repository.search.SearchPanel;
import org.openide.DialogDescriptor;
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
    static final String ICON_PATH = "org/codehaus/mevenide/repository/RepoBrowser.png"; //NOI18N
    
    private static final String PREFERRED_ID = "M2RepositoryBrowserTopComponent"; //NOI18N

    private BeanTreeView btv;

    private ExplorerManager manager;
    
    private boolean searchMode = false;

    private SearchPanel searchPanel;

    private DialogDescriptor searchDD;
    
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
        pnlExplorer.add(btv, BorderLayout.CENTER);
        btnSearch.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/repository/find.gif"))); //NOI18N
        btnBack.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/repository/backToBrowse.gif"))); //NOI18N
        btnIndex.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/repository/refresh.png"))); //NOI18N
        btnSearch.setText(null);
        btnBack.setText(null);
        btnIndex.setText(null);
        btnBack.setMargin(new Insets(1,1,1,1));
        btnSearch.setMargin(new Insets(1,1,1,1));
        btnIndex.setMargin(new Insets(1,1,1,1));
        LocalRepositoryIndexer.getInstance().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                checkMode();
            }
        });
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlExplorer = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnBack = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnIndex = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        pnlExplorer.setLayout(new java.awt.BorderLayout());
        add(pnlExplorer, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);

        btnBack.setText("Back to browse");
        btnBack.setToolTipText("Return back to browse mode");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBack);

        btnSearch.setText("Search");
        btnSearch.setToolTipText("Search Local Repository");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSearch);

        btnIndex.setText("Index");
        btnIndex.setToolTipText("Reindex local repository");
        btnIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndexActionPerformed(evt);
            }
        });
        jToolBar1.add(btnIndex);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void btnIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndexActionPerformed
        btnIndex.setEnabled(false);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                LocalRepositoryIndexer ind = LocalRepositoryIndexer.getInstance();
                try {
                    ind.updateIndex();
                } catch (RepositoryIndexException ex) {
                    ex.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            btnIndex.setEnabled(true);
                        }
                    });
                }
            }
        });

    }//GEN-LAST:event_btnIndexActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
            searchMode = false;
            checkMode();
            searchPanel = null;
            searchDD = null;
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if (searchPanel != null && searchDD != null) {
            ((SearchAction)SearchAction.get(SearchAction.class)).performAction(searchDD, searchPanel);
        } else {
            SearchAction.get(SearchAction.class).actionPerformed(null);
        }
        
    }//GEN-LAST:event_btnSearchActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnIndex;
    private javax.swing.JButton btnSearch;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlExplorer;
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
            return (M2RepositoryBrowserTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING,
                "There seem to be multiple components with the '" + PREFERRED_ID + //NOI18N
                "' ID. That is a potential source of errors and unexpected behavior."); //NOI18N
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        checkMode();
    }
    
    public void componentClosed() {
        manager.setRootContext(new AbstractNode(Children.LEAF));
    }
    
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
    }
    
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }
    
    private void checkMode() {
        btnBack.setVisible(searchMode);
        if (!searchMode) {
            manager.setRootContext(createRootNode());
//            btnSearch.setText("Search");
        } else {
//            btnSearch.setText("Back to browse");
        }
                
    }
    
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return btv.requestFocusInWindow();
    }

    public void requestFocus() {
        super.requestFocus();
        btv.requestFocus();
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }

    private Node createRootNode() {
        return new AbstractNode(new GroupIdListChildren());
    }

    public void showSearchResults(Node root) {
        manager.setRootContext(root);
        searchMode = true;
        checkMode();
    }

    public void setSearchDialogCache(DialogDescriptor dd, SearchPanel panel) {
        searchDD = dd;
        searchPanel = panel;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return M2RepositoryBrowserTopComponent.getDefault();
        }
    }
    
}