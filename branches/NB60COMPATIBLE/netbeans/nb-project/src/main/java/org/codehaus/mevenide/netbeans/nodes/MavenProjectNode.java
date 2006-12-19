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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.mevenide.netbeans.ActionProviderImpl;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.problems.ProblemReport;
import org.codehaus.mevenide.netbeans.problems.ProblemReporter;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.problems.ProblemsPanel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;


/** A node to represent project root.
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenProjectNode extends AbstractNode {
     
     private NbMavenProject project;
     private ProjectInformation info;
     private Image icon;
     private ProblemReporter reporter;
     public MavenProjectNode(Lookup lookup, NbMavenProject proj) {
        super(NodeFactorySupport.createCompositeChildren(proj, "Projects/org-codehaus-mevenide-netbeans/Nodes"), lookup); //NOI18N
        this.project = proj;
        info = (ProjectInformation)project.getLookup().lookup(ProjectInformation.class);
        ProjectURLWatcher.addPropertyChangeListener(project, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (NbMavenProject.PROP_PROJECT.equals(event.getPropertyName())) {
                    fireDisplayNameChange(null, getDisplayName());
                    fireIconChange();
                }
            }
        });
//        setIconBase("org/mevenide/netbeans/projects/resources/MavenIcon");
        reporter = (ProblemReporter)proj.getLookup().lookup(ProblemReporter.class);
        reporter.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fireIconChange();
                        fireOpenedIconChange();
                        fireDisplayNameChange(null, getDisplayName());
                        fireShortDescriptionChange(null, getShortDescription());
                    }
                });
            }
        });
    }
    
    
    public String getDisplayName() {
        return project.getDisplayName();
    }
    
    public Image getIcon(int param) {
        //HACK - 1. getImage call
        // 2. assume project root folder, should be Generic Sources root (but is the same)
        Image img = ((ImageIcon)info.getIcon()).getImage();
        FileObject fo = project.getProjectDirectory();
        try {
            img = fo.getFileSystem().getStatus().annotateIcon(img, param, Collections.singleton(fo));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        if (reporter.getReports().size() > 0) {
            img = Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/netbeans/brokenProjectBadge.png"), 8, 0);
        }
        return img;
    }
    
    public Image getOpenedIcon(int param) {
        //HACK - 1. getImage call
        // 2. assume project root folder, should be Generic Sources root (but is the same)
        Image img = ((ImageIcon)info.getIcon()).getImage();
        FileObject fo = project.getProjectDirectory();
        try {
            img = fo.getFileSystem().getStatus().annotateIcon(img, param, Collections.singleton(fo));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        if (reporter.getReports().size() > 0) {
            img = Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/netbeans/brokenProjectBadge.png"), 8, 0);
        }
        return img;
    }
    
    public javax.swing.Action[] getActions(boolean param) {
//       javax.swing.Action[] spr = super.getActions(param);
        ArrayList lst = new ArrayList();
        ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
        lst.add(CommonProjectActions.newFileAction());
        lst.add(null);
    
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_BUILD, "Build", null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_REBUILD, "Clean and Build", null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_CLEAN, "Clean", null));
        lst.add(ProjectSensitiveActions.projectCommandAction("javadoc", "Generate Javadoc", null));
        lst.add(null);
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_RUN, "Run", null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_DEBUG, "Debug", null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_TEST, "Test", null));
        lst.add(null);
//        if (isMultiproject) {
//            lst.add(provider.createBasicMavenAction("Build (multiproject)", ActionProviderImpl.COMMAND_MULTIPROJECTBUILD));
//            lst.add(provider.createBasicMavenAction("Clean (multiproject)", ActionProviderImpl.COMMAND_MULTIPROJECTCLEAN));
//        }
        Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(AdditionalM2ActionsProvider.class));
        Iterator it = res.allInstances().iterator();
        while (it.hasNext()) {
            AdditionalM2ActionsProvider act = (AdditionalM2ActionsProvider)it.next();
            Action[] acts = act.createPopupActions(project);
            lst.addAll(Arrays.asList(acts));
        }
//        lst.add(new LifecycleMapTest());
        lst.add(provider.createCustomPopupAction()); 
        // separator
        lst.add(null);
        lst.add(project.createRefreshAction());
        lst.add(CommonProjectActions.setAsMainProjectAction());
        lst.add(CommonProjectActions.openSubprojectsAction());
        if ("pom".equalsIgnoreCase(project.getOriginalMavenProject().getPackaging())) {
            lst.add(new CloseSuprojectsAction());
        }
        lst.add(CommonProjectActions.closeProjectAction());
        lst.add(null);
        lst.add(SystemAction.get(FindAction.class));
        lst.add(null);
        lst.add(CommonProjectActions.renameProjectAction());
        lst.add(CommonProjectActions.moveProjectAction());
        lst.add(CommonProjectActions.copyProjectAction());
        lst.add(CommonProjectActions.deleteProjectAction());
            
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Projects/Actions"); // NOI18N
            if (fo != null) {
                DataObject dobj = DataObject.find(fo);
                FolderLookup actionRegistry = new FolderLookup((DataFolder)dobj);
                Lookup.Template query = new Lookup.Template(Object.class);
                Lookup lookup = actionRegistry.getLookup();
                Iterator it2 = lookup.lookup(query).allInstances().iterator();
                if (it2.hasNext()) {
                    lst.add(null);
                }
                while (it2.hasNext()) {
                    Object next = it2.next();
                    if (next instanceof Action) {
                        lst.add(next);
                    } else if (next instanceof JSeparator) {
                        lst.add(null);
                    }
                }
            }
        } catch (DataObjectNotFoundException ex) {
            // data folder for existing fileobject expected
            ErrorManager.getDefault().notify(ex);
        }
        lst.add(null);
        lst.add(SystemAction.get(ToolsAction.class));
        lst.add(null);
        if (reporter.getReports().size() > 0) {
            lst.add(new ShowProblemsAction());
        }
        
        lst.add(CommonProjectActions.customizeProjectAction());
        
        return (Action[])lst.toArray(new Action[lst.size()]);
    }

    public String getShortDescription() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><i>Location:</i><b> ").append(project.getProjectDirectory().getPath()).append("</b><br>");
        buf.append("<i>GroupId:</i><b> ").append(project.getOriginalMavenProject().getGroupId()).append("</b><br>");
        buf.append("<i>ArtifactId:</i><b> ").append(project.getOriginalMavenProject().getArtifactId()).append("</b><br>");
        buf.append("<i>Version:</i><b> ").append(project.getOriginalMavenProject().getVersion()).append("</b><br>");
        //TODO escape the short description
        buf.append("<i>Description:</i> ").append(breakPerLine(project.getShortDescription(), "Description:".length()));
        if (reporter.getReports().size() > 0) {
            buf.append("<br><b>Problems:</b><br><ul>");
            Iterator it = reporter.getReports().iterator();
            while (it.hasNext()) {
                ProblemReport elem = (ProblemReport) it.next();
                buf.append("<li>" + elem.getShortDescription() + "</li>");
            }
            buf.append("</ul>");
        }
        buf.append("</html>");
        return buf.toString();
    }

    private String breakPerLine(String string, int start) {
        StringBuffer buf = new StringBuffer();
        StringTokenizer tok = new StringTokenizer(string, " ", true);
        int charCount = start;
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            charCount = charCount + token.length();
            if (charCount > 50) {
                charCount = 0;
                buf.append("<br>");
            }
            buf.append(token);
        }
        return buf.toString();
        
    }
    
    private class CloseSuprojectsAction extends AbstractAction {
        public CloseSuprojectsAction() {
            putValue(Action.NAME, "Close Required Projects");
        }

        public void actionPerformed(ActionEvent e) {
            SubprojectProvider subs = (SubprojectProvider)project.getLookup().lookup(SubprojectProvider.class);
            Set lst = subs.getSubprojects();
            Project[] arr = (Project[]) lst.toArray(new Project[lst.size()]);
            OpenProjects.getDefault().close(arr);
        }
    }

//    private class LifecycleMapTest extends AbstractAction {
//        public LifecycleMapTest() {
//            putValue(Action.NAME, "Lifecycle map test.");
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            try {
//                Map map = EmbedderFactory.getOnlineEmbedder().getLifecycleMappings(
//                        EmbedderFactory.getOnlineEmbedder().readProject(project.getPOMFile()), "default", "install");
//                Iterator it = map.entrySet().iterator();
//                while (it.hasNext()) {
//                    Map.Entry elem = (Map.Entry) it.next();
//                    System.out.println("elem=" + elem.getKey());
//                    List mojos = (List)elem.getValue();
//                    Iterator it2 = mojos.iterator();
//                    while (it2.hasNext()) {
//                        MojoExecution exec = (MojoExecution) it2.next();
//                        System.out.println(" value=" + exec.getExecutionId() + " " + exec.getMojoDescriptor().getFullGoalName());
//                        System.out.println(" config =" + exec.getConfiguration());
//                    }
//                }
//            } catch (PluginNotFoundException ex) {
//                ex.printStackTrace();
//            } catch (BuildFailureException ex) {
//                ex.printStackTrace();
//            } catch (ProjectBuildingException ex) {
//                ex.printStackTrace();
//            } catch (LifecycleExecutionException ex) {
//                ex.printStackTrace();
//            }
//            
//        }
//    }
    
    private class ShowProblemsAction extends AbstractAction {
        
        public ShowProblemsAction() {
            putValue(Action.NAME, "Show and Resolve Problems...");
        }
        
        public void actionPerformed(ActionEvent arg0) {
            JButton butt = new JButton();
            ProblemsPanel panel = new ProblemsPanel(reporter);
            panel.setActionButton(butt);
            DialogDescriptor dd = new DialogDescriptor(panel, "Show Problems");
            dd.setOptions(new Object[] { butt, "Close" });
            dd.setClosingOptions(new Object[] {"Close"});
            dd.setModal(false);
            DialogDisplayer.getDefault().notify(dd);
        }
    };
}
