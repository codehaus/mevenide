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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.netbeans.api.customizer.LocationComboFactory;
import org.mevenide.netbeans.api.customizer.OriginChange;
import org.mevenide.netbeans.project.queries.MavenFileOwnerQueryImpl;
import org.mevenide.netbeans.project.writer.NbProjectWriter;
import org.mevenide.repository.RepoPathElement;
import org.netbeans.api.project.ProjectInformation;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.Presenter;
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
    
    public Action[] getActions(boolean context) {
        if (element.isLeaf()) {
            Action[] retValue = new Action[3];
            retValue[0] = new AddAsDependencyAction();
            retValue[1] = null;
            retValue[2] = ((PropertiesAction)PropertiesAction.get(PropertiesAction.class)).createContextAwareInstance(Lookups.singleton(this));
            return retValue;
        }
        return new Action[0];
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
    
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set basicProps = sheet.get(Sheet.PROPERTIES);
        try {
            PropertySupport.Reflection artifactId = new PropertySupport.Reflection(element, String.class, "getArtifactId", null);
            artifactId.setName("artifactId");
            artifactId.setDisplayName("Artifact Id");
            artifactId.setShortDescription("");
            PropertySupport.Reflection groupId = new PropertySupport.Reflection(element, String.class, "getGroupId", null);
            groupId.setName("groupId");
            groupId.setDisplayName("Group Id");
            groupId.setShortDescription("");
            PropertySupport.Reflection version = new PropertySupport.Reflection(element, String.class, "getVersion", null);
            version.setName("version");
            version.setDisplayName("Version");
            version.setShortDescription("");
            PropertySupport.Reflection type = new PropertySupport.Reflection(element, String.class, "getType", null);
            type.setName("type");
            type.setDisplayName("Type");
            type.setShortDescription("");
            
            
            basicProps.put(new Node.Property[] {
                artifactId, groupId, version, type
            });
        } catch (NoSuchMethodException exc) {
            exc.printStackTrace();
        }
        return sheet;
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
    
    private class AddAsDependencyAction extends AbstractAction implements Presenter.Popup {
        private MavenProject project;
        
        public AddAsDependencyAction() {
        }
        
        public AddAsDependencyAction(MavenProject proj) {
            project = proj;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            Project[] projs = project.getContext().getPOMContext().getProjectLayers();
            ArrayList deps = new ArrayList();
            DependencyPOMChange change = null;
            for (int i = 0; i < projs.length; i++) {
                List ones = projs[i].getDependencies();
                if (ones != null) {
                    Iterator it = ones.iterator();
                    while (it.hasNext()) {
                        Dependency dep = (Dependency)it.next();
                        DependencyPOMChange chng = DependencyPOMChange.createChangeInstance(dep, i, new HashMap(),
                                LocationComboFactory.createPOMChange(project, false), false);
                            deps.add(chng);
                        if (((element.getArtifactId().equals(dep.getArtifactId()) && 
                             element.getGroupId().equals(dep.getGroupId()))
                             || (element.getArtifactId().equals(element.getGroupId()) && 
                                 element.getArtifactId().equals(dep.getId()))
                             ) && (element.getType().equals(dep.getType()) || (element.getType().equals("jar") && dep.getType() == null))) {
                            change = chng;
                        }
                    }
                }
            }
            if (change == null) {
                change = DependencyPOMChange.createChangeInstance(null, OriginChange.LOCATION_POM, new HashMap(), LocationComboFactory.createPOMChange(project, false), false);
                deps.add(change);
            } else {
                NotifyDescriptor dd = new NotifyDescriptor.Confirmation(
                        "The project already has a dependency with '" + element.getGroupId() + ":" + element.getArtifactId() + "' id. Replace it?",
                        "Dependency conflict", 
                        NotifyDescriptor.YES_NO_OPTION,
                        NotifyDescriptor.QUESTION_MESSAGE);
                Object ret = DialogDisplayer.getDefault().notify(dd);
                if (ret != NotifyDescriptor.YES_OPTION) {
                    return;
                }
            }
            HashMap newValues = new HashMap();
            newValues.put("artifactId", element.getArtifactId());
            newValues.put("groupId", element.getGroupId());
            newValues.put("version", element.getVersion());
            newValues.put("type", element.getType());
            change.setNewValues(newValues, new HashMap());
            try {
                NbProjectWriter writer = new NbProjectWriter(project);
                writer.applyChanges(deps);
            } catch (Exception exc) {
                ErrorManager.getDefault().notify(ErrorManager.USER, exc);
            }
            
        }
        
        public JMenuItem getPopupPresenter() {
            JMenu menu = new JMenu();
            if (project == null) {
                menu.setText("Add as dependency to");
                Set projs = MavenFileOwnerQueryImpl.getInstance().getOpenedProjects();
                if (projs.size() == 0) {
                    menu.setEnabled(false);
                } else {
                    Iterator it = projs.iterator();
                    while (it.hasNext()) {
                        MavenProject prj = (MavenProject)it.next();
                        ProjectInformation info = (ProjectInformation)prj.getLookup().lookup(ProjectInformation.class);
                        JMenuItem item = new JMenuItem(new AddAsDependencyAction(prj));
                        item.setText(info.getDisplayName());
                        item.setIcon(info.getIcon());
                        menu.add(item);
                    }
                }
            }
            return menu;
        }
    }
    
}

