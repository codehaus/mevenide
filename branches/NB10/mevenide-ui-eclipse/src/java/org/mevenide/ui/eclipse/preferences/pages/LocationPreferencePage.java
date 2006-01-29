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

package org.mevenide.ui.eclipse.preferences.pages;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.runner.RunnerUtils;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LocationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String PAGE_NAME = Mevenide.getResourceString("LocationPreferencePage.title"); //$NON-NLS-1$
    private static final String PAGE_DESC = Mevenide.getResourceString("LocationPreferencePage.RequiredLocations"); //$NON-NLS-1$
    
	private DirectoryFieldEditor mavenHomeEditor;
	private DirectoryFieldEditor mavenLocalHomeEditor;
	private DirectoryFieldEditor javaHomeEditor;
	private DirectoryFieldEditor mavenRepositoryEditor;
    private FileFieldEditor toolsJarEditor;
	
    public LocationPreferencePage() {
        super(GRID);
        super.setDescription(PAGE_DESC);
        super.setPreferenceStore(Mevenide.getInstance().getCustomPreferenceStore());
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
//		WorkbenchHelp.setHelp(getControl(), IDebugHelpContextIds.CONSOLE_PREFERENCE_PAGE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
        javaHomeEditor = new DirectoryFieldEditor(
    			MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, 
    			Mevenide.getResourceString("LocationPreferencePage.java.home.label"),  //$NON-NLS-1$
    			getFieldEditorParent());
        javaHomeEditor.setErrorMessage(Mevenide.getResourceString("LocationPreferencePage.java.home.error")); //$NON-NLS-1$
//        javaHomeEditor.setEmptyStringAllowed(false);
        javaHomeEditor.setPreferenceStore(getPreferenceStore());
        javaHomeEditor.load();
		if (StringUtils.isNull(javaHomeEditor.getStringValue())) {
			javaHomeEditor.setStringValue(getDefaultLocationFinder().getJavaHome());
		}
		addField(javaHomeEditor);

        mavenHomeEditor = new DirectoryFieldEditor(
    			MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, 
    			Mevenide.getResourceString("LocationPreferencePage.maven.home.label"),  //$NON-NLS-1$
    			getFieldEditorParent());
        mavenHomeEditor.setErrorMessage(Mevenide.getResourceString("LocationPreferencePage.maven.home.error")); //$NON-NLS-1$
//        mavenHomeEditor.setEmptyStringAllowed(false);
        mavenHomeEditor.setPreferenceStore(getPreferenceStore());
        mavenHomeEditor.load();
		if (StringUtils.isNull(mavenHomeEditor.getStringValue())) {
			mavenHomeEditor.setStringValue(getDefaultLocationFinder().getMavenHome());
		}
		addField(mavenHomeEditor);

        mavenLocalHomeEditor = new DirectoryFieldEditor(
    			MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, 
    			Mevenide.getResourceString("LocationPreferencePage.maven.local.home.label"),  //$NON-NLS-1$
				getFieldEditorParent());
        mavenLocalHomeEditor.setErrorMessage(Mevenide.getResourceString("LocationPreferencePage.maven.local.home.error")); //$NON-NLS-1$
//        mavenLocalHomeEditor.setEmptyStringAllowed(false);
        mavenLocalHomeEditor.setPreferenceStore(getPreferenceStore());
        mavenLocalHomeEditor.load();
		if (StringUtils.isNull(mavenLocalHomeEditor.getStringValue())) {
			mavenLocalHomeEditor.setStringValue(getDefaultLocationFinder().getMavenLocalHome());
		}
		addField(mavenLocalHomeEditor);

		mavenRepositoryEditor = new DirectoryFieldEditor(
    			MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, 
    			Mevenide.getResourceString("LocationPreferencePage.maven.repo.label"),  //$NON-NLS-1$
				getFieldEditorParent());
        mavenRepositoryEditor.setErrorMessage(Mevenide.getResourceString("LocationPreferencePage.maven.repo.error")); //$NON-NLS-1$
//		mavenRepositoryEditor.setEmptyStringAllowed(false);
		mavenRepositoryEditor.setPreferenceStore(getPreferenceStore());
		mavenRepositoryEditor.load();
		if (StringUtils.isNull(mavenRepositoryEditor.getStringValue())) {
			mavenRepositoryEditor.setStringValue(getDefaultLocationFinder().getMavenLocalRepository());
		}
		addField(mavenRepositoryEditor);

	    toolsJarEditor = new FileFieldEditor(
	    		MevenidePreferenceKeys.TOOLS_JAR_PREFERENCE_KEY, 
	            Mevenide.getResourceString("LocationPreferencePage.tools.jar.label"),  //$NON-NLS-1$, 
				getFieldEditorParent());
        toolsJarEditor.setErrorMessage(Mevenide.getResourceString("LocationPreferencePage.tools.jar.error")); //$NON-NLS-1$
//	    toolsJarEditor.setEmptyStringAllowed(false);
	    toolsJarEditor.setPreferenceStore(getPreferenceStore());
	    toolsJarEditor.load();
		if (StringUtils.isNull(toolsJarEditor.getStringValue())) {
			toolsJarEditor.setStringValue(RunnerUtils.getToolsJar());
		}
		addField(toolsJarEditor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	private ILocationFinder getDefaultLocationFinder() {
        return Mevenide.getInstance().getPOMManager().getDefaultLocationFinder();
    }
}
