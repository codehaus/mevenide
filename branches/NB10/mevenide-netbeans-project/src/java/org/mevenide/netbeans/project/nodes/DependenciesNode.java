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

package org.mevenide.netbeans.project.nodes;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.netbeans.api.customizer.LocationComboFactory;
import org.mevenide.netbeans.api.customizer.OriginChange;
import org.mevenide.netbeans.project.dependencies.DependencyEditor;
import org.mevenide.netbeans.project.dependencies.DependencyNode;
import org.mevenide.netbeans.project.dependencies.RepositoryUtilities;
import org.mevenide.netbeans.project.writer.NbProjectWriter;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.repository.IRepositoryReader;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * root node for dependencies in project's view.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class DependenciesNode extends AbstractNode {
    private MavenProject project;
    DependenciesNode(MavenProject mavproject) {
        super(new DependenciesChildren(mavproject));
        setName("Dependencies"); //NOI18N
        setDisplayName("Dependencies");
        project = mavproject;
        setIconBase("org/mevenide/netbeans/project/resources/defaultFolder"); //NOI18N
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        retValue = Utilities.mergeImages(retValue,
                Utilities.loadImage("org/mevenide/netbeans/project/resources/libraries-badge.png"),
                8, 8);
        return retValue;
    }
    
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        retValue = Utilities.mergeImages(retValue,
                Utilities.loadImage("org/mevenide/netbeans/project/resources/libraries-badge.png"),
                8, 8);
        return retValue;
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] { new AddDependencyAction(),
                              null,
                              new DownloadAction(),
                              new DownloadJavadocSrcAction()
        };
    }
    
    private MavenProject getProject() {
        return project;
    }
    
    private static class DependenciesChildren extends Children.Keys implements PropertyChangeListener {
        private MavenProject project;
        private List deps;
        public DependenciesChildren(MavenProject proj) {
            super();
            project = proj;
        }
        
        protected Node[] createNodes(Object obj) {
            DependencyPOMChange combo = (DependencyPOMChange)obj;
            Lookup look = Lookups.fixed(new Object[] {
                combo,
                project, 
                deps
            });
            return new Node[] { new DependencyNode(look, true) };
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (MavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                regenerateKeys();
                refresh();
            }
        }
        
//        public void refreshChildren() {
//            Node[] nods = getNodes();
//            for (int i = 0; i < nods.length; i++) {
//                if (nods[i] instanceof DependencyNode) {
//                    ((DependencyNode)nods[i]).refreshNode();
//                }
//            }
//        }
        
        protected void addNotify() {
            super.addNotify();
            project.addPropertyChangeListener(this);
            regenerateKeys();
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            project.removePropertyChangeListener(this);
            super.removeNotify();
        }
        
        private void regenerateKeys() {
            Project[] projs = project.getContext().getPOMContext().getProjectLayers();
            deps = new ArrayList();
            for (int i = 0; i < projs.length; i++) {
                List ones = projs[i].getDependencies();
                if (ones != null) {
                    Iterator it = ones.iterator();
                    while (it.hasNext()) {
                        Dependency dep = (Dependency)it.next();
                        deps.add(DependencyPOMChange.createChangeInstance(dep, i, new HashMap(),
                                LocationComboFactory.createPOMChange(project, false), false));
                    }
                }
            }
            setKeys(deps);
        }
    }
    
    private class AddDependencyAction extends AbstractAction {
        public AddDependencyAction() {
            putValue(Action.NAME, "Add Dependency...");
        }
        public void actionPerformed(ActionEvent event) {
            DependencyPOMChange change = DependencyPOMChange.createChangeInstance(null, 
                    OriginChange.LOCATION_POM, new HashMap(), 
                    LocationComboFactory.createPOMChange(project, false), false);
            DependencyEditor ed = new DependencyEditor(project, change);
            DialogDescriptor dd = new DialogDescriptor(ed, "Add Dependency");
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (ret == NotifyDescriptor.OK_OPTION) {
                HashMap props = ed.getProperties();
                MavenSettings.getDefault().checkDependencyProperties(props.keySet());
                change.setNewValues(ed.getValues(), props);
                IContentProvider pr = change.getChangedContent();
                String artifactId = pr.getValue("artifactId");
                String groupId = pr.getValue("groupId");
                String type = pr.getValue("type");
                List changes = new ArrayList();//DependencyNode.createChangeInstancesList(project, new HashMap());
                changes.addAll(((DependenciesChildren)getChildren()).deps);
                Iterator it = changes.iterator();
                boolean reused = false;
                while (it.hasNext()) {
                    DependencyPOMChange element = (DependencyPOMChange)it.next();
                    IContentProvider prov = element.getChangedContent();
                    String depArtifactId = prov.getValue("artifactId");
                    String depGroupId = prov.getValue("groupId");
                    String depId = prov.getValue("id");
                    String depType = prov.getValue("type");
                    if (((   artifactId.equals(depArtifactId)  
                          && groupId.equals(depGroupId))
                       || (   artifactId.equals(groupId)  
                           && artifactId.equals(depId))
                        ) && (type.equals(depType) || (type.equals("jar") && depType == null))) 
                    {
                        NotifyDescriptor d2 = new NotifyDescriptor.Confirmation(
                                "The project already has a dependency with '" + groupId + ":" + artifactId + "' id. Replace it?",
                                "Dependency conflict", 
                                NotifyDescriptor.YES_NO_OPTION,
                                NotifyDescriptor.QUESTION_MESSAGE);
                        Object ret2 = DialogDisplayer.getDefault().notify(d2);
                        if (ret2 != NotifyDescriptor.YES_OPTION) {
                            return;
                        }
                        element.setNewValues(ed.getValues(), props);
                        reused = true;
                    }
                }
                if (!reused) {
                    changes.add(change);
                }
                try {
                    NbProjectWriter writer = new NbProjectWriter(project);
                    writer.applyChanges(changes);
                } catch (Exception exc) {
                    ErrorManager.getDefault().notify(ErrorManager.USER, exc);
                }
            }
        }
    }
    
    private class DownloadAction extends AbstractAction {

        public DownloadAction() {
            putValue(Action.NAME, "Download missing dependencies");
        }

        public void actionPerformed(ActionEvent evnt) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    List lst = new ArrayList(((DependenciesChildren)getChildren()).deps);
                    Iterator it = lst.iterator();
                    boolean atLeastOneDownloaded = false;
                    while (it.hasNext()) {
                        DependencyPOMChange change = (DependencyPOMChange)it.next();
                        IRepositoryReader[] readers = RepositoryUtilities.createRemoteReaders(project.getPropertyResolver());
                        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent(), project.getPropertyResolver());
                        try {
                            boolean downloaded = RepositoryUtilities.downloadArtifact(readers, project, dep);
                            if (downloaded) {
                                atLeastOneDownloaded = true;
                            }
                        } catch (FileNotFoundException e) {
                            StatusDisplayer.getDefault().setStatusText(dep.getArtifact() 
                            + " is not available in repote repositories.");
                        } catch (Exception exc) {
                            StatusDisplayer.getDefault().setStatusText("Error downloading " 
                                    + dep.getArtifact() + " : " + exc.getLocalizedMessage());
                        }
                    }
                    if (atLeastOneDownloaded) {
                        project.firePropertyChange(MavenProject.PROP_PROJECT);
                    }
                }
            });
        }
        
    }
    
    private class DownloadJavadocSrcAction extends AbstractAction {
        public DownloadJavadocSrcAction() {
            putValue(Action.NAME, "Check repository(ies) for javadoc and sources");
        }
        
        public void actionPerformed(ActionEvent evnt) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    List lst = new ArrayList(((DependenciesChildren)getChildren()).deps);
                    Iterator it = lst.iterator();
                    boolean atLeastOneDownloaded = false;
                    while (it.hasNext()) {
                        DependencyPOMChange change = (DependencyPOMChange)it.next();
                        IRepositoryReader[] readers = RepositoryUtilities.createRemoteReaders(project.getPropertyResolver());
                        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent(), project.getPropertyResolver());
                        try {
                            dep.setType("javadoc.jar");
                            boolean downloaded = RepositoryUtilities.downloadArtifact(readers, project, dep);
                            if (downloaded) {
                                atLeastOneDownloaded = true;
                            }
                        } catch (FileNotFoundException e) {
                            StatusDisplayer.getDefault().setStatusText(dep.getArtifact() 
                                    + " is not available in repote repositories.");
                        } catch (Exception exc) {
                            StatusDisplayer.getDefault().setStatusText("Error downloading " 
                                    + dep.getArtifact() + " : " + exc.getLocalizedMessage());
                        }
                        boolean sourceDownloaded = false;
                        try {
                            dep.setType("java-source");
                            // this type is the default for 1.1+
                            boolean downloaded = RepositoryUtilities.downloadArtifact(readers, project, dep);
                            if (downloaded) {
                                atLeastOneDownloaded = true;
                            } else {
                                dep.setType("src.jar");
                                //the old one needs to be checked as well..
                                downloaded = RepositoryUtilities.downloadArtifact(readers, project, dep);
                                if (downloaded) {
                                    atLeastOneDownloaded = true;
                                    sourceDownloaded = true;
                                }
                            }
                        } catch (FileNotFoundException e) {
                            StatusDisplayer.getDefault().setStatusText(dep.getArtifact()
                            + " is not available in repote repositories.");
                        } catch (Exception exc) {
                            StatusDisplayer.getDefault().setStatusText("Error downloading "
                                    + dep.getArtifact() + " : " + exc.getLocalizedMessage());
                        }
                        if (!sourceDownloaded) {
                            try {
                                dep.setType("src.jar");
                                //the old one needs to be checked as well..
                                boolean downloaded = RepositoryUtilities.downloadArtifact(readers, project, dep);
                                if (downloaded) {
                                    atLeastOneDownloaded = true;
                                }
                            } catch (FileNotFoundException e) {
                                StatusDisplayer.getDefault().setStatusText(dep.getArtifact()
                                + " is not available in repote repositories.");
                            } catch (Exception exc) {
                                StatusDisplayer.getDefault().setStatusText("Error downloading "
                                        + dep.getArtifact() + " : " + exc.getLocalizedMessage());
                            }
                        }
                    }
                    if (atLeastOneDownloaded) {
                        project.firePropertyChange(MavenProject.PROP_PROJECT);
                    }
                }
            });
        }
        
    }    
}

