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

package org.mevenide.netbeans.api.project;

import javax.swing.Action;
import org.mevenide.netbeans.project.MavenProject;

/**
 * Interface that allows to put additional items to project node's popup.
 * Implementations should be registered in default lookup.
 * (Using META-INF/services/AdditionalMavenLookupProvider file in the module's jar.)
 * It's purpose is to get additional implementations of APIs that are related to 4.1 only, 
 * or some other custom aspect of the project. 
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface AdditionalActionsProvider {
    /**
     * add action instances to the popup of the project.
     */
    Action[] createPopupActions(MavenProject project);
}
