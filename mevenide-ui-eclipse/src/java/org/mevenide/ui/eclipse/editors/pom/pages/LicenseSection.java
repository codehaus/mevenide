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

import org.apache.maven.project.License;
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
public class LicenseSection extends PageSection {
	
	private TableEntry licenseTable;
	
    public LicenseSection(
        OrganizationPage page, 
       	Composite parent, 
       	FormToolkit toolkit)
    {
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("LicenseSection.header"));
		setDescription(Mevenide.getResourceString("LicenseSection.description"));
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pom.pages.PageSection#createSectionContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    protected Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getPomEditor().getPom();
		
		// POM license table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		licenseTable = new TableEntry(viewer, toggle, "License", container, factory, this);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List licenses = (List) value;
				pom.setLicenses(licenses);
			}
			public Object acceptParent() {
				return getParentPom().getLicenses();
			}
		};
		licenseTable.addEntryChangeListener(adaptor);
		licenseTable.addOverrideAdaptor(adaptor);
		licenseTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					License license = new License();
					pom.addLicense(license);
					return license;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List licenses = pom.getLicenses();
					if (licenses != null) {
						licenses.remove(object);
						licenses.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List licenses = pom.getLicenses();
					if (licenses != null) {
						licenses.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		factory.paintBordersFor(container);
		return container;
    }
	
	public void update(Project pom) {
		licenseTable.removeAll();
		List licenses = pom.getLicenses();
		List parentLicenses = isInherited() ? getParentPom().getLicenses() : null;
		if (licenses != null && !licenses.isEmpty()) {
			licenseTable.addEntries(licenses);
			licenseTable.setInherited(false);
		}
		else if (parentLicenses != null) {
			licenseTable.addEntries(licenses, true);
			licenseTable.setInherited(true);
		}
		else {
			licenseTable.setInherited(false);
		}
	}

}
