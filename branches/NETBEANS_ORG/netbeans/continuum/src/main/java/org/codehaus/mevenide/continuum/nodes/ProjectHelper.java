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
package org.codehaus.mevenide.continuum.nodes;

import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.model.scm.ScmResult;


/**
 *
 * @author laurent.foret
 */
public class ProjectHelper {
    
    private static ProjectHelper instance;
    
    private ProjectHelper() {
    }
    
    public static ProjectHelper getInstance() {
        if (instance == null) instance = new ProjectHelper();
        return instance;
    }
    
    public String getDisplayableStateFrom(Project project) {
        int state = project.getState();
        String stateStr = "";
        boolean running = false;
        if (state == 6 || state == 7 || state == 8 ) {
            running = true;
            state = project.getOldState();
        }
        if (state == 1) {
            stateStr = "Never built before";
        }
        if (state == 2) {
            stateStr = "Last Build successful.";
        }
        if (state == 3 || state == 4) {
            stateStr = "Last Build failed.";
            ScmResult res = project.getCheckoutResult();
            if (res != null) {
                //TODO.. add more details
            }
        }
        if (running) {
            stateStr = stateStr + " (Now running)";
        }
        return stateStr;
    }
    
    
    
}
