/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.profiler;

import org.codehaus.mevenide.netbeans.api.execute.PrerequisitesChecker;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.netbeans.api.project.Project;

/**
 *
 * @author mkleint
 */
public class RunCheckerImpl implements PrerequisitesChecker {
    private Project project;

    public RunCheckerImpl(Project prj) {
        project = prj;
    }
    public boolean checkRunConfig(String actionName, RunConfig config) {
        if (actionName.equals("profile")) {
            //process RunConfig to inject correct port and possibly start the profiler listening.
        }
        return true;
    }

}