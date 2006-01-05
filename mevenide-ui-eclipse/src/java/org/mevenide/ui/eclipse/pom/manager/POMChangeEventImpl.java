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
 * A concrete implementation of <code>POMChangeEvent</code> that can
 * be used by clients.
 *
 * @see POMChangeEvent
 * @see POMManager
 */
public class POMChangeEventImpl implements POMChangeEvent {

    private int           flags;
    private IProject      project; 
    private IQueryContext context; 
    
    /**
     * Create a change event with the given flags for the given context and project.
     * @param context the query context to which the state change applies
     * @param flags the flags that describe the change
     * @param project the project whose state has change
     */
    public POMChangeEventImpl(IQueryContext context, int flags, IProject project) {
        this.flags = flags;
        this.project = project;
        this.context = context;
    }

    /* (non-Javadoc)
     * @see org.eclipse.team.core.subscribers.ISubscriberChangeEvent#getFlags()
     */
    public int getFlags() {
        return flags;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMChangeEvent#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMChangeEvent#getQueryContext()
     */
    public IQueryContext getQueryContext() {
        return this.context;
    }

}
