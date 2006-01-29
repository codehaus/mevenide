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

import java.util.Dictionary;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Constants;

/**
 * A dialog to display one or more errors to the user, as contained in an
 * <code>IStatus</code> object. If an error contains additional detailed
 * information then a Details button is automatically supplied, which shows or
 * hides an error details viewer when pressed by the user.
 * 
 * @see org.eclipse.core.runtime.IStatus
 */
public class NotifyingErrorDialog extends ErrorDialog {
    private Plugin plugin;

    /**
	 * Creates an error dialog. Note that the dialog will have no visual
	 * representation (no widgets) until it is told to open.
	 * <p>
	 * Normally one should use <code>openError</code> to create and open one
	 * of these. This constructor is useful only if the error object being
	 * displayed contains child items <it>and </it> you need to specify a mask
	 * which will be used to filter the displaying of these children.
	 * </p>
	 * 
	 * @param parentShell
	 *            the shell under which to create this dialog
	 * @param dialogTitle
	 *            the title to use for this dialog, or <code>null</code> to
	 *            indicate that the default title should be used
	 * @param message
	 *            the message to show in this dialog, or <code>null</code> to
	 *            indicate that the error's message should be shown as the
	 *            primary message
	 * @param status
	 *            the error to show to the user
	 * @param displayMask
	 *            the mask to use to filter the displaying of child items, as
	 *            per <code>IStatus.matches</code>
	 * @see org.eclipse.core.runtime.IStatus#matches(int)
	 */
	public NotifyingErrorDialog(Plugin plugin, Shell parentShell, String dialogTitle, String message, IStatus status, int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
        this.plugin = plugin;
	}

	/**
	 * This implementation of the <code>Dialog</code> framework method creates
	 * and lays out a composite and calls <code>createMessageArea</code> and
	 * <code>createCustomArea</code> to populate it. Subclasses should
	 * override <code>createCustomArea</code> to add contents below the
	 * message.
	 */
	protected Control createDialogArea(Composite parent) {
        Dictionary headers = plugin.getBundle().getHeaders();

        // look for trailing info like 'singleton=true' and remove it
        String id = valueOf(headers, Constants.BUNDLE_SYMBOLICNAME);
        int pos = id.indexOf(';');
        if (pos > 0) {
            id = id.substring(0, pos);
        }

        Composite composite = (Composite)super.createDialogArea(parent);

		Group infoBlock = new Group(composite, SWT.SHADOW_IN);
		infoBlock.setLayout(new GridLayout(2, false));
		infoBlock.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		infoBlock.setFont(composite.getFont());
		infoBlock.setText("Plug-in Information");

	    addString(infoBlock, "ID:");
	    addString(infoBlock, id);

	    addString(infoBlock, "Name:");
	    addString(infoBlock, valueOf(headers, Constants.BUNDLE_NAME));

	    addString(infoBlock, "Version:");
	    addString(infoBlock, valueOf(headers, Constants.BUNDLE_VERSION));

	    addString(infoBlock, "Provider:");
	    addString(infoBlock, valueOf(headers, Constants.BUNDLE_VENDOR));

	    addString(infoBlock, "Site:");
	    addString(infoBlock, valueOf(headers, Constants.BUNDLE_DOCURL));

	    addString(infoBlock, "Contact:");
	    addString(infoBlock, valueOf(headers, Constants.BUNDLE_CONTACTADDRESS));

	    return composite;
	}

	/**
	 * Opens an error dialog to display the given error. Use this method if the
	 * error object being displayed does not contain child items, or if you wish
	 * to display all such items without filtering.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param dialogTitle
	 *            the title to use for this dialog, or <code>null</code> to
	 *            indicate that the default title should be used
	 * @param message
	 *            the message to show in this dialog, or <code>null</code> to
	 *            indicate that the error's message should be shown as the
	 *            primary message
	 * @param status
	 *            the error to show to the user
	 * @return the code of the button that was pressed that resulted in this
	 *         dialog closing. This will be <code>Dialog.OK</code> if the OK
	 *         button was pressed, or <code>Dialog.CANCEL</code> if this
	 *         dialog's close window decoration or the ESC key was used.
	 */
	public static int openError(Plugin plugin, Shell parent, String dialogTitle, String message, IStatus status) {
		return openError(plugin, parent, dialogTitle, message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
	}

	/**
	 * Opens an error dialog to display the given error. Use this method if the
	 * error object being displayed contains child items <it>and </it> you wish
	 * to specify a mask which will be used to filter the displaying of these
	 * children. The error dialog will only be displayed if there is at least
	 * one child status matching the mask.
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the title to use for this dialog, or <code>null</code> to
	 *            indicate that the default title should be used
	 * @param message
	 *            the message to show in this dialog, or <code>null</code> to
	 *            indicate that the error's message should be shown as the
	 *            primary message
	 * @param status
	 *            the error to show to the user
	 * @param displayMask
	 *            the mask to use to filter the displaying of child items, as
	 *            per <code>IStatus.matches</code>
	 * @return the code of the button that was pressed that resulted in this
	 *         dialog closing. This will be <code>Dialog.OK</code> if the OK
	 *         button was pressed, or <code>Dialog.CANCEL</code> if this
	 *         dialog's close window decoration or the ESC key was used.
	 * @see org.eclipse.core.runtime.IStatus#matches(int)
	 */
	public static int openError(Plugin plugin, Shell parentShell, String title, String message, IStatus status, int displayMask) {
		ErrorDialog dialog = new NotifyingErrorDialog(plugin, parentShell, title, message, status, displayMask);
		return dialog.open();
	}

    private Label addString(Composite parent, String text) {
        Label label = new Label(parent, getMessageLabelStyle());
	    label.setText(text);
	    label.setLayoutData(new GridData());
	    return label;
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