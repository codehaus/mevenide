/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
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

import org.apache.maven.project.Project;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OrganizationSection extends PageSection {

	private OverridableTextEntry nameText;
	private OverridableTextEntry urlText;
	private OverridableTextEntry logoText;
	private Button logoButton;

    public OrganizationSection(OrganizationPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("OrganizationSection.header"));
		setDescription(Mevenide.getResourceString("OrganizationSection.description"));
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
		
		// POM short description textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("OrganizationSection.nameText.label"), 
			Mevenide.getResourceString("OrganizationSection.nameText.tooltip"), 
			factory
		);
		nameText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void updateProject(String value) {
				pom.getOrganization().setName(value);
			}
			public String getParentProjectAttribute() {
				return getParentPom().getOrganization().getName();
			}
		};
		nameText.addEntryChangeListener(adaptor);
		nameText.addOverrideAdaptor(adaptor);
				
		// POM project url textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("OrganizationSection.urlText.label"),
			Mevenide.getResourceString("OrganizationSection.urlText.tooltip"), 
			factory
		);
		urlText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void updateProject(String value) {
				pom.getOrganization().setUrl(value);
			}
			public String getParentProjectAttribute() {
				return getParentPom().getOrganization().getUrl();
			}
		};
		urlText.addEntryChangeListener(adaptor);
		urlText.addOverrideAdaptor(adaptor);
		
		// POM logo textbox and file browse button
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("OrganizationSection.logoText.label"), 
			Mevenide.getResourceString("OrganizationSection.logoText.tooltip"), 
			factory
		);
		logoText = new OverridableTextEntry(createText(container, factory), toggle);
		adaptor = new OverrideAdaptor() {
			public void updateProject(String value) {
				pom.getOrganization().setLogo(value);
			}
			public String getParentProjectAttribute() {
				return getParentPom().getOrganization().getLogo();
			}
		};
		logoText.addEntryChangeListener(adaptor);
		logoText.addOverrideAdaptor(adaptor);
		
		Composite buttonContainer = factory.createComposite(container);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 1;
		buttonContainer.setLayoutData(data);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);

		String labelName = Mevenide.getResourceString("OrganizationSection.logoButton.label");
		String toolTip = Mevenide.getResourceString("OrganizationSection.logoButton.tooltip");
		logoButton = factory.createButton(buttonContainer, labelName, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		logoButton.setLayoutData(data);
		logoButton.setToolTipText(toolTip);

		final String title = Mevenide.getResourceString("OrganizationSection.logoButton.dialog.title");
		logoButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						FileDialog dialog = new FileDialog(
							getPage().getEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(title);
						
						String imageFile = dialog.open();
						if (imageFile != null) {
							logoText.setFocus();
							logoText.setText(imageFile);
						}
					}
					catch ( Exception ex ) {
//						log.error("Unable to browse for logo images", ex);
					}
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		setIfDefined(nameText, pom.getOrganization().getName(), isInherited() ? getParentPom().getOrganization().getName() : null);
		setIfDefined(urlText, pom.getOrganization().getUrl(), isInherited() ? getParentPom().getOrganization().getUrl() : null);
		setIfDefined(logoText, pom.getOrganization().getLogo(), isInherited() ? getParentPom().getOrganization().getLogo() : null);

		super.update(pom);
	}

}
