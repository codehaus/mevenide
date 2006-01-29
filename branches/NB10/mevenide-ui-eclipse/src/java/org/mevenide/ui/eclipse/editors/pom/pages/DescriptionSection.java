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
package org.mevenide.ui.eclipse.editors.pom.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DescriptionSection extends PageSection {

	private static final Log log = LogFactory.getLog(DescriptionSection.class);

	private OverridableTextEntry shortDescText;
	private OverridableTextEntry inceptionYearText;
	private OverridableTextEntry urlText;
	private OverridableTextEntry currentVersionText;
	private OverridableTextEntry logoText;
	private OverridableTextEntry packageText;

    public DescriptionSection(
        DescriptionPage page, 
		Composite parent, 
		FormToolkit toolkit) 
   	{
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("DescriptionSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("DescriptionSection.description")); //$NON-NLS-1$
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
		
		// POM short description textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("DescriptionSection.shortDescText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("DescriptionSection.shortDescText.tooltip"),  //$NON-NLS-1$
			factory
		);
		shortDescText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setShortDescription((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getShortDescription();
			}
		};
		shortDescText.addEntryChangeListener(adaptor);
		shortDescText.addOverrideAdaptor(adaptor);
		
		// POM project inception year textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("DescriptionSection.inceptionYearText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("DescriptionSection.inceptionYearText.tooltip"),  //$NON-NLS-1$
			factory
		);
		inceptionYearText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setInceptionYear((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getInceptionYear();
			}
		};
		inceptionYearText.addEntryChangeListener(adaptor);
		inceptionYearText.addOverrideAdaptor(adaptor);
		
		// POM project url textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("DescriptionSection.urlText.label"), //$NON-NLS-1$
			Mevenide.getResourceString("DescriptionSection.urlText.tooltip"),  //$NON-NLS-1$
			factory
		);
		urlText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setUrl((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getUrl();
			}
		};
		urlText.addEntryChangeListener(adaptor);
		urlText.addOverrideAdaptor(adaptor);
		
		// POM current version textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("DescriptionSection.currentVersionText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("DescriptionSection.currentVersionText.tooltip"),  //$NON-NLS-1$
			factory
		);
		currentVersionText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setCurrentVersion((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getCurrentVersion();
			}
		};
		currentVersionText.addEntryChangeListener(adaptor);
		currentVersionText.addOverrideAdaptor(adaptor);
		
		// POM logo textbox and file browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("DescriptionSection.logoText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("DescriptionSection.logoText.tooltip"),  //$NON-NLS-1$
			factory
		);
		String labelName = Mevenide.getResourceString("DescriptionSection.logoButton.label"); //$NON-NLS-1$
		String toolTip = Mevenide.getResourceString("DescriptionSection.logoButton.tooltip"); //$NON-NLS-1$
		final String title = Mevenide.getResourceString("DescriptionSection.logoButton.dialog.title"); //$NON-NLS-1$
		logoText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setLogo((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getLogo();
			}
		};
		logoText.addEntryChangeListener(adaptor);
		logoText.addOverrideAdaptor(adaptor);
		logoText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						FileDialog dialog = new FileDialog(
							getPage().getPomEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(title);
						
						String imageFile = dialog.open();
						if (imageFile != null) {
							logoText.setFocus();
							imageFile = getRelativePath(imageFile);
							logoText.setText(imageFile);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for logo images", ex); //$NON-NLS-1$
					}
				}
			}
		);
		
		// POM package textbox and package browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("DescriptionSection.packageText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("DescriptionSection.packageText.tooltip"),  //$NON-NLS-1$
			factory
		);
		labelName = Mevenide.getResourceString("DescriptionSection.packageButton.label"); //$NON-NLS-1$
		toolTip = Mevenide.getResourceString("DescriptionSection.packageButton.tooltip"); //$NON-NLS-1$
		final String title1 = Mevenide.getResourceString("DescriptionSection.packageButton.dialog.title"); //$NON-NLS-1$
		final String message1 = Mevenide.getResourceString("DescriptionSection.packageButton.dialog.message"); //$NON-NLS-1$
		packageText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setPackage((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getPackage();
			}
		};
		packageText.addEntryChangeListener(adaptor);
		packageText.addOverrideAdaptor(adaptor);
		packageText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						IProject project = getPage().getPomEditor().getProject();
						IJavaProject javaProject = JavaCore.create(project);
						SelectionDialog dialog = JavaUI.createPackageDialog(
							getPage().getPomEditor().getSite().getShell(),
							javaProject,
							IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS
						);
						dialog.setTitle(title1);
						dialog.setMessage(message1);
						
						if (dialog.open() == Window.OK) {
							Object resource = dialog.getResult()[0];
							if (log.isDebugEnabled()) {
								log.debug("package selected = " + resource.getClass().getName()); //$NON-NLS-1$
							}
							IPackageFragment packageResource = (IPackageFragment) resource;
							packageText.setFocus();
							packageText.setText(packageResource.getElementName());
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for packages", ex); //$NON-NLS-1$
					}
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
    }

	public void update(Project pom) {
		setIfDefined(shortDescText, pom.getShortDescription(), isInherited() ? getParentPom().getShortDescription() : null);
		setIfDefined(inceptionYearText, pom.getInceptionYear(), isInherited() ? getParentPom().getInceptionYear() : null);
		setIfDefined(urlText, pom.getUrl(), isInherited() ? getParentPom().getUrl() : null);
		setIfDefined(logoText, pom.getLogo(), isInherited() ? getParentPom().getLogo() : null);
		setIfDefined(packageText, pom.getPackage(), isInherited() ? getParentPom().getPackage() : null);
		setIfDefined(currentVersionText, pom.getCurrentVersion(), isInherited() ? getParentPom().getCurrentVersion() : null);
	}
	
}
