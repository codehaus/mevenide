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
package org.mevenide.ui.eclipse.editors.pom.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class BuildDirectoriesSection extends PageSection {

	private static final Log log = LogFactory.getLog(BuildDirectoriesSection.class);

	private OverridableTextEntry sourceText;
	private OverridableTextEntry aspectsText;
	private OverridableTextEntry unitTestsText;
	private OverridableTextEntry nagEmailText;

    public BuildDirectoriesSection(
        BuildPage page,
		Composite parent, 
		FormToolkit toolkit) 
   	{
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("BuildDirectoriesSection.header"));
		setDescription(Mevenide.getResourceString("BuildDirectoriesSection.description"));
    }

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 4 : 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getPomEditor().getPom();
		
		// Build source directory textbox and browse button
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("BuildDirectoriesSection.sourceText.label"),
			Mevenide.getResourceString("BuildDirectoriesSection.sourceText.tooltip"), 
			factory
		);
		String labelName = Mevenide.getResourceString("BuildDirectoriesSection.sourceButton.label");
		String toolTip = Mevenide.getResourceString("BuildDirectoriesSection.sourceButton.tooltip");
		final String sourceTitle = Mevenide.getResourceString("BuildDirectoriesSection.sourceButton.dialog.title");
		sourceText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setSourceDirectory(pom, (String) value);
			}
			public Object acceptParent() {
				return getSourceDirectory(getParentPom());
			}
		};
		sourceText.addEntryChangeListener(adaptor);
		sourceText.addOverrideAdaptor(adaptor);
		sourceText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getPomEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(sourceTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							sourceText.setFocus();
							directory = getRelativePath(directory);
							sourceText.setText(directory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for source directory", ex);
					}
				}

			}
		);
		
		
		// Build aspect source directory textbox and browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("BuildDirectoriesSection.aspectsText.label"),
			Mevenide.getResourceString("BuildDirectoriesSection.aspectsText.tooltip"), 
			factory
		);
		labelName = Mevenide.getResourceString("BuildDirectoriesSection.aspectsButton.label");
		toolTip = Mevenide.getResourceString("BuildDirectoriesSection.aspectsButton.tooltip");
		final String aspectsTitle = Mevenide.getResourceString("BuildDirectoriesSection.aspectsButton.dialog.title");
		aspectsText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setAspectSourceDirectory(pom, (String) value);
			}
			public Object acceptParent() {
				return getAspectSourceDirectory(getParentPom());
			}
		};
		aspectsText.addEntryChangeListener(adaptor);
		aspectsText.addOverrideAdaptor(adaptor);
		aspectsText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getPomEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(aspectsTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							aspectsText.setFocus();
							directory = getRelativePath(directory);
							aspectsText.setText(directory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for aspect source directory", ex);
					}
				}
			}
		);
		
		// Build unit tests source directory textbox and browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("BuildDirectoriesSection.unitTestsText.label"),
			Mevenide.getResourceString("BuildDirectoriesSection.unitTestsText.tooltip"), 
			factory
		);
		labelName = Mevenide.getResourceString("BuildDirectoriesSection.unitTestsButton.label");
		toolTip = Mevenide.getResourceString("BuildDirectoriesSection.unitTestsButton.tooltip");
		final String unitTestsTitle = Mevenide.getResourceString("BuildDirectoriesSection.unitTestsButton.dialog.title");
		unitTestsText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setUnitTestSourceDirectory(pom, (String) value);
			}
			public Object acceptParent() {
				return getUnitTestSourceDirectory(getParentPom());
			}
		};
		unitTestsText.addEntryChangeListener(adaptor);
		unitTestsText.addOverrideAdaptor(adaptor);
		unitTestsText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getPomEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(unitTestsTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							unitTestsText.setFocus();
							directory = getRelativePath(directory);
							unitTestsText.setText(directory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for unit test source directory", ex);
					}
				}
			}
		);
				
		// Build integration nag email address textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("BuildDirectoriesSection.nagEmailText.label"),
			Mevenide.getResourceString("BuildDirectoriesSection.nagEmailText.tooltip"), 
			factory
		);
		nagEmailText = new OverridableTextEntry(createText(container, factory), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setNagEmailAddress(pom, (String) value);
			}
			public Object acceptParent() {
				return getNagEmailAddress(getParentPom());
			}
		};
		nagEmailText.addEntryChangeListener(adaptor);
		nagEmailText.addOverrideAdaptor(adaptor);
		
		factory.paintBordersFor(container);
		return container;
	}

    public void update(Project pom) {
		setIfDefined(sourceText, getSourceDirectory(pom), getInheritedSourceDirectory());
		setIfDefined(aspectsText, getAspectSourceDirectory(pom), getInheritedAspectSourceDirectory());
		setIfDefined(unitTestsText, getUnitTestSourceDirectory(pom), getInheritedUnitTestSourceDirectory());
		setIfDefined(nagEmailText, getNagEmailAddress(pom), getInheritedNagEmailAddress());
	}
	
	private void setSourceDirectory(Project pom, String sourceDir) {
		getOrCreateBuild(pom).setSourceDirectory(sourceDir);
	}
	
	private String getSourceDirectory(Project pom) {
		return pom.getBuild() != null ? pom.getBuild().getSourceDirectory() : null;
	}
	
	private String getInheritedSourceDirectory() {
		return isInherited() 
			? getSourceDirectory(getParentPom())
			: null;
	}

	private void setAspectSourceDirectory(Project pom, String sourceDir) {
		getOrCreateBuild(pom).setAspectSourceDirectory(sourceDir);
	}
	
	private String getAspectSourceDirectory(Project pom) {
		return pom.getBuild() != null ? pom.getBuild().getAspectSourceDirectory() : null;
	}
	
	private String getInheritedAspectSourceDirectory() {
		return isInherited() 
			? getAspectSourceDirectory(getParentPom())
			: null;
	}

	private void setUnitTestSourceDirectory(Project pom, String sourceDir) {
		getOrCreateBuild(pom).setUnitTestSourceDirectory(sourceDir);
	}
	
	private String getUnitTestSourceDirectory(Project pom) {
		return pom.getBuild() != null ? pom.getBuild().getUnitTestSourceDirectory() : null;
	}
	
	private String getInheritedUnitTestSourceDirectory() {
		return isInherited() 
			? getUnitTestSourceDirectory(getParentPom())
			: null;
	}

	private void setNagEmailAddress(Project pom, String sourceDir) {
		getOrCreateBuild(pom).setNagEmailAddress(sourceDir);
	}
	
	private String getNagEmailAddress(Project pom) {
		return pom.getBuild() != null ? pom.getBuild().getNagEmailAddress() : null;
	}
	
	private String getInheritedNagEmailAddress() {
		return isInherited() 
			? getNagEmailAddress(getParentPom())
			: null;
	}

	private Build getOrCreateBuild(Project pom) {
		Build build = pom.getBuild();
		if (build == null) {
			build = new Build();
			pom.setBuild(build);
		}
		return build;
	}

}
