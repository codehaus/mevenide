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

package org.mevenide.ui.eclipse.util;

import org.eclipse.core.internal.events.ILifecycleListener;
import org.eclipse.core.internal.events.LifecycleEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;

/**
 * TODO: Describe what LifecycleListener represents.
 */
public class LifecycleListener implements ILifecycleListener {

    /**
     * Initializes this exception handler with contact information from the plugin.
     * @param plugin the plugin to report against
     */
    public LifecycleListener(Plugin plugin) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.internal.events.ILifecycleListener#handleEvent(org.eclipse.core.internal.events.LifecycleEvent)
     */
    public void handleEvent(LifecycleEvent event) throws CoreException {
        switch (event.kind) {
        case LifecycleEvent.PRE_PROJECT_CLOSE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Closing " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_PROJECT_CHANGE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Changing " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_PROJECT_COPY: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Copying " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_PROJECT_CREATE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Creating " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_PROJECT_DELETE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Deleting " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_PROJECT_OPEN: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Opening " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_PROJECT_MOVE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Moving " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_LINK_COPY: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Copying link " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_LINK_CREATE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Creating link " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_LINK_DELETE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Deleting link " + event.resource.getName());
            }
            break;
        }

        case LifecycleEvent.PRE_LINK_MOVE: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Moving link " + event.resource.getName());
            }
            break;
        }

        default: {
            if (Tracer.isDebugging()) {
                Tracer.trace("Unknown event: " + event.kind);
            }
        }
        }
    }

}
