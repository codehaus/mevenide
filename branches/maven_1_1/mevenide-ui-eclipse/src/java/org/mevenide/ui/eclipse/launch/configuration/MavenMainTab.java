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
package org.mevenide.ui.eclipse.launch.configuration;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.StringUtils;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenMainTab extends ExternalToolsMainTab {
	
	public MavenMainTab() {
		super();
	}
	
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topLayout.makeColumnsEqualWidth = false;
		composite.setLayout(topLayout);		
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		composite.setFont(font);
		
		//just to avoid NPE in ExternalToolsMainTab
		createLocationComponent(composite);		
		new Label(composite, SWT.NULL);
		
		createWorkDirectoryComponent(composite);
		
		locationField.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			}
		);
		
	}
	
	protected void createLocationComponent(Composite composite) {
		super.createLocationComponent(composite);
		super.locationField.setEnabled(false);
		super.fileLocationButton.setEnabled(false);
		super.variablesLocationButton.setEnabled(false);
		super.workspaceLocationButton.setEnabled(false);
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
	    updateWorkingDirectory(configuration);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String location = workDirectoryField.getText().trim();
		if (location.length() == 0) {
			configuration.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, (String)null);
		} else {
			configuration.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, location);
		}
	}
	
	protected String getWorkingDirectoryLabel() {
		return Mevenide.getResourceString("MavenMainTab.working.directory.label"); //$NON-NLS-1$
	}
	
	public boolean isValid(ILaunchConfiguration launchConfig) {
	    if ( StringUtils.isNull(workDirectoryField.getText()) ) {
	        setErrorMessage(Mevenide.getResourceString("MavenMainTab.working.directory.null")); //$NON-NLS-1$
	    }
	    else {
	       setErrorMessage(null);
	    }
        return !StringUtils.isNull(workDirectoryField.getText());
    }
}
