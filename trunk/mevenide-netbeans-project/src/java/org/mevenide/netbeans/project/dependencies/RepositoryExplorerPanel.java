/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.netbeans.project.dependencies;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;
import org.apache.maven.util.DownloadMeter;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.netbeans.project.ProxyUtilities;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.repository.RepositoryReaderFactory;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.project.libraries.LibraryTypeRegistry;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryTypeProvider;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;



/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RepositoryExplorerPanel extends JPanel implements ExplorerManager.Provider {
    
    private ExplorerManager manager;
    private BeanTreeView btv;
    private IPropertyResolver resolver;
    private ILocationFinder finder;
    private RepoPathElement[] roots;
    private IRepositoryReader localReader;
    private URI[] rootUris;
    /** Creates new form CustomGoalsPanel */
    public RepositoryExplorerPanel(IPropertyResolver resolver, ILocationFinder finder) {
        initComponents();
        this.resolver = resolver;
        this.finder = finder;
        createRoots();
        
        GridBagConstraints fillConstraints = new GridBagConstraints();
        fillConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fillConstraints.gridheight = GridBagConstraints.REMAINDER;
        fillConstraints.fill = GridBagConstraints.BOTH;
        fillConstraints.weightx = 1.0;
        fillConstraints.weighty = 1.0;
        
        manager = new ExplorerManager();
        btv = new BeanTreeView();    // Add the BeanTreeView
        btv.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
        btv.setPopupAllowed(true);
        btv.setRootVisible(false);
        btv.setDefaultActionAllowed(false);
        pnlDeps.add(btv, fillConstraints);
        manager.setRootContext(createSeparateRootNode());
        btv.expandAll();
        
        //TEMPORARY
        btnLibrary.setVisible(false);
        //TEMPORARY - END
        btnLibrary.setEnabled(manager.getSelectedNodes().length != 0);
        btnDownload.setEnabled(manager.getSelectedNodes().length != 0);
        
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(event.getPropertyName())) {
                    Node[] nds = manager.getSelectedNodes();
                    boolean isLeaf = nds.length != 0;
                    for (int i = 0 ; i < nds.length; i++) {
                        RepoPathElement el = (RepoPathElement)nds[i].getLookup().lookup(RepoPathElement.class);
                        if (el == null || !el.isLeaf()) {
                            isLeaf = false;
                            break;
                        }
                    }
                    btnLibrary.setEnabled(isLeaf);
                    btnDownload.setEnabled(isLeaf);
                }
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlDeps = new javax.swing.JPanel();
        btnMerge = new javax.swing.JToggleButton();
        btnDownload = new javax.swing.JButton();
        btnLibrary = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pnlDeps.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        add(pnlDeps, gridBagConstraints);

        btnMerge.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/mevenide/netbeans/project/resources/MergeRepos.png")));
        btnMerge.setToolTipText("Split/Merge Repository Roots");
        btnMerge.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/mevenide/netbeans/project/resources/SplitRepos.png")));
        btnMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMergeActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(btnMerge, gridBagConstraints);

        btnDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/mevenide/netbeans/project/resources/Download.png")));
        btnDownload.setToolTipText("Download artifact");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnDownload, gridBagConstraints);

        btnLibrary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/mevenide/netbeans/project/resources/AddLibrary.png")));
        btnLibrary.setToolTipText("Create Library From Artifact(s)");
        btnLibrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLibraryActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnLibrary, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(jPanel1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void btnLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLibraryActionPerformed
//        final Node[] nodes = manager.getSelectedNodes();
//        RequestProcessor.getDefault().post(new Runnable() {
//            public void run() {
//                createLibrary(nodes);
//            }
//        });
                
    }//GEN-LAST:event_btnLibraryActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        final Node[] nodes = manager.getSelectedNodes();
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                download(nodes);
            }
        });
        
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMergeActionPerformed
        // TODO add your handling code here:
        Node root = btnMerge.isSelected() ? createCombinedRootNode() : createSeparateRootNode();
        manager.setRootContext(root);
        
    }//GEN-LAST:event_btnMergeActionPerformed
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    private void createRoots() {
        File fil = new File(finder.getMavenLocalRepository());
        localReader = RepositoryReaderFactory.createLocalRepositoryReader(fil);
        RepoPathElement root = new RepoPathElement(localReader);
        Collection cols = new ArrayList();
        Collection cols2 = new ArrayList();
        cols.add(root);
        cols2.add(fil.toURI());
        String proxyhost = ProxyUtilities.getProxyHost();
        String proxyport = ProxyUtilities.getProxyPort();
        String repos = resolver.getResolvedValue("maven.repo.remote"); //NOI18N
        IRepositoryReader reader;
        if (repos != null) {
            StringTokenizer tokens = new StringTokenizer(repos, ",");
            while (tokens.hasMoreTokens()) {
                URI uri = URI.create(tokens.nextToken());
                if (proxyport != null && proxyhost != null 
                        && proxyhost.trim().length() > 0 
                        && proxyport.trim().length() > 0 ) {
                    reader = RepositoryReaderFactory.createRemoteRepositoryReader(uri, proxyhost, proxyport);
                } else {
                    reader = RepositoryReaderFactory.createRemoteRepositoryReader(uri);
                }
                RepoPathElement remoteRoot = new RepoPathElement(reader);
                cols.add(remoteRoot);
                cols2.add(uri);
            }
        }
        
        roots = (RepoPathElement[])cols.toArray(new RepoPathElement[cols.size()]);
        rootUris = (URI[])cols2.toArray(new URI[cols2.size()]);;
    }
    
    private Node createSeparateRootNode() {
        Collection cols = new ArrayList();
        for (int i = 0; i < roots.length; i++) {
            cols.add(new RepositoryNode(roots[i], rootUris[i]));
        }
        Children.Array arr = new Children.Array();
        Node[] nds = new Node[cols.size()];
        arr.add((Node[])cols.toArray(nds));
        return new AbstractNode(arr);
    }
    
    private Node createCombinedRootNode() {
        Children.Array arr = new Children.Array();
        Node[] nds = new Node[1];
        nds[0] = new MultiRepositoryNode(new RepoPathGrouper(roots), "Multiple Repository Root");
        arr.add(nds);
        return new AbstractNode(arr);
    }
    
 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnLibrary;
    private javax.swing.JToggleButton btnMerge;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlDeps;
    // End of variables declaration//GEN-END:variables
    
    private void download(final Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            Lookup.Result res = (Lookup.Result)nodes[i].getLookup().lookup(new Lookup.Template(RepoPathElement.class));
            if (res != null) {
                boolean done = false;
                Iterator it = res.allInstances().iterator();
                RepoPathElement element = null;
                while (it.hasNext() && !done) {
                    element = (RepoPathElement)it.next();
                    try {
                        done = ProxyUtilities.downloadArtifact(finder, resolver, element);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        done = false;
                        StatusDisplayer.getDefault().setStatusText("Download failed: " + exc.getLocalizedMessage());
                    }
                }
                if (done) {
                    Node nd = nodes[i];
                    while (nd != null && nd instanceof LocalRepoRefresher) {
                        ((LocalRepoRefresher)nd).markAsDownloaded();
                        nd = nd.getParentNode();
                    }
                }
            }
        }
    }
    
