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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.mevenide.ui.eclipse.Mevenide;

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

    public SiteGenerationSection(OrganizationPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("SiteGenerationSection.header"));
		setDescription(Mevenide.getResourceString("SiteGenerationSection.description"));
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
				pom.setSiteAddress((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getSiteAddress();
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
				pom.setDistributionSite((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getDistributionSite();
			}
		};
		distSiteText.addEntryChangeListener(adaptor);
		distSiteText.addOverrideAdaptor(adaptor);
		
		// Issue tracking address textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("SiteGenerationSection.issueTrackingText.label"),
			Mevenide.getResourceString("SiteGenerationSection.issueTrackingText.tooltip"), 
			factory
		);
		issueTrackingText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setIssueTrackingUrl((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getIssueTrackingUrl();
			}
		};
		issueTrackingText.addEntryChangeListener(adaptor);
		issueTrackingText.addOverrideAdaptor(adaptor);
		
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
				pom.setSiteDirectory((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getSiteDirectory();
			}
		};
		siteDirectoryText.addEntryChangeListener(adaptor);
		siteDirectoryText.addOverrideAdaptor(adaptor);
		
		siteDirectoryText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getEditor().getSite().getShell(),
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
				pom.setDistributionDirectory((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getDistributionDirectory();
			}
		};
		distDirectoryText.addEntryChangeListener(adaptor);
		distDirectoryText.addOverrideAdaptor(adaptor);
		distDirectoryText.addBrowseButtonListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(
							getPage().getEditor().getSite().getShell(),
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

	public void update(Project pom) {
		setIfDefined(siteAddressText, pom.getSiteAddress(), isInherited() ? getParentPom().getSiteAddress() : null);
		setIfDefined(issueTrackingText, pom.getIssueTrackingUrl(), isInherited() ? getParentPom().getIssueTrackingUrl() : null);
		setIfDefined(siteDirectoryText, pom.getSiteDirectory(), isInherited() ? getParentPom().getSiteDirectory() : null);
		setIfDefined(distDirectoryText, pom.getDistributionDirectory(), isInherited() ? getParentPom().getDistributionDirectory() : null);

		super.update(pom);
	}

}
