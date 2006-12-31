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

package org.mevenide.ui.eclipse.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

/**
 * The exception handler shows an error dialog when one of its displayError methods
 * is called. If the passed exception is a <code>CoreException</code> an error dialog
 * pops up showing the exception's status information. For a <code>InvocationTargetException</code>
 * a normal message dialog pops up showing the exception's message. Additionally the exception
 * is written to the platform log.
 */
public class ExceptionHandler {
    private Plugin plugin;

    /**
     * Initializes this exception handler with contact information from the plugin.
     * @param plugin the plugin to report against
     */
    public ExceptionHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param s
     *            the error to show to the user
     */
    public void displayError(String title, String message, IStatus s) {
        this.plugin.getLog().log(s);

        MultiStatus status = createMultiStatus(message, s);
        addStackTrace(status, s.getException());

        String dialogMsg
            = message + "\n"
            + "Press 'Details >>' for more information.\n"
            + "See the error log for more information."
            ;

        // if the 'message' resource string and the IStatus' message are the same,
        // don't show both in the dialog
        if (s != null && message.equals(s.getMessage())) {
            dialogMsg = null;
        }

        openError(this.plugin, title, dialogMsg, status);
    }

    private void addStackTrace(MultiStatus s, Throwable t) {
        if (s != null && t != null) {
	        synchronized (s) {
	            String pluginId = this.plugin.getBundle().getSymbolicName();

	            s.add(new Status(IStatus.INFO, pluginId, 0, t.toString(), null));

	            StackTraceElement[] trace = t.getStackTrace();
	            for (int i = 0; i < trace.length; ++i) {
	                s.add(new Status(IStatus.INFO, pluginId, 0, "   at " + trace[i], null));
	            }

	            Throwable cause = t.getCause();
	            if (cause != null) {
	                addStackTraceAsCause(pluginId, cause, s, trace);
	            }
	        }
	    }
    }

    private void addStackTraceAsCause(String pluginId, Throwable t, MultiStatus s, StackTraceElement[] causedTrace) {
        // Compute number of frames in common between t and caused
        StackTraceElement[] trace = t.getStackTrace();
        int m = trace.length - 1, n = causedTrace.length - 1;
        while (m >= 0 && n >=0 && trace[m].equals(causedTrace[n])) {
            m--; n--;
        }
        int framesInCommon = trace.length - 1 - m;

        s.add(new Status(IStatus.INFO, pluginId, 0, "Caused by: " + t, null));

        for (int i = 0; i <= m; ++i) {
            s.add(new Status(IStatus.INFO, pluginId, 0, "   tat " + trace[i], null));
        }

        if (framesInCommon != 0) {
            s.add(new Status(IStatus.INFO, pluginId, 0, "   ... " + framesInCommon + " more", null));
        }

        // Recurse if we have a cause
        Throwable cause = t.getCause();
        if (cause != null) {
            addStackTraceAsCause(pluginId, cause, s, trace);
        }
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param t
     *            the error to show to the user
     */
    public void displayError(String title, String message, Throwable t) {
        if (t instanceof InvocationTargetException) {
            t = ((InvocationTargetException) t).getTargetException();
        }

        if (t instanceof CoreException) {
            displayError(title, message, ((CoreException) t).getStatus());
        } else {
            displayError(title, message, newErrorStatus(message, t));
//            displayError(title, message, newErrorStatus(t.getLocalizedMessage(), t));
        }
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a MultiStatus that is used to display
     * information in the Details pane of the Error Dialog.
     * @param s the status to report
     * @return a MultiStatus suitable for use with the Error Dialog
     */
    private MultiStatus createMultiStatus(String message, IStatus s) {
        MultiStatus status = new MultiStatus(s.getPlugin(), s.getCode(), message, s.getException());
        status.merge(s);
        return status;
    }

    /**
     * Returns a new error status for this plugin with the given message
     * @param message the message to be included in the status
     * @param exception the exception to be included in the status or <code>null</code> if none
     * @return a new error status
     */
    private IStatus newErrorStatus(String message, Throwable exception) {
        return new Status(
            IStatus.ERROR,
			this.plugin.getBundle().getSymbolicName(),
			StatusConstants.INTERNAL_ERROR,
			(message == null)? "An internal error occurred.": message,
			exception
	    );
    }

    /**
     * Opens an error dialog to display the given error.
     * The dialog is opened in the UI thread regardless of the calling thread.
     * 
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param status
     *            the error to show to the user
     */
    private static void openError(final Plugin plugin, final String title, final String message, final IStatus status) {
        Display display = getStandardDisplay();
        display.asyncExec(new Runnable() {
            public void run() {
                NotifyingErrorDialog.openError(plugin, null, title, message, status);
            }
        });
    }

    /**
     * Returns the standard display to be used. The method first checks, if
     * the thread calling this method has an associated disaply. If so, this
     * display is returned. Otherwise the method returns the default display.
     */
    private static final Display getStandardDisplay() {
        final Display display = Display.getCurrent();
        return (display != null) ? display : Display.getDefault();
    }
}
