/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.preferences;

import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.DefaultProjectUnmarshaller;
import org.apache.maven.project.Project;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.Mevenide;

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
	private DirectoryFieldEditor javaHomeEditor;
	private DirectoryFieldEditor mavenRepoEditor;
	private FileFieldEditor pomTemplateLocationEditor; 
	private BooleanFieldEditor checkTimestampEditor;
	
	private StringButtonFieldEditor defaultGoalsEditor;
	
	private String javaHome ;
	private String mavenHome ;
	private String mavenRepository;
	private String pomTemplateLocation;
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

	public Composite createContent(Composite parent) {
		topLevelContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		mavenHomeEditor = createEditor("maven.home", "Maven home", mavenHome);
		javaHomeEditor = createEditor("java.home", "Java home", javaHome);
		mavenRepoEditor = createEditor("maven.repo", "Maven Repository", mavenRepository);
		
		pomTemplateLocationEditor = new FileFieldEditor("pom.template.location", "POM Template", true, topLevelContainer);
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
						page.setErrorMessage("Pom Template is not a valid Maven project Descriptor.");
					}
					page.getContainer().updateButtons();
					page.getContainer().updateMessage();
				}

			}
		);
		
		
		createDefaultGoalsComposite(parent);
		
		defaultGoalsEditor.getTextControl(topLevelContainer).addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					defaultGoals = ((Text)e.getSource()).getText();
				}
			}
		);

		Composite compositeB = new Composite(parent, SWT.NULL);
		GridLayout layoutB = new GridLayout();
		layoutB.numColumns = 3;
		compositeB.setLayout(layoutB);
		compositeB.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		checkTimestampEditor = new BooleanFieldEditor("mevenide.checktimestamp", "Check timestamp before synchronizing", compositeB);
		checkTimestampEditor.fillIntoGrid(compositeB, 3);		
		checkTimestampEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		checkTimestampEditor.load();
		return topLevelContainer;
	}

	
	private void createDefaultGoalsComposite(Composite parent) {
		defaultGoalsEditor = new StringButtonFieldEditor() {
			protected Button getChangeControl(Composite parent) {
				Button b = super.getChangeControl(parent);
				b.setEnabled(false);
				return b;
			}
			protected String changePressed() {
				return "java:compile";
			}
		};
		
		defaultGoalsEditor.setPreferenceName("maven.launch.defaultgoals");
		defaultGoalsEditor.setLabelText("Default Goals");
		defaultGoalsEditor.setPreferencePage(page);
		defaultGoalsEditor.fillIntoGrid(topLevelContainer, 3);
		defaultGoalsEditor.setPreferenceStore(preferencesManager.getPreferenceStore());
		defaultGoalsEditor.load();
		if ( defaultGoalsEditor.getStringValue() == null ) {
			defaultGoalsEditor.setStringValue("java:compile");
		} 
		defaultGoalsEditor.setChangeButtonText("Choose...");
		
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
		Mevenide.getPlugin().setMavenHome(mavenHome);
		
		javaHome = javaHomeEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getPlugin().setJavaHome(javaHome);
		
		mavenRepository = mavenRepoEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getPlugin().setMavenRepository(mavenRepository);
		
		pomTemplateLocation = pomTemplateLocationEditor.getTextControl(topLevelContainer).getText();
		Mevenide.getPlugin().setPomTemplate(pomTemplateLocation);
		
		checkTimestamp = checkTimestampEditor.getBooleanValue();
		Mevenide.getPlugin().setCheckTimestamp(checkTimestamp);
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

	public String getJavaHome() {
		return javaHome;
	}

	public String getMavenHome() {
		return mavenHome;
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

}
