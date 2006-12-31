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

import org.apache.maven.project.Contributor;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.ContributorPropertySource;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ContributorsSection extends PageSection {

	private TableEntry contribTable;
    private TableViewer contribViewer;
	
	public ContributorsSection(
		TeamPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("ContributorsSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("ContributorsSection.description")); //$NON-NLS-1$
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
		
		// POM contributors table
		Button toggle = createOverrideToggle(container, factory, 1, true);
        contribViewer = createTableViewer(container, factory, 1, SWT.MULTI);
		contribTable = new TableEntry(contribViewer, toggle, Mevenide.getResourceString("ContributorsSection.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List contributors = (List) value;
				pom.setContributors(contributors);
			}
			public Object acceptParent() {
				return getParentPom().getContributors();
			}
		};
		contribTable.addEntryChangeListener(adaptor);
		contribTable.addOverrideAdaptor(adaptor);
		contribTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Contributor contributor = new Contributor();
					if ( pom.getContributors() == null ) {
					    pom.setContributors(new ArrayList());
					}
					pom.addContributor(contributor);
					return contributor;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List contributors = pom.getContributors();
					if (contributors != null) {
						contributors.remove(object);
						contributors.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List contributors = pom.getContributors();
					if (contributors != null) {
						contributors.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		contribTable.removeAll();
		List contributors = pom.getContributors();
		List parentContributors = isInherited() ? getParentPom().getContributors() : null;
		if (contributors != null && !contributors.isEmpty()) {
			contribTable.addEntries(contributors);
			contribTable.setInherited(false);
		}
		else if (parentContributors != null) {
			contribTable.addEntries(contributors, true);
			contribTable.setInherited(true);
		}
		else {
			contribTable.setInherited(false);
		}
	}

    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        if (input != null && input instanceof Contributor) {
            Contributor contributor = (Contributor) input;
            TableItem[] items = contribViewer.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
                ContributorPropertySource src = (ContributorPropertySource) items[i].getData();
                if (src.getSource().equals(contributor)) {
                    ensureExpanded();
                    contribViewer.getTable().select(i);
                    return true;
                }
            }
        }
        return super.setFormInput(input);
    }

}
