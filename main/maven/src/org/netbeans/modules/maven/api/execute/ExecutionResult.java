/* ==========================================================================
 * Copyright 2008 Mevenide Team
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

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.maven.execute.ActionToGoalUtils;
import org.openide.windows.InputOutput;

/**
 *
 * @author mkleint
 */
public final class ExecutionResult {

    private int res;
    private InputOutput io;
    private ProgressHandle handle;

    public static final int EXECUTION_ABORTED = -10;

    static {
        AccessorImpl impl = new AccessorImpl();
        impl.assign();
    }
    
    static class AccessorImpl extends ActionToGoalUtils.ResultAccessor {
        
         public void assign() {
             if (ActionToGoalUtils.ACCESSOR == null) {
                 ActionToGoalUtils.ACCESSOR = this;
             }
         }
    
        @Override
        public ExecutionResult createResult(int result, InputOutput inputoutput, ProgressHandle handle) {
            return new ExecutionResult(result, inputoutput, handle);
        }
    }

    private ExecutionResult(int result, InputOutput inputoutput, ProgressHandle handle) {
        res = result;
        this.io = inputoutput;
        this.handle = handle;
    }

    public int getExitCode() {
        return res;
    }

    public InputOutput getInputOutput() {
        return io;
    }

    public ProgressHandle getProgressHandle() {
        return handle;
    }

    
}
