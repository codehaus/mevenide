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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.OverridableTextEntry;

/**
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a> 
 * @version $Id$
 */
public class IssueManagementSection extends PageSection {

	private static final Log log = LogFactory.getLog(IssueManagementSection.class);

	private OverridableTextEntry systemText;
	private OverridableTextEntry urlText;
	

    public IssueManagementSection(
		OverviewPage page, 
		Composite parent, 
		FormToolkit toolkit) 
   	{
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("DescriptionSection.header"));
		setDescription(Mevenide.getResourceString("DescriptionSection.description"));
    }

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 4 : 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final MavenProject pom = getPage().getPomEditor().getPom();
		
		// POM short description textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IssueManagementSection.systemText.label"), 
			Mevenide.getResourceString("IssueManagementSection.systemText.tooltip"), 
			factory
		);
		systemText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.getModel().getIssueManagement().setSystem((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getModel().getIssueManagement().getSystem();
			}
		};
		systemText.addEntryChangeListener(adaptor);
		systemText.addOverrideAdaptor(adaptor);
		
		// POM project url textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IssueManagementSection.urlText.label"),
			Mevenide.getResourceString("IssueManagementSection.urlText.tooltip"), 
			factory
		);
		urlText = new OverridableTextEntry(createText(container, factory, 2), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.getModel().getIssueManagement().setUrl((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getModel().getIssueManagement().getUrl();
			}
		};
		urlText.addEntryChangeListener(adaptor);
		urlText.addOverrideAdaptor(adaptor);
		
		factory.paintBordersFor(container);
		return container;
    }

	public void update(MavenProject pom) {
		setIfDefined(systemText, pom.getModel().getIssueManagement().getSystem(), isInherited() ? getParentPom().getModel().getIssueManagement().getSystem() : null);
		setIfDefined(urlText, pom.getModel().getIssueManagement().getUrl(), isInherited() ? getParentPom().getModel().getIssueManagement().getUrl() : null);
	}
	
}
