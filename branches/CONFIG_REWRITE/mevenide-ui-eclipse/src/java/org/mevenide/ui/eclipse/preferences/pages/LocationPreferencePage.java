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
package org.mevenide.ui.eclipse.preferences.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.runner.RunnerUtils;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
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

    private FileFieldEditor toolsJarEditor;
    private String toolsJarLocation;
	
    public LocationPreferencePage() {
        super(Mevenide.getResourceString("LocationPreferencePage.title")); //$NON-NLS-1$
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
	    
	    topLevelContainer.setText(Mevenide.getResourceString("LocationPreferencePage.RequiredLocations")); //$NON-NLS-1$
	    		
		createJavaHomeEditor();
		createMavenHomeEditor();
		createMavenLocalHomeEditor();
		createMavenRepositoryEditor();
		createToolJarEditor();
    }

	protected void performDefaults() {
        reloadLocations();
        super.performDefaults();
        performOk();
    }
	
    private void reloadLocations() {
        reloadMavenRepository();
        reloadMavenLocalHome();
        reloadMavenHome();
        reloadJavaHome();
        reloadToolsJarLocation();
    }
	
	private void createMavenRepositoryEditor() {
        mavenRepositoryEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.maven.repo.label"),  //$NON-NLS-1$
			mavenRepository
		);
		if ( StringUtils.isNull(mavenRepositoryEditor.getStringValue()) ) {
		    reloadMavenRepository();
		}
    }

    private void reloadMavenRepository() {
        mavenRepositoryEditor.setStringValue(getDefaultLocationFinder().getMavenLocalRepository());
    }

	private void createToolJarEditor() {
	    toolsJarEditor = new FileFieldEditor(MevenidePreferenceKeys.TOOLS_JAR_PREFERENCE_KEY, 
										             Mevenide.getResourceString("LocationPreferencePage.tools.jar.label"),  //$NON-NLS-1$, 
										             topLevelContainer);
	    
	    toolsJarEditor.fillIntoGrid(topLevelContainer, 3);
	    toolsJarEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
	    toolsJarEditor.load();
		
	    if ( StringUtils.isNull(toolsJarEditor.getStringValue()) ) {
		    reloadToolsJarLocation();
		}
    }
	
    private void reloadToolsJarLocation() {
        String toolsJar = RunnerUtils.getToolsJar();
        if ( !StringUtils.isNull(toolsJar) ) {
            toolsJarEditor.setStringValue(toolsJar);
        }
    }

    private void createMavenLocalHomeEditor() {
        mavenLocalHomeEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.maven.local.home.label"),  //$NON-NLS-1$
			mavenLocalHome
		);
		if ( StringUtils.isNull(mavenLocalHomeEditor.getStringValue()) ) {
		    reloadMavenLocalHome();
		}
    }

    private void reloadMavenLocalHome() {
        mavenLocalHomeEditor.setStringValue(getDefaultLocationFinder().getMavenLocalHome());
    }

    private void createMavenHomeEditor() {
        mavenHomeEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.maven.home.label"),  //$NON-NLS-1$
			mavenHome
		);
		if ( isNull(mavenHomeEditor) ) {
			log.debug("mavenHomeEditor is null, loading from env : " +  //$NON-NLS-1$
			          getDefaultLocationFinder().getMavenHome());
		    reloadMavenHome();
		}
    }

    private void reloadMavenHome() {
        mavenHomeEditor.setStringValue(getDefaultLocationFinder().getMavenHome());
    }

    private void createJavaHomeEditor() {
        javaHomeEditor = createEditor(
			MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("LocationPreferencePage.java.home.label"),  //$NON-NLS-1$
			javaHome
		);
		if ( isNull(javaHomeEditor) ) {
			log.debug("javaHomeEditor is null, loading from env : " +  //$NON-NLS-1$
			          getDefaultLocationFinder().getJavaHome());
		    reloadJavaHome();
		}
    }

    private void reloadJavaHome() {
        javaHomeEditor.setStringValue(getDefaultLocationFinder().getJavaHome());
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
        return (isEditorOk(javaHomeEditor) 
				|| isEditorOk(mavenHomeEditor) 
				|| isEditorOk(mavenRepositoryEditor)) && finish();
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
		preferencesManager.setValue(MevenidePreferenceKeys.TOOLS_JAR_PREFERENCE_KEY, toolsJarLocation);
		
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
		if ( !StringUtils.isNull(mavenLocalHome) ) {
			Mevenide.getInstance().setMavenLocalHome(mavenLocalHome);
		}
				
		mavenRepository = mavenRepositoryEditor.getTextControl(topLevelContainer).getText();
		if ( !StringUtils.isNull(mavenRepository) ) {
			Mevenide.getInstance().setMavenRepository(mavenRepository);
		}
		
		toolsJarLocation = toolsJarEditor.getTextControl(topLevelContainer).getText();
	}
	
	public void init(IWorkbench workbench) {
    }
}
