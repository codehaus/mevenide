/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.pom.manager;

import java.util.EventListener;

/**
 * A POM change listener is notified of changes to POMs. 
 * <p>
 * Clients may implement this interface.
 * </p>
 * @see POMManager#addListener(POMChangeListener)
 */
public interface POMChangeListener extends EventListener {
    
    /**
     * Notifies this listener that some POM's properties have changed.
     * <p>
     * The changes have already happened when this method is invoked.
     * </p>
     * 
     * @param deltas detailing the kinds of changes
     */
    public void pomChanged(POMChangeEvent e);
}

