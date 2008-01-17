/*
 * UsagesUI.java
 *
 * Created on December 26, 2007, 11:25 AM
 */
package org.codehaus.mevenide.netbeans.actions.usages.ui;

import java.awt.Image;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.indexer.api.NBArtifactInfo;
import org.codehaus.mevenide.indexer.api.NBGroupInfo;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.codehaus.mevenide.netbeans.nodes.NodeUtils;
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
                final List<NbMavenProject> openProjects = getOpenProjects(artifact, type);
                Children children = new Children.Keys<NbMavenProject>() {

                    @Override
                    protected Node[] createNodes(NbMavenProject nmp) {
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
        final List<NBGroupInfo> list = RepositoryUtil.getDefaultRepositoryIndexer().
                findDependencyUsage(RepositoryPreferences.LOCAL_REPO_ID,
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

    public List<NbMavenProject> getOpenProjects(Artifact artifact, int type) {
        List<NbMavenProject> mavenProjects = new ArrayList<NbMavenProject>();
        //get all open projects

        Project[] prjs = OpenProjects.getDefault().getOpenProjects();

        for (Project project : prjs) {
            //varify is this a maven project 
            NbMavenProject mavProj = project.getLookup().lookup(NbMavenProject.class);
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
            Image badge = Utilities.loadImage("org/codehaus/mevenide/netbeans/actions/usages/ArtifactBadge.png", true); //NOI18N
            return Utilities.mergeImages(super.getIcon(arg0), badge, 0, 0);
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
            setIconBaseWithExtension("org/codehaus/mevenide/netbeans/DependencyIcon.png"); //NOI18N
        }

        @Override
        public String getDisplayName() {
            return version.getVersion()+" [ "+version.getType()+" ]";
        }
    }

    private static class OpenProjectNode extends AbstractNode {

        private NbMavenProject project;
        private ProjectInformation pi;

        public OpenProjectNode(NbMavenProject project) {
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
