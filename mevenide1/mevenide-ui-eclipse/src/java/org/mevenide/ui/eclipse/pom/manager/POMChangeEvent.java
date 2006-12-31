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

import org.eclipse.core.resources.IProject;
import org.mevenide.context.IQueryContext;

/**
 * A change event that describes a change in a POM or its context.
 * <p>
 * Clients are not intented to implement.
 * </p>
 * @see POMChangeListener
 */
public interface POMChangeEvent {
    /*====================================================================
     * Constants defining the kinds of changes to POMs:
     *====================================================================*/
    /**
     * Delta kind constant indicating that the POM has not been changed in any way.
     * @see org.eclipse.core.resources.IResourceDelta#getKind()
     */
    public static final int NO_CHANGE = 0;
    /**
     * Delta kind constant (bit mask) indicating that a POM has been added to the POM manager.
     * @see #getFlags
     */
    public static final int POM_ADDED = 0x1;
    /**
     * Delta kind constant (bit mask) indicating that a POM has been removed from the POM manager.
     * @see  #getFlags
     */
    public static final int POM_REMOVED = 0x2;
    /**
     * Delta kind constant (bit mask) indicating that a POM has been updated or modified.
     * @see #getFlags
     */
    public static final int POM_CHANGED = 0x4;
    
    /**
     * Return the flags that describe the type of change.
     * The returned value should be ANDed with the change type
     * flags to determine whether the change event is of 
     * a particular type. For exampe,
     * <pre>
     *   if (event.getFlags() & POMChangeEvent.POM_ADDED) {
     *      // a POM has been added
     *   }
     * </pre>
     * @return the flags that describe the type of change
     */
    public abstract int getFlags();
    
    /**
     * Return the Eclipse project whose state with
     * respect to the IQueryContext has changed.
     * @return the project whose state has changed
     */
    public abstract IProject getProject();
    
    /**
     * Return the IQueryContext to which this change event applies.
     * @return the query context to which this change event applies
     */
    public abstract IQueryContext getQueryContext();
}
