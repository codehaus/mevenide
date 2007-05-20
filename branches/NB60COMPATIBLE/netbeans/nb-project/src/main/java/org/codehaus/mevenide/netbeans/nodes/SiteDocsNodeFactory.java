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

package org.codehaus.mevenide.netbeans.nodes;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
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
        NbMavenProject prj = project.getLookup().lookup(NbMavenProject.class);
        return new NList(prj);
    }
    
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener {
        private NbMavenProject project;
        
        private NList(NbMavenProject prj) {
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
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
            if (NbMavenProject.PROP_RESOURCE.equals(evt.getPropertyName()) &&
                    SITE.equals(evt.getNewValue())) {
                fireChange();
            }
        }
        
        public void addNotify() {
            ProjectURLWatcher.addPropertyChangeListener(project, this);
            ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
            watcher.addWatchedPath(SITE);
        }
        
        public void removeNotify() {
            ProjectURLWatcher.removePropertyChangeListener(project, this);
            ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
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
