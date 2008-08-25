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
 * Contributor(s): theanuradha@netbeans.org
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.actions.usages.ui;

import java.awt.Image;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.indexer.api.NBArtifactInfo;
import org.netbeans.modules.maven.indexer.api.NBGroupInfo;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryQueries;
import org.netbeans.modules.maven.spi.nodes.NodeUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G (theanuradha-at-netbeans.org)
 */
public class UsagesUI extends javax.swing.JPanel implements ExplorerManager.Provider {

    static final int TYPE_DEPENDENCY = 0;
    static final int TYPE_COMPILE = 1;
    static final int TYPE_TEST = 2;
    static final int TYPE_RUNTIME = 3;
    private ExplorerManager explorerManager = new ExplorerManager();

    /** Creates new form UsagesUI */
    public UsagesUI(final String libDef, final Artifact artifact) {
        initComponents();
        initNodes(libDef, artifact);

    }

     void initNodes(final String libDef, final Artifact artifact) {
        Children openProjectsChildren = new Children.Keys<Integer>() {

            @Override
            protected Node[] createNodes(Integer type) {
                final List<NbMavenProjectImpl> openProjects = getOpenProjects(artifact, type);
                Children children = new Children.Keys<NbMavenProjectImpl>() {

                    @Override
                    protected Node[] createNodes(NbMavenProjectImpl nmp) {
                        return new Node[]{new OpenProjectNode(nmp)};
                    }

                    @Override
                    protected void addNotify() {
                        super.addNotify();
                        setKeys(openProjects);
                    }
                };
                AbstractNode node = new AbstractNode(children) {

                    @Override
                    public String getHtmlDisplayName() {
                        return getDisplayName();
                    }

                    @Override
                    public Image getIcon(int arg0) {
                        return NodeUtils.getTreeFolderIcon(false);
                    }

                    @Override
                    public Image getOpenedIcon(int arg0) {
                        return NodeUtils.getTreeFolderIcon(true);
                    }
                };
                switch (type) {
                    case TYPE_DEPENDENCY:
                         {
                            node.setDisplayName(NbBundle.getMessage(UsagesUI.class, "LBL_Dependancy"));//NOI18N
                        }
                        break;
                    case TYPE_COMPILE:
                         {
                            node.setDisplayName(NbBundle.getMessage(UsagesUI.class, "LBL_TYPE_COMPILE"));//NOI18N
                        }
                        break;
                    case TYPE_TEST:
                         {
                            node.setDisplayName(NbBundle.getMessage(UsagesUI.class, "LBL_TYPE_TEST"));//NOI18N
                        }
                        break;
                    case TYPE_RUNTIME:
                         {
                            node.setDisplayName(NbBundle.getMessage(UsagesUI.class, "LBL_TYPE_RUNTIME"));//NOI18N
                        }
                        break;
                }
                return new Node[]{node};
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(new Integer[]{TYPE_DEPENDENCY, TYPE_COMPILE, TYPE_TEST, TYPE_RUNTIME});
            }
        };

        final AbstractNode openProjectsNode = new AbstractNode(openProjectsChildren) {

            @Override
            public String getHtmlDisplayName() {
                return NbBundle.getMessage(UsagesUI.class, "LBL_Description", libDef);
            }

            @Override
            public Image getIcon(int arg0) {
                return NodeUtils.getTreeFolderIcon(false);
            }

            @Override
            public Image getOpenedIcon(int arg0) {
                return NodeUtils.getTreeFolderIcon(true);
            }
        };
        final List<NBGroupInfo> list = RepositoryQueries.findDependencyUsage(
                artifact.getGroupId(),
                    artifact.getArtifactId(), artifact.getVersion());
        Children repoChildren = new Children.Keys<NBGroupInfo>() {

            @Override
            protected Node[] createNodes(NBGroupInfo ug) {
                return new Node[]{new GroupNode(ug)};
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(list);
            }
        };
        AbstractNode repoNode = new AbstractNode(repoChildren) {

            @Override
            public String getHtmlDisplayName() {

                return NbBundle.getMessage(UsagesUI.class, "LBL_Repo", libDef);
            }

            @Override
            public Image getIcon(int arg0) {
                return NodeUtils.getTreeFolderIcon(false);
            }

            @Override
            public Image getOpenedIcon(int arg0) {
                return NodeUtils.getTreeFolderIcon(true);
            }
        };

        Children.Array array = new Children.Array();
        array.add(new Node[]{openProjectsNode, repoNode});
        explorerManager.setRootContext(new AbstractNode(array));
        final BeanTreeView beanTreeView = (BeanTreeView) jScrollPane1;
        beanTreeView.setPopupAllowed(false);
        beanTreeView.setRootVisible(false);

        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                beanTreeView.expandAll();

            }
        }, 100);
         RequestProcessor.getDefault().post(new Runnable() {

            public void run() {

                try {
                    explorerManager.setSelectedNodes(new Node[]{openProjectsNode});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        }, 600);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.selectionBackground")));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public List<NbMavenProjectImpl> getOpenProjects(Artifact artifact, int type) {
        List<NbMavenProjectImpl> mavenProjects = new ArrayList<NbMavenProjectImpl>();
        //get all open projects

        Project[] prjs = OpenProjects.getDefault().getOpenProjects();

        for (Project project : prjs) {
            //varify is this a maven project 
            NbMavenProjectImpl mavProj = project.getLookup().lookup(NbMavenProjectImpl.class);
            if (mavProj != null) {

                MavenProject mp = mavProj.getOriginalMavenProject();
                List artifacts = new ArrayList();
                switch (type) {
                    case TYPE_DEPENDENCY:
                         {
                            artifacts.addAll(mp.getDependencyArtifacts());
                        }
                        break;
                    case TYPE_COMPILE:
                         {
                            artifacts.addAll(mp.getCompileArtifacts());
                        }
                        break;
                    case TYPE_TEST:
                         {
                            artifacts.addAll(mp.getTestArtifacts());
                            artifacts.removeAll(mp.getCompileArtifacts());
                        }
                        break;
                    case TYPE_RUNTIME:
                         {
                            artifacts.addAll(mp.getRuntimeArtifacts());
                            artifacts.removeAll(mp.getCompileArtifacts());
                        }
                        break;
                }

                for (Object o : artifacts) {
                    Artifact d = (Artifact) o;
                    if (d.getGroupId().equals(artifact.getGroupId()) && d.getArtifactId().equals(artifact.getArtifactId()) && d.getVersion().equals(artifact.getVersion())) {

                        mavenProjects.add(mavProj);
                        break;
                    }
                }
            }



        }

        return mavenProjects;

    }

    private static class GroupNode extends AbstractNode {

        NBGroupInfo group;

        public GroupNode(final NBGroupInfo group) {
            super(new Children.Keys<NBArtifactInfo>() {

                @Override
                protected Node[] createNodes(NBArtifactInfo arg0) {
                    return new Node[]{new ArtifactNode(arg0)};
                }

                @Override
                protected void addNotify() {
                    super.addNotify();
                    setKeys(group.getArtifactInfos());
                }
            });
            this.group = group;

        }

        @Override
        public Image getIcon(int arg0) {
            return NodeUtils.getTreeFolderIcon(false);
        }

        @Override
        public Image getOpenedIcon(int arg0) {
            return NodeUtils.getTreeFolderIcon(true);
        }

        @Override
        public String getDisplayName() {
            return group.getName();
        }
    }

    private static class ArtifactNode extends AbstractNode {

        NBArtifactInfo artifact;

        public ArtifactNode(final NBArtifactInfo artifact) {
            super(new Children.Keys<NBVersionInfo>() {

                @Override
                protected Node[] createNodes(NBVersionInfo arg0) {
                    return new Node[]{new VertionNode(arg0)};
                }

                @Override
                protected void addNotify() {
                    super.addNotify();
                    setKeys(artifact.getVersionInfos());
                }
            });
            this.artifact = artifact;

        }

        @Override
        public Image getIcon(int arg0) {
            Image badge = Utilities.loadImage("org/netbeans/modules/maven/actions/usages/ArtifactBadge.png", true); //NOI18N
            return badge;
        }

        @Override
        public Image getOpenedIcon(int arg0) {
            return getIcon(arg0);
        }

        @Override
        public String getDisplayName() {
            return artifact.getName();
        }
    }

    private static class VertionNode extends AbstractNode {

        NBVersionInfo version;

        public VertionNode(NBVersionInfo version) {
            super(Children.LEAF);
            this.version = version;
            setIconBaseWithExtension("org/netbeans/modules/maven/DependencyIcon.png"); //NOI18N
        }

        @Override
        public String getDisplayName() {
            return version.getVersion()+" [ "+version.getType()+" ]";
        }
    }

    private static class OpenProjectNode extends AbstractNode {

        private NbMavenProjectImpl project;
        private ProjectInformation pi;

        public OpenProjectNode(NbMavenProjectImpl project) {
            super(Children.LEAF);
            this.project = project;
            pi = ProjectUtils.getInformation(project);
        }

        @Override
        public Image getIcon(int arg0) {
            return Utilities.icon2Image(pi.getIcon());
        }

        @Override
        public String getDisplayName() {
            return pi.getDisplayName();
        }
    }
}
