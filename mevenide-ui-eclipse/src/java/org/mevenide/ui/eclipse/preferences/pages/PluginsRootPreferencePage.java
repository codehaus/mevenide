/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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

import java.io.File;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.util.StringUtils;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class PluginsRootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    
    private static final String LAST_IMPORT_FOLDER = "PluginsRoot.Import.LastOpenFolder"; //$NON-NLS-1$
    private static final String LAST_EXPORT_FOLDER = "PluginsRoot.Export.LastOpenFolder"; //$NON-NLS-1$
    
    private PreferencesManager preferencesManager;
    
    public PluginsRootPreferencePage() {
        super(Mevenide.getResourceString("PluginsRootPreferencePage.title")); //$NON-NLS-1$
        preferencesManager = PreferencesManager.getManager();
		preferencesManager.loadPreferences();
    }

	protected Control createContents(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 2;
	    composite.setLayout(layout);
	    composite.setLayoutData(new GridData());
	  
	    createImportButton(composite);
	    createExportButton(composite);
	    
	    return composite;
	}
  

    private void createExportButton(Composite composite) {
        Button exportProperties = createButton(composite, "PluginsRoot.ExportButton.Text", "PluginsRoot.ExportButton.Tooltip"); //$NON-NLS-1$ //$NON-NLS-1$
	    
	    exportProperties.addSelectionListener(new SelectionListener() {
	        public void widgetDefaultSelected(SelectionEvent arg0) { }
	        public void widgetSelected(SelectionEvent arg0) {
                String choice = openFileChoiceDialog(LAST_EXPORT_FOLDER);
                if ( !StringUtils.isNull(choice) ) {
                    
                }
            }
	    });
    }

    private void createImportButton(Composite composite) {
        Button importProperties = createButton(composite, "PluginsRoot.ImportButton.Text", "PluginsRoot.ImportButton.Tooltip"); //$NON-NLS-1$ //$NON-NLS-2$
	    
	    importProperties.addSelectionListener(new SelectionListener() {
	        public void widgetDefaultSelected(SelectionEvent arg0) { }
	        public void widgetSelected(SelectionEvent arg0) {
                String choice = openFileChoiceDialog(LAST_IMPORT_FOLDER);
                if ( !StringUtils.isNull(choice) ) {
                    //effectively import the file
                    System.err.println(choice);
                }
            }
	    });
    }
    
    private Button createButton(Composite composite, String buttonText, String buttonTooltip) {
        Button exportProperties = new Button(composite, SWT.PUSH);
	    exportProperties.setText(Mevenide.getResourceString(buttonText)); 
	    exportProperties.setToolTipText(Mevenide.getResourceString(buttonTooltip)); 
	    GridData exportButtonData = new GridData();
	    exportButtonData.grabExcessHorizontalSpace = false;
	    exportProperties.setLayoutData(exportButtonData);
        return exportProperties;
    }


    public void init(IWorkbench workbench) {
    }

    private String openFileChoiceDialog(String filterPathKey) {
        FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
        dialog.setFilterExtensions(new String[] {"*.properties", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
        String lastOpenLocation = preferencesManager.getValue(filterPathKey); 
        dialog.setFilterPath(!StringUtils.isNull(lastOpenLocation) ? lastOpenLocation : System.getProperty("user.home")); //$NON-NLS-1$
        String choice = dialog.open();
        if ( !StringUtils.isNull(choice) ) {
            File f = new File(choice);
            lastOpenLocation = f.getParent();
            preferencesManager.setValue(filterPathKey, lastOpenLocation); 
        }
        return choice;
    }
}
    
    