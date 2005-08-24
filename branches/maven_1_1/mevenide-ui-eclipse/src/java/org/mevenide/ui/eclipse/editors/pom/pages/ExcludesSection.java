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

import java.util.List;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.UnitTest;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ExcludesSection extends PageSection {

	private ExcludesSubsection subsection;
	private IExcludesAdaptor excludesAdaptor;
	private TableEntry excludesTable;
	
	public ExcludesSection(
	    UnitTestsPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("UnitTestExcludesSection.header")); //$NON-NLS-1$
	}
	
	void setExcludesAdaptor(IExcludesAdaptor newExcludesAdaptor) {
		this.excludesAdaptor = newExcludesAdaptor;
	}

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		subsection = new ExcludesSubsection(this, excludesAdaptor);
		
		excludesTable = subsection.createWidget(container, factory, true);
		
		factory.paintBordersFor(container);

		return container;
	}

	public void update(Project pom) {
		subsection.updateTableEntries(excludesTable, getExcludes(pom), getInheritedExcludes(), false);
	}

	public void setExcludes(Object target, List newExcludes) {
		List excludes = getOrCreateUnitTest(target).getExcludes();
		excludes.removeAll(excludes);
		excludes.addAll(newExcludes);
		getPage().getPomEditor().setModelDirty(true);
	}
	
	public void addExclude(Object target, String exclude) {
		getOrCreateUnitTest(target).addExclude(exclude);
		getPage().getPomEditor().setModelDirty(true);
	}
	
	public List getExcludes(Object source) {
		Project pom = (Project) source;
		return pom.getBuild() != null 
			? pom.getBuild().getUnitTest() != null
				? pom.getBuild().getUnitTest().getExcludes()
				: null
			: null;
	}
	
	public List getInheritedExcludes() {
		return isInherited() 
			? getExcludes(getParentPom())
			: null;
	}

	private UnitTest getOrCreateUnitTest(Object model) {
		Project pom = (Project) model;
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
