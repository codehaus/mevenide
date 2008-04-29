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

package org.codehaus.mevenide.netbeans.api.execute;

/**
 * check wheather the given runConfig has a chance to be sucessful or needs corrective measure.
 * Usecase would be the netbeans-run-plugin which requires a netbeans-public profile with a
 * jar and assembly configurations..
 * Alternatively can be used for post processing of the RunConfig before it gets
 * executed.
 * @author mkleint
 */
public interface PrerequisitesChecker {
    
    /**
     * @returns true if the execution shall continue., otherwise it will be aborted.
     */
    boolean checkRunConfig(String actionName, RunConfig config);
    
}
