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

package org.mevenide.ui.eclipse.goals.filter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
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
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.model.Plugin;
import org.mevenide.ui.eclipse.goals.view.GoalsLabelProvider;
import org.mevenide.util.StringUtils;

/**  
 * @todo factorize updateCheckedItems so that it can be shared between GoalsPickerDIalog and GoalsFilterDialog
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: GoalFilterDialog.java,v 1.1 4 avr. 2004 Exp gdodinet 
 * 
 */
public class GoalFilterDialog extends Dialog {
	
	private Text patternText;
	private boolean shouldApplyCustomFilters;
	private String regex;
	
	private CheckboxTreeViewer goalsViewer; 
	private List checkedItems = new ArrayList();
	
	private GoalsProvider goalsProvider;
	private Button applyCustomFiltersButton;
	private Button deSelectAllButton;
	private String filteredGoals; 
	
	public GoalFilterDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
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
		initializeTree();
		
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
		selectAllButton.setText(Mevenide.getResourceString("GoalFilterDialog.SelectAll")); //$NON-NLS-1$
		GridData sData = new GridData();
		sData.grabExcessHorizontalSpace = false;
		selectAllButton.setLayoutData(sData);
		selectAllButton.addMouseListener(
			new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					setAllChecked(true);
				}
			}
		);
		
		deSelectAllButton = new Button(composite, SWT.NULL);
		deSelectAllButton.setText(Mevenide.getResourceString("GoalFilterDialog.DeselectAll")); //$NON-NLS-1$
		GridData dsData = new GridData();
		deSelectAllButton.setLayoutData(dsData);
		deSelectAllButton.addMouseListener(
			new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					setAllChecked(false);
				}
			}
		);
	}
	
	private void setAllChecked(boolean state) {
	    TreeItem[] children = goalsViewer.getTree().getItems();
		for (int i = 0; i < children.length; i++) {
			TreeItem item = children[i];
			item.setChecked(state);
		}
		goalsViewer.setGrayedElements(new Object[0]);
	}
	
	private void createRegexFilterEditor(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gridData);
		
		applyCustomFiltersButton = new Button(composite, SWT.CHECK);
		applyCustomFiltersButton.setSelection(getPreferenceStore().getBoolean(CustomPatternFilter.APPLY_CUSTOM_FILTERS_KEY));
		applyCustomFiltersButton.setText(Mevenide.getResourceString("GoalFilterDialog.RegexFilter.Text")); //$NON-NLS-1$
		GridData checkboxData = new GridData();
		checkboxData.grabExcessHorizontalSpace = false;
		applyCustomFiltersButton.setLayoutData(checkboxData);
		
		patternText = new Text(composite, SWT.BORDER );
		patternText.setText(getPreferenceStore().getString(CustomPatternFilter.CUSTOM_FILTERS_KEY));
		patternText.setEnabled(applyCustomFiltersButton.getSelection());
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.grabExcessHorizontalSpace = true;
		patternText.setLayoutData(textData);
		
		final Label label = new Label(composite, SWT.NULL);
		label.setText(Mevenide.getResourceString("GoalFilterDialog.RegexPatterns.Label.Text")); //$NON-NLS-1$
		label.setEnabled(applyCustomFiltersButton.getSelection());
		
		applyCustomFiltersButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					label.setEnabled(applyCustomFiltersButton.getSelection());
					patternText.setEnabled(applyCustomFiltersButton.getSelection());
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
		label.setText(Mevenide.getResourceString("GoalFilterDialog.GlobalGoalFiltering.Text")); //$NON-NLS-1$
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
    	
    	createTreeListeners();
    	
	}
	
	private void createTreeListeners() {
		goalsViewer.addCheckStateListener(
        	new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
                	updateCheckedItems(event);
        		}
        	}
        );
    	
    	goalsViewer.addTreeListener(
    		new ITreeViewerListener() {
    			public void treeCollapsed(TreeExpansionEvent e) {}
    			public void treeExpanded(TreeExpansionEvent e) {
    				Object o = e.getElement();
    				if ( o instanceof Plugin ) {
    					updateCheckedItems(new CheckStateChangedEvent(goalsViewer, o, goalsViewer.getChecked(o)));
    				}
				}
    		}
    	);
	}

	private void initializeTree() {
		String goalsAsString = getPreferenceStore().getString(GlobalGoalFilter.ORIGIN_FILTER_GOALS);
		List goals = deserializeFilteredGoals(goalsAsString);  
		TreeItem[] children = goalsViewer.getTree().getItems();
		for (int i = 0; i < children.length; i++) {
			TreeItem item = children[i];
			if ( goals.contains(((Element) item.getData()).getFullyQualifiedName()) ) {
				item.setChecked(true);
			}
		}
	}
	
	private List deserializeFilteredGoals(String goalsAsString) {
		List goals = new ArrayList();
		if ( !StringUtils.isNull(goalsAsString) ) {
		    StringTokenizer tokenizer = new StringTokenizer(goalsAsString, ","); //$NON-NLS-1$
		    while ( tokenizer.hasMoreTokens() ) {
		    	goals.add(tokenizer.nextToken());
		    }
		}
		return goals;
	}
	
	private void updateCheckedItems(CheckStateChangedEvent e) {
		
		boolean isSelectionChecked = ((CheckboxTreeViewer) e.getSource()).getChecked(e.getElement());
		
		if ( e.getElement() instanceof Goal ) {
			Goal goal = (Goal) e.getElement();
			updateCheckedGoal(isSelectionChecked, goal);
			updateGrayedState(goal, isSelectionChecked);
		}
		else {
			Plugin plugin = (Plugin) e.getElement();
			goalsViewer.setSubtreeChecked(plugin, isSelectionChecked);
			goalsViewer.setGrayed(plugin, false);
		}
	}
	
	private void updateGrayedState(Goal goal, boolean isGoalChecked) {
		Plugin parentPlugin = goal.getPlugin();
		Goal[] goals = (Goal[]) ((IStructuredContentProvider) goalsViewer.getContentProvider()).getElements(parentPlugin);
		boolean consistentCheckedState = true;
		for (int i = 0; i < goals.length; i++) {
			if ( isGoalChecked != goalsViewer.getChecked(goals[i]) ) {
			    consistentCheckedState = false;
			    break;
			}
		}
		goalsViewer.setGrayed(parentPlugin, !consistentCheckedState);
		goalsViewer.setChecked(parentPlugin, consistentCheckedState && isGoalChecked);
	}
	
	private void updateCheckedGoal(boolean isSelectionChecked, Goal goal) {
		String fullyQualifiedGoalName = goal.getPlugin().getName();
		if ( !goal.getName().equals(Goal.DEFAULT_GOAL) ) {
			fullyQualifiedGoalName += Goal.SEPARATOR + goal.getName();
		}
		if ( isSelectionChecked ) {
			checkedItems.add(fullyQualifiedGoalName);
		}
		else {
			checkedItems.remove(fullyQualifiedGoalName);
		}
	}
	
	protected void okPressed() {
		shouldApplyCustomFilters = applyCustomFiltersButton.getSelection();
        getPreferenceStore().setValue(CustomPatternFilter.APPLY_CUSTOM_FILTERS_KEY, shouldApplyCustomFilters);
		
	    regex = patternText.getText();
        getPreferenceStore().setValue(CustomPatternFilter.CUSTOM_FILTERS_KEY, regex);
	    
	    filteredGoals = getSerializedFilteredGoals();
        getPreferenceStore().setValue(GlobalGoalFilter.ORIGIN_FILTER_GOALS, filteredGoals);
	    
	    commitChanges();
	    super.okPressed();
	}
	
	private String getSerializedFilteredGoals() {
		Object[] checkedElements = goalsViewer.getCheckedElements();
		StringBuffer buffer = new StringBuffer(""); //$NON-NLS-1$
		for (int i = 0; i < checkedElements.length; i++) {
			buffer.append(((Element) checkedElements[i]).getFullyQualifiedName());
			if ( i != checkedElements.length - 1 ) {
				buffer.append(","); //$NON-NLS-1$
			}
		}
		return buffer.toString();
	}
	
	public String getRegex() {
		return regex;
	}
	
	public boolean shouldApplyCustomFilters() {
		return shouldApplyCustomFilters;
	}
	
	public String getFilteredGoals() {
		return filteredGoals;
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
