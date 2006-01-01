/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse.preferences;

import org.mevenide.environment.ILocationFinder;

/**  
 * Constant definitions for plug-in preferences
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: MevenidePreferenceKeys.java,v 1.1 15 sept. 2003 Exp gdodinet 
 */
public interface MevenidePreferenceKeys {

    String JAVA_HOME_PREFERENCE_KEY                 = ILocationFinder.JAVA_HOME;
    String MEVENIDE_CHECKTIMESTAMP_PREFERENCE_KEY   = "mevenide.checktimestamp";      //$NON-NLS-1$
    String JAVA_HEAP_SIZE_PREFERENCE_KEY            = "maven.heap.size";              //$NON-NLS-1$
    String MAVEN_HOME_PREFERENCE_KEY                = ILocationFinder.MAVEN_HOME;
    String MAVEN_LAUNCH_DEFAULTGOALS_PREFERENCE_KEY = "maven.launch.defaultgoals";    //$NON-NLS-1$
    String MAVEN_LOCAL_HOME_PREFERENCE_KEY          = ILocationFinder.MAVEN_HOME_LOCAL;
    String MAVEN_REPO_PREFERENCE_KEY                = ILocationFinder.MAVEN_REPO_LOCAL;
    String POM_TEMPLATE_LOCATION_PREFERENCE_KEY     = "pom.template.location";        //$NON-NLS-1$
    String DEFAULT_GOALS_PREFERENCE_KEY             = "maven.launch.defaultgoals";    //$NON-NLS-1$
    String REGISTERED_DEPENPENCY_TYPES              = "mevenide.pom.dependency.type"; //$NON-NLS-1$
    String TOOLS_JAR_PREFERENCE_KEY                 = "tools.jar";                    //$NON-NLS-1$

    /**
     * Controls automatic POM synchronization behavior.
     * <p>value: {@value}</p>
     */
    String AUTOSYNC_ENABLED = "mevenide.autosync.enabled"; //$NON-NLS-1$

}
