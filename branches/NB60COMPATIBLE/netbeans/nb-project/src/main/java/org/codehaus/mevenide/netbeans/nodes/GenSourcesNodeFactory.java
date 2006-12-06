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
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class GenSourcesNodeFactory implements NodeFactory {
    
    /** Creates a new instance of GenSourcesNodeFactory */
    public GenSourcesNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProject prj = project.getLookup().lookup(NbMavenProject.class);
        return  new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<SourceGroup> implements PropertyChangeListener {
        private NbMavenProject project;
        private NList(NbMavenProject prj) {
            project = prj;
        }
        
        public List<SourceGroup> keys() {
            List<SourceGroup> list = new ArrayList<SourceGroup>();
            Sources srcs = project.getLookup().lookup(Sources.class);
            if (srcs == null) {
                throw new IllegalStateException("need Sources instance in lookup"); //NOI18N
            }
            SourceGroup[] gengroup = srcs.getSourceGroups(MavenSourcesImpl.TYPE_GEN_SOURCES);
            for (int i = 0; i < gengroup.length; i++) {
                list.add(gengroup[i]);
            }
            return list;
        }
        
        public Node node(SourceGroup group) {
            return PackageView.createPackageView(group);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
        }
        
        public void addNotify() {
            ProjectURLWatcher.addPropertyChangeListener(project, this);
        }
        
        public void removeNotify() {
            ProjectURLWatcher.removePropertyChangeListener(project, this);
        }
    }
}
