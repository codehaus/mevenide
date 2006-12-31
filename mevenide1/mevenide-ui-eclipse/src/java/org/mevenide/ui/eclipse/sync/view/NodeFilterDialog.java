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

package org.mevenide.ui.eclipse.sync.view;

import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
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
import org.mevenide.ui.eclipse.Mevenide;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class NodeFilterDialog extends Dialog {
	
	private Text groupIdText;
	
	private Button enableArtifactFilteringButton;
	private Button enableDirectoryFilteringButton;
	
	private Button resourceButton;
	private Button aspectButton;
	private Button testButton;
	private Button sourceButton;
	private Button outputButton;

	private Group directoryTypeChoiceGroup;

	private boolean enableArtifactFiltering;
	private String groupIdFilter;
	
	private boolean enableDirectoryFiltering;
	private boolean filterSource;
	private boolean filterTest;
	private boolean filterAspect;
	private boolean filterResource;
	private boolean filterOutput;
	
	NodeFilterDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		super.setBlockOnOpen(true);
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
		enableArtifactFilteringButton.setSelection(getPreferenceStore().getBoolean(MavenArtifactNodeFilter.APPLY_FILTERS_KEY));
		enableArtifactFilteringButton.setText(Mevenide.getResourceString("NodeFilterDialog.Enable.Filtering.Text")); //$NON-NLS-1$
		GridData checkboxData = new GridData();
		checkboxData.grabExcessHorizontalSpace = false;
		enableArtifactFilteringButton.setLayoutData(checkboxData);
		
		groupIdText = new Text(composite, SWT.BORDER );
		groupIdText.setText(getPreferenceStore().getString(MavenArtifactNodeFilter.GROUP_ID_FILTER));
		groupIdText.setEnabled(enableArtifactFilteringButton.getSelection());
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.grabExcessHorizontalSpace = true;
		groupIdText.setLayoutData(textData);
		
		final Label label = new Label(composite, SWT.NULL);
		label.setText(Mevenide.getResourceString("NodeFilterDialog.RegexFiltering.Description.Label")); //$NON-NLS-1$
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
		enableDirectoryFilteringButton.setSelection(getPreferenceStore().getBoolean(DirectoryNodeFilter.APPLY_FILTERS_KEY));
		enableDirectoryFilteringButton.setText(Mevenide.getResourceString("NodeFilterDialog.Directory.Filtering.Text")); //$NON-NLS-1$
		GridData checkboxData = new GridData();
		checkboxData.grabExcessHorizontalSpace = false;
		enableDirectoryFilteringButton.setLayoutData(checkboxData);
		
		directoryTypeChoiceGroup = new Group(parent, SWT.NULL);
		directoryTypeChoiceGroup.setText(Mevenide.getResourceString("NodeFilterDialog.Directory.Filtering.Refine.Text")); //$NON-NLS-1$
		GridLayout layout = new GridLayout();
		directoryTypeChoiceGroup.setLayout(layout);
		GridData typeChoiceGridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		directoryTypeChoiceGroup.setLayoutData(typeChoiceGridData);
		
		String buttonText = Mevenide.getResourceString("NodeFilterDialog.Source.Filtering.Text"); //$NON-NLS-1$
		sourceButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		sourceButton.setSelection(getPreferenceStore().getBoolean(DirectoryNodeFilter.APPLY_SOURCE_FILTERS_KEY));
		
		buttonText = Mevenide.getResourceString("NodeFilterDialog.Test.Filtering.Text"); //$NON-NLS-1$
		testButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		testButton.setSelection(getPreferenceStore().getBoolean(DirectoryNodeFilter.APPLY_TEST_FILTERS_KEY));
		
		buttonText = Mevenide.getResourceString("NodeFilterDialog.Aspects.Filtering.Text"); //$NON-NLS-1$
		aspectButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		aspectButton.setSelection(getPreferenceStore().getBoolean(DirectoryNodeFilter.APPLY_ASPECT_FILTERS_KEY));
		
		buttonText = Mevenide.getResourceString("NodeFilterDialog.Resources.Filtering.Text"); //$NON-NLS-1$
		resourceButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		resourceButton.setSelection(getPreferenceStore().getBoolean(DirectoryNodeFilter.APPLY_RESOURCE_FILTERS_KEY));
		
		buttonText = Mevenide.getResourceString("NodeFilterDialog.Output.Filtering.Text"); //$NON-NLS-1$
		outputButton = createDirectoryTypeButton(directoryTypeChoiceGroup, buttonText);
		outputButton.setSelection(getPreferenceStore().getBoolean(DirectoryNodeFilter.APPLY_OUTPUT_FILTERS_KEY));
		
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
		outputButton.setEnabled(enableDirectoryFilteringButton.getSelection());
	}

	private Button createDirectoryTypeButton(Composite composite, String buttonText) {
		Button button = new Button(composite, SWT.CHECK);
		button.setText(buttonText);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		button.setLayoutData(data);
		return button;
	}

	protected void okPressed() {
		enableArtifactFiltering = this.enableArtifactFilteringButton.getSelection();
		groupIdFilter = this.groupIdText.getText();
		
		enableDirectoryFiltering = this.enableDirectoryFilteringButton.getSelection();
		filterSource = this.sourceButton.getSelection();
		filterTest = this.testButton.getSelection();
		filterAspect = this.aspectButton.getSelection();
		filterResource = this.resourceButton.getSelection();
		
        getPreferenceStore().setValue(DirectoryNodeFilter.APPLY_FILTERS_KEY, enableDirectoryFiltering);
        getPreferenceStore().setValue(DirectoryNodeFilter.APPLY_SOURCE_FILTERS_KEY, filterSource);
        getPreferenceStore().setValue(DirectoryNodeFilter.APPLY_TEST_FILTERS_KEY, filterTest);
        getPreferenceStore().setValue(DirectoryNodeFilter.APPLY_ASPECT_FILTERS_KEY, filterAspect);
        getPreferenceStore().setValue(DirectoryNodeFilter.APPLY_RESOURCE_FILTERS_KEY, filterResource);
        getPreferenceStore().setValue(DirectoryNodeFilter.APPLY_OUTPUT_FILTERS_KEY, filterOutput);
		
        getPreferenceStore().setValue(MavenArtifactNodeFilter.APPLY_FILTERS_KEY, enableArtifactFiltering);
        getPreferenceStore().setValue(MavenArtifactNodeFilter.GROUP_ID_FILTER, groupIdFilter);

        commitChanges();
		super.okPressed();
	}
	
	public boolean shouldEnableArtifactFiltering() {
		return enableArtifactFiltering;
	}
	public boolean shouldEnableDirectoryFiltering() {
		return enableDirectoryFiltering;
	}
	public boolean shouldFilterAspect() {
		return filterAspect;
	}
	public boolean shouldFilterResource() {
		return filterResource;
	}
	public boolean shouldFilterSource() {
		return filterSource;
	}
	public boolean shouldFilterTest() {
		return filterTest;
	}
	public boolean shouldFilterOutput() {
		return filterOutput;
	}
	public String getGroupIdFilter() {
		return groupIdFilter;
	}

    /**
     * Saves the changes made to preferences.
     * @return <tt>true</tt> if the preferences were saved
     */
    private boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Unable to save preferences.", e);
        }

        return false;
    }

    /**
     * @return the preference store to use in this object
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}
