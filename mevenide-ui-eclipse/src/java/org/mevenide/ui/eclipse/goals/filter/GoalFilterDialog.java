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
package org.mevenide.ui.eclipse.goals.filter;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.viewer.GoalsLabelProvider;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: GoalFilterDialog.java,v 1.1 4 avr. 2004 Exp gdodinet 
 * 
 */
public class GoalFilterDialog extends Dialog {
	
	private static final String ORIGIN_FILTER_KEY = "mevenide.goals.outline.filter.origin";
	private static final String CUSTOM_FILTERS_KEY = "mevenide.goals.outline.filter.custom";
	private static final String GOAL_FILTER_MESSAGE = "...";
	private static final String GOAL_FILTER_TITLE = "Goal Filter";
	
	private PreferencesManager preferencesManager;
	
	private StringFieldEditor regexFilterEditor;
	private String regex;
	
	private CheckboxTreeViewer goalsViewer; 
	
	//if all goals selected : needed to support main view shortcut
	private boolean filterOrigin; 
	
	public GoalFilterDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		preferencesManager = PreferencesManager.getManager();
		preferencesManager.loadPreferences();
	}
	
	protected Control createContents(Composite parent) {
	    
	    Control contents = super.createContents(parent);
	    
		setBlockOnOpen(true);
	    
        return contents;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		
		composite.setLayoutData(gridData);
		
		createRegexFilterEditor(composite);
		createSeparator(composite);
		createGoalsTree(composite);
		createSelectionButtonPanel(composite);
		
		goalsViewer.setInput(Element.NULL_ROOT);
		
		return composite;
	}
	
	private void createSelectionButtonPanel(Composite composite) {
		Button selectAllButton = new Button(composite, SWT.NULL);
		selectAllButton.setText("Select All");
		selectAllButton.setLayoutData(new GridData());
		selectAllButton.addMouseListener(
			new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					//goalsViewer.setCheckedElements(goalsViewer.);
				}
			}
		);
		
		Button deSelectAllButton = new Button(composite, SWT.NULL);
		deSelectAllButton.setText("Deselect All");
		GridData dsData = new GridData();
		dsData.verticalAlignment = GridData.BEGINNING;
		deSelectAllButton.setLayoutData(dsData);
		deSelectAllButton.addMouseListener(
			new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					//goalsViewer.setCheckedElements(goalsViewer.);
				}
			}
		);
	}
	
	private void createRegexFilterEditor(Composite composite) {
		regexFilterEditor = new StringFieldEditor(CUSTOM_FILTERS_KEY, "Custom filter regular expression (matching names will be hidden) :", composite);
		regexFilterEditor.fillIntoGrid(composite, 3);
		regexFilterEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		regexFilterEditor.load();
		regexFilterEditor.setStringValue(preferencesManager.getValue(CUSTOM_FILTERS_KEY));
	}

	private void createSeparator(Composite composite) {
		Label nullLabel = new Label(composite, SWT.NULL);
		GridData nullLabelData = new GridData();
		nullLabelData.horizontalSpan = 2;
		nullLabel.setLayoutData(nullLabelData);
	}

	private void createGoalsTree(Composite parent) {
		Text label = new Text(parent, SWT.READ_ONLY);
		label.setText("Filtered global goals");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		goalsViewer = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    	
    	GoalsProvider goalsProvider = new GoalsProvider();
        GoalsLabelProvider goalsLabelProvider = new GoalsLabelProvider();
    	
    	goalsViewer.setContentProvider(goalsProvider);
    	goalsViewer.setLabelProvider(goalsLabelProvider);
    	
    	GridData gridData = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
    	gridData.horizontalSpan = 2;
    	gridData.grabExcessVerticalSpace = true;
    	gridData.grabExcessHorizontalSpace = true;
    	gridData.heightHint = 300;
    
    	goalsViewer.getTree().setLayoutData(gridData);
	}
	
	protected void okPressed() {
	    super.okPressed();
	    
	    regex = regexFilterEditor.getStringValue();
	    preferencesManager.setValue(CUSTOM_FILTERS_KEY, regex);
	    
	    preferencesManager.store();
	}
	
	public boolean isFilterOrigin() {
		return filterOrigin;
	}
	
	public String getRegex() {
		return regex;
	}
}
