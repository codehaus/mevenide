/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.mevenide.ui.eclipse.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MevenidePreferenceDialog {
	private Composite topLevelContainer;
	private DirectoryFieldEditor mavenHomeEditor;
	private DirectoryFieldEditor javaHomeEditor;
	private DirectoryFieldEditor mavenRepoEditor;
	private BooleanFieldEditor checkTimestampEditor;
	
	private String javaHome ;
	private String mavenHome ;
	private String mavenRepository;
	private boolean checkTimestamp;
	
	private PreferencesManager preferencesManager;
	
	public MevenidePreferenceDialog(PreferencesManager manager) {
		this.preferencesManager = manager;
	}

	public Composite createContent(Composite parent) {
		topLevelContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		mavenHomeEditor = createEditor("maven.home", "Maven home", mavenHome);
		javaHomeEditor = createEditor("java.home", "Java home", javaHome);
		mavenRepoEditor = createEditor("maven.repo", "Maven Repository", mavenRepository);
		
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
		checkTimestamp = checkTimestampEditor.getBooleanValue();
		Mevenide.getPlugin().setCheckTimestamp(checkTimestamp);
	}
	
	
	public boolean canFinish() {
		return !isEditorOk(javaHomeEditor) 
				|| !isEditorOk(mavenHomeEditor) 
				|| !isEditorOk(mavenRepoEditor);
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

}
