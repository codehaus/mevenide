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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Branch;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class BranchesSection extends PageSection {

	private TableEntry branchTable;
	
	public BranchesSection(
		RepositoryPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("BranchesSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("BranchesSection.description")); //$NON-NLS-1$
	}

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getPomEditor().getPom();
		
		// POM branch table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		branchTable = new TableEntry(viewer, toggle, Mevenide.getResourceString("BranchesSection.TableEntry.ToolTip"), container, factory, this);  //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List branches = (List) value;
				pom.setBranches(branches);
			}
			public Object acceptParent() {
				return getParentPom().getBranches();
			}
		};
		branchTable.addEntryChangeListener(adaptor);
		branchTable.addOverrideAdaptor(adaptor);
		branchTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Branch branch = new Branch();
					if ( pom.getBranches() == null ) {
					    pom.setBranches(new ArrayList());
					}
					pom.addBranch(branch);
					return branch;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List branches = pom.getBranches();
					if (branches != null) {
						branches.remove(object);
						branches.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List branches = pom.getBranches();
					if (branches != null) {
						branches.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		branchTable.removeAll();
		List branches = pom.getBranches();
		List parentBranches = isInherited() ? getParentPom().getBranches() : null;
		if (branches != null && !branches.isEmpty()) {
			branchTable.addEntries(branches);
			branchTable.setInherited(false);
		}
		else if (parentBranches != null) {
			branchTable.addEntries(branches, true);
			branchTable.setInherited(true);
		}
		else {
			branchTable.setInherited(false);
		}
	}

}
