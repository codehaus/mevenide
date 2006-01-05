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
 * Presents a client control for editing information on the build-time library
 * dependencies of this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DependenciesPage extends AbstractPomEditorPage {

	private DependenciesSection depsSection;
    
    public DependenciesPage(MevenidePomEditor editor) {
        super(editor, MevenideResources.DEPENDENCIES_PAGE_ID, MevenideResources.DEPENDENCIES_PAGE_TAB, MevenideResources.DEPENDENCIES_PAGE_HEADING);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
     */
    public void initialize(FormEditor editor) {
        super.initialize(editor);

        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.addProjectChangeListener(ProjectComparator.DEPENDENCIES, this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.removeProjectChangeListener(ProjectComparator.DEPENDENCIES, this);
        }

        super.dispose();
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

		depsSection = new DependenciesSection(this, parent, factory);
		Control control = depsSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(depsSection);
	}

}
