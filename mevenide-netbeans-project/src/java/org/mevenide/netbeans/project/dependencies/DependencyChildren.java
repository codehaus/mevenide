/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.maven.project.Dependency;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
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
            IRepositoryReader read = (i == 0 ? 
                    RepositoryUtilities.createLocalReader(project.getLocFinder()) : 
                    readers[i - 1]);
            els[i] = new RepoPathElement(read, null, grId, type, null, artId, ext);
        }
        RepoPathGrouper gr = new RepoPathGrouper(els);
        MultiRepositoryNode nd = new MultiRepositoryNode(gr);
        return new VersionsNode(nd);
    }
    
    private class VersionsNode extends AbstractNode {
        VersionsNode(Node node) {
            super(new VersionsChildren(node));
            setName("versions");
            setDisplayName("Other Available Versions");
        }
    }
    
    private class VersionsChildren extends FilterNode.Children {
        VersionsChildren(Node original) {
            super(original);
        }
    }
}
 