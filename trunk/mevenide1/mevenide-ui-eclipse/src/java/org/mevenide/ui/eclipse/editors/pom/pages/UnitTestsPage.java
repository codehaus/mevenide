/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
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
 * Presents a client control for editing information relating to the
 * build process and environment for this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class UnitTestsPage extends AbstractPomEditorPage {

	private IncludesSection includesSection;
	private ExcludesSection excludesSection;
	private ResourcesSection resourcesSection;

    public UnitTestsPage(MevenidePomEditor editor) {
        super(editor, MevenideResources.UNIT_TESTS_PAGE_ID, MevenideResources.UNIT_TESTS_PAGE_TAB, MevenideResources.UNIT_TESTS_PAGE_HEADING);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
     */
    public void initialize(FormEditor editor) {
        super.initialize(editor);

        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.addProjectChangeListener(ProjectComparator.UNIT_TESTS, this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.removeProjectChangeListener(ProjectComparator.UNIT_TESTS, this);
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

		includesSection = new IncludesSection(this, parent, factory);
		IIncludesAdaptor includesAdaptor = new IIncludesAdaptor() {
			public void setIncludes(Object target, List newIncludes) {
				Project pom = (Project) target;
				List includes = getOrCreateUnitTest(pom).getIncludes();
				includes.removeAll(includes);
				includes.addAll(newIncludes);
				getPomEditor().setModelDirty(true);
			}
	
			public void addInclude(Object target, String include) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addInclude(include);
				getPomEditor().setModelDirty(true);
			}
	
			public List getIncludes(Object source) {
				Project pom = (Project) source;
				return pom.getBuild() != null 
					? pom.getBuild().getUnitTest() != null
						? pom.getBuild().getUnitTest().getIncludes()
						: null
					: null;
			}
		};
		includesSection.setIncludesAdaptor(includesAdaptor);
		Control control = includesSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		addSection(includesSection);
		
		excludesSection = new ExcludesSection(this, parent, factory);
		IExcludesAdaptor excludesAdaptor = new IExcludesAdaptor() {
			public void setExcludes(Object target, List newExcludes) {
				Project pom = (Project) target;
				List excludes = getOrCreateUnitTest(pom).getExcludes();
				excludes.removeAll(excludes);
				excludes.addAll(newExcludes);
				getPomEditor().setModelDirty(true);
			}
	
			public void addExclude(Object target, String exclude) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addExclude(exclude);
				getPomEditor().setModelDirty(true);
			}
	
			public List getExcludes(Object source) {
				Project pom = (Project) source;
				return pom.getBuild() != null 
					? pom.getBuild().getUnitTest() != null
						? pom.getBuild().getUnitTest().getExcludes()
						: null
					: null;
			}
		};
		excludesSection.setExcludesAdaptor(excludesAdaptor);
		control = excludesSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		addSection(excludesSection);
		
		resourcesSection = new ResourcesSection(this, parent, factory, "UnitTestResourcesSection"); //$NON-NLS-1$
		IResourceAdaptor adaptor = new IResourceAdaptor() {
			public void setResources(Object target, List resources) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).setResources(resources);
				getPomEditor().setModelDirty(true);
			}
		
			public void addResource(Object target, Resource resource) {
				Project pom = (Project) target;
				UnitTest unitTest = getOrCreateUnitTest(pom);
				if ( unitTest.getResources() == null ) {
				    unitTest.setResources(new ArrayList());
				}
				unitTest.addResource(resource);
				getPomEditor().setModelDirty(true);
			}
		
			public List getResources(Object source) {
				Project pom = (Project) source;
				Build build = pom.getBuild();
				if (build != null) {
					UnitTest unitTest = build.getUnitTest();
					if (unitTest != null) {
						return unitTest.getResources();
					}
				}
				return null;
			}
		};
		resourcesSection.setResourceAdaptor(adaptor);

		control = resourcesSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		addSection(resourcesSection);
	}

	private UnitTest getOrCreateUnitTest(Project pom) {
		Build build = pom.getBuild();
		if (build == null) {
			build = new Build();
			pom.setBuild(build);
		}
		UnitTest unitTest = build.getUnitTest();
		if (unitTest == null) {
			unitTest = new UnitTest();
			build.setUnitTest(unitTest);
		}
		return unitTest;
	}

}
