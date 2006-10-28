/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.mevenide.netbeans.graph.ModulesGraphTopComponent;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

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

    public Action[] getActions(boolean bool) {
        return new Action[] {
            new ShowGraphAction()  
        };
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
                return  new Node[] { new ProjectFilterNode(project, proj, prov.createLogicalView(), isPom) };
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
                        if (prj != null && prj.getLookup().lookup(NbMavenProject.class) != null) {
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
        private NbMavenProject project;
        private NbMavenProject parent;
        ProjectFilterNode(NbMavenProject parent, NbMavenProject proj, Node original, boolean isPom) {
            super(original, isPom ? new ModulesChildren(proj) : Children.LEAF);
//            disableDelegation(DELEGATE_GET_ACTIONS);
            project = proj;
            this.parent = parent;
        }

        public Action[] getActions(boolean b) {
            ArrayList lst = new ArrayList();
            lst.add(new OpenProjectAction(project));
            lst.add(new RemoveModuleAction(parent, project));
//            lst.addAll(Arrays.asList(super.getActions(b)));
            return (Action[])lst.toArray(new Action[lst.size()]);
        }
        
        
    }
    
    private static class RemoveModuleAction extends AbstractAction {
        private NbMavenProject project;
        private NbMavenProject parent;
        public RemoveModuleAction(NbMavenProject parent, NbMavenProject proj) {
            putValue(Action.NAME, "Remove Module");
            project = proj;
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("Do you want to remove the module from parent pom?", NotifyDescriptor.YES_NO_OPTION);
            Object ret = DialogDisplayer.getDefault().notify(nd);
            if (ret == NotifyDescriptor.YES_OPTION) {
                try {
                    Model model = EmbedderFactory.getProjectEmbedder().readModel(parent.getPOMFile());
                    Iterator it = model.getModules().iterator();
                    while (it.hasNext()) {
                        String path = (String) it.next();
                        File rel = new File(parent.getPOMFile().getParent(), path);
                        File norm = FileUtil.normalizeFile(rel);
                        FileObject folder = FileUtil.toFileObject(norm);
                        if (folder != null && folder.equals(project.getProjectDirectory())) {
                            it.remove();
                            break;
                        }
                    }
                    WriterUtils.writePomModel(FileUtil.toFileObject(parent.getPOMFile()), model);
                    parent.firePropertyChange(NbMavenProject.PROP_PROJECT);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                } catch (MavenEmbedderException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private static class OpenProjectAction extends AbstractAction {
        private NbMavenProject project;
        public OpenProjectAction(NbMavenProject proj) {
            putValue(Action.NAME, "Open Project");
            project = proj;
        }

        public void actionPerformed(ActionEvent e) {
            OpenProjects.getDefault().open(new Project[] {project}, false);
        }
    }
    
    
    private class ShowGraphAction extends AbstractAction {
        public ShowGraphAction() {
            putValue(Action.NAME, "Show Module Graph");
        }

        public void actionPerformed(ActionEvent e) {
            TopComponent tc = new ModulesGraphTopComponent(project);
            WindowManager.getDefault().findMode("editor").dockInto(tc);
            tc.open();
            tc.requestActive();
        }
    }
}
