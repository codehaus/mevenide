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

package org.codehaus.mevenide.netbeans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.repository.indexing.RepositoryIndexException;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.JarPackagingRunChecker;
import org.codehaus.mevenide.netbeans.execute.MavenJavaExecutor;
import org.codehaus.mevenide.netbeans.execute.ModelRunConfig;
import org.codehaus.mevenide.netbeans.execute.RunConfig;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.ui.RunGoalsPanel;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.execution.ExecutorTask;
import org.openide.util.Lookup;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ActionProviderImpl implements ActionProvider {
    
    private NbMavenProject project;
    private static String[] supported = new String[] {
        ActionProvider.COMMAND_BUILD,
        ActionProvider.COMMAND_CLEAN,
        ActionProvider.COMMAND_REBUILD,
        "javadoc", //NOI18N
        ActionProvider.COMMAND_TEST,
        ActionProvider.COMMAND_TEST_SINGLE,
        ActionProvider.COMMAND_RUN,
        ActionProvider.COMMAND_RUN_SINGLE,
        ActionProvider.COMMAND_DEBUG,
        ActionProvider.COMMAND_DEBUG_SINGLE,
        ActionProvider.COMMAND_DEBUG_TEST_SINGLE
    };
    
    
    /** Creates a new instance of ActionProviderImpl */
    public ActionProviderImpl(NbMavenProject proj) {
        project = proj;
    }
    
    public String[] getSupportedActions() {
        return supported;
    }
    
    public void invokeAction(String action, Lookup lookup) {
        RunConfig rc = ActionToGoalUtils.createRunConfig(action, project, lookup);
        assert rc != null;
        runGoal(action, lookup, rc);
    }
    
    
    private void runGoal(String action, Lookup lookup, RunConfig config) {
        // save all edited files.. maybe finetune for project's files only, however that would fail for multiprojects..
        LifecycleManager.getDefault().saveAll();
        
        // check the prerequisites
        JarPackagingRunChecker jar = new JarPackagingRunChecker();
        if (!jar.checkRunConfig(action, config)) {
            return;
        }
        
        // setup executor now..
        ExecutorTask task = MavenJavaExecutor.executeMaven("Maven", config);
        
        // fire project change on when finishing maven execution, to update the classpath etc. -MEVENIDE-83
        task.addTaskListener(new TaskListener() {
            public void taskFinished(Task task2) {
                project.firePropertyChange(NbMavenProject.PROP_PROJECT);
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
        //TODO needs some MAJOR performance optimizations.. for each action, the mappings are loaded all over
        // again from each provider..
        RunConfig rc = ActionToGoalUtils.createRunConfig(action, project, lookup);
        return rc != null;
    }
    
    public Action createBasicMavenAction(String name, String action) {
        return new BasicAction(name, action);
    }
    
    public Action createCustomMavenAction(String name, NetbeansActionMapping mapping) {
        return new CustomAction(name, mapping);
    }
    
    
    private final class BasicAction extends AbstractAction {
        private String actionid;
        
        
        private BasicAction(String name, String act) {
            actionid = act;
            putValue(Action.NAME, name);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            ActionProviderImpl.this.invokeAction(actionid, ActionProviderImpl.this.project.getLookup());
        }

        public boolean isEnabled() {
            return ActionProviderImpl.this.isActionEnabled(actionid, ActionProviderImpl.this.project.getLookup());
        }
    }
    
    private final class CustomAction extends AbstractAction {
        private NetbeansActionMapping mapping;
        
        
        private CustomAction(String name, NetbeansActionMapping mapp) {
            mapping = mapp;
            putValue(Action.NAME, name);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            RunGoalsPanel pnl = new RunGoalsPanel();
            DialogDescriptor dd = new DialogDescriptor(pnl, "Run Maven");
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
                ModelRunConfig rc = new ModelRunConfig(project, mapping);
                rc.setOffline(Boolean.valueOf(pnl.isOffline()));
                rc.setShowDebug(pnl.isShowDebug());
                rc.setRecursive(pnl.isRecursive());
                rc.setUpdateSnapshots(pnl.isUpdateSnapshots());
                runGoal("custom", Lookup.EMPTY, rc);
            }
        }
    }
    
}
