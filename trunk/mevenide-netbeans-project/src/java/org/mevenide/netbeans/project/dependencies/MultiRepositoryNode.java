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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import org.mevenide.repository.RepoPathElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 * node representing a repository artifact
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MultiRepositoryNode extends AbstractNode implements LocalRepoRefresher {
    private static final Object LOADING = new Object();
    
    private RepoPathGrouper element;
    private String label;
    private boolean downloaded = false;
    
    public MultiRepositoryNode(RepoPathGrouper el) {
        super(el.isLeaf() ? Children.LEAF : new RepositoryChildren(el),
              Lookups.proxy(new Provider(el)));
        element = el;
        if (element.getLevel() == RepoPathElement.LEVEL_GROUP || element.getLevel() == RepoPathElement.LEVEL_TYPE) {
            setIconBase("org/mevenide/netbeans/project/resources/defaultFolder"); //NOI18N
        } else if (element.getLevel() == RepoPathElement.LEVEL_ARTIFACT) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        } else if (element.getLevel() == RepoPathElement.LEVEL_VERSION) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        } else if (element.getLevel() == RepoPathElement.LEVEL_ROOT) {
            setIconBase("org/mevenide/netbeans/project/resources/RepositoryRoot"); //NOI18N
        } else {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        }
        if (element.isRemote() && element.isLocal()) {
            setShortDescription("Defined in both remote and local repositories.");
        }
        else if (element.isLocal()) {
            setShortDescription("Defined in local repository.");
        } else if (element.isRemote()) {
            setShortDescription("Defined in remote repository only.");
        }
        
    }
    
    /**
     * constructor for use when creating the root node.
     */ 
    public MultiRepositoryNode(RepoPathGrouper el, String rootLabel) {
        this(el);
        label = rootLabel;
    }
    
    
    public String getDisplayName() {
        return createName();
    }
    
    public String getHtmlDisplayName() {
        if ((element.isLocal() || downloaded) == element.isRemote()) {
            return "<html><b><font color='#8d8ba7'>" + getDisplayName() + "</font></b></html>";
        }
        if (element.isLocal()) {
            return super.getHtmlDisplayName();
        }
        if (element.isRemote()) {
            return "<html><b><font color='#9f9a93'>" + getDisplayName() + "</font></b></html>";
        }
        throw new IllegalStateException();
    }
    
    public int getRepoLevel() {
        return element.getLevel();
    }
    
    private String createName() {
        switch (element.getLevel()) {
            case RepoPathElement.LEVEL_ROOT : 
                String str = label == null ? "" : label;
                return str;
            case RepoPathElement.LEVEL_GROUP : 
                return element.getGroupId();
            case RepoPathElement.LEVEL_TYPE : 
                return element.getType();
            case RepoPathElement.LEVEL_ARTIFACT :
                return element.getArtifactId();
            case RepoPathElement.LEVEL_VERSION :
                Node parent = getParentNode();
                if (parent != null && parent instanceof MultiRepositoryNode) {
                    int level = ((MultiRepositoryNode)parent).getRepoLevel();
                    if (level == RepoPathElement.LEVEL_ARTIFACT) {
                       return element.getVersion(); 
                    }
                } 
                return element.getArtifactId() + "  (" + element.getVersion() + ")";
        }
        throw new IllegalStateException();
    }
    
    
    public boolean canDestroy() {
        return false;
    }
    
    public boolean canRename() {
        return false;
    }
    
    
    public boolean canCut() {
        return false;
    }
    
    public boolean hasCustomizer() {
        return false;
    }
    
    public boolean canCopy() {
        return false;
    }
    
    public void markAsDownloaded() {
        downloaded = true;
        //TODO for now just pretend..
        fireIconChange();
        fireDisplayNameChange(null, getDisplayName());
    }
    
    private static class RepositoryChildren extends Children.Keys {
        private RepoPathGrouper element;
        private Collection keys;
        RepositoryChildren(RepoPathGrouper el) {
            element = el;
            keys = Collections.EMPTY_LIST;
        }
        protected Node[] createNodes(Object obj) {
            if (obj == LOADING) {
                AbstractNode nd = new AbstractNode(Children.LEAF);
                nd.setName("Loading");
                nd.setDisplayName("Loading...");
                return new Node[] {nd};
            }
            if (obj instanceof String) {
                AbstractNode nd = new AbstractNode(Children.LEAF);
                nd.setName("Error");
                nd.setDisplayName((String)obj);
                return new Node[] {nd};
            }
            if (obj instanceof RepoPathGrouper) {
                return new Node[] { new MultiRepositoryNode((RepoPathGrouper)obj)};
            }
            System.out.println("wrong object..");
            return new Node[0];
        }

        protected void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
            super.removeNotify();
        }

        protected void addNotify() {
            setKeys(Collections.singleton(LOADING));
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        RepoPathGrouper[] els = element.getChildren();
                        Collection col = new TreeSet(RepoGrouperComparator.getInstance());
                        if (element.getLevel() == RepoPathElement.LEVEL_TYPE) {
                            for (int i = 0; i < els.length; i++) {
                                RepoPathGrouper[] childs = els[i].getChildren();
                                if (childs.length == 1) {
                                    col.add(childs[0]);
                                } else {
                                    col.add(els[i]);
                                }
                            }
                        } else {
                            col.addAll(Arrays.asList(els));
                        }
                        keys = col;
                        setKeys(col);
                    } catch (Exception exc) {
                        //TODO
                        exc.printStackTrace();
                        setKeys(Collections.singleton(exc.getLocalizedMessage()));
                    }
                }
            });
            super.addNotify();
        }
        
//        public void addChild(RepoPathElement element) {
//            if (isInitialized()) {
//                
//            }
//        }
    }

    private static class Provider implements Lookup.Provider {
        private RepoPathGrouper gr;
        private Lookup look;
        public Provider(RepoPathGrouper grouper) {
            gr = grouper;
            RepoPathElement[] els = gr.getElements();
            look = Lookups.fixed(els);
        }
        public Lookup getLookup() {
            return look;
        }
        
    }
}

