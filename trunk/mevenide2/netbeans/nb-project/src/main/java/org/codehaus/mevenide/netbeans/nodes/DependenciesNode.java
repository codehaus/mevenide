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

import java.awt.Image;
import org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * root node for dependencies in project's view.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependenciesNode extends AbstractNode {
    static final int TYPE_COMPILE = 0;
    static final int TYPE_TEST = 1;
    static final int TYPE_RUNTIME = 2;
    
    private NbMavenProject project;
    
    DependenciesNode(NbMavenProject mavproject, int type) {
        super(new DependenciesChildren(mavproject, type), Lookups.fixed(mavproject));
        setName("Dependencies" + type); //NOI18N
        switch (type) {
            case TYPE_COMPILE : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Libraries")); break;
            case TYPE_TEST : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Test_Libraries")); break;
            case TYPE_RUNTIME : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Runtime_Libraries")); break;
            default : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Libraries")); break;
        }
        project = mavproject;
        setIconBaseWithExtension("org/codehaus/mevenide/netbeans/defaultFolder.gif"); //NOI18N
    }
    
    @Override
    public Image getIcon(int param) {
        Image retValue = Utilities.mergeImages(getTreeFolderIcon(false),
                Utilities.loadImage("org/codehaus/mevenide/netbeans/libraries-badge.png"), //NOI18N
                8, 8);
        return retValue;
    }
    
    @Override
    public Image getOpenedIcon(int param) {
        Image retValue = Utilities.mergeImages(getTreeFolderIcon(true),
                Utilities.loadImage("org/codehaus/mevenide/netbeans/libraries-badge.png"), //NOI18N
                8, 8);
        return retValue;
    }
    
    @Override
    public Action[] getActions(boolean context) {
        ArrayList<Action> toRet = new ArrayList<Action>();
        toRet.add(new AddDependencyAction());
        toRet.add(null);
        toRet.add(new ResolveDepsAction(project));
        toRet.add(new DownloadJavadocSrcAction(true));
        toRet.add(new DownloadJavadocSrcAction(false));
        MavenProjectNode.loadLayerActions("Projects/org-codehaus-mevenide-netbeans/DependenciesActions", toRet); //NOI18N
        return toRet.toArray(new Action[toRet.size()]);
    }
    
    private static class DependenciesChildren extends Children.Keys<Artifact> implements PropertyChangeListener {
        private NbMavenProject project;
        private int type;
        public DependenciesChildren(NbMavenProject proj, int type) {
            super();
            project = proj;
            this.type = type;
        }
        
        protected Node[] createNodes(Artifact art) {
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
        
        @Override
        protected void addNotify() {
            super.addNotify();
            ProjectURLWatcher.addPropertyChangeListener(project, this);
            regenerateKeys();
        }
        
        @Override
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            ProjectURLWatcher.removePropertyChangeListener(project, this);
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
    
    private class AddDependencyAction extends AbstractAction {
        public AddDependencyAction() {
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(DependenciesNode.class, "BTN_Add_Library"));
        }
        public void actionPerformed(ActionEvent event) {
            AddDependencyPanel pnl = new AddDependencyPanel();
        
            DialogDescriptor dd = new DialogDescriptor(pnl, org.openide.util.NbBundle.getMessage(DependenciesNode.class, "TIT_Add_Library"));
            dd.setClosingOptions(new Object[] {
                pnl.getOkButton(),
                DialogDescriptor.CANCEL_OPTION
            });
            dd.setOptions(new Object[] {
                pnl.getOkButton(),
                DialogDescriptor.CANCEL_OPTION
            });
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (pnl.getOkButton() == ret) {
                addDependency(project, pnl.getGroupId(), pnl.getArtifactId(), pnl.getVersion(), null, pnl.getScope(), null);
            }
        }
    }
    
    /**
     * a somewhat api method for adding dependenciy to pom.
     * TODO: expose exception handling..
     */
    public static void addDependency(NbMavenProject project, 
            String group, 
            String artifact, 
            String version, 
            String type, 
            String scope, 
            String classifier) {
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
        Model model = WriterUtils.loadModel(fo);
        if (model != null) {
            Dependency dep = PluginPropertyUtils.checkModelDependency(model, group, artifact, true);
            dep.setVersion(version);
            if (scope != null) {
                dep.setScope(scope);
            }
            if (type != null) {
                dep.setType(type);
            }
            if (classifier != null) {
                dep.setClassifier(classifier);
            }
            try {
                WriterUtils.writePomModel(fo, model);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class DownloadJavadocSrcAction extends AbstractAction {
        private boolean javadoc;
        public DownloadJavadocSrcAction(boolean javadoc) {
            putValue(Action.NAME, javadoc ? org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Download_Javadoc") : org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Download__Sources"));
            this.javadoc = javadoc;
        }
        
        public void actionPerformed(ActionEvent evnt) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
                    Node[] nds = getChildren().getNodes();
                    ProgressContributor[] contribs = new ProgressContributor[nds.length];
                    for (int i = 0; i < nds.length; i++) {
                        contribs[i] = AggregateProgressFactory.createProgressContributor("multi-" + i); //NOI18N
                    }
                    String label = javadoc ? NbBundle.getMessage(DependenciesNode.class, "Progress_Javadoc") : NbBundle.getMessage(DependenciesNode.class, "Progress_Source");
                    AggregateProgressHandle handle = AggregateProgressFactory.createHandle(label, 
                            contribs, null, null);
                    handle.start();
                    try {
                    ProgressTransferListener.setAggregateHandle(handle);
                    for (int i = 0; i < nds.length; i++) {
                        if (nds[i] instanceof DependencyNode) {
                            DependencyNode nd = (DependencyNode)nds[i];
                            if (javadoc && !nd.hasJavadocInRepository()) {
                                nd.downloadJavadocSources(online, contribs[i], javadoc);
                            } else if (!javadoc && !nd.hasSourceInRepository()) {
                                nd.downloadJavadocSources(online, contribs[i], javadoc);
                            } else {
                                contribs[i].finish();
                            }
                        }
                    }
                    } finally {
                        handle.finish();
                        ProgressTransferListener.clearAggregateHandle();
                    }
                }
            });
        }
    }  

    public static class ResolveDepsAction extends AbstractAction {
        private Project project;
        public ResolveDepsAction(Project prj) {
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Download"));
            project = prj;
        }
        
        public void actionPerformed(ActionEvent evnt) {
            setEnabled(false);
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    project.getLookup().lookup(ProjectURLWatcher.class).triggerDependencyDownload();
                }
            });
        }
    }  
    private static class DependenciesComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Artifact art1 = (Artifact)o1;
            Artifact art2 = (Artifact)o2;
            boolean transitive1 = art1.getDependencyTrail().size() > 2;
            boolean transitive2 = art2.getDependencyTrail().size() > 2;
            if (transitive1 && !transitive2) {
                return 1;
            }
            if (!transitive1 && transitive2)  {
                return -1;
            }
            return art1.getFile().getName().compareTo(art2.getFile().getName());
        }
        
    }
    
    
    private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER = "Tree.openIcon"; // NOI18N
    private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.openedIcon"; // NOI18N
    private static final String ICON_PATH = "org/codehaus/mevenide/netbeans/defaultFolder.gif"; // NOI18N
    private static final String OPENED_ICON_PATH = "org/codehaus/mevenide/netbeans/defaultFolderOpen.gif"; // NOI18N
    
    /**
     * Returns default folder icon as {@link java.awt.Image}. Never returns
     * <code>null</code>.
     *
     * @param opened wheter closed or opened icon should be returned.
     * 
     * copied from apisupport/project
     */
    public static Image getTreeFolderIcon(boolean opened) {
        Image base = null;
        Icon baseIcon = UIManager.getIcon(opened ? OPENED_ICON_KEY_UIMANAGER : ICON_KEY_UIMANAGER); // #70263
        if (baseIcon != null) {
            base = Utilities.icon2Image(baseIcon);
        } else {
            base = (Image) UIManager.get(opened ? OPENED_ICON_KEY_UIMANAGER_NB : ICON_KEY_UIMANAGER_NB); // #70263
            if (base == null) { // fallback to our owns
                base = Utilities.loadImage(opened ? OPENED_ICON_PATH : ICON_PATH, true);
            }
        }
        assert base != null;
        return base;
    }
    
}

