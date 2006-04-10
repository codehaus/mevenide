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

package org.codehaus.mevenide.netbeans.execute;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Profile;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * checks if the run/debug actions with default mappings can be sucessfully executed or not.
 * checks the profile configuration...
 * @author mkleint
 */
public class JarPackagingRunChecker implements PrerequisitesChecker {
    
    private List applicableActions = Arrays.asList(new String[] {
        ActionProvider.COMMAND_RUN,
        ActionProvider.COMMAND_RUN_SINGLE,
        ActionProvider.COMMAND_DEBUG,
        ActionProvider.COMMAND_DEBUG_SINGLE
    });
    /** Creates a new instance of JarPackagingRunChecker */
    public JarPackagingRunChecker() {
    }

    public boolean checkRunConfig(String actionName, RunConfig config) {
        if (applicableActions.contains(actionName)) {
            Iterator it = config.getGoals().iterator();
            while (it.hasNext()) {
                String goal = (String) it.next();
                if (goal.indexOf("org.codehaus.mevenide:netbeans-run-plugin") > -1) {
                    List profiles = config.getProject().getOriginalMavenProject().getModel().getProfiles();
                    Iterator it2 = profiles.iterator();
                    boolean warn = true;
                    while (it2.hasNext()) {
                        Profile prof = (Profile) it2.next();
                        if ("netbeans-public".equals(prof.getId())) {
                            // consider correct if profile is in..
                            warn = false;
                            break;
                        }
                    }
                    if (warn) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message("In order to run the project, Netbeans needs a custom profile in your pom.xml. " +
                                "To create and customize the profile, go to the project's Properties dialog and update the Run panel.");
                        DialogDisplayer.getDefault().notify(nd);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
}
