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
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class DependenciesNodeFactory implements NodeFactory {
    
    private static final String KEY_DEPENDENCIES = "dependencies"; //NOI18N
    private static final String KEY_TEST_DEPENDENCIES = "dependencies2"; //NOI18N
    private static final String KEY_RUNTIME_DEPENDENCIES = "dependencies3"; //NOI18N
    
    /** Creates a new instance of DependenciesNodeFactory */
    public DependenciesNodeFactory() {
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
            list.add(KEY_DEPENDENCIES);
            MavenProject orig = project.getOriginalMavenProject();
            List runtimes = new ArrayList(orig.getRuntimeArtifacts());
            runtimes.removeAll(orig.getCompileArtifacts());
            if (runtimes.size() > 0) {
                list.add(KEY_RUNTIME_DEPENDENCIES);
            }
            List tests = new ArrayList(orig.getTestArtifacts());
            tests.removeAll(orig.getRuntimeArtifacts());
            if (tests.size() > 0) {
                list.add(KEY_TEST_DEPENDENCIES);
            }
            return list;
        }
        
        public Node node(String key) {
            if (key == KEY_DEPENDENCIES) {
                return  new DependenciesNode(project, DependenciesNode.TYPE_COMPILE);
            } else if (key == KEY_TEST_DEPENDENCIES) {
                return  new DependenciesNode(project, DependenciesNode.TYPE_TEST);
            } else if (key == KEY_RUNTIME_DEPENDENCIES) {
                return  new DependenciesNode(project, DependenciesNode.TYPE_RUNTIME);
            }
            assert false: "Wrong key for Dependencies NodeFactory: " + key;
            return null;
        }
        
        public void addNotify() {
            project.addPropertyChangeListener(this);
            
        }
        
        public void removeNotify() {
            project.removePropertyChangeListener(this);
        }
    }
}
