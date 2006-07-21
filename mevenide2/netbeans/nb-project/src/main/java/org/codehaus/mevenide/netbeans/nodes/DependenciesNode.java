/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.indexing.RepositoryIndexException;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.ProgressTransferListener;
import org.codehaus.mevenide.netbeans.graph.DependencyGraphTopComponent;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * root node for dependencies in project's view.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class DependenciesNode extends AbstractNode {
    static final int TYPE_COMPILE = 0;
    static final int TYPE_TEST = 1;
    static final int TYPE_RUNTIME = 2;
    
    private NbMavenProject project;
    
    DependenciesNode(NbMavenProject mavproject, int type) {
        super(new DependenciesChildren(mavproject, type));
        setName("Dependencies" + type); //NOI18N
        switch (type) {
            case TYPE_COMPILE : setDisplayName("Dependencies"); break;
            case TYPE_TEST : setDisplayName("Test Dependencies"); break;
            case TYPE_RUNTIME : setDisplayName("Runtime Dependencies"); break;
        }
        project = mavproject;
        setIconBaseWithExtension("org/codehaus/mevenide/netbeans/defaultFolder.gif"); //NOI18N
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        retValue = Utilities.mergeImages(retValue,
                Utilities.loadImage("org/codehaus/mevenide/netbeans/libraries-badge.png"),
                8, 8);
        return retValue;
    }
    
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        retValue = Utilities.mergeImages(retValue,
                Utilities.loadImage("org/codehaus/mevenide/netbeans/libraries-badge.png"),
                8, 8);
        return retValue;
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] { 
//                              new AddDependencyAction(),
//                              null,
//                              new DownloadAction(),
                              new ResolveDepsAction(),
                              new DownloadJavadocSrcAction(),
                              new ShowGraphAction()
        };
    }
    
    private NbMavenProject getProject() {
        return project;
    }
    
    private static class DependenciesChildren extends Children.Keys implements PropertyChangeListener {
        private NbMavenProject project;
        private List deps;
        private int type;
        public DependenciesChildren(NbMavenProject proj, int type) {
            super();
            project = proj;
            this.type = type;
        }
        
        protected Node[] createNodes(Object obj) {
            Artifact art = (Artifact)obj;
            Lookup look = Lookups.fixed(new Object[] {
                art,
                project
            });
            return new Node[] { new DependencyNode(look, true) };
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                regenerateKeys();
                refresh();
            }
        }
        
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
            TreeSet lst = new TreeSet(new DependenciesComparator());
            if (type == TYPE_COMPILE) {
                lst.addAll(project.getOriginalMavenProject().getCompileArtifacts());
            }
            if (type == TYPE_TEST) {
                lst.addAll(project.getOriginalMavenProject().getTestArtifacts());
                lst.removeAll(project.getOriginalMavenProject().getCompileArtifacts());
            }
            if (type == TYPE_RUNTIME) {
                lst.addAll(project.getOriginalMavenProject().getRuntimeArtifacts());
                lst.removeAll(project.getOriginalMavenProject().getCompileArtifacts());
            }
            setKeys(lst);
        }
    }
    
