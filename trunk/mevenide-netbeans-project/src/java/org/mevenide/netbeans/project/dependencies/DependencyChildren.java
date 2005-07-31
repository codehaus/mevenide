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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.project.Dependency;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.netbeans.project.writer.NbProjectWriter;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * node children for a long living dependency node.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyChildren extends Children.Keys {
    private static final String KEY_VERSIONS = "Versions"; //NOI18N
    private static final String KEY_SOURCES = "Sources"; //NOI18N
    private static final String KEY_CLASSES = "Classes"; //NOI18N
    private static final String KEY_JAVADOC = "Javadoc"; //NOI18N
    private MavenProject project;
    private DependencyPOMChange change;
    private DependencyNode parentNode;
    
    /** Creates a new instance of DependencyChildren 
     * @param lookup - expects instance of MavenProject, DependencyPOMChange
     */
    public DependencyChildren(Lookup lookup) {
        super();
        project = (MavenProject)lookup.lookup(MavenProject.class);
        change = (DependencyPOMChange)lookup.lookup(DependencyPOMChange.class);
    }

    protected org.openide.nodes.Node[] createNodes(Object obj) {
        Node node = null;
        if (obj == KEY_VERSIONS) {
            node = createVersionsNode();
        }
        if (obj == KEY_SOURCES) {
            node = createSourcesNode();
        }
        if (obj == KEY_CLASSES) {
            node = createClassesNode();
        }
        if (obj == KEY_JAVADOC) {
            node = createJavadocNode();
        }
        if (node == null) {
            return new Node[0];
        }
        return new Node[] { node };
    }

    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.EMPTY_LIST);
    }

    protected void addNotify() {
        parentNode = (DependencyNode)getNode();
        doRefresh();
        super.addNotify();
    }
    
    /**
     * called from parent DependencyNode on events from mavenproject or mavenfileownerquery
     */
    void doRefresh() {
        if (parentNode == null) {
            return;
        }
        Collection nodes = new ArrayList();
        nodes.add(KEY_VERSIONS);
        if (!parentNode.isDependencyProjectOpen()) {
            if (parentNode.hasSourceInRepository()) {
                nodes.add(KEY_SOURCES);
            } else {
                nodes.add(KEY_CLASSES);
            }
            if (parentNode.hasJavadocInRepository()) {
                nodes.add(KEY_JAVADOC);
            }
        }
        setKeys(nodes);
    }
    
    private Node createVersionsNode() {
        IRepositoryReader[] readers = RepositoryUtilities.createRemoteReaders(project.getPropertyResolver());
        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent());
        RepoPathElement[] els = new RepoPathElement[readers.length + 1];
        String grId = dep.getGroupId() != null ? dep.getGroupId() : dep.getId();
        String artId = dep.getArtifactId() != null ? dep.getArtifactId() : dep.getId();
        String type = dep.getType() == null ? "jar" : dep.getType();
        String ext = dep.getExtension();
        for (int i = 0; i < els.length; i++) {
            IRepositoryReader read = (i == 0  
                    ? RepositoryUtilities.createLocalReader(project.getLocFinder())  
                    : readers[i - 1]);
            els[i] = new RepoPathElement(read, null, grId, type, null, artId, ext);
        }
        RepoPathGrouper gr = new RepoPathGrouper(els);
        MultiRepositoryNode nd = new MultiRepositoryNode(gr);
        AbstractNode ret = new AbstractNode(new VersionsChildren(nd));
        ret.setName("versions");
        ret.setDisplayName("Available Versions");
        ret.setIconBase("org/mevenide/netbeans/project/resources/Versions");

        return ret;
    }
    
    private Node createSourcesNode() {
        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent());
        dep.setType("src.jar");
        URI uri = FileUtilities.getDependencyURI(dep,  project);
        FileObject obj = FileUtilities.convertURItoFileObject(uri);
        if (obj != null) {
            try {
                DataObject dobj = DataObject.find(obj);
                Node original =  dobj.getNodeDelegate();
                AbstractNode src = new AbstractNode(new FilterNode.Children(original));
                src.setName("sources");
                src.setDisplayName("Browse Sources");
                return src;
            } catch (DataObjectNotFoundException exc) {
                ErrorManager.getDefault().notify(exc);
                return null;
            }
        }
        return null;
    }
    
    private Node createJavadocNode() {
        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent());
        dep.setType("javadoc.jar");
        URI uri = FileUtilities.getDependencyURI(dep,  project);
        FileObject obj = FileUtilities.convertURItoFileObject(uri);
        if (obj == null) {
            // let's try the old way of "javadoc" extension.
            // kind of weird anyway because the javadoc extension is not recognized by jardataloader
            dep.setType("javadoc");
            uri = FileUtilities.getDependencyURI(dep,  project);
            obj = FileUtilities.convertURItoFileObject(uri);
        }
        if (obj != null) {
            try {
                DataObject dobj = DataObject.find(obj);
                Node original =  dobj.getNodeDelegate();
                AbstractNode src = new AbstractNode(new FilterNode.Children(original));
                src.setName("javadoc");
                src.setDisplayName("Browse Javadoc");
                return src;
            } catch (DataObjectNotFoundException exc) {
                ErrorManager.getDefault().notify(exc);
                return null;
            }
        } 
        return null;
    }
    
    private Node createClassesNode() {
        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent());
        URI uri = FileUtilities.getDependencyURI(dep,  project);
        FileObject obj = FileUtilities.convertURItoFileObject(uri);
        if (obj != null) {
            try {
                DataObject dobj = DataObject.find(obj);
                Node original =  dobj.getNodeDelegate();
                AbstractNode src = new AbstractNode(new FilterNode.Children(original));
                src.setName("content");
                src.setDisplayName("Browse Content");
                return src;
            } catch (DataObjectNotFoundException exc) {
                ErrorManager.getDefault().notify(exc);
                return null;
            }
        }
        return null;
    }
    
    private class VersionsChildren extends FilterNode.Children {
        VersionsChildren(Node original) {
            super(original);
        }

        protected Node[] createNodes(Object key) {
            if (key instanceof Node) {
                return new Node[] { new OneVersionNode((Node)key) };
            }
            return super.createNodes(key);
        }
        
        
    }
    
    private class OneVersionNode extends FilterNode {
        public OneVersionNode(Node original) {
            super(original);
        }

        public Action[] getActions(boolean context) {
            Action[] parent;
            parent = super.getActions(context);
            Action[] toRet = new Action[parent.length + 1];
            toRet[0] = new SetAsDependencyAction(getLookup());
            for (int i = 1; i < toRet.length; i++) {
                toRet[i] = parent[i -1];
            }
            return toRet;
        }

    }
    
    private class SetAsDependencyAction extends AbstractAction {
        private Lookup lookup;
        public SetAsDependencyAction(Lookup look) {
            super();
            lookup = look;
            putValue(Action.NAME, "Set as Dependency");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            Lookup.Result res = lookup.lookup(new Lookup.Template(RepoPathElement.class));
            Collection col = res.allInstances();
            RepoPathElement element = (RepoPathElement)col.iterator().next();
            HashMap newValues = change.getOldValues();
            newValues.put("version", element.getVersion());
            change.setNewValues(newValues, change.getOldProperties());
            try {
                NbProjectWriter writer = new NbProjectWriter(project);
                List deps = (List)DependencyChildren.this.getNode().getLookup().lookup(List.class);
                writer.applyChanges(deps);
            } catch (Exception exc) {
                ErrorManager.getDefault().notify(ErrorManager.USER, exc);
            }
            
        }
        
    }
}
