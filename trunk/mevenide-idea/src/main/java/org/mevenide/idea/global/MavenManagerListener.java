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
package org.mevenide.idea.global;

import java.util.EventListener;

/**
 * An event listener for the Maven manager component.
 *
 * @author Arik
 */
public interface MavenManagerListener extends EventListener {

    /**
     * Invoked when the user changes the Maven home.
     *
     * @param pEvent the event information object
     */
    void mavenHomeChanged(MavenHomeChangedEvent pEvent);

}
