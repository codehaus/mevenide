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

import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.api.execute.PrerequisitesChecker;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Profile;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * checks if the run/debug actions with default mappings can be sucessfully executed or not.
 * checks the profile configuration...
 * @author mkleint
 * @deprecated just for compatibility reasons there, should not
 * be actually showing any UI, since the org.codehaus.mevenide:netbeans-run-plugin is no more default.
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
                if (goal.indexOf("org.codehaus.mevenide:netbeans-run-plugin") > -1) { //NOI18N
                    List profiles = config.getProject().getOriginalMavenProject().getModel().getProfiles();
                    Iterator it2 = profiles.iterator();
                    boolean warn = true;
                    while (it2.hasNext()) {
                        Profile prof = (Profile) it2.next();
                        if ("netbeans-public".equals(prof.getId())) { //NOI18N
                            // consider correct if profile is in..
                            warn = false;
                            break;
                        }
                    }
                    if (warn) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(JarPackagingRunChecker.class, "MSG_Need_Project_Customizer"));
                        DialogDisplayer.getDefault().notify(nd);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
}
