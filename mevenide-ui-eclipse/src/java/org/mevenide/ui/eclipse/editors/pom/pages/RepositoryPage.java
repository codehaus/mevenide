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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.project.ProjectComparator;
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;

/**
 * Presents a client control for editing information on the source 
 * configuration management system used by this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class RepositoryPage extends AbstractPomEditorPage {

	private ScmConnectionSection scmSection;
	private VersionsSection versionsSection;
	private BranchesSection branchesSection;

	public RepositoryPage(MevenidePomEditor editor) {
        super(editor, MevenideResources.REPOSITORY_PAGE_ID, MevenideResources.REPOSITORY_PAGE_TAB, MevenideResources.REPOSITORY_PAGE_HEADING);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
     */
    public void initialize(FormEditor editor) {
        super.initialize(editor);

        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.addProjectChangeListener(ProjectComparator.REPOSITORY, this);
            comparator.addProjectChangeListener(ProjectComparator.VERSIONS, this);
            comparator.addProjectChangeListener(ProjectComparator.BRANCHES, this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.removeProjectChangeListener(ProjectComparator.REPOSITORY, this);
            comparator.removeProjectChangeListener(ProjectComparator.VERSIONS, this);
            comparator.removeProjectChangeListener(ProjectComparator.BRANCHES, this);
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

		scmSection = new ScmConnectionSection(this, parent, factory);
		Control control = scmSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(scmSection);

		versionsSection = new VersionsSection(this, parent, factory);
		control = versionsSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(versionsSection);

		branchesSection = new BranchesSection(this, parent, factory);
		control = branchesSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(branchesSection);
	}

}
