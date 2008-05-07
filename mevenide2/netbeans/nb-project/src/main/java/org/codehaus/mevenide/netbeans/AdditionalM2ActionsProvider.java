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

package org.codehaus.mevenide.netbeans;

import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.netbeans.api.project.Project;
import org.openide.util.Lookup;

/**
 * Interface that allows to put additional items to project's popup plus to provide specific
 * implementations of ActionProvider actions.
 * Implementations should be registered in default lookup.
 * (Using META-INF/services/AdditionalM2ActionsProvider file in the module's jar.)
 * It's purpose is to get additional implementations of APIs that are related to 5.0 only, 
 * or some other custom aspect of the project from dependant modules..
 * 
 * //TODO move to api/spi package, get rid of NbMavenProject reference.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface AdditionalM2ActionsProvider {

    
    /**
     * Create an instance of RunConfig configured for execution.
     * @param actionName one of the ActionProvider constants
     * @returns RunConfig or null, if action not supported
     */
    RunConfig createConfigForDefaultAction(String actionName, Project project, Lookup lookup);

    /**
     * get a action to maven mapping configuration for the given action. No context specific value replacements
     * happen.
     */
    NetbeansActionMapping getMappingForAction(String actionName, Project project);

    /**
     * return  action supported or not
     */
    boolean isActionEnable(String action, Project project, Lookup lookup);
}
