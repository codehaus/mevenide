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

import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.model.UnitTest;
import org.apache.maven.project.MavenProject;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents a client control for editing information relating to the
 * build process and environment for this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class UnitTestsPage extends AbstractPomEditorPage {

    private static final String ID = Mevenide.getResourceString("UnitTestsPage.id");
    private static final String TAB = Mevenide.getResourceString("UnitTestsPage.tab.label");
    private static final String HEADING = Mevenide.getResourceString("UnitTestsPage.heading");
    
	private IncludesSection includesSection;
	private ExcludesSection excludesSection;
	private ResourcesSection resourcesSection;

    public UnitTestsPage(MevenidePomEditor editor) {
        super(editor, ID, TAB, HEADING);
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomEditorPage#createPageContent(org.eclipse.swt.widgets.Composite)
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
				MavenProject pom = (MavenProject) target;
				List includes = getOrCreateUnitTest(pom).getIncludes();
				includes.removeAll(includes);
				includes.addAll(newIncludes);
				getPomEditor().setModelDirty(true);
			}
	
			public void addInclude(Object target, String include) {
				MavenProject pom = (MavenProject) target;
				getOrCreateUnitTest(pom).addInclude(include);
				getPomEditor().setModelDirty(true);
			}
	
			public List getIncludes(Object source) {
				MavenProject pom = (MavenProject) source;
				return pom.getModel().getBuild() != null 
					? pom.getModel().getBuild().getUnitTest() != null
						? pom.getModel().getBuild().getUnitTest().getIncludes()
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
				MavenProject pom = (MavenProject) target;
				List excludes = getOrCreateUnitTest(pom).getExcludes();
				excludes.removeAll(excludes);
				excludes.addAll(newExcludes);
				getPomEditor().setModelDirty(true);
			}
	
			public void addExclude(Object target, String exclude) {
				MavenProject pom = (MavenProject) target;
				getOrCreateUnitTest(pom).addExclude(exclude);
				getPomEditor().setModelDirty(true);
			}
	
			public List getExcludes(Object source) {
				MavenProject pom = (MavenProject) source;
				return pom.getModel().getBuild() != null 
					? pom.getBuild().getUnitTest() != null
						? pom.getModel().getBuild().getUnitTest().getExcludes()
						: null
					: null;
			}
		};
		excludesSection.setExcludesAdaptor(excludesAdaptor);
		control = excludesSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		addSection(excludesSection);
		
		resourcesSection = new ResourcesSection(this, parent, factory, "UnitTestResourcesSection");
		IResourceAdaptor adaptor = new IResourceAdaptor() {
			public void setResources(Object target, List resources) {
				MavenProject pom = (MavenProject) target;
				getOrCreateUnitTest(pom).setResources(resources);
				getPomEditor().setModelDirty(true);
			}
		
			public void addResource(Object target, Resource resource) {
				MavenProject pom = (MavenProject) target;
				getOrCreateUnitTest(pom).addResource(resource);
				getPomEditor().setModelDirty(true);
			}
		
			public List getResources(Object source) {
				MavenProject pom = (MavenProject) source;
				Build build = pom.getModel().getBuild();
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

	private UnitTest getOrCreateUnitTest(MavenProject pom) {
		Build build = pom.getModel().getBuild();
		if (build == null) {
			build = new Build();
			pom.getModel().setBuild(build);
		}
		UnitTest unitTest = build.getUnitTest();
		if (unitTest == null) {
			unitTest = new UnitTest();
			build.setUnitTest(unitTest);
		}
		return unitTest;
	}

}
