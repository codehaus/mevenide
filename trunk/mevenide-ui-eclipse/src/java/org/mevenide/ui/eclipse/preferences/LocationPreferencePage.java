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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenidePreferenceKeys;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LocationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private static Log log = LogFactory.getLog(LocationPreferencePage.class);
    
    private PreferencesManager preferencesManager;
	
    private Group topLevelContainer;
    
	private DirectoryFieldEditor mavenHomeEditor;
	private DirectoryFieldEditor mavenLocalHomeEditor;
	private DirectoryFieldEditor javaHomeEditor;
	private DirectoryFieldEditor mavenRepositoryEditor;
    
	private String javaHome ;
	private String mavenHome ;
	private String mavenLocalHome ;
	private String mavenRepository;
	
    public LocationPreferencePage() {
        super(Mevenide.getResourceString("LocationPreferencePage.title"));
        //setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
		preferencesManager = PreferencesManager.getManager();
		preferencesManager.loadPreferences();
    }

	protected Control createContents(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    composite.setLayout(layout);
	    
	    GridData data = new GridData(GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.grabExcessVerticalSpace = true;
	    composite.setLayoutData(data);
	    
	    createEditors(composite);
		
		createRegenerateButtonArea(composite);
		
		return topLevelContainer;
	}

	private void createEditors(Composite parent) {
        topLevelContainer = new Group(parent, SWT.NULL);
        
        GridLayout layout = new GridLayout();
        layout.marginWidth = 10;
	    layout.marginHeight = 10;
	    topLevelContainer.setLayout(layout);
	    
	    GridData groupData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
	    groupData.grabExcessVerticalSpace = false;
	    topLevelContainer.setLayoutData(groupData);
	    
	    topLevelContainer.setText(Mevenide.getResourceString("LocationPreferencePage.RequiredLocations"));
	    		
		createJavaHomeEditor();
		createMavenHomeEditor();
		createMavenLocalHomeEditor();
		createMavenRepositoryEditor();
    }

    private void createRegenerateButtonArea(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NULL);
	    composite.setLayout(new GridLayout());
	    GridData data = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.grabExcessVerticalSpace = true;
	    composite.setLayoutData(data);
	    
	    Button regenerateButton = new Button(composite, SWT.PUSH);
	    regenerateButton.setText(Mevenide.getResourceString("LocationPreferencePage.Regenerate"));
	    regenerateButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    
	    regenerateButton.addSelectionListener(
	        new SelectionListener() {
			    public void widgetDefaultSelected(SelectionEvent event) {
		        }
			    public void widgetSelected(SelectionEvent event) {
		        }
	    	}
	    );
	    

	}
	
	private void createMavenRepositoryEditor() {
        mavenRepositoryEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.maven.repo.label"), 
			mavenRepository
		);
		if ( mavenRepositoryEditor.getStringValue() == null || mavenRepositoryEditor.getStringValue().trim().equals("") ) {
		    mavenRepositoryEditor.setStringValue(getDefaultLocationFinder().getMavenLocalRepository());
		}
    }

    private void createMavenLocalHomeEditor() {
        mavenLocalHomeEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.maven.local.home.label"), 
			mavenLocalHome
		);
		if ( mavenLocalHomeEditor.getStringValue() == null || mavenLocalHomeEditor.getStringValue().trim().equals("") ) {
		    mavenLocalHomeEditor.setStringValue(getDefaultLocationFinder().getMavenLocalHome());
		}
    }

    private void createMavenHomeEditor() {
        mavenHomeEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.maven.home.label"), 
			mavenHome
		);
		if ( isNull(mavenHomeEditor) ) {
			log.debug("mavenHomeEditor is null, loading from env : " + getDefaultLocationFinder().getMavenHome());
		    mavenHomeEditor.setStringValue(getDefaultLocationFinder().getMavenHome());
		}
    }

    private void createJavaHomeEditor() {
        javaHomeEditor = createEditor(
			MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.java.home.label"), 
			javaHome
		);
		if ( isNull(javaHomeEditor) ) {
			log.debug("javaHomeEditor is null, loading from env : " + getDefaultLocationFinder().getJavaHome());
		    javaHomeEditor.setStringValue(getDefaultLocationFinder().getJavaHome());
		}
    }

    private DirectoryFieldEditor createEditor(String property, String name, String value) {
		DirectoryFieldEditor editor = new DirectoryFieldEditor(property, name, topLevelContainer);
		editor.fillIntoGrid(topLevelContainer, 3);
		editor.setPreferenceStore(preferencesManager.getPreferenceStore());
		editor.load();
		return editor;
	}
	
	private boolean isNull(DirectoryFieldEditor editor) {
        return StringUtils.isNull(editor.getStringValue());
    }
	
    public boolean performOk() {
        return (!isEditorOk(javaHomeEditor) 
				|| !isEditorOk(mavenHomeEditor) 
				|| !isEditorOk(mavenRepositoryEditor)) && finish();
    }
    
    private boolean isEditorOk(DirectoryFieldEditor editor) {
		return editor != null 
				&& editor.getTextControl(topLevelContainer).getText() != null;
	}
    
	private boolean finish() {
		update();
		
		preferencesManager.setValue(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, mavenHome);
		preferencesManager.setValue(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, mavenLocalHome);
		preferencesManager.setValue(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, javaHome);
		preferencesManager.setValue(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, mavenRepository);
		
		Mevenide.getInstance().initEnvironment();
		
		return preferencesManager.store();
	}
	
	private ILocationFinder getDefaultLocationFinder() {
        return ConfigUtils.getDefaultLocationFinder();
    }
	
	public void update() {
		mavenHome = mavenHomeEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setMavenHome(mavenHome);

		javaHome = javaHomeEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setJavaHome(javaHome);
	
		mavenLocalHome = mavenLocalHomeEditor.getTextControl(topLevelContainer).getText();
		if ( mavenLocalHome != null && !mavenLocalHome.trim().equals("") ) {
			Mevenide.getInstance().setMavenLocalHome(mavenLocalHome);
		}
				
		mavenRepository = mavenRepositoryEditor.getTextControl(topLevelContainer).getText();
		if ( mavenRepository != null && !mavenRepository.trim().equals("") ) {
			Mevenide.getInstance().setMavenRepository(mavenRepository);
		}
	}
	
	public void init(IWorkbench workbench) {
    }
}
