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
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
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
public class SourcesNodeFactory implements NodeFactory {
    
    /** Creates a new instance of SourcesNodeFactory */
    public SourcesNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProject prj = project.getLookup().lookup(NbMavenProject.class);
        return  new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<SourceGroup> implements ChangeListener {
        private NbMavenProject project;
        private NList(NbMavenProject prj) {
            project = prj;
        }
        
        public List<SourceGroup> keys() {
            List<SourceGroup> list = new ArrayList<SourceGroup>();
            Sources srcs = ProjectUtils.getSources(project);
            SourceGroup[] javagroup = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (int i = 0; i < javagroup.length; i++) {
                    list.add(javagroup[i]);
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
            Sources srcs = ProjectUtils.getSources(project);
            srcs.addChangeListener(this);
        }
        
        public void removeNotify() {
            Sources srcs = ProjectUtils.getSources(project);
            srcs.removeChangeListener(this);
        }

        public void stateChanged(ChangeEvent arg0) {
            fireChange();
        }
    }
}
