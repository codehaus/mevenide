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

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.launch.configuration.MavenArgumentsTab;


class CustomLaunchConfigurationDialog extends TitleAreaDialog {

    CustomLaunchConfigurationDialog(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        Composite mainArea = new Composite(parent, SWT.RESIZE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        mainArea.setLayout(layout);
        
        TableViewer viewer = new TableViewer(mainArea);
        viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createConfigurationTabs(mainArea);
        createNewDeleteButtons(mainArea);
        createApplyRevertButtons(mainArea);
        
        return mainArea;
    }

    private void createApplyRevertButtons(Composite mainArea) {
        Composite applyRevertButtonsArea = new Composite(mainArea, SWT.NULL);
        applyRevertButtonsArea.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        GridLayout applyRevertButtonsAreaLayout = new GridLayout();
        applyRevertButtonsAreaLayout.numColumns = 2;
        applyRevertButtonsArea.setLayout(applyRevertButtonsAreaLayout);
        
        Button applyButton = new Button(applyRevertButtonsArea, SWT.NULL);
        applyButton.setText("Apply");
        
        Button revertButton = new Button(applyRevertButtonsArea, SWT.NULL);
        revertButton.setText("Revert");
    }

    private void createNewDeleteButtons(Composite mainArea) {
        Composite newDeleteButtonsArea = new Composite(mainArea, SWT.NULL);
        
        GridLayout newDeleteButtonsAreaLayout = new GridLayout();
        newDeleteButtonsAreaLayout.makeColumnsEqualWidth = true;
        newDeleteButtonsAreaLayout.numColumns = 2;
        newDeleteButtonsArea.setLayout(newDeleteButtonsAreaLayout);
        
        createConfigurationHandlerButton(newDeleteButtonsArea, "New");
        createConfigurationHandlerButton(newDeleteButtonsArea, "Delete");
    }

    private void createConfigurationHandlerButton(Composite newDeleteButtonsArea, String buttonText) {
        Button newButton = new Button(newDeleteButtonsArea, SWT.NULL);
        newButton.setText(buttonText);
        newButton.setAlignment(SWT.CENTER);
        newButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void createConfigurationTabs(Composite mainArea) {
        TabFolder launchConfigurationTabFolder = new TabFolder(mainArea, SWT.NULL);
        launchConfigurationTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        MavenArgumentsTab argumentsTab = new MavenArgumentsTab();
        createTabItem(launchConfigurationTabFolder, argumentsTab, "Arguments");
        
        RefreshTab refreshTab = new RefreshTab();
        createTabItem(launchConfigurationTabFolder, refreshTab, "Refresh");
    }

    private void createTabItem(TabFolder launchConfigurationTabFolder, ILaunchConfigurationTab argumentsTab, String tabText) {
        TabItem mavenArgumentsItem = new TabItem(launchConfigurationTabFolder, SWT.NULL);
        argumentsTab.createControl(launchConfigurationTabFolder);
        argumentsTab.initializeFrom(new LaunchConfiguration(Mevenide.getInstance().getStateLocation().append(new Path("configurations/one.cfg"))){});
        mavenArgumentsItem.setControl(argumentsTab.getControl());
        mavenArgumentsItem.setImage(argumentsTab.getImage());
        mavenArgumentsItem.setText(tabText);
    }
    
    
}