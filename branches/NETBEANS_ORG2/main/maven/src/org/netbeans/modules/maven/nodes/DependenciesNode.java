/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.nodes;

import java.awt.Image;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
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
 * @author  Milos Kleint
 */
public class DependenciesNode extends AbstractNode {
    static final int TYPE_COMPILE = 0;
    static final int TYPE_TEST = 1;
    static final int TYPE_RUNTIME = 2;
    
    private NbMavenProjectImpl project;
    
    DependenciesNode(NbMavenProjectImpl mavproject, int type) {
        super(new DependenciesChildren(mavproject, type), Lookups.fixed(mavproject));
        setName("Dependencies" + type); //NOI18N
        switch (type) {
            case TYPE_COMPILE : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Libraries")); break;
            case TYPE_TEST : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Test_Libraries")); break;
            case TYPE_RUNTIME : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Runtime_Libraries")); break;
            default : setDisplayName(org.openide.util.NbBundle.getMessage(DependenciesNode.class, "LBL_Libraries")); break;
        }
        project = mavproject;
        setIconBaseWithExtension("org/netbeans/modules/maven/defaultFolder.gif"); //NOI18N
    }
    
    @Override
    public Image getIcon(int param) {
        Image retValue = Utilities.mergeImages(getTreeFolderIcon(false),
                Utilities.loadImage("org/netbeans/modules/maven/libraries-badge.png"), //NOI18N
                8, 8);
        return retValue;
    }
    
    @Override
    public Image getOpenedIcon(int param) {
        Image retValue = Utilities.mergeImages(getTreeFolderIcon(true),
                Utilities.loadImage("org/netbeans/modules/maven/libraries-badge.png"), //NOI18N
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
        MavenProjectNode.loadLayerActions("Projects/org-netbeans-modules-maven/DependenciesActions", toRet); //NOI18N
        return toRet.toArray(new Action[toRet.size()]);
    }
    
    private static class DependenciesChildren extends Children.Keys<Artifact> implements PropertyChangeListener {
        private NbMavenProjectImpl project;
        private int type;
        public DependenciesChildren(NbMavenProjectImpl proj, int type) {
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
            if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
                regenerateKeys();
                refresh();
            }
        }
        
        @Override
        protected void addNotify() {
            super.addNotify();
            NbMavenProject.addPropertyChangeListener(project, this);
            regenerateKeys();
        }
        
        @Override
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            NbMavenProject.removePropertyChangeListener(project, this);
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
               ModelUtils. addDependency(project.getProjectDirectory().getFileObject("pom.xml")/*NOI18N*/,
                       pnl.getGroupId(), pnl.getArtifactId(), pnl.getVersion(),
                       null, pnl.getScope(), null,false);
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
                    project.getLookup().lookup(NbMavenProject.class).triggerDependencyDownload();
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
            return art1.getArtifactId().compareTo(art2.getArtifactId());
        }
        
    }
    
    
    private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER = "Tree.openIcon"; // NOI18N
    private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.openedIcon"; // NOI18N
    private static final String ICON_PATH = "org/netbeans/modules/maven/defaultFolder.gif"; // NOI18N
    private static final String OPENED_ICON_PATH = "org/netbeans/modules/maven/defaultFolderOpen.gif"; // NOI18N
    
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