//    private class AddDependencyAction extends AbstractAction {
//        public AddDependencyAction() {
//            putValue(Action.NAME, "Add Dependency...");
//        }
//        public void actionPerformed(ActionEvent event) {
//            DependencyPOMChange change = DependencyPOMChange.createChangeInstance(null, 
//                    OriginChange.LOCATION_POM, new HashMap(), 
//                    LocationComboFactory.createPOMChange(project, false), false);
//            DependencyEditor ed = new DependencyEditor(project, change);
//            DialogDescriptor dd = new DialogDescriptor(ed, "Add Dependency");
//            Object ret = DialogDisplayer.getDefault().notify(dd);
//            if (ret == NotifyDescriptor.OK_OPTION) {
//                HashMap props = ed.getProperties();
//                MavenSettings.getDefault().checkDependencyProperties(props.keySet());
//                change.setNewValues(ed.getValues(), props);
//                IContentProvider pr = change.getChangedContent();
//                String artifactId = pr.getValue("artifactId");
//                String groupId = pr.getValue("groupId");
//                String type = pr.getValue("type");
//                List changes = new ArrayList();//DependencyNode.createChangeInstancesList(project, new HashMap());
//                changes.addAll(((DependenciesChildren)getChildren()).deps);
//                Iterator it = changes.iterator();
//                boolean reused = false;
//                while (it.hasNext()) {
//                    DependencyPOMChange element = (DependencyPOMChange)it.next();
//                    IContentProvider prov = element.getChangedContent();
//                    String depArtifactId = prov.getValue("artifactId");
//                    String depGroupId = prov.getValue("groupId");
//                    String depId = prov.getValue("id");
//                    String depType = prov.getValue("type");
//                    if (((   artifactId.equals(depArtifactId)  
//                          && groupId.equals(depGroupId))
//                       || (   artifactId.equals(groupId)  
//                           && artifactId.equals(depId))
//                        ) && (type.equals(depType) || (type.equals("jar") && depType == null))) 
//                    {
//                        NotifyDescriptor d2 = new NotifyDescriptor.Confirmation(
//                                "The project already has a dependency with '" + groupId + ":" + artifactId + "' id. Replace it?",
//                                "Dependency conflict", 
//                                NotifyDescriptor.YES_NO_OPTION,
//                                NotifyDescriptor.QUESTION_MESSAGE);
//                        Object ret2 = DialogDisplayer.getDefault().notify(d2);
//                        if (ret2 != NotifyDescriptor.YES_OPTION) {
//                            return;
//                        }
//                        element.setNewValues(ed.getValues(), props);
//                        reused = true;
//                    }
//                }
//                if (!reused) {
//                    changes.add(change);
//                }
//                try {
//                    NbProjectWriter writer = new NbProjectWriter(project);
//                    writer.applyChanges(changes);
//                } catch (Exception exc) {
//                    ErrorManager.getDefault().notify(ErrorManager.USER, exc);
//                }
//            }
//        }
//    }
//    
//    private class DownloadAction extends AbstractAction {
//
//        public DownloadAction() {
//            putValue(Action.NAME, "Download missing dependencies");
//        }
//
//        public void actionPerformed(ActionEvent evnt) {
//            RequestProcessor.getDefault().post(new Runnable() {
//                public void run() {
//                    List lst = new ArrayList(((DependenciesChildren)getChildren()).deps);
//                    Iterator it = lst.iterator();
//                    boolean atLeastOneDownloaded = false;
//                    while (it.hasNext()) {
//                        DependencyPOMChange change = (DependencyPOMChange)it.next();
//                        IRepositoryReader[] readers = RepositoryUtilities.createRemoteReaders(project.getPropertyResolver());
//                        Dependency dep = DependencyNode.createDependencySnapshot(change.getChangedContent(), project.getPropertyResolver());
//                        try {
//                            boolean downloaded = RepositoryUtilities.downloadArtifact(readers, project, dep);
//                            if (downloaded) {
//                                atLeastOneDownloaded = true;
//                            }
//                        } catch (FileNotFoundException e) {
//                            StatusDisplayer.getDefault().setStatusText(dep.getArtifact() 
//                            + " is not available in repote repositories.");
//                        } catch (Exception exc) {
//                            StatusDisplayer.getDefault().setStatusText("Error downloading " 
//                                    + dep.getArtifact() + " : " + exc.getLocalizedMessage());
//                        }
//                    }
//                    if (atLeastOneDownloaded) {
//                        project.firePropertyChange(MavenProject.PROP_PROJECT);
//                    }
//                }
//            });
//        }
//        
//    }
//    
    private class DownloadJavadocSrcAction extends AbstractAction {
        public DownloadJavadocSrcAction() {
            putValue(Action.NAME, "Check repository(ies) for javadoc and sources");
        }
        
        public void actionPerformed(ActionEvent evnt) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
                    Node[] nds = getChildren().getNodes();
                    for (int i = 0; i < nds.length; i++) {
                        if (nds[i] instanceof DependencyNode) {
                            DependencyNode nd = (DependencyNode)nds[i];
                            if (!nd.hasJavadocInRepository() || !nd.hasSourceInRepository()) {
                                nd.downloadJavadocSources(online);
                            }
                        }
                    }
                    StatusDisplayer.getDefault().setStatusText("Done retrieving javadocs and sources from remote repositories.");
                }
            });
        }
    }  

    private class ResolveDepsAction extends AbstractAction {
        public ResolveDepsAction() {
            putValue(Action.NAME, "Download Dependencies");
        }
        
        public void actionPerformed(ActionEvent evnt) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
                    boolean ok = true; 
                    try {
                        online.readProjectWithDependencies(FileUtil.toFile(project.getProjectDirectory().getFileObject("pom.xml")), 
                                                           new ProgressTransferListener());
                    } catch (ArtifactNotFoundException ex) {
                        ex.printStackTrace();
                        ok = false;
                    } catch (ArtifactResolutionException ex) {
                        ex.printStackTrace();
                        ok = false;
                    } catch (ProjectBuildingException ex) {
                        ex.printStackTrace();
                        ok = false;
                    }
                    if (ok) {
                        StatusDisplayer.getDefault().setStatusText("Done retrieving dependencies from remote repositories.");
                    }
                    project.firePropertyChange(NbMavenProject.PROP_PROJECT);
                }
            });
        }
    }  
    
    private class ShowGraphAction extends AbstractAction {
        public ShowGraphAction() {
            putValue(Action.NAME, "Show Dependency Graph");
        }

        public void actionPerformed(ActionEvent e) {
            TopComponent tc = new DependencyGraphTopComponent(project);
            WindowManager.getDefault().findMode("editor").dockInto(tc);
            tc.open();
            tc.requestActive();
        }
    }
    
    private static class DependenciesComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Artifact art1 = (Artifact)o1;
            Artifact art2 = (Artifact)o2;
            boolean transitive1 = art1.getDependencyTrail().size() > 2;
            boolean transitive2 = art2.getDependencyTrail().size() > 2;
            if (transitive1 && !transitive2) return 1;
            if (!transitive1 && transitive2) return -1;
            return art1.getFile().getName().compareTo(art2.getFile().getName());
        }
        
    }
}

