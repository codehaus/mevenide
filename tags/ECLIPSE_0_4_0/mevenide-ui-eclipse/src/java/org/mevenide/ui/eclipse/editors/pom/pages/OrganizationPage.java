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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;

/**
 * Presents information on the organization, licensing, and site generation
 * aspects of the project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OrganizationPage extends AbstractPomEditorPage {

    private static final String ID = Mevenide.getResourceString("OrganizationPage.id"); //$NON-NLS-1$
    private static final String TAB = Mevenide.getResourceString("OrganizationPage.tab.label"); //$NON-NLS-1$
    private static final String HEADING = Mevenide.getResourceString("OrganizationPage.heading"); //$NON-NLS-1$
    
	private OrganizationSection orgSection;
	private LicenseSection licenseSection;
	private SiteGenerationSection siteGenSection;
	
    public OrganizationPage(MevenidePomEditor editor) {
        super(editor, ID, TAB, HEADING);
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pom.pages.AbstractPomEditorPage#createPageContent(org.eclipse.swt.widgets.Composite)
     */
    protected void createPageContent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		FormToolkit factory = getEditor().getToolkit();

		orgSection = new OrganizationSection(this, parent, factory);
		Control control = orgSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(orgSection);

		licenseSection = new LicenseSection(this, parent, factory);
		control = licenseSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(licenseSection);

		siteGenSection = new SiteGenerationSection(this, parent, factory);
		control = siteGenSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(siteGenSection);
    }

}
