/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.navigator;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.build.model.ModelLineage;
import org.apache.maven.project.build.model.ModelLineageIterator;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.actions.EditAction;
import org.openide.cookies.EditCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author  mkleint
 */
public class POMInheritancePanel extends javax.swing.JPanel implements ExplorerManager.Provider, Runnable {
    private transient ExplorerManager explorerManager = new ExplorerManager();
    
    private BeanTreeView treeView;
    private DataObject current;
    private FileChangeAdapter adapter = new FileChangeAdapter(){
            @Override
            public void fileChanged(FileEvent fe) {
                showWaitNode();
                RequestProcessor.getDefault().post(POMInheritancePanel.this);
            }
        };

    /** Creates new form POMInheritancePanel */
    public POMInheritancePanel() {
        initComponents();
        treeView = (BeanTreeView)jScrollPane1;
    }
    
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    void navigate(DataObject d) {
        if (current != null) {
            current.getPrimaryFile().removeFileChangeListener(adapter);
        }
        current = d;
        current.getPrimaryFile().addFileChangeListener(adapter);
        showWaitNode();
        RequestProcessor.getDefault().post(this);
    }
    
    public void run() {
        if (current != null) {
            File file = FileUtil.toFile(current.getPrimaryFile());
            // can be null for stuff in jars?
            if (file != null) {
                try {
                    ModelLineage lin = EmbedderFactory.createModelLineage(file, EmbedderFactory.createOnlineEmbedder(), false);
                    final Children ch = new PomChildren(lin);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                           treeView.setRootVisible(false);
                           explorerManager.setRootContext(new AbstractNode(ch));
                        } 
                    });
                } catch (ProjectBuildingException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.FINE, "Error reading model lineage", ex);
                }
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                       treeView.setRootVisible(false);
                       explorerManager.setRootContext(new WaitNode());
                    } 
                });
            }
        }
    }

    /**
     * 
     */
    void release() {
        if (current != null) {
            current.getPrimaryFile().removeFileChangeListener(adapter);
        }
        current = null;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               treeView.setRootVisible(false);
               explorerManager.setRootContext(new WaitNode());
            } 
        });
    }

    /**
     * 
     */
    public void showWaitNode() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               treeView.setRootVisible(true);
               explorerManager.setRootContext(new WaitNode());
            } 
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    
    
    
    private static class WaitNode extends AbstractNode {
        
        private Image waitIcon = Utilities.loadImage("org/codehaus/mevenide/navigator/wait.gif"); // NOI18N
        
        WaitNode( ) {
            super( Children.LEAF );
        }
        
        @Override
        public Image getIcon(int type) {
             return waitIcon;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @java.lang.Override
        public java.lang.String getDisplayName() {
            return org.openide.util.NbBundle.getMessage(POMInheritancePanel.class, "LBL_Wait");
        }
        
    }
    
    private static class PomChildren extends Children.Keys<ModelLineage> {

        public PomChildren(ModelLineage lineage) {
            setKeys(new ModelLineage[] {lineage});
        }
        
        @Override
        protected Node[] createNodes(ModelLineage key) {
            ModelLineageIterator it = key.lineageIterator();
            List<POMNode> nds = new ArrayList<POMNode>();
            while (it.hasNext()) {
                it.next();
                Model mdl = it.getModel();
                File fl = FileUtil.normalizeFile(it.getPOMFile());
                FileObject fo = FileUtil.toFileObject(fl);
                InstanceContent ic = new InstanceContent();
                if (fo != null && !"pom".equals(fo.getExt())) { //NOI18N
                    try {
                        DataObject dobj = DataObject.find(fo);
                        if (dobj != null) {
                            ic.add(dobj);
                            EditCookie ec = dobj.getLookup().lookup(EditCookie.class);
                            if (ec != null) {
                                ic.add(ec);
                            }
                        }
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                
                nds.add(new POMNode(fl, mdl, new AbstractLookup(ic)));
            }
            return nds.toArray(new Node[0]);
        }
        
    }

    
    private static class POMNode extends AbstractNode {
        
        private Image icon = Utilities.loadImage("org/codehaus/mevenide/navigator/Maven2Icon.gif"); // NOI18N
        private boolean readonly = false;
        private POMNode(File key, Model mdl, Lookup lkp) {
            super( Children.LEAF, lkp);
            setDisplayName(NbBundle.getMessage(POMInheritancePanel.class, "TITLE_PomNode", mdl.getArtifactId(), mdl.getVersion()));
            if (key.getName().endsWith("pom")) { //NOI18N
                //coming from repository
                readonly = true;
            }
            setShortDescription(key.getAbsolutePath());
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[] {
                new MyEditAction()
            };
        }

        @Override
        public Action getPreferredAction() {
            return EditAction.get(EditAction.class);
        }

        @Override
        public String getHtmlDisplayName() {
            if (readonly) {
                return NbBundle.getMessage(POMInheritancePanel.class, "HTML_TITLE_PomNode", getDisplayName());
            }
            return null;
        }
        
        @Override
        public Image getIcon(int type) {
             return icon;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
        
        private class MyEditAction extends AbstractAction {
            
            public MyEditAction() {
                putValue(NAME, NbBundle.getMessage(POMInheritancePanel.class, "ACTION_Edit"));
                setEnabled(true);
            }
            
            public void actionPerformed(ActionEvent e) {
                EditCookie ec = POMNode.this.getLookup().lookup(EditCookie.class);
                if (ec != null) {
                    ec.edit();
                }
            }
        }
    }
}
