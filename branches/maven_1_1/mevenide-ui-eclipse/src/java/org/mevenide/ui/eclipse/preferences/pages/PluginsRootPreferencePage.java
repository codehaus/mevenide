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

package org.mevenide.ui.eclipse.preferences.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.mevenide.ui.eclipse.preferences.dynamic.DynamicPreferencesManager;
import org.mevenide.util.StringUtils;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class PluginsRootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private static final Log log = LogFactory.getLog(PluginsRootPreferencePage.class);
    
    private static final String PAGE_NAME = Mevenide.getResourceString("PluginsRootPreferencePage.title"); //$NON-NLS-1$
//    private static final String PAGE_DESC = Mevenide.getResourceString("PluginsRootPreferencePage.description"); //$NON-NLS-1$

    private static final String LAST_IMPORT_FOLDER = "PluginsRoot.Import.LastOpenFolder"; //$NON-NLS-1$
    private static final String LAST_EXPORT_FOLDER = "PluginsRoot.Export.LastOpenFolder"; //$NON-NLS-1$
    
    public PluginsRootPreferencePage() {
        super(PAGE_NAME);
//        super.setDescription(PAGE_DESC);
        super.setPreferenceStore(Mevenide.getInstance().getCustomPreferenceStore());
        super.noDefaultAndApplyButton();
    }

	protected Control createContents(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 1;
	    composite.setLayout(layout);
	    GridData data = new GridData(GridData.FILL_BOTH);
	    data.grabExcessVerticalSpace = true;
	    composite.setLayoutData(data);
	  
	    Composite buttonsIndirectionComposite = createButtonsIndirectionComposite(composite);
	    
	    createImportButton(buttonsIndirectionComposite);
	    createExportButton(buttonsIndirectionComposite);
	    
	    return composite;
	}
  

    private Composite createButtonsIndirectionComposite(Composite composite) {
        Composite buttonsArea = new Composite(composite, SWT.NULL);
	    GridData buttonsAreaData = new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_BOTH);
	    buttonsAreaData.grabExcessHorizontalSpace = true;
	    buttonsArea.setLayoutData(buttonsAreaData);
	    GridLayout buttonsAreaLayout = new GridLayout();
	    buttonsAreaLayout.numColumns = 1;
	    buttonsArea.setLayout(buttonsAreaLayout);
	    
	    Composite buttonsIndirection = new Composite(composite, SWT.NULL);
	    GridData buttonsIndirectionData = new GridData(GridData.VERTICAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_END | GridData.FILL_BOTH);
	    buttonsIndirectionData.grabExcessHorizontalSpace = true;
	    buttonsIndirection.setLayoutData(buttonsIndirectionData);
	    GridLayout buttonsIndirectionLayout = new GridLayout();
	    buttonsIndirectionLayout.numColumns = 2;
	    buttonsIndirection.setLayout(buttonsIndirectionLayout);
        return buttonsIndirection;
    }

    private void createExportButton(Composite composite) {
        Button exportProperties = createButton(composite, "PluginsRoot.ExportButton.Text", "PluginsRoot.ExportButton.Tooltip"); //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$
	    
	    exportProperties.addSelectionListener(new SelectionListener() {
	        public void widgetDefaultSelected(SelectionEvent arg0) { }
	        public void widgetSelected(SelectionEvent arg0) {
                String choice = openFileChoiceDialog(LAST_EXPORT_FOLDER);
                if ( !StringUtils.isNull(choice) ) {
                    Properties properties = DynamicPreferencesManager.getDynamicManager().exportProperties();
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(choice);
                        properties.store(output, Mevenide.getResourceString("PluginsRootPreferencePage.GeneratedBy") + new SimpleDateFormat("yyyyMMdd").format(new Date())); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    catch (Exception e) {
                        String message = "Unable to store properties";  //$NON-NLS-1$
                        log.error(message, e);
                    }
                    finally {
                        if ( output != null ) {
                            try {
                                output.close();
                            }
                            catch (Exception e1) {
                                String message = "Unable to close outputStream";  //$NON-NLS-1$
                                log.warn(message, e1);
                            }
                        }
                    }
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
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileInputStream(choice));
                        DynamicPreferencesManager.getDynamicManager().importProperties(properties);
                    }
                    catch (Exception e) {
                        String message = "Unable to load properties file";  //$NON-NLS-1$
                        log.error(message, e);
                    }
                }
            }
	    });
    }
    
    private Button createButton(Composite composite, String buttonText, String buttonTooltip) {
        Button exportProperties = new Button(composite, SWT.PUSH);
	    exportProperties.setText(Mevenide.getResourceString(buttonText)); 
	    exportProperties.setToolTipText(Mevenide.getResourceString(buttonTooltip)); 
	    exportProperties.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        return exportProperties;
    }


    public void init(IWorkbench workbench) {
    }

    private String openFileChoiceDialog(String filterPathKey) {
        FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
        dialog.setFilterExtensions(new String[] {"*.properties", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
        String lastOpenLocation = getPreferenceStore().getString(filterPathKey); 
        dialog.setFilterPath(!StringUtils.isNull(lastOpenLocation) ? lastOpenLocation : System.getProperty("user.home")); //$NON-NLS-1$
        String choice = dialog.open();
        if ( !StringUtils.isNull(choice) ) {
            File f = new File(choice);
            lastOpenLocation = f.getParent();
            getPreferenceStore().setValue(filterPathKey, lastOpenLocation); 
        }
        return choice;
    }
}
