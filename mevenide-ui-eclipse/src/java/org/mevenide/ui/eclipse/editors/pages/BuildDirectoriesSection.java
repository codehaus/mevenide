/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.mevenide.ui.eclipse.editors.pages;

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
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class BuildDirectoriesSection extends PageSection {

	private static final Log log = LogFactory.getLog(BuildDirectoriesSection.class);

	private OverridableTextEntry sourceText;
	private OverridableTextEntry aspectsText;
	private OverridableTextEntry unitTestsText;
	private OverridableTextEntry integrationTestsText;
	private OverridableTextEntry nagEmailText;

    public BuildDirectoriesSection(BuildPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("BuildDirectoriesSection.header"));
		setDescription(Mevenide.getResourceString("BuildDirectoriesSection.description"));
    }

    public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 4 : 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getEditor().getPom();
		
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
							getPage().getEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(sourceTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							sourceText.setFocus();
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
							getPage().getEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(aspectsTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							aspectsText.setFocus();
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
							getPage().getEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(unitTestsTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							unitTestsText.setFocus();
							unitTestsText.setText(directory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for unit test source directory", ex);
					}
				}
			}
		);
		
		// Build integration unit tests source directory textbox and browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("BuildDirectoriesSection.integrationTestsText.label"),
			Mevenide.getResourceString("BuildDirectoriesSection.integrationTestsText.tooltip"), 
			factory
		);
		labelName = Mevenide.getResourceString("BuildDirectoriesSection.integrationTestsButton.label");
		toolTip = Mevenide.getResourceString("BuildDirectoriesSection.integrationTestsButton.tooltip");
		final String intgrationTestsTitle = Mevenide.getResourceString("BuildDirectoriesSection.integrationTestsButton.dialog.title");
		integrationTestsText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setIntegrationUnitTestSourceDirectory(pom, (String) value);
			}
			public Object acceptParent() {
				return getIntegrationUnitTestSourceDirectory(getParentPom());
			}
		};
		integrationTestsText.addEntryChangeListener(adaptor);
		integrationTestsText.addOverrideAdaptor(adaptor);
		integrationTestsText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(intgrationTestsTitle);
						
						String directory = dialog.open();
						if (directory != null) {
							sourceText.setFocus();
							sourceText.setText(directory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for intgration tests directory", ex);
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
		setIfDefined(integrationTestsText, getIntegrationUnitTestSourceDirectory(pom), getInheritedIntegrationUnitTestSourceDirectory());
		setIfDefined(nagEmailText, getNagEmailAddress(pom), getInheritedNagEmailAddress());

		super.update(pom);
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

	private void setIntegrationUnitTestSourceDirectory(Project pom, String sourceDir) {
		getOrCreateBuild(pom).setIntegrationUnitTestSourceDirectory(sourceDir);
	}
	
	private String getIntegrationUnitTestSourceDirectory(Project pom) {
		return pom.getBuild() != null ? pom.getBuild().getIntegrationUnitTestSourceDirectory() : null;
	}
	
	private String getInheritedIntegrationUnitTestSourceDirectory() {
		return isInherited() 
			? getIntegrationUnitTestSourceDirectory(getParentPom())
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
