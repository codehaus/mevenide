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

import org.apache.maven.project.Project;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents information on the organization, licensing, and site generation
 * aspects of the project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OrganizationPage extends AbstractPomEditorPage {

    public static final String HEADING = Mevenide.getResourceString("OrganizationPage.heading");
    
	private OrganizationSection orgSection;
	private LicenseSection licenseSection;
	private SiteGenerationSection siteGenSection;
	
    public OrganizationPage(MevenidePomEditor editor) {
        super(HEADING, editor);
    }

	protected void initializePage(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		PageWidgetFactory factory = getFactory();
		factory.setBackgroundColor(MevenideColors.WHITE);

		orgSection = new OrganizationSection(this);
		Control control = orgSection.createControl(parent, factory);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);

		licenseSection = new LicenseSection(this);
		control = licenseSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);

		siteGenSection = new SiteGenerationSection(this);
		control = siteGenSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
	}

	public void update(Project pom) {
		orgSection.update(pom);
		licenseSection.update(pom);
		siteGenSection.update(pom);
		
		setUpdateNeeded(false);
	}
	
}
