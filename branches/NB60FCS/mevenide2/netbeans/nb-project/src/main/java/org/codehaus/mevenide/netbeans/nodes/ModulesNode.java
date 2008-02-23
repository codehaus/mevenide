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

import java.awt.Image;
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
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
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
import org.openide.util.Utilities;

/**
 * display the modules for pom packaged project
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ModulesNode extends AbstractNode {

    /** Creates a new instance of ModulesNode */
    public ModulesNode(NbMavenProject proj) {
        super(new ModulesChildren(proj));
        setName("Modules"); //NOI18N
        setDisplayName(org.openide.util.NbBundle.getMessage(ModulesNode.class, "LBL_Modules"));
    }

    @Override
    public Action[] getActions(boolean bool) {
        return new Action[]{};
    }

    private Image getIcon(boolean opened) {
        Image badge = Utilities.loadImage("org/codehaus/mevenide/netbeans/modules-badge.png", true); //NOI18N
        return Utilities.mergeImages(NodeUtils.getTreeFolderIcon(opened), badge, 8, 8);
    }

    @Override
    public Image getIcon(int type) {
        return getIcon(false);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(true);
    }


    static class ModulesChildren extends Children.Keys<NbMavenProject> {

        private NbMavenProject project;
        private PropertyChangeListener listener;

        ModulesChildren(NbMavenProject proj) {
            project = proj;
            listener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                        loadModules();
                    }
                }
            };
        }

        @Override
        public void addNotify() {
            loadModules();
            ProjectURLWatcher.addPropertyChangeListener(project, listener);
        }

        @Override
        public void removeNotify() {
            ProjectURLWatcher.removePropertyChangeListener(project, listener);
            setKeys(Collections.EMPTY_LIST);
        }

        protected Node[] createNodes(NbMavenProject proj) {
            boolean isPom = "pom".equals(proj.getOriginalMavenProject().getPackaging());
            LogicalViewProvider prov = proj.getLookup().lookup(LogicalViewProvider.class);
            return new Node[]{new ProjectFilterNode(project, proj, prov.createLogicalView(), isPom)};
        }

        private void loadModules() {
            Collection<NbMavenProject> modules = new ArrayList<NbMavenProject>();
            File base = project.getOriginalMavenProject().getBasedir();
            for (Iterator it = project.getOriginalMavenProject().getModules().iterator(); it.hasNext();) {
                String elem = (String) it.next();
                File projDir = FileUtil.normalizeFile(new File(base, elem));
                FileObject fo = FileUtil.toFileObject(projDir);
                if (fo != null) {
                    try {
                        Project prj = ProjectManager.getDefault().findProject(fo);
                        if (prj != null && prj.getLookup().lookup(NbMavenProject.class) != null) {
                            modules.add((NbMavenProject) prj);
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

        @Override
        public Action[] getActions(boolean b) {
            ArrayList<Action> lst = new ArrayList<Action>();
            lst.add(new OpenProjectAction(project));
            lst.add(new RemoveModuleAction(parent, project));
//            lst.addAll(Arrays.asList(super.getActions(b)));
            return lst.toArray(new Action[lst.size()]);
        }

        @Override
        public Action getPreferredAction() {
            return new OpenProjectAction(project);
        }
    }

    private static class RemoveModuleAction extends AbstractAction {

        private NbMavenProject project;
        private NbMavenProject parent;

        public RemoveModuleAction(NbMavenProject parent, NbMavenProject proj) {
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ModulesNode.class, "BTN_Remove_Module"));
            project = proj;
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(org.openide.util.NbBundle.getMessage(ModulesNode.class, "MSG_Remove_Module"), NotifyDescriptor.YES_NO_OPTION);
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
                    ProjectURLWatcher.fireMavenProjectReload(parent);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (XmlPullParserException ex) {
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
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ModulesNode.class, "BTN_Open_Project"));
            project = proj;
        }

        public void actionPerformed(ActionEvent e) {
            OpenProjects.getDefault().open(new Project[]{project}, false);
        }
    }
}