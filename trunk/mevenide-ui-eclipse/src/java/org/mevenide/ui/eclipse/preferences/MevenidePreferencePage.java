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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenidePreferenceKeys;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class MevenidePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private PreferencesManager manager;
	
    private MevenidePreferenceDialog dialog;
    
    public MevenidePreferencePage() {
        super(Mevenide.getResourceString("MavenPreferencePage.title"));
        //setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
		manager = PreferencesManager.getManager();
		manager.loadPreferences();
        dialog = new MevenidePreferenceDialog(manager, this);
       	
    }

	

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//*should*not be necessary
		dialog.setJavaHome(manager.getValue(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY));
		dialog.setMavenHome(manager.getValue(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY));
		dialog.setMavenRepo(manager.getValue(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY));
		dialog.setPomTemplateLocation(manager.getValue(MevenidePreferenceKeys.POM_TEMPLATE_LOCATION_PREFERENCE_KEY));
		dialog.setHeapSize(manager.getIntValue(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY));
		dialog.setDefaultGoals(manager.getValue(MevenidePreferenceKeys.DEFAULT_GOALS_PREFERENCE_KEY));
		
		return dialog.createContent(composite);
	}
  
  
    public boolean performOk() {
       if ( dialog.canFinish() ) {
        	return false;
        }
        else {
            return finish();
        }
    }
    
	private boolean finish() {
		dialog.update();
		
		manager.setValue(
			MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, 
			dialog.getMavenHome()
		);
		manager.setValue(
			MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, 
			dialog.getMavenLocalHome()
		);
		manager.setValue(
			MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, 
			dialog.getJavaHome()
		);
		manager.setValue(
			MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, 
			dialog.getMavenRepo()
		);
		manager.setValue(
			MevenidePreferenceKeys.POM_TEMPLATE_LOCATION_PREFERENCE_KEY, 
			dialog.getPomTemplateLocation()
		);
		manager.setValue(
			MevenidePreferenceKeys.DEFAULT_GOALS_PREFERENCE_KEY, 
			dialog.getDefaultGoals()
		);
		
		if ( dialog.getHeapSize() != 0 ) {
			manager.setIntValue(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY, dialog.getHeapSize());
		}
		
		Mevenide.getInstance().initEnvironment();
		
		return manager.store();
	}
	
	
	
	public void init(IWorkbench workbench) {
    }
}
    
    