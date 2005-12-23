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

package org.codehaus.mevenide.netbeans.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 * display the modules for pom packaged project
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ModulesNode extends AbstractNode {
    
    private NbMavenProject project;
    
    /** Creates a new instance of ModulesNode */
    public ModulesNode(NbMavenProject proj) {
        super(new ModulesChildren(proj));
        project = proj;
        setName("Modules");//NOI18N
        setDisplayName("Modules");
    }
    
    static class ModulesChildren extends Children.Keys {
        
        private NbMavenProject project;
        
        ModulesChildren(NbMavenProject proj) {
            project = proj;
            project.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    loadModules();
                }
            });
        }
        
        public void addNotify() {
            loadModules();
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object object) {
            if (object instanceof NbMavenProject) {
                NbMavenProject proj = (NbMavenProject)object;
                boolean isPom = "pom".equals(proj.getOriginalMavenProject().getPackaging());
                LogicalViewProvider prov = (LogicalViewProvider) proj.getLookup().lookup(LogicalViewProvider.class);
                return  new Node[] { new ProjectFilterNode(proj, prov.createLogicalView(), isPom) };
            }
            return new Node[0];
        }

        private void loadModules() {
            Collection modules = new ArrayList();
            File base = project.getOriginalMavenProject().getBasedir();
            for (Iterator it = project.getOriginalMavenProject().getModules().iterator(); it.hasNext();) {
                String elem = (String) it.next();
                File projDir = FileUtil.normalizeFile(new File(base, elem));
                FileObject fo = FileUtil.toFileObject(projDir);
                if (fo != null) {
                    try {
                        Project prj = ProjectManager.getDefault().findProject(fo);
                        if (prj instanceof NbMavenProject) {
                            modules.add(prj);
                        }
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    //TODO broken module reference.. show as such..
                }
            }
            setKeys(modules);
        }
    }
    
    private static class ProjectFilterNode extends FilterNode {
        ProjectFilterNode(NbMavenProject proj, Node original, boolean isPom) {
            super(original, isPom ? new ModulesChildren(proj) : Children.LEAF);
            disableDelegation(DELEGATE_GET_ACTIONS);
        }
    }
    
}
