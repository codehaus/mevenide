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

import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Constants;

/**
 * The exception handler shows an error dialog when one of its displayError methods
 * is called. If the passed exception is a <code>CoreException</code> an error dialog
 * pops up showing the exception's status information. For a <code>InvocationTargetException</code>
 * a normal message dialog pops up showing the exception's message. Additionally the exception
 * is written to the platform log.
 */
public class ExceptionHandler {
    private AbstractUIPlugin plugin;
    private String           pluginId;
    private String           pluginName;
    private String[]         pluginInfo = new String[6];

    /**
     * Initializes this exception handler with contact information from the plugin.
     * @param plugin the plugin to report against
     */
    public ExceptionHandler(AbstractUIPlugin plugin) {
        this.plugin = plugin;

        final Dictionary headers = plugin.getBundle().getHeaders();

        this.pluginId = valueOf(headers, Constants.BUNDLE_SYMBOLICNAME);
        this.pluginName = valueOf(headers, Constants.BUNDLE_NAME);

        // look for trailing info like 'singleton=true' and remove it
        int pos = pluginId.indexOf(';');
        if (pos > 0) {
            this.pluginId = this.pluginId.substring(0, pos);
        }

        this.pluginInfo = new String[6];
        this.pluginInfo[0] = "Plug-in ID:       " + this.pluginId;
        this.pluginInfo[1] = "Plug-in Name:     " + this.pluginName;
        this.pluginInfo[2] = "Plug-in Version:  " + valueOf(headers, Constants.BUNDLE_VERSION);
        this.pluginInfo[3] = "Plug-in Provider: " + valueOf(headers, Constants.BUNDLE_VENDOR);
        this.pluginInfo[4] = "Plug-in Site:     " + valueOf(headers, Constants.BUNDLE_DOCURL);
        this.pluginInfo[5] = "Plug-in Contact:  " + valueOf(headers, Constants.BUNDLE_CONTACTADDRESS);
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

        MultiStatus status = createMultiStatus(s);

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

        openError(title, dialogMsg, status);
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
     * @param e
     *            the error to show to the user
     */
    public void displayError(String title, String message, CoreException e) {
        displayError(title, message, e.getStatus());
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
            displayError(title, message, (CoreException) t);
        } else {
            displayError(title, message, newErrorStatus(t.getLocalizedMessage(), t));
        }
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a MultiStatus that is used to display
     * information in the Details pane of the Error Dialog.
     * @param s the status to report
     * @return a MultiStatus suitable for use with the Error Dialog
     */
    private MultiStatus createMultiStatus(IStatus s) {
        int severity = s.getSeverity();
        int code = s.getCode();
        String msg = s.getMessage();

        MultiStatus status = new MultiStatus(this.pluginId, code, msg, null);
        status.add(new Status(severity, this.pluginId, 0, msg, null));
        status.add(new Status(severity, this.pluginId, 0, "", null)); // adds a blank line
        for (int i = 0; i < this.pluginInfo.length; ++i) {
            status.add(new Status(severity, this.pluginId, 0,
                    this.pluginInfo[i], null));
        }

        return status;
    }

    /**
     * Returns a new error status for this plugin with the given message
     * @param message the message to be included in the status
     * @param exception the exception to be included in the status or <code>null</code> if none
     * @return a new error status
     */
    private IStatus newErrorStatus(String message, Throwable exception) {
        return new Status(IStatus.ERROR, this.pluginId, StatusConstants.INTERNAL_ERROR, message, exception);
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
    private static void openError(final String title, final String message, final IStatus status) {
        Display display = getStandardDisplay();
        display.asyncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(null, title, message, status);
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
