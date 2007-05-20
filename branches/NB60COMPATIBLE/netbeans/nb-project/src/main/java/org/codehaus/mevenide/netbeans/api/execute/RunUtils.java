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

package org.codehaus.mevenide.netbeans.api.execute;

import java.io.File;
import org.codehaus.mevenide.netbeans.execute.MavenCommandLineExecutor;
import org.codehaus.mevenide.netbeans.execute.MavenExecutor;
import org.codehaus.mevenide.netbeans.execute.MavenJavaExecutor;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
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
    public static ExecutorTask executeMaven(String runtimeName, RunConfig config) {
        File home = MavenExecutionSettings.getDefault().getCommandLinePath();
        MavenExecutor exec;
        if (MavenExecutionSettings.getDefault().isUseCommandLine() && home != null) {
            exec = new MavenCommandLineExecutor(config);
        } else {
            exec = new MavenJavaExecutor(config);
        }
        return executeMavenImpl(runtimeName, exec);
    }
    
    private static ExecutorTask executeMavenImpl(String runtimeName, MavenExecutor exec) {
        ExecutorTask task =  ExecutionEngine.getDefault().execute(runtimeName, exec, exec.getInputOutput());
        exec.setTask(task);
        return task;
    }
    
    
}
