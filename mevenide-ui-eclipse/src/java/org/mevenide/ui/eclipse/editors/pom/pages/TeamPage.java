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
import org.eclipse.ui.forms.editor.FormEditor;
import org.mevenide.project.ProjectComparator;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;

/**
 * Presents a client control for editing information on the team
 * (contributors and developers) for the project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class TeamPage extends AbstractPomEditorPage {

	private ContributorsSection contribSection;
	private DevelopersSection devSection;
	private MailingListsSection mailListSection;
	
    public TeamPage(MevenidePomEditor editor) {
        super(editor, MevenideResources.TEAM_PAGE_ID, MevenideResources.TEAM_PAGE_TAB, MevenideResources.TEAM_PAGE_HEADING);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
     */
    public void initialize(FormEditor editor) {
        super.initialize(editor);

        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.addProjectChangeListener(ProjectComparator.CONTRIBUTORS, this);
            comparator.addProjectChangeListener(ProjectComparator.DEVELOPERS, this);
            comparator.addProjectChangeListener(ProjectComparator.MAILINGLISTS, this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.removeProjectChangeListener(ProjectComparator.CONTRIBUTORS, this);
            comparator.removeProjectChangeListener(ProjectComparator.DEVELOPERS, this);
            comparator.removeProjectChangeListener(ProjectComparator.MAILINGLISTS, this);
        }

        super.dispose();
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

		devSection = new DevelopersSection(this, parent, factory);
		Control control = devSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(devSection);

		contribSection = new ContributorsSection(this, parent, factory);
		control = contribSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(contribSection);

		mailListSection = new MailingListsSection(this, parent, factory);
		control = mailListSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(mailListSection);
	}

}
