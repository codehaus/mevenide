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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.model.Plugin;
import org.mevenide.ui.eclipse.goals.viewer.GoalsLabelProvider;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;

/**  
 * @todo factorize updateCheckedItems so that it can be shared between GoalsPickerDIalog and GoalsFilterDialog
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
	
	private Text patternText;
	private String regex;
	
	private CheckboxTreeViewer goalsViewer; 
	private List checkedItems = new ArrayList();
	
	//if all goals selected : needed to support main view shortcut
	private boolean filterOrigin;
	private GoalsProvider goalsProvider; 
	
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
	
	private void createSelectionButtonPanel(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		composite.setLayoutData(gridData);
		
		Button selectAllButton = new Button(composite, SWT.NULL);
		selectAllButton.setText("Select All");
		GridData sData = new GridData();
		sData.grabExcessHorizontalSpace = false;
		selectAllButton.setLayoutData(sData);
		selectAllButton.addMouseListener(
			new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					//goalsViewer.setSubtreeChecked(goalsViewer.getInput(), true);
				}
			}
		);
		
		Button deSelectAllButton = new Button(composite, SWT.NULL);
		deSelectAllButton.setText("Deselect All");
		GridData dsData = new GridData();
		deSelectAllButton.setLayoutData(dsData);
		deSelectAllButton.addMouseListener(
			new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					//goalsViewer.setSubtreeChecked(goalsViewer.getInput(), false);
				}
			}
		);
	}
	
	private void createRegexFilterEditor(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gridData);
		
		final Button checkbox = new Button(composite, SWT.CHECK);
		GridData checkboxData = new GridData();
		checkboxData.grabExcessHorizontalSpace = false;
		checkbox.setLayoutData(checkboxData);
		checkbox.setText("Custom filter regular expressions (matching names will be hidden) :");
		
		patternText = new Text(composite, SWT.BORDER );
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.grabExcessHorizontalSpace = true;
		patternText.setLayoutData(textData);
		patternText.setText(preferencesManager.getValue(CUSTOM_FILTERS_KEY));
		patternText.setEnabled(checkbox.getSelection());
		
		final Label label = new Label(composite, SWT.NULL);
		label.setText("Patterns are separated by comma");
		label.setEnabled(checkbox.getSelection());
		
		checkbox.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					label.setEnabled(checkbox.getSelection());
					patternText.setEnabled(checkbox.getSelection());
				}
			}
		);
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
    	
    	goalsProvider = new GoalsProvider();
		GoalsLabelProvider goalsLabelProvider = new GoalsLabelProvider();
    	
    	goalsViewer.setContentProvider(goalsProvider);
    	goalsViewer.setLabelProvider(goalsLabelProvider);
    	
    	GridData gridData = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
    	gridData.horizontalSpan = 2;
    	gridData.grabExcessVerticalSpace = true;
    	gridData.grabExcessHorizontalSpace = true;
    	gridData.heightHint = 300;
    
    	goalsViewer.getTree().setLayoutData(gridData);
    	
    	goalsViewer.addCheckStateListener(
        	new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
                	updateCheckedItems(event);
        		}
        	}
        );
	}
	
	private void updateCheckedItems(CheckStateChangedEvent e) {
		
		boolean isSelectionChecked = ((CheckboxTreeViewer) e.getSource()).getChecked(e.getElement());
		
		if ( e.getElement() instanceof Goal ) {
			Goal goal = (Goal) e.getElement();
			updateCheckedGoal(isSelectionChecked, goal);
		}
		else {
			Plugin plugin = (Plugin) e.getElement();
			//it is way too confusing when plugins are checkable. indeed when 
			//theres a default goal, both the default and the plugin should be 
			//checkable, thus we got the goal multiple times. I think its best
			//to just disable plugins.
			
			//updateCheckedPlugin(isSelectionChecked, plugin);
			
			//prevent user to check a plugin	
			goalsViewer.setChecked(plugin, false);
		}
	}
	
	private void updateCheckedGoal(boolean isSelectionChecked, Goal goal) {
		String fullyQualifiedGoalName = goal.getPlugin().getName();
		if ( !goal.getName().equals(Goal.DEFAULT_GOAL) ) {
			fullyQualifiedGoalName += ":" + goal.getName();
		}
		if ( isSelectionChecked ) {
			checkedItems.add(fullyQualifiedGoalName);
		}
		else {
			checkedItems.remove(fullyQualifiedGoalName);
		}
	}
	
	private void updateCheckedPlugin(boolean isSelectionChecked, Plugin plugin) {
		String pluginName = plugin.getName();
		String[] goals = goalsProvider.getGoalsGrabber().getGoals(pluginName);
		if ( goals != null && goals.length > 0 ) {
			if ( !Arrays.asList(goals).contains(Goal.DEFAULT_GOAL) ) {
				goalsViewer.setChecked(pluginName, false);
			}
			else {
				if ( isSelectionChecked ) {
					checkedItems.add(pluginName);
				}
				else {
					checkedItems.remove(pluginName);
				}
			}
		}
	}
	
	protected void okPressed() {
	    super.okPressed();
	    
	    regex = patternText.getText();
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
