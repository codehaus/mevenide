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

package org.netbeans.maven.nodes;
import org.netbeans.maven.spi.nodes.AbstractMavenNodeList;
import org.netbeans.maven.api.NbMavenProject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import org.netbeans.maven.NbMavenProjectImpl;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class SiteDocsNodeFactory implements NodeFactory {
    private static final String KEY_SITE = "SITE"; //NOI18N
    private static final String SITE = "src/site"; //NOI18N
    
    /** Creates a new instance of SiteDocsNodeFactory */
    public SiteDocsNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProjectImpl prj = project.getLookup().lookup(NbMavenProjectImpl.class);
        return new NList(prj);
    }
    
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener {
        private NbMavenProjectImpl project;
        
        private NList(NbMavenProjectImpl prj) {
            project = prj;
        }
        
        public List<String> keys() {
            //TODO handle custom locations of sit docs
            if (project.getProjectDirectory().getFileObject(SITE) != null) {
                return Collections.singletonList(KEY_SITE);
            }
            return Collections.emptyList();
        }
        
        public Node node(String key) {
            return createSiteDocsNode();
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
            if (NbMavenProjectImpl.PROP_RESOURCE.equals(evt.getPropertyName()) &&
                    SITE.equals(evt.getNewValue())) {
                fireChange();
            }
        }
        
        @Override
        public void addNotify() {
            NbMavenProject.addPropertyChangeListener(project, this);
            NbMavenProject watcher = project.getLookup().lookup(NbMavenProject.class);
            watcher.addWatchedPath(SITE);
        }
        
        @Override
        public void removeNotify() {
            NbMavenProject.removePropertyChangeListener(project, this);
            NbMavenProject watcher = project.getLookup().lookup(NbMavenProject.class);
            watcher.removeWatchedPath(SITE);
        }
        
        private Node createSiteDocsNode() {
            Node n =  null;
            //TODO handle custom locations of sit docs
            FileObject fo = project.getProjectDirectory().getFileObject(SITE);
            if (fo != null) {
                DataFolder fold = DataFolder.findFolder(fo);
                if (fold != null) {
                    n = new SiteDocsNode(project, fold.getNodeDelegate().cloneNode());
                }
            }
            return n;
        }
        
        
    }
}
