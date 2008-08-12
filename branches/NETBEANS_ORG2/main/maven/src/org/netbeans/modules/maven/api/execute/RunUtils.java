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

package org.netbeans.modules.maven.api.execute;

import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.execute.MavenCommandLineExecutor;
import org.netbeans.modules.maven.execute.MavenExecutor;
import org.netbeans.modules.maven.execute.MavenJavaExecutor;
import org.netbeans.modules.maven.options.MavenExecutionSettings;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;

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
        MavenExecutor exec;
        boolean useEmbedded = false;
        if (config.getProject() != null) {
            AuxiliaryProperties props = config.getProject().getLookup().lookup(AuxiliaryProperties.class);
            String val = props.get(Constants.HINT_USE_EXTERNAL, true);
            if ("false".equalsIgnoreCase(val)) { //NOI18N
                useEmbedded = true;
            }
        }
        useEmbedded = useEmbedded || !MavenExecutionSettings.getDefault().isUseCommandLine();
        
        if (!useEmbedded && MavenExecutionSettings.canFindExternalMaven()) {
            exec = new MavenCommandLineExecutor(config);
        } else {
            exec = new MavenJavaExecutor(config);
        }
        return executeMavenImpl(config.getTaskDisplayName(), exec);
    }

    
    
    private static ExecutorTask executeMavenImpl(String runtimeName, MavenExecutor exec) {
        ExecutorTask task =  ExecutionEngine.getDefault().execute(runtimeName, exec, exec.getInputOutput());
        exec.setTask(task);
        return task;
    }


}
