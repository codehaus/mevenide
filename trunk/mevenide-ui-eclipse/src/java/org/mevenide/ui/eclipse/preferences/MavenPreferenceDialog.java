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

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenPreferenceDialog {
	private Composite topLevelContainer;
	private DirectoryFieldEditor mavenHomeEditor;
	private DirectoryFieldEditor javaHomeEditor;
	private DirectoryFieldEditor mavenRepoEditor;
		
	private String javaHome ;
	private String mavenHome ;
	private String mavenRepository;
	
	public Composite createContent(Composite parent) {
		topLevelContainer = new Composite(parent, SWT.NULL);
		mavenHomeEditor = createEditor("maven.home", "Maven home", mavenHome);
		javaHomeEditor = createEditor("java.home", "Java home", javaHome);
		mavenRepoEditor = createEditor("maven.repo", "Maven Repository", mavenRepository);
		return topLevelContainer;
	}

	private DirectoryFieldEditor createEditor(String property, String name, String value) {
		DirectoryFieldEditor editor = new DirectoryFieldEditor(property, name, topLevelContainer);
		editor.getTextControl(topLevelContainer).setText(value);
		return editor;
	}
	
	public void update() {
		mavenHome = mavenHomeEditor.getTextControl(topLevelContainer).getText();
		MavenPlugin.getPlugin().setMavenHome(mavenHome);
		javaHome = javaHomeEditor.getTextControl(topLevelContainer).getText();
		MavenPlugin.getPlugin().setJavaHome(javaHome);
		mavenRepository = mavenRepoEditor.getTextControl(topLevelContainer).getText();
		MavenPlugin.getPlugin().setMavenRepository(mavenRepository);
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

}
