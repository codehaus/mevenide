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

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.view.GoalsPickerDialog;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RunMavenPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private static final String PAGE_NAME = Mevenide.getResourceString("RunMavenPreferencePage.title"); //$NON-NLS-1$
//    private static final String PAGE_DESC = Mevenide.getResourceString("RunMavenPreferencePage.description"); //$NON-NLS-1$

	private final class DefaultGoalsEditor extends StringButtonFieldEditor {

        private Button changeButton;

        private DefaultGoalsEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        protected Button getChangeControl(Composite parent) {
            if ( changeButton == null ) {
                changeButton = new Button(parent, SWT.NULL);
                changeButton.setEnabled(true);
                changeButton.addDisposeListener(new DisposeListener() {
        			public void widgetDisposed(DisposeEvent event) {
        				changeButton = null;
        			}
        		});
                changeButton.addSelectionListener(new SelectionAdapter() {
    				public void widgetSelected(SelectionEvent evt) {
    					String newValue = changePressed();
    					if (newValue != null) {
    						setStringValue(newValue);
    					}
    				}
    			});
            }
        	return changeButton;
        }

        protected void doFillIntoGrid(Composite parent, int numColumns) {
            super.doFillIntoGrid(parent, numColumns);
            GridData data = new GridData();
            data.grabExcessHorizontalSpace = false;
            data.horizontalAlignment = GridData.END;
        	changeButton.setLayoutData(data);
        	
        	GridData textData = new GridData(GridData.FILL);
        	textData.grabExcessHorizontalSpace = true;
        	textData.horizontalAlignment = GridData.FILL;
        	getTextControl().setLayoutData(textData);
        }

        protected String changePressed() {
        	GoalsPickerDialog goalsPickerDialog = new GoalsPickerDialog();
        	goalsPickerDialog.setOverrideMessage(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.choose.message.override")); //$NON-NLS-1$
        	goalsPickerDialog.setOverrideTitle(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.choose.message.title")); //$NON-NLS-1$
        	goalsPickerDialog.setGoalsOrder(defaultGoalsEditor.getTextControl(topLevelContainer).getText());
        	int ok = goalsPickerDialog.open();
        	if ( ok == Window.OK ) {
        		return goalsPickerDialog.getOrderedGoals();
        	}
    		return defaultGoalsEditor.getTextControl(topLevelContainer).getText();
        }
    }

    private static final String DEFAULT_HEAP_SIZE = "160"; //$NON-NLS-1$
    
    private IntegerFieldEditor heapSizeEditor;
	private StringButtonFieldEditor defaultGoalsEditor;
	
	private Composite topLevelContainer;
	
	public RunMavenPreferencePage() {
        super(PAGE_NAME);
//        super.setDescription(PAGE_DESC);
        super.setPreferenceStore(Mevenide.getInstance().getCustomPreferenceStore());
//        super.setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
    }
	
    protected Control createContents(Composite parent) {
		topLevelContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		topLevelContainer.setLayout(layout);
		
		createHeapSizeEditor();	
		createDefaultGoalsEditor();
		
		return topLevelContainer;
	}
		
	private void createHeapSizeEditor() {
        heapSizeEditor = new IntegerFieldEditor(
			MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY, 
			Mevenide.getResourceString("RunMavenPreferencePage.heap.size.label"),  //$NON-NLS-1$
			topLevelContainer
		);
		heapSizeEditor.fillIntoGrid(topLevelContainer, 2);
		heapSizeEditor.setPreferenceStore(getPreferenceStore());
		heapSizeEditor.load();
		if ( heapSizeEditor.getIntValue() <= 0 ) {
			heapSizeEditor.setStringValue(DEFAULT_HEAP_SIZE);
		}
		new Label(topLevelContainer, SWT.NULL);
    }

    private void createDefaultGoalsEditor() {
		defaultGoalsEditor = new DefaultGoalsEditor(MevenidePreferenceKeys.MAVEN_LAUNCH_DEFAULTGOALS_PREFERENCE_KEY, Mevenide.getResourceString("RunMavenPreferencePage.default.goals.label"), topLevelContainer); //$NON-NLS-1$
		
		defaultGoalsEditor.fillIntoGrid(topLevelContainer, 3);
		defaultGoalsEditor.setPreferenceStore(getPreferenceStore());
		defaultGoalsEditor.load();
		if ( defaultGoalsEditor.getStringValue() == null ) {
			defaultGoalsEditor.setStringValue(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.null")); //$NON-NLS-1$
		} 
		defaultGoalsEditor.setChangeButtonText(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.choose")); //$NON-NLS-1$
	}
	
	public void init(IWorkbench workbench) { }

}
