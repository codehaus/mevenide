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

package org.netbeans.modules.maven.nodes;
import org.netbeans.modules.maven.spi.nodes.AbstractMavenNodeList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class ModulesNodeFactory implements NodeFactory {
    
    private static final String KEY_MODULES = "modules"; //NOI18N
    
    /** Creates a new instance of ModulesNodeFactory */
    public ModulesNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProjectImpl prj = project.getLookup().lookup(NbMavenProjectImpl.class);
        return new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener {
        private NbMavenProjectImpl project;
        NList(NbMavenProjectImpl prj) {
            project = prj;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
        }
        
        public List<String> keys() {
            if ("pom".equals(project.getOriginalMavenProject().getPackaging())) { //NOI18N
                return Collections.singletonList(KEY_MODULES);
            }
            return Collections.emptyList();
        }
        
        public Node node(String key) {
            return  new ModulesNode(project);
        }
        
        @Override
        public void addNotify() {
            NbMavenProject.addPropertyChangeListener(project, this);
        }
        
        @Override
        public void removeNotify() {
            NbMavenProject.removePropertyChangeListener(project, this);
        }
    }
}
