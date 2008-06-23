/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.netbeans.maven.api.execute;

import java.io.File;
import org.netbeans.maven.api.Constants;
import org.netbeans.maven.api.NbMavenProject;
import org.netbeans.maven.execute.MavenCommandLineExecutor;
import org.netbeans.maven.execute.MavenExecutor;
import org.netbeans.maven.execute.MavenJavaExecutor;
import org.netbeans.maven.options.MavenExecutionSettings;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.NbBundle;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 * Utility method for executing a maven build, using the RunConfig.
 * @author mkleint
 */
public final class RunUtils {
    
    
    /** Creates a new instance of RunUtils */
    private RunUtils() {
    }
    
    /**
     *  execute maven build in netbeans execution engine.
     */
    public static ExecutorTask executeMaven(RunConfig config) {
        File home = MavenExecutionSettings.getDefault().getCommandLinePath();
        MavenExecutor exec;
        boolean useCommandLine = false;
        if (config.getProject()!= null) {
            NbMavenProject prj = config.getProject().getLookup().lookup(NbMavenProject.class);
            String val = prj.getMavenProject().getProperties().getProperty(Constants.HINT_USE_EXTERNAL);
            if ("true".equalsIgnoreCase(val)) { //NOI18N
                useCommandLine = true;
            }
        }
        useCommandLine = useCommandLine || MavenExecutionSettings.getDefault().isUseCommandLine();
        //now a bit of hacky code. check in the goals for deployment plugin goals and
        // fallback to embedded, because the deploy plugin needs to run in same VM.
        boolean warnDeploy = false;
        boolean warnRun = false;
        if (useCommandLine) {
            for (String goal : config.getGoals()) {
                if (goal.contains("netbeans-deploy-plugin")) { //NOI18N
                    warnDeploy = true;
                    useCommandLine = false;
                    break;
                }
                if (goal.contains("netbeans-run-plugin")) { //NOI18N
                    warnRun = true;
                    useCommandLine = false;
                    break;
                }
            }
        }
        
        if (useCommandLine && home != null && home.exists()) {
            exec = new MavenCommandLineExecutor(config);
        } else {
            exec = new MavenJavaExecutor(config);
            if (warnDeploy) {
                exec.addInitialMessage(NbBundle.getMessage(RunUtils.class, "MSG_Deploy1"), null);
                exec.addInitialMessage(NbBundle.getMessage(RunUtils.class, "MSG_Deploy2"), null);
                exec.addInitialMessage("", null); //NOI18N
            }
            if (warnRun) {
                exec.addInitialMessage(NbBundle.getMessage(RunUtils.class, "MSG_Run1"), null);
                exec.addInitialMessage(NbBundle.getMessage(RunUtils.class, "MSG_Run2"), null);
                exec.addInitialMessage("", null); //NOI18N
            }
            if (useCommandLine) {
                exec.addInitialMessage(NbBundle.getMessage(RunUtils.class, "MSG_MissingMaven1"), new ShowOptionsListener());
                exec.addInitialMessage(NbBundle.getMessage(RunUtils.class, "MSG_MissingMaven2"), null);
                exec.addInitialMessage("", null); //NOI18N
            }
        }
        return executeMavenImpl(config.getTaskDisplayName(), exec);
    }
    
    private static ExecutorTask executeMavenImpl(String runtimeName, MavenExecutor exec) {
        ExecutorTask task =  ExecutionEngine.getDefault().execute(runtimeName, exec, exec.getInputOutput());
        exec.setTask(task);
        return task;
    }
    
    private static class ShowOptionsListener implements OutputListener {

        public void outputLineSelected(OutputEvent ev) {
        }

        public void outputLineAction(OutputEvent ev) {
            //TODO when #109538 gets fixed.
            OptionsDisplayer.getDefault().open(); //NOI18N - the id is the name of instance in layers.
        }

        public void outputLineCleared(OutputEvent ev) {
        }
        
    }
    
}
