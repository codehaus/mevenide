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

import java.util.Dictionary;

import org.eclipse.core.internal.events.ILifecycleListener;
import org.eclipse.core.internal.events.LifecycleEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Constants;

/**
 * TODO: Describe what LifecycleListener represents.
 */
public class LifecycleListener implements ILifecycleListener {
    private Plugin plugin;
    private String pluginId;

    /**
     * Initializes this exception handler with contact information from the plugin.
     * @param plugin the plugin to report against
     */
    public LifecycleListener(Plugin plugin) {
        this.plugin = plugin;

        final Dictionary headers = plugin.getBundle().getHeaders();
        this.pluginId = valueOf(headers, Constants.BUNDLE_SYMBOLICNAME);

        // look for trailing info like 'singleton=true' and remove it
        int pos = pluginId.indexOf(';');
        if (pos > 0) {
            this.pluginId = this.pluginId.substring(0, pos);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.internal.events.ILifecycleListener#handleEvent(org.eclipse.core.internal.events.LifecycleEvent)
     */
    public void handleEvent(LifecycleEvent event) throws CoreException {
        switch (event.kind) {
        case LifecycleEvent.PRE_PROJECT_CLOSE: {
            info("Closing " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_PROJECT_CHANGE: {
            info("Changing " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_PROJECT_COPY: {
            info("Copying " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_PROJECT_CREATE: {
            info("Creating " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_PROJECT_DELETE: {
            info("Deleting " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_PROJECT_OPEN: {
            info("Opening " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_PROJECT_MOVE: {
            info("Moving " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_LINK_COPY: {
            info("Copying link " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_LINK_CREATE: {
            info("Creating link " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_LINK_DELETE: {
            info("Deleting link " + event.resource.getName());
            break;
        }

        case LifecycleEvent.PRE_LINK_MOVE: {
            info("Moving link " + event.resource.getName());
            break;
        }

        default: {
            info("Unknown event: " + event.kind);
        }
        }
    }

    /**
     * Logs an informational message in the Eclipse log for this plugin.
     * @param message the message to log
     */
    private void info(final String message) {
        if (false) {
            final IStatus status = new Status(IStatus.INFO, this.pluginId, 0, message, null);
            this.plugin.getLog().log(status);
        }
    }

    /**
     * Returns the value of the specified key or
     * the empty string if the values does not exist.
     * @param headers the headers to search
     * @param key the name of the value to return
     * @return the value represented by key
     */
    private static String valueOf(final Dictionary headers, final String key) {
        String result = null;

        if (key != null) {
            result = (String)headers.get(key);
        }

        return (result == null)? "": result;
    }

}
