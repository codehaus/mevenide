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

package org.netbeans.modules.maven.spi.actions;

import java.util.Set;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.openide.util.Lookup;

/**
 * Interface that allows to put additional items to project's popup plus to provide specific
 * implementations of ActionProvider actions.
 * Implementations should be registered in default lookup.
 * (Using META-INF/services/MavenActionsProvider file in the module's jar.)
 * or some other custom aspect of the project from dependant modules..
 * 
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface MavenActionsProvider {

    
    /**
     * Create an instance of RunConfig configured for execution.
     * @param actionName one of the ActionProvider constants
     * @returns RunConfig or null, if action not supported
     */
    RunConfig createConfigForDefaultAction(String actionName, Project project, Lookup lookup);

    /**
     * get a action to maven mapping configuration for the given action. No context specific value replacements
     * happen.
     * @return
     */
    NetbeansActionMapping getMappingForAction(String actionName, Project project);

    /**
     * return is action is supported or not
     * @param action action name, see ActionProvider for details.
     * @param project project that the action is invoked on.
     * @param lookup context for the action
     * @return
     */
    boolean isActionEnable(String action, Project project, Lookup lookup);

    /**
     * returns a list of supported actions, see ActionProvider.getSupportedActions()
     * @return
     */
    Set<String> getSupportedDefaultActions();
}
