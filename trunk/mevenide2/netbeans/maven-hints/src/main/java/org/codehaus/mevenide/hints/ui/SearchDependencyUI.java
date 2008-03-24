/*
 *  Copyright 2008 Anuradha.
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
package org.codehaus.mevenide.hints.ui;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import org.codehaus.mevenide.hints.ui.nodes.ArtifactNode;
import org.codehaus.mevenide.hints.ui.nodes.VersionNode;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author  Anuradha G
 */
public class SearchDependencyUI extends javax.swing.JPanel implements ExplorerManager.Provider {

    private ExplorerManager explorerManager = new ExplorerManager();
    private JButton addButton = new JButton(NbBundle.getMessage(SearchDependencyUI.class, "BTN_Add"));
    private BeanTreeView beanTreeView;
    private NBVersionInfo nbvi;
    /** Creates new form SearchDependencyUI */
    public SearchDependencyUI(String clazz) {
        initComponents();
        beanTreeView = (BeanTreeView) treeView;
        beanTreeView.setPopupAllowed(false);
        beanTreeView.setRootVisible(false);
        addButton.setEnabled(false);

        txtClassName.setText(clazz);
        txtClassName.selectAll();
        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent arg0) {
                if (arg0.getPropertyName().equals("selectedNodes")) {//NOI18N

                    Node[] selectedNodes = explorerManager.getSelectedNodes();
                   
                    for (Node node : selectedNodes) {
                        if (node instanceof VersionNode) {

                            nbvi=((VersionNode) node).getNBVersionInfo();
        
                            
                            break;

                        }else if(node instanceof ArtifactNode){
                            NBVersionInfo info=null;
                            ArtifactNode an=(ArtifactNode) node;
                            List<NBVersionInfo> infos = an.getVersionInfos();
                            for (NBVersionInfo nbvi : infos) {
                                if(info==null || nbvi.getVersion().compareTo(info.getVersion())>0){
                                
                                  info=nbvi;
                                }
                            }
                            nbvi=info;
                        }
                    }
                    if(nbvi!=null){
                     lblSelected.setText(nbvi.getGroupId()+" : "+nbvi.getArtifactId()
                             +" : "+nbvi.getVersion()+ " [ " + nbvi.getType() 
                             + (nbvi.getClassifier() != null ? ("," + nbvi.getClassifier()) : "")+" ]");
                    }else{
                     lblSelected.setText(null);
                    }
                    addButton.setEnabled(nbvi!=null);

                }
            }
        });
        explorerManager.setRootContext(createEmptyNode());
        load();
    }

    public NBVersionInfo getSelectedVersion() {
        
        return nbvi;
    }

    public JButton getAddButton() {
        return addButton;
    }
    private RequestProcessor.Task task;

    public synchronized void load() {
        if (task != null && !task.isFinished()) {
            task.cancel();
        }
        task = RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                lblSelected.setText(null);
                beanTreeView.setRootVisible(true);
                explorerManager.setRootContext(createLoadingNode());
                List<NBVersionInfo> infos = RepositoryQueries.findVersionsByClass(txtClassName.getText());
                Map<String, List<NBVersionInfo>> map = new HashMap<String, List<NBVersionInfo>>();

                for (NBVersionInfo nbvi : infos) {
                    String key = nbvi.getGroupId() + " : " + nbvi.getArtifactId();
                    List<NBVersionInfo> get = map.get(key);
                    if (get == null) {
                        get = new ArrayList<NBVersionInfo>();
                        map.put(key, get);
                    }
                    get.add(nbvi);
                }
                Set<String> keySet = map.keySet();
                if (keySet.size() > 0) {
                    Children.Array array = new Children.Array();
                    AbstractNode node = new AbstractNode(array);
                    List<String> keyList = new ArrayList<String>(keySet);
                    Collections.sort(keyList);
                    for (String key : keyList) {
                        array.add(new Node[]{new ArtifactNode(key, map.get(key))});
                    }

                    beanTreeView.setRootVisible(false);
                    explorerManager.setRootContext(node);
                } else {
                    beanTreeView.setRootVisible(true);
                    explorerManager.setRootContext(createEmptyNode());
                }
            }
        },100);

    }

    public String getClassSearchName() {
        return txtClassName.getText().trim();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblClassName = new javax.swing.JLabel();
        txtClassName = new javax.swing.JTextField();
        treeView = new BeanTreeView();
        lblMatchingArtifacts = new javax.swing.JLabel();
        lblSelected = new javax.swing.JLabel();

        lblClassName.setText(org.openide.util.NbBundle.getMessage(SearchDependencyUI.class, "LBL_Class_Name")); // NOI18N

        txtClassName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtClassNameKeyReleased(evt);
            }
        });

        treeView.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("ComboBox.selectionBackground")));

        lblMatchingArtifacts.setText(org.openide.util.NbBundle.getMessage(SearchDependencyUI.class, "LBL_Matching_artifacts")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblSelected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 430, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, treeView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .add(txtClassName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblMatchingArtifacts, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblClassName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblClassName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtClassName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblMatchingArtifacts)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(treeView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblSelected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void txtClassNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClassNameKeyReleased
    load();
}//GEN-LAST:event_txtClassNameKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblClassName;
    private javax.swing.JLabel lblMatchingArtifacts;
    private javax.swing.JLabel lblSelected;
    private javax.swing.JScrollPane treeView;
    private javax.swing.JTextField txtClassName;
    // End of variables declaration//GEN-END:variables

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public static Node createLoadingNode() {
        AbstractNode nd = new AbstractNode(Children.LEAF) {

            @Override
            public Image getIcon(int arg0) {
                return Utilities.loadImage("org/codehaus/mevenide/hints/wait.gif");
            }

            @Override
            public Image getOpenedIcon(int arg0) {
                return getIcon(arg0);
            }
        };
        nd.setName("Loading"); //NOI18N

        nd.setDisplayName(NbBundle.getMessage(SearchDependencyUI.class, "Node_Loading"));
        return nd;
    }

    public static Node createEmptyNode() {
        AbstractNode nd = new AbstractNode(Children.LEAF) {

            @Override
            public Image getIcon(int arg0) {
                return Utilities.loadImage("org/codehaus/mevenide/hints/empty.png");
            }

            @Override
            public Image getOpenedIcon(int arg0) {
                return getIcon(arg0);
            }
        };
        nd.setName("Empty"); //NOI18N

        nd.setDisplayName(NbBundle.getMessage(SearchDependencyUI.class, "Node_Empty"));
        return nd;
    }
}
