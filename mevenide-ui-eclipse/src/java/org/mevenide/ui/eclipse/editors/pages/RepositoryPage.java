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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents a client control for editing information on the source 
 * configuration management system used by this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class RepositoryPage extends AbstractPomEditorPage {

	public static final String HEADING = Mevenide.getResourceString("RepositoryPage.heading");
    
	private ScmConnectionSection scmSection;
	private VersionsSection versionsSection;
	private BranchesSection branchesSection;

	public RepositoryPage(MevenidePomEditor editor) {
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

		scmSection = new ScmConnectionSection(this);
		Control control = scmSection.createControl(parent, factory);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(scmSection);

		versionsSection = new VersionsSection(this);
		control = versionsSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(versionsSection);

		branchesSection = new BranchesSection(this);
		control = branchesSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(branchesSection);
	}

}
