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
package org.mevenide.ui.eclipse.preferences;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.view.GoalsPickerDialog;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RunMavenPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
	private static final String DEFAULT_HEAP_SIZE = "160";
    
    private IntegerFieldEditor heapSizeEditor;
	private StringButtonFieldEditor defaultGoalsEditor;
	
	private int heapSize;
	private String defaultGoals;
	
	private PreferencesManager preferencesManager;
	private Composite topLevelContainer;
	
	public RunMavenPreferencePage() {
        super(Mevenide.getResourceString("RunMavenPreferencePage.title"));
        //setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
        preferencesManager = PreferencesManager.getManager();
        preferencesManager.loadPreferences();
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
			Mevenide.getResourceString("RunMavenPreferencePage.heap.size.label"), 
			topLevelContainer
		);
		heapSizeEditor.fillIntoGrid(topLevelContainer, 3);
		heapSizeEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		heapSizeEditor.load();
		if ( heapSizeEditor.getIntValue() <= 0 ) {
			heapSizeEditor.setStringValue(DEFAULT_HEAP_SIZE);
		}
		
    }

    private void createDefaultGoalsEditor() {
		defaultGoalsEditor = 
		    new StringButtonFieldEditor(
		            MevenidePreferenceKeys.MAVEN_LAUNCH_DEFAULTGOALS_PREFERENCE_KEY,
		            Mevenide.getResourceString("RunMavenPreferencePage.default.goals.label"),
		            topLevelContainer) {
				protected Button getChangeControl(Composite parent) {
					Button b = super.getChangeControl(parent);
					b.setEnabled(true);
					return b;
				}
				protected String changePressed() {
					String backup = defaultGoalsEditor.getTextControl(topLevelContainer).getText();
					GoalsPickerDialog goalsPickerDialog = new GoalsPickerDialog();
					goalsPickerDialog.setOverrideMessage(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.choose.message.override"));
					goalsPickerDialog.setOverrideTitle(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.choose.message.title"));
					goalsPickerDialog.setGoalsOrder(defaultGoalsEditor.getTextControl(topLevelContainer).getText());
					int ok = goalsPickerDialog.open();
					if ( ok == Window.OK ) {
						return goalsPickerDialog.getOrderedGoals();
					}
					else {
						return defaultGoalsEditor.getTextControl(topLevelContainer).getText();
					}
				}
		};
		
		defaultGoalsEditor.fillIntoGrid(topLevelContainer, 3);
		defaultGoalsEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		defaultGoalsEditor.load();
		if ( defaultGoalsEditor.getStringValue() == null ) {
			defaultGoalsEditor.setStringValue(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.null"));
		} 
		defaultGoalsEditor.setChangeButtonText(Mevenide.getResourceString("RunMavenPreferencePage.default.goals.choose"));
		
		defaultGoalsEditor.getTextControl(topLevelContainer).addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					defaultGoals = ((Text)e.getSource()).getText();
				}
			}
		);
		if ( StringUtils.isNull(defaultGoalsEditor.getStringValue()) ) {
		    defaultGoalsEditor.getTextControl(topLevelContainer).setText(Mevenide.getInstance().getDefaultGoals());
		}
    
	}
	
	public void update() {
		defaultGoals = defaultGoalsEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setDefaultGoals(defaultGoals);

		heapSize = heapSizeEditor.getIntValue();
		if ( heapSize != 0 ) {
			Mevenide.getInstance().setHeapSize(heapSize);
		}
	}
	
	public void init(IWorkbench workbench) { }
	
	private boolean finish() {
		update();
		
		preferencesManager.setValue(
			MevenidePreferenceKeys.DEFAULT_GOALS_PREFERENCE_KEY, 
			defaultGoals
		);
		
		if ( heapSize != 0 ) {
		    preferencesManager.setIntValue(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY, heapSize);
		}
		
		Mevenide.getInstance().initEnvironment();
		
		return preferencesManager.store();
	}
    
}
