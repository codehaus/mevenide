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
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;

/**
 * Presents a client control for editing the reports to be generated for
 * this project.
 *  
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ReportsPage extends AbstractPomEditorPage {

	private ReportsSection reportsSection;

	public ReportsPage(MevenidePomEditor editor) {
        super(editor, MevenideResources.REPORTS_PAGE_ID, MevenideResources.REPORTS_PAGE_TAB, MevenideResources.REPORTS_PAGE_HEADING);
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pom.pages.AbstractPomEditorPage#createPageContent(org.eclipse.swt.widgets.Composite)
     */
    protected void createPageContent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		FormToolkit factory = getEditor().getToolkit();

		reportsSection = new ReportsSection(this, parent, factory);
		Control control = reportsSection.getSection();
		GridData gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		addSection(reportsSection);
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pom.IPomEditorPage#isPropertySourceSupplier()
	 */
	public boolean isPropertySourceSupplier() {
		return false;
	}
	
}
