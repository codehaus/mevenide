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

import org.apache.maven.project.Project;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class IncludesSection extends PageSection {

	private IncludesSubsection subsection;
	private IIncludesAdaptor includesAdaptor;
	private TableEntry includesTable;
	
	public IncludesSection(
	    UnitTestsPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("UnitTestIncludesSection.header"));
	}
	
	void setIncludesAdaptor(IIncludesAdaptor adaptor) {
		this.includesAdaptor = adaptor;
	}

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		subsection = new IncludesSubsection(this, includesAdaptor);
		
		includesTable = subsection.createWidget(container, factory, true);
		
		factory.paintBordersFor(container);
		
		return container;
	}

	public void update(Project pom) {
		subsection.updateTableEntries(includesTable, getIncludes(pom), getInheritedIncludes(), true);
	}

	private List getIncludes(Project pom) {
		return includesAdaptor.getIncludes(pom);
	}
	
	private List getInheritedIncludes() {
		return isInherited() 
			? getIncludes(getParentPom())
			: null;
	}

}
