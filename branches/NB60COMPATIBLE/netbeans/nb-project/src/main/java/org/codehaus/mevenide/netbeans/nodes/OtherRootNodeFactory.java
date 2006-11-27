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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class OtherRootNodeFactory implements NodeFactory {
    
    private static final String KEY_OTHER = "otherRoots"; //NOI18N
    private static final String KEY_OTHER_TEST = "otherTestRoots"; //NOI18N
    
    /** Creates a new instance of DependenciesNodeFactory */
    public OtherRootNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProject prj = project.getLookup().lookup(NbMavenProject.class);
        return new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener {
        private NbMavenProject project;
        NList(NbMavenProject prj) {
            project = prj;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
        }
        
        public List<String> keys() {
            List<String> list = new ArrayList<String>();
            if (project.getOtherRoots(false).length > 0) {
                list.add(KEY_OTHER);
            }
            if (project.getOtherRoots(true).length > 0) {
                list.add(KEY_OTHER_TEST);
            }
            return list;
        }
        
        public Node node(String key) {
            if (key == KEY_OTHER) {
                return new OthersRootNode(project, false);
            } else if (key == KEY_OTHER_TEST) {
                return new OthersRootNode(project, true);
            }
            assert false: "Wrong key for Dependencies NodeFactory: " + key;
            return null;
        }
        
        public void addNotify() {
            ProjectURLWatcher.addPropertyChangeListener(project, this);
        }
        
        public void removeNotify() {
            ProjectURLWatcher.removePropertyChangeListener(project, this);
        }
    }
}
