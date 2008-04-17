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
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.configurations.ConfigurationProviderEnabler;
import org.codehaus.mevenide.netbeans.configurations.M2ConfigProvider;
import org.codehaus.mevenide.netbeans.problems.ProblemReport;
import org.codehaus.mevenide.netbeans.problems.ProblemReporter;
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
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;


/** A node to represent project root.
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenProjectNode extends AnnotatedAbstractNode {
     
     private NbMavenProject project;
     private ProjectInformation info;
     private ProblemReporter reporter;

     public MavenProjectNode(Lookup lookup, NbMavenProject proj) {
        super(NodeFactorySupport.createCompositeChildren(proj, "Projects/org-codehaus-mevenide-netbeans/Nodes"), lookup); //NOI18N
        this.project = proj;
        info = project.getLookup().lookup(ProjectInformation.class);
        ProjectURLWatcher.addPropertyChangeListener(project, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (NbMavenProject.PROP_PROJECT.equals(event.getPropertyName())) {
                    fireDisplayNameChange(null, getDisplayName());
                    fireIconChange();
                }
            }
        });
        reporter = proj.getLookup().lookup(ProblemReporter.class);
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
        setFiles(Collections.<FileObject>singleton(proj.getProjectDirectory()));
    }

    
    @Override
    public String getDisplayName() {
        return project.getDisplayName();
    }
    
    @Override
    public Image getIconImpl(int param) {
        //HACK - 1. getImage call
        // 2. assume project root folder, should be Generic Sources root (but is the same)
        Image img = ((ImageIcon)info.getIcon()).getImage();
        if (reporter.getReports().size() > 0) {
            img = Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/netbeans/brokenProjectBadge.png"), 8, 0);//NOI18N
        }
        return img;
    }
    
    @Override
    public Image getOpenedIconImpl(int param) {
        //HACK - 1. getImage call
        // 2. assume project root folder, should be Generic Sources root (but is the same)
        Image img = ((ImageIcon)info.getIcon()).getImage();
        if (reporter.getReports().size() > 0) {
            img = Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/netbeans/brokenProjectBadge.png"), 8, 0);//NOI18N
        }
        return img;
    }
    
    @Override
    public javax.swing.Action[] getActions(boolean param) {
        ArrayList<Action> lst = new ArrayList<Action>();
        ActionProviderImpl provider = project.getLookup().lookup(ActionProviderImpl.class);
        lst.add(CommonProjectActions.newFileAction());
        lst.add(null);
    
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_BUILD, NbBundle.getMessage(MavenProjectNode.class, "ACT_Build"), null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_REBUILD, NbBundle.getMessage(MavenProjectNode.class, "ACT_Clean_Build"), null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_CLEAN, NbBundle.getMessage(MavenProjectNode.class, "ACT_Clean"), null));
        lst.add(ProjectSensitiveActions.projectCommandAction("javadoc", NbBundle.getMessage(MavenProjectNode.class, "ACT_Javadoc"), null)); //NOI18N
        lst.add(null);
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_RUN, NbBundle.getMessage(MavenProjectNode.class, "ACT_Run"), null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_DEBUG, NbBundle.getMessage(MavenProjectNode.class, "ACT_Debug"), null));
        lst.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_TEST, NbBundle.getMessage(MavenProjectNode.class, "ACT_Test"), null));
        //TODO move to apisupport somehow..
        Action act = ProjectSensitiveActions.projectCommandAction("nbmreload", NbBundle.getMessage(MavenProjectNode.class, "ACT_NBM_Reload"), null);
        if (act != null && act.isEnabled()) {
            lst.add(act);
        }
        lst.add(null);

        lst.add(provider.createCustomPopupAction()); 
        if (project.getLookup().lookup(ConfigurationProviderEnabler.class).isConfigurationEnabled()) {
            lst.add(CommonProjectActions.setProjectConfigurationAction());
        } else {
            lst.add(provider.createProfilesPopupAction());
        }
        
        // separator
        lst.add(null);
        lst.add(NbMavenProject.createRefreshAction());
        lst.add(CommonProjectActions.setAsMainProjectAction());
        lst.add(CommonProjectActions.openSubprojectsAction());
        if (ProjectURLWatcher.TYPE_POM.equalsIgnoreCase(project.getProjectWatcher().getPackagingType())) { //NOI18N
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
            
        loadLayerActions("Projects/Actions", lst); //NOI18N
        lst.add(null);
        lst.add(SystemAction.get(ToolsAction.class));
        lst.add(null);
        if (reporter.getReports().size() > 0) {
            lst.add(new ShowProblemsAction());
        }
        
        lst.add(CommonProjectActions.customizeProjectAction());
        
        return lst.toArray(new Action[lst.size()]);
    }
    
    public static void loadLayerActions(String path, ArrayList lst) {
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(path); // NOI18N
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
    }

    @Override
    public String getShortDescription() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><i>").append(NbBundle.getMessage(MavenProjectNode.class, "DESC_Project1")).append("</i><b> ").append(project.getProjectDirectory().getPath()).append("</b><br><i>"); //NOI18N
        buf.append(NbBundle.getMessage(MavenProjectNode.class, "DESC_Project2")).append("</i><b> ").append(project.getOriginalMavenProject().getGroupId()).append("</b><br><i>");//NOI18N
        buf.append(NbBundle.getMessage(MavenProjectNode.class, "DESC_Project3")).append("</i><b> ").append(project.getOriginalMavenProject().getArtifactId()).append("</b><br><i>");//NOI18N
        buf.append(NbBundle.getMessage(MavenProjectNode.class, "DESC_Project4")).append("</i><b> ").append(project.getOriginalMavenProject().getVersion()).append("</b><br><i>");//NOI18N
        //TODO escape the short description
        buf.append(NbBundle.getMessage(MavenProjectNode.class, "DESC_Project5")).append("</i> ").append(breakPerLine(project.getShortDescription(), NbBundle.getMessage(MavenProjectNode.class, "DESC_Project5").length()));//NOI18N
        if (reporter.getReports().size() > 0) {
            buf.append("<br><b>").append(NbBundle.getMessage(MavenProjectNode.class, "DESC_Project6")).append("</b><br><ul>");//NOI18N
            Iterator it = reporter.getReports().iterator();
            while (it.hasNext()) {
                ProblemReport elem = (ProblemReport) it.next();
                buf.append("<li>" + elem.getShortDescription() + "</li>");//NOI18N
            }
            buf.append("</ul>");//NOI18N
        }
        buf.append("</html>");//NOI18N
        return buf.toString();
    }

    private String breakPerLine(String string, int start) {
        StringBuffer buf = new StringBuffer();
        StringTokenizer tok = new StringTokenizer(string, " ", true);//NOI18N
        int charCount = start;
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            charCount = charCount + token.length();
            if (charCount > 50) {
                charCount = 0;
                buf.append("<br>");//NOI18N
            }
            buf.append(token);
        }
        return buf.toString();
        
    }
    
    private class CloseSuprojectsAction extends AbstractAction {
        public CloseSuprojectsAction() {
            putValue(Action.NAME, NbBundle.getMessage(MavenProjectNode.class, "ACT_CloseRequired"));
        }

        public void actionPerformed(ActionEvent e) {
            SubprojectProvider subs = project.getLookup().lookup(SubprojectProvider.class);
            Set lst = subs.getSubprojects();
            Project[] arr = (Project[]) lst.toArray(new Project[lst.size()]);
            OpenProjects.getDefault().close(arr);
        }
    }
    
    private class ShowProblemsAction extends AbstractAction {
        
        public ShowProblemsAction() {
            putValue(Action.NAME, NbBundle.getMessage(MavenProjectNode.class, "ACT_ShowProblems"));
        }
        
        public void actionPerformed(ActionEvent arg0) {
            JButton butt = new JButton();
            
            ProblemsPanel panel = new ProblemsPanel(reporter);
            panel.setActionButton(butt);
            JButton close = new JButton();
            panel.setCloseButton(close);
            close.setText(NbBundle.getMessage(MavenProjectNode.class, "BTN_Close"));
            DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(MavenProjectNode.class, "TIT_Show_Problems"));
            dd.setOptions(new Object[] { butt,  close});
            dd.setClosingOptions(new Object[] { close });
            dd.setModal(false);
            DialogDisplayer.getDefault().notify(dd);
        }
    }
}
