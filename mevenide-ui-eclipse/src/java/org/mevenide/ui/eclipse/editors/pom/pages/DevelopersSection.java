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

import org.apache.maven.project.Developer;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.DeveloperPropertySource;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DevelopersSection extends PageSection {

	private TableEntry devTable;
    private TableViewer devViewer;
	
	public DevelopersSection(
		TeamPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("DevelopersSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("DevelopersSection.description")); //$NON-NLS-1$
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
		
		// POM developers table
		Button toggle = createOverrideToggle(container, factory, 1, true);
        devViewer = createTableViewer(container, factory, 1, SWT.MULTI);
		devTable = new TableEntry(devViewer, toggle, Mevenide.getResourceString("DevelopersSection.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List developers = (List) value;
				pom.setDevelopers(developers);
			}
			public Object acceptParent() {
				return getParentPom().getDevelopers();
			}
		};
		devTable.addEntryChangeListener(adaptor);
		devTable.addOverrideAdaptor(adaptor);
		devTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Developer developer = new Developer();
					if ( pom.getDevelopers() == null ) {
					    pom.setDevelopers(new ArrayList());
					}
					pom.addDeveloper(developer);
					return developer;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List developers = pom.getDevelopers();
					if (developers != null) {
						developers.remove(object);
						developers.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List developers = pom.getDevelopers();
					if (developers != null) {
						developers.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		devTable.removeAll();
		List developers = pom.getDevelopers();
		List parentDevelopers = isInherited() ? getParentPom().getDevelopers() : null;
		if (developers != null && !developers.isEmpty()) {
			devTable.addEntries(developers);
			devTable.setInherited(false);
		}
		else if (parentDevelopers != null) {
			devTable.addEntries(developers, true);
			devTable.setInherited(true);
		}
		else {
			devTable.setInherited(false);
		}
	}

    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        if (input != null && input instanceof Developer) {
            Developer developer = (Developer) input;
            TableItem[] items = devViewer.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
                DeveloperPropertySource src = (DeveloperPropertySource) items[i].getData();
                if (src.getSource().equals(developer)) {
                    ensureExpanded();
                    devViewer.getTable().select(i);
                    return true;
                }
            }
        }
        return super.setFormInput(input);
    }

}
