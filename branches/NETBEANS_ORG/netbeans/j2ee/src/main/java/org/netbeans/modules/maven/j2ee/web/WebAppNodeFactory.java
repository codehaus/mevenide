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

package org.netbeans.modules.maven.j2ee.web;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.netbeans.maven.api.NbMavenProject;
import org.netbeans.maven.spi.nodes.AbstractMavenNodeList;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class WebAppNodeFactory implements NodeFactory {
    private static final String KEY_WEBAPP = "webapp"; //NOI18N
    
    /** Creates a new instance of SiteDocsNodeFactory */
    public WebAppNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        Project prj = project.getLookup().lookup(Project.class);
        return new NList(prj);
    }
    
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener{
        private Project project;
        private NbMavenProject mavenproject;
        private String currentWebAppKey;
        
        private NList(Project prj) {
            project = prj;
            mavenproject = project.getLookup().lookup(NbMavenProject.class);
        }
        
        public List<String> keys() {
            URI webapp = mavenproject.getWebAppDirectory();
            if (webapp != null) {
                currentWebAppKey = KEY_WEBAPP + webapp.toString();
                return Collections.singletonList(currentWebAppKey);
            }
            return Collections.emptyList();
        }
        
        public Node node(String key) {
            return createWebAppNode();
        }
        
        private Node createWebAppNode() {
            Node n =  null;
            try {
                FileObject fo = URLMapper.findFileObject(mavenproject.getWebAppDirectory().toURL());
                if (fo != null) {
                    DataFolder fold = DataFolder.findFolder(fo);
                    File fil = FileUtil.toFile(fo);
                    if (fold != null) {
                        n = new WebAppFilterNode(project, fold.getNodeDelegate().cloneNode(), fil);
                    }
                }
            } catch (MalformedURLException exc) {
                n = null;
            }
            return n;
        }
        
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
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
