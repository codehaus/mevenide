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
package org.mevenide.ui.eclipse.nature;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.launch.configuration.MavenArgumentsTab;


class CustomLaunchConfigurationDialog extends TitleAreaDialog {

    private ILaunchConfiguration launchConfiguration;

    private Composite parentComposite;
    
    private Composite layer;

    private TabFolder launchConfigurationTabFolder;
    
    private TabItem nullTab;
    
    private static final Point DEFAULT_INITIAL_DIALOG_SIZE = new Point(620, 560);
    
    CustomLaunchConfigurationDialog(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        parentComposite = parent;
        
        setTitle("Manage Maven Configurations");
        setTitleImage(Mevenide.getInstance().getImageRegistry().get(IImageRegistry.EXT_TOOLS_WIZ));
        setMessage("This dialog is used to control goal activation rules");
        getShell().setText("Maven Configurations");
        
        Composite mainArea = new Composite(parent, SWT.RESIZE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        mainArea.setLayout(layout);
        mainArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createTabFolder(mainArea);
        
        return mainArea;
    }

    private void createTabFolder(Composite parent) {
        TableViewer viewer = new TableViewer(parent);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = false;
        viewer.getTable().setLayoutData(gd);
        
        Composite configurationTabsArea = new Composite(parent, SWT.NULL);
        configurationTabsArea.setLayout(new GridLayout());
        configurationTabsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createConfigurationNameArea(configurationTabsArea);
        
        launchConfigurationTabFolder = new TabFolder(configurationTabsArea, SWT.NULL);
        launchConfigurationTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        nullTab = new TabItem(launchConfigurationTabFolder, SWT.NULL);
       
        createNewDeleteButtons(parent);
        createApplyRevertButtons(parent);
    }

    private void createApplyRevertButtons(Composite mainArea) {
        Composite applyRevertButtonsArea = new Composite(mainArea, SWT.NULL);
        applyRevertButtonsArea.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        GridLayout applyRevertButtonsAreaLayout = new GridLayout();
        applyRevertButtonsAreaLayout.numColumns = 2;
        applyRevertButtonsArea.setLayout(applyRevertButtonsAreaLayout);
        
        Button applyButton = new Button(applyRevertButtonsArea, SWT.NULL);
        applyButton.setText("Apply");
        applyButton.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                try {
                    launchConfiguration.getWorkingCopy().doSave();
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        });
        
        Button revertButton = new Button(applyRevertButtonsArea, SWT.NULL);
        revertButton.setText("Revert");
    }

    private void createNewDeleteButtons(Composite mainArea) {
        Composite newDeleteButtonsArea = new Composite(mainArea, SWT.NULL);
        
        GridLayout newDeleteButtonsAreaLayout = new GridLayout();
        newDeleteButtonsAreaLayout.makeColumnsEqualWidth = true;
        newDeleteButtonsAreaLayout.numColumns = 2;
        newDeleteButtonsArea.setLayout(newDeleteButtonsAreaLayout);
        
        Button newButton = createConfigurationHandlerButton(newDeleteButtonsArea, "New");
        newButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if ( layer != null ) {
                    layer.dispose();
                }
                createLaunchConfiguration();
                createTabItems(parentComposite);
            }
        });
        createConfigurationHandlerButton(newDeleteButtonsArea, "Delete");
    }

    private Button createConfigurationHandlerButton(Composite newDeleteButtonsArea, String buttonText) {
        Button newButton = new Button(newDeleteButtonsArea, SWT.NULL);
        newButton.setText(buttonText);
        GridData data = new GridData();
		newButton.setLayoutData(data);
        newButton.setAlignment(SWT.CENTER);
        newButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return newButton;
    }

    private void createTabItems(Composite mainArea) {
        
        nullTab.dispose();
        
        MavenArgumentsTab argumentsTab = new MavenArgumentsTab();
        createTabItem(launchConfigurationTabFolder, argumentsTab, "Arguments", launchConfiguration);
        
        RefreshTab refreshTab = new RefreshTab();
        
        createTabItem(launchConfigurationTabFolder, refreshTab, "Refresh", launchConfiguration);
        
        launchConfigurationTabFolder.update();
        launchConfigurationTabFolder.redraw();
    }
    
    private void createLaunchConfiguration() {
        try {
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType type = launchManager.getLaunchConfigurationType("org.mevenide.ui.launching.ActionDefinitionConfigType");
            String name = launchManager.generateUniqueLaunchConfigurationNameFrom("(new configuration)");
            ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(ResourcesPlugin.getWorkspace().getRoot(), name);
            Map optionsMap = new HashMap();
			workingCopy.setAttribute(MavenArgumentsTab.OPTIONS_MAP, optionsMap);
            CommonTab tab = new CommonTab();
            tab.setDefaults(workingCopy);
            tab.dispose();
            launchConfiguration = workingCopy.doSave();
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    
    protected Point getInitialSize() {	
        IDialogSettings settings = getDialogSettings();
		try {
			int x, y;
			x = settings.getInt(IDebugPreferenceConstants.DIALOG_WIDTH);
			y = settings.getInt(IDebugPreferenceConstants.DIALOG_HEIGHT);
			return new Point(x, y);
		} catch (NumberFormatException e) {
		}
		return DEFAULT_INITIAL_DIALOG_SIZE;
	}
    
    protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = DebugUIPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(getDialogSettingsSectionName());
		if (section == null) {
			section = settings.addNewSection(getDialogSettingsSectionName());
		} 
		return section;
	}
    
    protected String getDialogSettingsSectionName() {
		return IDebugUIConstants.PLUGIN_ID + ".LAUNCH_CONFIGURATIONS_DIALOG_SECTION"; //$NON-NLS-1$
	}
    
    private void createConfigurationNameArea(Composite mainArea) {
        Composite nameArea = new Composite(mainArea, SWT.NULL);
        GridLayout nameAreaLayout = new GridLayout();
        nameAreaLayout.numColumns = 2;
        nameAreaLayout.makeColumnsEqualWidth = false;
        nameArea.setLayout(nameAreaLayout);
        nameArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Text configurationNameLabel = new Text(nameArea, SWT.READ_ONLY);
        configurationNameLabel.setText("Name:");
        
        Text configurationNameText = new Text(nameArea, SWT.BORDER);
        configurationNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void createTabItem(TabFolder launchConfigurationTabFolder, ILaunchConfigurationTab argumentsTab, String tabText, ILaunchConfiguration launchConfiguration) {
        TabItem mavenArgumentsItem = new TabItem(launchConfigurationTabFolder, SWT.NULL);
        argumentsTab.createControl(launchConfigurationTabFolder);
        argumentsTab.initializeFrom(launchConfiguration);
        mavenArgumentsItem.setControl(argumentsTab.getControl());
        mavenArgumentsItem.setImage(argumentsTab.getImage());
        mavenArgumentsItem.setText(tabText);
    }
    
    
}