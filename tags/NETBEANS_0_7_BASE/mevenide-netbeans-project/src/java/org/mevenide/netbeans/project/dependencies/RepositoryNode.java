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
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import org.mevenide.repository.RepoPathElement;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 * node representing a repository artifact
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RepositoryNode extends AbstractNode implements LocalRepoRefresher {
    private final static Object LOADING = new Object();
    
    private RepoPathElement element;
    private URI root;
    
    public RepositoryNode(RepoPathElement el) {
        super(el.isLeaf() ? Children.LEAF : new RepositoryChildren(el),
                Lookups.singleton(el));
        element = el;
        if (element.getLevel() == RepoPathElement.LEVEL_GROUP || element.getLevel() == RepoPathElement.LEVEL_TYPE) {
            setIconBase("org/mevenide/netbeans/project/resources/defaultFolder"); //NOI18N
        } else if (element.getLevel() == RepoPathElement.LEVEL_ARTIFACT) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        } else if (element.getLevel() == RepoPathElement.LEVEL_VERSION) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        } else {
            setIconBase("org/mevenide/netbeans/project/resources/RepositoryRoot"); //NOI18N
        }
//        setDisplayName(createName());
    }
    
    /**
     * constructor for use when creating the root node.
     */ 
    public RepositoryNode(RepoPathElement el, URI rootUri) {
        this(el);
        root = rootUri;
//        setDisplayName(createName());
    }
    
    public String getDisplayName() {
        return createName();
    }
    
    public int getRepoLevel() {
        return element.getLevel();
    }
    
    public String getHtmlDisplayName() {
        if (element.isRemote()) {
            return "<html><b><font color='#9f9a93'>" + getDisplayName() + "</font></b></html>";
        }
        return super.getHtmlDisplayName();
    }
    
    
    private String createName() {
        switch (element.getLevel()) {
            case RepoPathElement.LEVEL_ROOT : 
                String str = root == null ? "" : root.toString();
                if (str.startsWith("http://")) {
                    return "Remote at " + str;
                }
                return "Local at " + str;
            case RepoPathElement.LEVEL_GROUP : 
                return element.getGroupId();
            case RepoPathElement.LEVEL_TYPE : 
                return element.getType();
            case RepoPathElement.LEVEL_ARTIFACT :
                return element.getArtifactId();
            case RepoPathElement.LEVEL_VERSION :
                Node parent = getParentNode();
                if (parent != null && parent instanceof RepositoryNode) {
                    int level = ((RepositoryNode)parent).getRepoLevel();
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
        //what do to?
//        
//        if (element.isRemote() || isLeaf()) {
//            return;
//        }
//        ((RepositoryChildren)getChildren()).addChild(el);
        
    }
    
    public static RepoPathElement generateSameLevelElement(RepoPathElement element, 
                                                           RepoPathElement el) {
        String group = null;
        String type = null;
        String artifact = null;
        String version = null;
        String ext = null;
        if (element.getLevel() == RepoPathElement.LEVEL_VERSION) {
            type = el.getType();
            artifact = el.getArtifactId();
            version = el.getVersion();
            ext = el.getExtension();
            group = el.getGroupId();
        } else if (element.getLevel() == RepoPathElement.LEVEL_ARTIFACT) {
            type = el.getType();
            artifact = el.getArtifactId();
            group = el.getGroupId();
        } else if (element.getLevel() == RepoPathElement.LEVEL_TYPE) {
            type = el.getType();
            group = el.getGroupId();
        } else if (element.getLevel() == RepoPathElement.LEVEL_GROUP) {
            group = el.getGroupId();
        }
        return new RepoPathElement(element.getReader(), null, group, type, artifact, version, ext);
    }
    
    private static class RepositoryChildren extends Children.Keys {
        private RepoPathElement element;
        private Collection keys;
        RepositoryChildren(RepoPathElement el) {
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
            if (obj instanceof RepoPathElement) {
                return new Node[] { new RepositoryNode((RepoPathElement)obj)};
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
            runLoad();
            super.addNotify();
        }
        
        private void runLoad() {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        RepoPathElement[] els = element.getChildren();
                        Collection col = new TreeSet(RepoElementComparator.getInstance());
                        if (element.getLevel() == RepoPathElement.LEVEL_TYPE) {
                            for (int i = 0; i < els.length; i++) {
                                RepoPathElement[] childs = els[i].getChildren();
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
                        exc.printStackTrace(System.err);
                        String loc = exc.getLocalizedMessage();
                        if (loc != null) {
                            setKeys(Collections.singleton(loc));
                        } else {
                            setKeys(Collections.EMPTY_SET);
                        }
                    }
                }
            });
        }
        
//        public void refreshElements() {
//            element.refreshChildren();
//            runLoad();
//        }
        
//        public void addChild(RepoPathElement newOne) {
//            if (isInitialized()) {
//                keys.add(generateSameLevelElement(element, newOne));
//                setKeys(keys);
//            }
//        }
    }

}
