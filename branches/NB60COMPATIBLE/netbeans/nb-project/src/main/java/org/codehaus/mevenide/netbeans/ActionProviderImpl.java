/* ==========================================================================
 * Copyright 2003-2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans;

import java.io.StringReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.maven.archiva.indexer.RepositoryIndexException;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.ModelRunConfig;
import org.codehaus.mevenide.netbeans.api.execute.PrerequisitesChecker;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.ui.RunGoalsPanel;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.execution.ExecutorTask;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.actions.Presenter;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ActionProviderImpl implements ActionProvider {
    
    private NbMavenProject project;
    private static String[] supported = new String[] {
        COMMAND_BUILD,
        COMMAND_CLEAN,
        COMMAND_REBUILD,
        "javadoc", //NOI18N
        COMMAND_TEST,
        COMMAND_TEST_SINGLE,
        COMMAND_RUN,
        COMMAND_RUN_SINGLE,
        COMMAND_DEBUG,
        COMMAND_DEBUG_SINGLE,
        COMMAND_DEBUG_TEST_SINGLE,
        
        //operations
        COMMAND_DELETE, 
        COMMAND_RENAME,
        COMMAND_MOVE,
        COMMAND_COPY
    };
    
    
    /** Creates a new instance of ActionProviderImpl */
    public ActionProviderImpl(NbMavenProject proj) {
        project = proj;
    }
    
    public String[] getSupportedActions() {
        return supported;
    }
    
    public void invokeAction(String action, Lookup lookup) {
        if (COMMAND_DELETE.equals(action)) {
            DefaultProjectOperations.performDefaultDeleteOperation(project);
            return ;
        }
        if (COMMAND_COPY.equals(action)) {
            DefaultProjectOperations.performDefaultCopyOperation(project);
            return ;
        }
        if (COMMAND_MOVE.equals(action)) {
            DefaultProjectOperations.performDefaultMoveOperation(project);
            return ;
        }
        
        if (COMMAND_RENAME.equals(action)) {
            DefaultProjectOperations.performDefaultRenameOperation(project, null);
            return ;
        }
        
        RunConfig rc = ActionToGoalUtils.createRunConfig(action, project, lookup);
        if (rc == null) {
            Logger.getLogger(ActionProviderImpl.class.getName()).log(Level.FINE, "No handling for action:" + action + ". Ignoring."); //NOI18N
        } else {
            runGoal(action, lookup, rc);
        }
    }
    
    
    private void runGoal(String action, Lookup lookup, RunConfig config) {
        // save all edited files.. maybe finetune for project's files only, however that would fail for multiprojects..
        LifecycleManager.getDefault().saveAll();
        
        // check the prerequisites
        Lookup.Result<PrerequisitesChecker> result = config.getProject().getLookup().lookup(new Lookup.Template<PrerequisitesChecker>(PrerequisitesChecker.class));
        for (PrerequisitesChecker elem : result.allInstances()) {
            if (!elem.checkRunConfig(action, config)) {
                return;
            }
        }
        
        // setup executor now..
        ExecutorTask task = RunUtils.executeMaven(NbBundle.getMessage(ActionProviderImpl.class, "TIT_Maven_Runtime_title"), config);
        
        // fire project change on when finishing maven execution, to update the classpath etc. -MEVENIDE-83
        task.addTaskListener(new TaskListener() {
            public void taskFinished(Task task2) {
                ProjectURLWatcher.fireMavenProjectReload(project);
                LocalRepositoryIndexer index = LocalRepositoryIndexer.getInstance();
                try {
                    index.updateIndexWithArtifacts(project.getOriginalMavenProject().getDependencyArtifacts());
                    //TODO add project's own artifact??
                } catch (RepositoryIndexException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    public boolean isActionEnabled(String action, Lookup lookup) {
        if (COMMAND_DELETE.equals(action) ||
            COMMAND_RENAME.equals(action) ||
            COMMAND_COPY.equals(action) ||
            COMMAND_MOVE.equals(action)) {
            return true;
        }
        //TODO needs some MAJOR performance optimizations.. for each action, the mappings are loaded all over
        // again from each provider..
        RunConfig rc = ActionToGoalUtils.createRunConfig(action, project, lookup);
        return rc != null;
    }
    
    public Action createBasicMavenAction(String name, String action) {
        return new BasicAction(name, action);
    }
    
    public Action createCustomMavenAction(String name, NetbeansActionMapping mapping) {
        return createCustomMavenAction(name, mapping, true);
    }
    
    public Action createCustomMavenAction(String name, NetbeansActionMapping mapping, boolean showUI) {
        return new CustomAction(name, mapping, showUI);
    }
    
    public Action createCustomPopupAction() {
        return new CustomPopupActions();
    }
    
    
    private final static class BasicAction extends AbstractAction implements ContextAwareAction {
        private String actionid;
        private Lookup context;
        private ActionProviderImpl provider;
        
        private BasicAction(String name, String act) {
            actionid = act;
            putValue(Action.NAME, name);
        }
        
        private BasicAction(String name, String act, Lookup cntxt) {
            this(name, act);
            Lookup.Result<Project> res = cntxt.lookup(new Lookup.Template<Project>(Project.class));
            if (res.allItems().size() == 1) {
                Project project = cntxt.lookup(Project.class);
                this.context = project.getLookup();
                provider = this.context.lookup(ActionProviderImpl.class);
            }
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (provider != null) {
                provider.invokeAction(actionid, context);
            }
        }

        public boolean isEnabled() {
            if (provider != null) {
                return provider.isActionEnabled(actionid, provider.project.getLookup());
            }
            return false;
        }

        public Action createContextAwareInstance(Lookup actionContext) {
            return new BasicAction((String)getValue(Action.NAME), actionid, actionContext);
        }
    }
    
    private final class CustomAction extends AbstractAction {
        private NetbeansActionMapping mapping;
        private boolean showUI;
        
        private CustomAction(String name, NetbeansActionMapping mapp, boolean showUI) {
            mapping = mapp;
            putValue(Action.NAME, name);
            this.showUI = showUI;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (!showUI) {
                ModelRunConfig rc = new ModelRunConfig(project, mapping);
                rc.setShowDebug(MavenExecutionSettings.getDefault().isShowDebug());
                
                runGoal("custom", Lookup.EMPTY, rc); //NOI18N
                return;
            }
            RunGoalsPanel pnl = new RunGoalsPanel();
            DialogDescriptor dd = new DialogDescriptor(pnl, NbBundle.getMessage(ActionProviderImpl.class, "TIT_Run_Maven"));
            ActionToGoalMapping maps = ActionToGoalUtils.readMappingsFromFileAttributes(project.getProjectDirectory());
            pnl.readMapping(mapping, project.getOriginalMavenProject(), project.getAvailableProfiles(), maps);
            pnl.setShowDebug(MavenExecutionSettings.getDefault().isShowDebug());
            pnl.setOffline(MavenSettingsSingleton.getInstance().createUserSettingsModel().isOffline());
            pnl.setRecursive(true);
            Object retValue = DialogDisplayer.getDefault().notify(dd);
            if (retValue == DialogDescriptor.OK_OPTION) {
                pnl.applyValues(mapping);
                if (maps.getActions().size() > 10) {
                    maps.getActions().remove(0);
                }
                maps.getActions().add(mapping);
                ActionToGoalUtils.writeMappingsToFileAttributes(project.getProjectDirectory(), maps);
                if (pnl.isRememberedAs() != null) {
                    try {
                        UserActionGoalProvider usr = project.getLookup().lookup(UserActionGoalProvider.class);
                        ActionToGoalMapping mappings = new NetbeansBuildActionXpp3Reader().read(new StringReader(usr.getRawMappingsAsString()));
                        String tit = "CUSTOM-" + pnl.isRememberedAs(); //NOI18N
                        mapping.setActionName(tit);
                        Iterator it = mappings.getActions().iterator();
                        NetbeansActionMapping exist = null;
                        while (it.hasNext()) {
                            NetbeansActionMapping m = (NetbeansActionMapping)it.next();
                            if (tit.equals(m.getActionName())) {
                                exist = m;
                                break;
                            }
                        }
                        if (exist != null) {
                            mappings.getActions().set(mappings.getActions().indexOf(exist), mapping);
                        } else {
                            mappings.addAction(mapping);
                        }
                        mapping.setDisplayName(pnl.isRememberedAs());
                        CustomizerProviderImpl.writeNbActionsModel(project.getProjectDirectory(), mappings);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                ModelRunConfig rc = new ModelRunConfig(project, mapping);
                rc.setOffline(Boolean.valueOf(pnl.isOffline()));
                rc.setShowDebug(pnl.isShowDebug());
                rc.setRecursive(pnl.isRecursive());
                rc.setUpdateSnapshots(pnl.isUpdateSnapshots());
                
                runGoal("custom", Lookup.EMPTY, rc); //NOI18N
            }
        }
    }

    private final class CustomPopupActions extends AbstractAction implements Presenter.Popup {
        
        private CustomPopupActions() {
            putValue(Action.NAME, NbBundle.getMessage(ActionProviderImpl.class, "LBL_Custom_Run"));
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
        }
    
        public JMenuItem getPopupPresenter() {
            //TODO have some caching/lazy construction strategy
            JMenu menu = new JMenu(NbBundle.getMessage(ActionProviderImpl.class, "LBL_Custom_Run"));
            NetbeansActionMapping[] maps = ActionToGoalUtils.getActiveCustomMappings(project);
            for (int i = 0; i < maps.length; i++) {
                NetbeansActionMapping mapp = maps[i];
                Action act = createCustomMavenAction(mapp.getActionName(), mapp, false);
                JMenuItem item = new JMenuItem(act);
                item.setText(mapp.getDisplayName() == null ? mapp.getActionName() : mapp.getDisplayName());
                menu.add(item);
            }
            menu.add(new JMenuItem(createCustomMavenAction(NbBundle.getMessage(ActionProviderImpl.class, "LBL_Custom_run_goals"), new NetbeansActionMapping())));
            return menu;
        }
    }
    
}
