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

package org.mevenide.ui.eclipse.preferences.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 *
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 */
public class MevenidePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String PAGE_NAME = Mevenide.getResourceString("MevenidePreferencePage.title"); //$NON-NLS-1$
    private static final String PAGE_DESC = Mevenide.getResourceString("MevenidePreferencePage.description"); //$NON-NLS-1$
    private static final String AUTO_NAME = Mevenide.getResourceString("MevenidePreferencePage.autosync.title"); //$NON-NLS-1$

    /**
     * Initializes a new instance of MevenidePreferencePage.
     */
    public MevenidePreferencePage() {
        super(GRID);
        super.setDescription(PAGE_DESC);
        super.setPreferenceStore(Mevenide.getInstance().getPreferenceStore());
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void createFieldEditors() {
       addField(new BooleanFieldEditor(MevenidePreferenceKeys.AUTOSYNC_ENABLED, AUTO_NAME, getFieldEditorParent()));
    }
  
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
    }
}
    
    