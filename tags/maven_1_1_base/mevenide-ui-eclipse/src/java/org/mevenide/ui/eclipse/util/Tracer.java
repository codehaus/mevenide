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

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.internal.core.PDECore;
import org.mevenide.ui.eclipse.Mevenide;

public final class Tracer {

    // general debug flag for the plugin
    private static final boolean DEBUG;
    private static final boolean DEBUG_POM_MANAGEMENT;
    private static final boolean DEBUG_POM_SYNCHRONIZATION;
    
    static {
        //init debug options
        DEBUG = Mevenide.getInstance().isDebugging() || PDECore.getDefault().isDebugging(); // FIXME: Using PDECore.getDefault().isDebugging() since I cannot determine how to enable debugging for a plugin that is under development.
        DEBUG_POM_MANAGEMENT = "true".equalsIgnoreCase(Platform.getDebugOption(Mevenide.PLUGIN_ID + "/POM_Management")); //$NON-NLS-1$ 
        DEBUG_POM_SYNCHRONIZATION = "true".equalsIgnoreCase(Platform.getDebugOption(Mevenide.PLUGIN_ID + "/POM_Synchronization")); //$NON-NLS-1$ 
    }

    /**
     * Returns whether this plug-in is in debug mode.
     * By default plug-ins are not in debug mode.  A plug-in can put itself
     * into debug mode or the user can set an execution option to do so.
     *
     * @return whether this plug-in is in debug mode
     */
    public static final boolean isDebugging() {
        return DEBUG;
    }

    /**
     * Returns whether this plug-in is tracing POM management.
     * @return whether this plug-in is tracing POM management
     */
    public static final boolean tracePOMManagement() {
        return DEBUG && DEBUG_POM_MANAGEMENT;
    }

    /**
     * Returns whether this plug-in is tracing POM synchronization.
     * @return whether this plug-in is tracing POM synchronization
     */
    public static final boolean tracePOMSynchronization() {
        return DEBUG && DEBUG_POM_SYNCHRONIZATION;
    }

    /**
     * Print a debug message to the console. 
     * Pre-pend the message with the current date and the name of the current thread.
     */
    public static final void trace(final String message) {
        if (isDebugging()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(new Date(System.currentTimeMillis()));
            buffer.append(" - ["); //$NON-NLS-1$
            buffer.append(Thread.currentThread().getName());
            buffer.append("] "); //$NON-NLS-1$
            buffer.append(message);
            System.out.println(buffer.toString());
        }
    }
}