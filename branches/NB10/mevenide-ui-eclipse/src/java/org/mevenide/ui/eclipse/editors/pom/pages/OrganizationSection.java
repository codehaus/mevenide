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
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OrganizationSection extends PageSection {

	private static final Log log = LogFactory.getLog(OrganizationSection.class);
    
	private OverridableTextEntry nameText;
	private OverridableTextEntry urlText;
	private OverridableTextEntry logoText;

    public OrganizationSection(
        OrganizationPage page, 
        Composite parent, 
        FormToolkit toolkit)
    {
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("OrganizationSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("OrganizationSection.description")); //$NON-NLS-1$
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pom.pages.PageSection#createSectionContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    protected Composite createSectionContent(Composite parent, FormToolkit factory) {
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
			Mevenide.getResourceString("OrganizationSection.nameText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("OrganizationSection.nameText.tooltip"),  //$NON-NLS-1$
			factory
		);
		nameText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
			    if ( value != null ) {
				    if ( pom.getOrganization() == null ) {
				        pom.setOrganization(new Organization());
				    }
				    pom.getOrganization().setName((String) value);
			    }
			}
			public Object acceptParent() {
			    return getParentPom() != null && getParentPom().getOrganization() != null ? getParentPom().getOrganization().getName() : null;
			}
		};
		nameText.addEntryChangeListener(adaptor);
		nameText.addOverrideAdaptor(adaptor);
				
		// POM project url textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("OrganizationSection.urlText.label"), //$NON-NLS-1$
			Mevenide.getResourceString("OrganizationSection.urlText.tooltip"),  //$NON-NLS-1$
			factory
		);
		urlText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
			    if ( value != null ) {
				    if ( pom.getOrganization() == null ) {
				        pom.setOrganization(new Organization());
				    }
				    pom.getOrganization().setUrl((String) value);
			    }
			}
			public Object acceptParent() {
				return getParentPom() != null && getParentPom().getOrganization() != null ? getParentPom().getOrganization().getUrl() : null;
			}
		};
		urlText.addEntryChangeListener(adaptor);
		urlText.addOverrideAdaptor(adaptor);
		
		// POM logo textbox and file browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("OrganizationSection.logoText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("OrganizationSection.logoText.tooltip"),  //$NON-NLS-1$
			factory
		);
		String labelName = Mevenide.getResourceString("OrganizationSection.logoButton.label"); //$NON-NLS-1$
		String toolTip = Mevenide.getResourceString("OrganizationSection.logoButton.tooltip"); //$NON-NLS-1$
		final String title = Mevenide.getResourceString("OrganizationSection.logoButton.dialog.title"); //$NON-NLS-1$
		logoText = new OverridableTextEntry(
			createText(container, factory), 
			toggle,
			createBrowseButton(container, factory, labelName, toolTip, 1)
		);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
			    if ( value != null ) {
				    if ( pom.getOrganization() == null ) {
				        pom.setOrganization(new Organization());
				    }
				    pom.getOrganization().setLogo((String) value);
			    }
			}
			public Object acceptParent() {
				return getParentPom() != null && getParentPom().getOrganization() != null ? getParentPom().getOrganization().getLogo() : null;
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
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
	    setIfDefined(nameText, pom.getOrganization() != null ? pom.getOrganization().getName() : null, isInherited() ? (getParentPom() != null && getParentPom().getOrganization() != null ? getParentPom().getOrganization().getName() : null) : null);
		setIfDefined(urlText, pom.getOrganization() != null ? pom.getOrganization().getUrl() : null, isInherited() ? (getParentPom() != null && getParentPom().getOrganization() != null ? getParentPom().getOrganization().getUrl() : null) : null);
		setIfDefined(logoText, pom.getOrganization() != null ? pom.getOrganization().getLogo() : null, isInherited() ? (getParentPom() != null && getParentPom().getOrganization() != null ? getParentPom().getOrganization().getLogo() : null) : null);
	}

}
