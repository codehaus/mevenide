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
package org.mevenide.ui.eclipse.sync.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class NodeFilterDialog extends Dialog {
	
	private PreferencesManager preferencesManager;
	
	private Text groupIdText;
	
	private Button enableArtifactFilteringButton;
	private Button enableDirectoryFilteringButton;
	
	private Button resourceButton;
	private Button aspectButton;
	private Button testButton;
	private Button sourceButton;

	private Group directoryTypeChoiceGroup;
	
	NodeFilterDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		super.setBlockOnOpen(true);
		preferencesManager = PreferencesManager.getManager(); 
		preferencesManager.loadPreferences();
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		
		composite.setLayoutData(gridData);
		
		createArtifactFilterDialogArea(composite);
		createDirectoryFilterDialogArea(composite);
		
		return composite;
	}

	private void createArtifactFilterDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gridData);
		
		enableArtifactFilteringButton = new Button(composite, SWT.CHECK);
		enableArtifactFilteringButton.setSelection(preferencesManager.getBooleanValue(MavenArtifactNodeFilter.APPLY_FILTERS_KEY));
		enableArtifactFilteringButton.setText("Enable artifact filtering");
		GridData checkboxData = new GridData();
		checkboxData.grabExcessHorizontalSpace = false;
		enableArtifactFilteringButton.setLayoutData(checkboxData);
		
		groupIdText = new Text(composite, SWT.BORDER );
		groupIdText.setText(preferencesManager.getValue(MavenArtifactNodeFilter.GROUP_ID_FILTER));
		groupIdText.setEnabled(enableArtifactFilteringButton.getSelection());
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.grabExcessHorizontalSpace = true;
		groupIdText.setLayoutData(textData);
		
		final Label label = new Label(composite, SWT.NULL);
		label.setText("(Matching groupId will be hidden. if not set all artifacts will be hidden.)");
		label.setEnabled(enableArtifactFilteringButton.getSelection());
		
		enableArtifactFilteringButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					groupIdText.setEnabled(enableArtifactFilteringButton.getSelection());
					label.setEnabled(enableArtifactFilteringButton.getSelection());
				}
			}
		);
		
	}

	private void createDirectoryFilterDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gridData);
		
		enableDirectoryFilteringButton = new Button(composite, SWT.CHECK);
		enableDirectoryFilteringButton.setSelection(preferencesManager.getBooleanValue(MavenArtifactNodeFilter.APPLY_FILTERS_KEY));
		enableDirectoryFilteringButton.setText("Enable directory filtering");
		GridData checkboxData = new GridData();
		checkboxData.grabExcessHorizontalSpace = false;
		enableDirectoryFilteringButton.setLayoutData(checkboxData);
		
		directoryTypeChoiceGroup = new Group(parent, SWT.NULL);
		directoryTypeChoiceGroup.setText("Refine directory filtering semantic");
		GridLayout layout = new GridLayout();
		directoryTypeChoiceGroup.setLayout(layout);
		GridData typeChoiceGridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		directoryTypeChoiceGroup.setLayoutData(typeChoiceGridData);
		
		String buttonText = "Filter Source directories";
		sourceButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		
		buttonText = "Filter Test directories";
		testButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		
		buttonText = "Filter Aspect directories";
		aspectButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		
		buttonText = "Filter Resource directories";
		resourceButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		
		enableDirectoryFilteringButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					updateDirectoryFilterControlEnablement();					
				}
			}
		);
		
		updateDirectoryFilterControlEnablement();
	}

	private void updateDirectoryFilterControlEnablement() {
		directoryTypeChoiceGroup.setEnabled(enableDirectoryFilteringButton.getSelection());
		sourceButton.setEnabled(enableDirectoryFilteringButton.getSelection());
		testButton.setEnabled(enableDirectoryFilteringButton.getSelection());
		aspectButton.setEnabled(enableDirectoryFilteringButton.getSelection());
		resourceButton.setEnabled(enableDirectoryFilteringButton.getSelection());
	}

	private Button createDirectoryTypeButton(Composite composite, String buttonText) {
		Button button = new Button(composite, SWT.CHECK);
		button.setText(buttonText);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		button.setLayoutData(data);
		return button;
	}

	
	
}