//    private void createLibrary(final Node[] nodes) {
//        Collection elements = new ArrayList();
//        for (int i = 0; i < nodes.length; i++) {
//            Lookup.Result res = (Lookup.Result)nodes[i].getLookup().lookup(new Lookup.Template(RepoPathElement.class));
//            if (res != null) {
//                boolean done = false;
//                RepoPathElement local = null;
//                Iterator it = res.allInstances().iterator();
//                while (it.hasNext() && !done) {
//                    RepoPathElement element = (RepoPathElement)it.next();
//                    if (!"jar".equals(element.getType()) &&
//                        !"javadoc.jar".equals(element.getType()) &&
//                        !"javadoc.jar".equals(element.getType())) {
//                        continue;
//                    }
//                    System.out.println("downloading " + element.getArtifactId());
//                    if (!element.isRemote()) {
//                        if (element.isLeaf()) {
//                            elements.add(element);
//                        }
//                        done = true;
//                    } else {
//                        try {
//                            done = ProxyUtilities.downloadArtifact(finder, resolver, element, new BarDownloadMeter(element.getRelativeURIPath()));
//                            if (done && element.isLeaf()) {
//                                element = new RepoPathElement(localReader, 
//                                                              element.getGroupId(), 
//                                                              element.getType(), 
//                                                              element.getVersion(), 
//                                                              element.getArtifactId(), 
//                                                              element.getExtension());
//                                elements.add(element);
//                            }
//                        } catch (Exception exc) {
//                            exc.printStackTrace();
//                            done = false;
//                            StatusDisplayer.getDefault().setStatusText("Download failed: " + exc.getLocalizedMessage());
//                        }
//                    }
//                }
//            }
//        }
//        if (elements.size() > 0) {
//            Iterator it = elements.iterator();
//            LibraryImplementation library = LibrariesSupport.createLibraryImplementation("j2se", new String[] {"classpath", "javadoc"});
//            List jars = new ArrayList();
//            List javadoc = new ArrayList();
//            String name = "";
//            String desc = "";
//            while (it.hasNext()) {
//                RepoPathElement el = (RepoPathElement)it.next();
//                URI uri = el.getURI();
//                try {
//                    URL url = uri.toURL();
//                    if ("jar".equals(el.getType())) {
//                        jars.add(uri.toURL());
//                        name = name + el.getArtifactId();
//                    }
//                    if ("javadoc.jar".equals(el.getType()) || "javadoc".equals(el.getType())) {
//                        javadoc.add(uri.toURL());
//                    }
//                } catch (MalformedURLException exc) {
//                    exc.printStackTrace();
//                }
//            }
//            if (jars.size() > 0) {
//                library.setContent("classpath", jars);
//            }
//            if (javadoc.size() > 0) {
//                library.setContent("javadoc", javadoc);
//            }
//            library.setName(name.length() > 0 ? name : "LibraryXXX");
//            library.setDescription(library.getName());
//            StatusDisplayer.getDefault().setStatusText("Created library " + library.getName());
//        }
//    }    
    

}
