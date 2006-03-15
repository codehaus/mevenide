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
package org.mevenide.ui.eclipse.editors.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.MavenProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class SiteGenerationSection extends PageSection {

	private static final Log log = LogFactory.getLog(SiteGenerationSection.class);

	private OverridableTextEntry siteAddressText;
	private OverridableTextEntry distSiteText;
	private OverridableTextEntry issueTrackingText;
	private OverridableTextEntry siteDirectoryText;
	private OverridableTextEntry distDirectoryText;

    public SiteGenerationSection(
        OrganizationPage page, 
   		Composite parent, 
   		FormToolkit toolkit)
   	{
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("SiteGenerationSection.header"));
		setDescription(Mevenide.getResourceString("SiteGenerationSection.description"));
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pages.PageSection#createSectionContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    protected Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 4 : 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final MavenProject pom = getPage().getPomEditor().getPom();
		
		// Site address textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("SiteGenerationSection.siteAddressText.label"),
			Mevenide.getResourceString("SiteGenerationSection.siteAddressText.tooltip"), 
			factory
		);
		siteAddressText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.getModel().setSiteAddress((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getModel().getSiteAddress();
			}
		};
		siteAddressText.addEntryChangeListener(adaptor);
		siteAddressText.addOverrideAdaptor(adaptor);
		
		// Distribution site (hostname) textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("SiteGenerationSection.distSiteText.label"),
			Mevenide.getResourceString("SiteGenerationSection.distSiteText.tooltip"), 
			factory
		);
		distSiteText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.getModel().setDistributionSite((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getModel().getDistributionSite();
			}
		};
		distSiteText.addEntryChangeListener(adaptor);
		distSiteText.addOverrideAdaptor(adaptor);
		
		// Site directory textbox and directory browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("SiteGenerationSection.siteDirectoryText.label"), 
			Mevenide.getResourceString("SiteGenerationSection.siteDirectoryText.tooltip"), 
			factory
		);
		String labelName = Mevenide.getResourceString("SiteGenerationSection.siteDirectoryButton.label");
		String toolTip = Mevenide.getResourceString("SiteGenerationSection.siteDirectoryButton.tooltip");
		final String title = Mevenide.getResourceString("SiteGenerationSection.siteDirectoryButton.dialog.title");
		siteDirectoryText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.getModel().setSiteDirectory((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getModel().getSiteDirectory();
			}
		};
		siteDirectoryText.addEntryChangeListener(adaptor);
		siteDirectoryText.addOverrideAdaptor(adaptor);
		
		siteDirectoryText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getPomEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(title);
						
						String siteDirectory = dialog.open();
						if (siteDirectory != null) {
							siteDirectoryText.setFocus();
							siteDirectoryText.setText(siteDirectory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for site deployment directory", ex);
					}
				}
			}
		);
		
		// Distribution directory textbox and directory browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("SiteGenerationSection.distDirectoryText.label"), 
			Mevenide.getResourceString("SiteGenerationSection.distDirectoryText.tooltip"), 
			factory
		);
		labelName = Mevenide.getResourceString("SiteGenerationSection.distDirectoryButton.label");
		toolTip = Mevenide.getResourceString("SiteGenerationSection.distDirectoryButton.tooltip");
		final String title1 = Mevenide.getResourceString("SiteGenerationSection.distDirectoryButton.dialog.title");
		distDirectoryText = new OverridableTextEntry(
			createText(container, factory), 
			toggle, 
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.getModel().setDistributionDirectory((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getModel().getDistributionDirectory();
			}
		};
		distDirectoryText.addEntryChangeListener(adaptor);
		distDirectoryText.addOverrideAdaptor(adaptor);
		distDirectoryText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getPomEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(title1);
						
						String directory = dialog.open();
						if (directory != null) {
							distDirectoryText.setFocus();
							distDirectoryText.setText(directory);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for distribution deployment directory", ex);
					}
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(MavenProject pom) {
		setIfDefined(siteAddressText, pom.getModel().getSiteAddress(), isInherited() ? getParentPom().getModel().getSiteAddress() : null);
		setIfDefined(siteDirectoryText, pom.getModel().getSiteDirectory(), isInherited() ? getParentPom().getModel().getSiteDirectory() : null);
		setIfDefined(distDirectoryText, pom.getModel().getDistributionDirectory(), isInherited() ? getParentPom().getModel().getDistributionDirectory() : null);
	}

}