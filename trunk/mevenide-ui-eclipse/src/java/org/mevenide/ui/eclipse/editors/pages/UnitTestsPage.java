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

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

	public static final String HEADING = Mevenide.getResourceString("UnitTestsPage.heading");
    
	private IncludesSection includesSection;
	private ExcludesSection excludesSection;
	private ResourcesSection resourcesSection;

    public UnitTestsPage(MevenidePomEditor editor) {
        super(HEADING, editor);
    }

	protected void initializePage(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		PageWidgetFactory factory = getFactory();

		includesSection = new IncludesSection(this);
		IIncludesAdaptor includesAdaptor = new IIncludesAdaptor() {
			public void setIncludes(Object target, List newIncludes) {
				Project pom = (Project) target;
				List includes = getOrCreateUnitTest(pom).getIncludes();
				includes.removeAll(includes);
				includes.addAll(newIncludes);
				getEditor().setModelDirty(true);
			}
	
			public void addInclude(Object target, String include) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addInclude(include);
				getEditor().setModelDirty(true);
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
		Control control = includesSection.createControl(parent, factory);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		addSection(includesSection);
		
		excludesSection = new ExcludesSection(this);
		IExcludesAdaptor excludesAdaptor = new IExcludesAdaptor() {
			public void setExcludes(Object target, List newExcludes) {
				Project pom = (Project) target;
				List excludes = getOrCreateUnitTest(pom).getExcludes();
				excludes.removeAll(excludes);
				excludes.addAll(newExcludes);
				getEditor().setModelDirty(true);
			}
	
			public void addExclude(Object target, String exclude) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addExclude(exclude);
				getEditor().setModelDirty(true);
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
		control = excludesSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		addSection(excludesSection);
		
		resourcesSection = new ResourcesSection(this, "UnitTestResourcesSection");
		IResourceAdaptor adaptor = new IResourceAdaptor() {
			public void setResources(Object target, List resources) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).setResources(resources);
				getEditor().setModelDirty(true);
			}
		
			public void addResource(Object target, Resource resource) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addResource(resource);
				getEditor().setModelDirty(true);
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

		control = resourcesSection.createControl(parent, factory);
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
