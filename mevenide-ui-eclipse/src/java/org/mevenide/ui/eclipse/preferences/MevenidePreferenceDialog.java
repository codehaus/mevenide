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

import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.goals.viewer.GoalsPickerDialog;
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.mevenide.util.StringUtils;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MevenidePreferenceDialog {
	

    private static Log log = LogFactory.getLog(MevenidePreferenceDialog.class);


	private Composite topLevelContainer;
	private DirectoryFieldEditor mavenHomeEditor;
	private DirectoryFieldEditor mavenLocalHomeEditor;
	private DirectoryFieldEditor javaHomeEditor;
	private DirectoryFieldEditor mavenRepoEditor;
	//private DirectoryFieldEditor pluginsInstallDirEditor;
	private FileFieldEditor pomTemplateLocationEditor; 
	private IntegerFieldEditor heapSizeEditor;
	//private BooleanFieldEditor checkTimestampEditor;
	
	private StringButtonFieldEditor defaultGoalsEditor;
	
	private String javaHome ;
	private String mavenHome ;
	private String mavenLocalHome ;
	private String mavenRepository;
	private String pomTemplateLocation;
	//private String pluginsInstallDir;

	private int heapSize;

	private boolean checkTimestamp;
	
	private String defaultGoals;
	
	private boolean invalidPomTemplate = false;
	
	private PreferencesManager preferencesManager;
	private MevenidePreferencePage page;
	
	public String getDefaultGoals() {
		return defaultGoals;
	}

	public void setDefaultGoals(String defaultGoals) {
		log.debug("Setting defaultGaosl to : " + defaultGoals);
		this.defaultGoals = defaultGoals;
	}

	public MevenidePreferenceDialog(PreferencesManager manager, MevenidePreferencePage page) {
		this.preferencesManager = manager;
		this.page = page;
	}

    private ILocationFinder getDefaultLocationFinder() {
        return ConfigUtils.getDefaultLocationFinder();
    }
    
	public Composite createContent(Composite parent) {
		topLevelContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		javaHomeEditor = createEditor(
			MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("MevenidePreferenceDialog.java.home.label"), 
			javaHome
		);
		if ( isNull(javaHomeEditor) ) {
			log.debug("javaHomeEditor is null, loading from env : " + getDefaultLocationFinder().getJavaHome());
		    javaHomeEditor.setStringValue(getDefaultLocationFinder().getJavaHome());
		}
		mavenHomeEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("MevenidePreferenceDialog.maven.home.label"), 
			mavenHome
		);
		if ( isNull(mavenHomeEditor) ) {
			log.debug("mavenHomeEditor is null, loading from env : " + getDefaultLocationFinder().getMavenHome());
		    mavenHomeEditor.setStringValue(getDefaultLocationFinder().getMavenHome());
		}
		mavenLocalHomeEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, 
			Mevenide.getResourceString("MevenidePreferenceDialog.maven.local.home.label"), 
			mavenLocalHome
		);
		if ( mavenLocalHomeEditor.getStringValue() == null || mavenLocalHomeEditor.getStringValue().trim().equals("") ) {
		    mavenLocalHomeEditor.setStringValue(getDefaultLocationFinder().getMavenLocalHome());
		}
		mavenRepoEditor = createEditor(
			MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, 
			Mevenide.getResourceString("MevenidePreferenceDialog.maven.repo.label"), 
			mavenRepository
		);
		if ( mavenRepoEditor.getStringValue() == null || mavenRepoEditor.getStringValue().trim().equals("") ) {
		    mavenRepoEditor.setStringValue(getDefaultLocationFinder().getMavenLocalRepository());
		}
		heapSizeEditor = new IntegerFieldEditor(
			MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY, 
			Mevenide.getResourceString("MevenidePreferenceDialog.heap.size.label"), 
			topLevelContainer
		);
		heapSizeEditor.fillIntoGrid(topLevelContainer, 2);
		heapSizeEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		heapSizeEditor.load();
		if ( heapSizeEditor.getIntValue() <= 0 ) {
			heapSizeEditor.setStringValue("160");
		}		

		Label label = new Label(topLevelContainer, SWT.NONE);
		label.setLayoutData(new GridData());
		
		createDefaultGoalsComposite(parent);
		
		defaultGoalsEditor.getTextControl(topLevelContainer).addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					defaultGoals = ((Text)e.getSource()).getText();
				}
			}
		);
		
		pomTemplateLocationEditor = new FileFieldEditor(
			MevenidePreferenceKeys.POM_TEMPLATE_LOCATION_PREFERENCE_KEY, 
			Mevenide.getResourceString("MevenidePreferenceDialog.pom.template.label"), 
			true, 
			topLevelContainer
		);
		pomTemplateLocationEditor.fillIntoGrid(topLevelContainer, 3);
		pomTemplateLocationEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		pomTemplateLocationEditor.load();
		pomTemplateLocationEditor.getTextControl(topLevelContainer).addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					try {
						if ( ((Text)event.getSource()).getText() != null && !((Text)event.getSource()).getText().trim().equals("") ) {
							DefaultProjectUnmarshaller dpu = new DefaultProjectUnmarshaller();
							Reader reader = new FileReader(((Text)event.getSource()).getText());
							Project project = dpu.parse(reader);
							invalidPomTemplate = false;
							page.setErrorMessage(null);
						}
					} 
					catch (Exception e) {
						//e.printStackTrace();
						invalidPomTemplate = true;
						page.setErrorMessage(Mevenide.getResourceString("MevenidePreferenceDialog.invalid.pom.error"));
					}
					page.getContainer().updateButtons();
					page.getContainer().updateMessage();
				}

			}
		);
		
		if ( StringUtils.isNull(defaultGoalsEditor.getStringValue()) ) {
		    defaultGoalsEditor.getTextControl(topLevelContainer).setText(Mevenide.getInstance().getDefaultGoals());
		}

		Composite compositeB = new Composite(parent, SWT.NULL);
		GridLayout layoutB = new GridLayout();
		layoutB.numColumns = 3;
		compositeB.setLayout(layoutB);
		compositeB.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return topLevelContainer;
	}

	
	private boolean isNull(DirectoryFieldEditor editor) {
        return editor.getStringValue() == null || editor.getStringValue().trim().equals("");
    }

    private void createDefaultGoalsComposite(final Composite parent) {
		defaultGoalsEditor = new StringButtonFieldEditor() {
			protected Button getChangeControl(Composite parent) {
				Button b = super.getChangeControl(parent);
				b.setEnabled(true);
				return b;
			}
			protected String changePressed() {
				String backup = defaultGoalsEditor.getTextControl(topLevelContainer).getText();
				GoalsPickerDialog goalsPickerDialog = new GoalsPickerDialog();
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
		
		defaultGoalsEditor.setPreferenceName(MevenidePreferenceKeys.MAVEN_LAUNCH_DEFAULTGOALS_PREFERENCE_KEY);
		defaultGoalsEditor.setLabelText(Mevenide.getResourceString("MevenidePreferenceDialog.default.goals.label"));
		defaultGoalsEditor.setPreferencePage(page);
		defaultGoalsEditor.fillIntoGrid(topLevelContainer, 3);
		defaultGoalsEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		defaultGoalsEditor.load();
		if ( defaultGoalsEditor.getStringValue() == null ) {
			defaultGoalsEditor.setStringValue(Mevenide.getResourceString("MevenidePreferenceDialog.default.goals.null"));
		} 
		defaultGoalsEditor.setChangeButtonText(Mevenide.getResourceString("MevenidePreferenceDialog.default.goals.choose"));
		
	}

	private DirectoryFieldEditor createEditor(String property, String name, String value) {
		DirectoryFieldEditor editor = new DirectoryFieldEditor(property, name, topLevelContainer);
		editor.fillIntoGrid(topLevelContainer, 3);
		editor.setPreferenceStore(preferencesManager.getPreferenceStore());
		editor.load();
		return editor;
	}
	
	public void update() {
		mavenHome = mavenHomeEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setMavenHome(mavenHome);


		javaHome = javaHomeEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setJavaHome(javaHome);
		
		
		pomTemplateLocation = pomTemplateLocationEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setPomTemplate(pomTemplateLocation);
		
		defaultGoals = defaultGoalsEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getInstance().setDefaultGoals(defaultGoals);

		mavenLocalHome = mavenLocalHomeEditor.getTextControl(topLevelContainer).getText();
		if ( mavenLocalHome != null && !mavenLocalHome.trim().equals("") ) {
			Mevenide.getInstance().setMavenLocalHome(mavenLocalHome);
		}
				
		mavenRepository = mavenRepoEditor.getTextControl(topLevelContainer).getText();
		if ( mavenRepository != null && !mavenRepository.trim().equals("") ) {
			Mevenide.getInstance().setMavenRepository(mavenRepository);
		}
		
		heapSize = heapSizeEditor.getIntValue();
		if ( heapSize != 0 ) {
			Mevenide.getInstance().setHeapSize(heapSize);
		}
	}
	
	
	public boolean canFinish() {
		return !invalidPomTemplate && (!isEditorOk(javaHomeEditor) 
				|| !isEditorOk(mavenHomeEditor) 
				|| !isEditorOk(mavenRepoEditor));
	}

	private boolean isEditorOk(DirectoryFieldEditor editor) {
		return editor != null 
				&& editor.getTextControl(topLevelContainer).getText() != null;
	}
	
	
	public void setJavaHome(String home) {
		javaHome = home;
	}

	public void setMavenHome(String home) {
		mavenHome = home;
	}

	public void setMavenLocalHome(String home) {
		mavenLocalHome = home;
	}

	public String getJavaHome() {
		return javaHome;
	}

	public String getMavenHome() {
		return mavenHome;
	}

	public String getMavenLocalHome() {
		return mavenLocalHome;
	}

	public String getMavenRepo() {
		return mavenRepository;
	}

	public void setMavenRepo(String string) {
		mavenRepository = string;
	}
	
	
	public boolean getCheckTimestamp() {
		return checkTimestamp;
	}

	public void setCheckTimestamp(boolean b) {
		checkTimestamp = b;
	}

	public String getPomTemplateLocation() {
		return pomTemplateLocation;
	}

	public Composite getTopLevelContainer() {
		return topLevelContainer;
	}

	public void setPomTemplateLocation(String string) {
		pomTemplateLocation = string;
	}

	public void setTopLevelContainer(Composite composite) {
		topLevelContainer = composite;
	}

	public boolean isInvalidPomTemplate() {
		return invalidPomTemplate;
	}
	
    public int getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(int heapSize) {
        this.heapSize = heapSize;
    }

}
