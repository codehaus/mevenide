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

import org.apache.maven.project.Project;
import org.apache.maven.project.Version;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.VersionPropertySource;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class VersionsSection extends PageSection {

	private TableEntry versionTable;
    private TableViewer versionViewer;
	
	public VersionsSection(
		RepositoryPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("VersionsSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("VersionsSection.description")); //$NON-NLS-1$
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
		
		// POM versions table
		Button toggle = createOverrideToggle(container, factory, 1, true);
        versionViewer = createTableViewer(container, factory, 1);
		versionTable = new TableEntry(versionViewer, toggle, Mevenide.getResourceString("VersionsSection.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List versions = (List) value;
				pom.setVersions(versions);
			}
			public Object acceptParent() {
				return getParentPom().getVersions();
			}
		};
		versionTable.addEntryChangeListener(adaptor);
		versionTable.addOverrideAdaptor(adaptor);
		versionTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Version version = new Version();
					if ( pom.getVersions() == null ) {
					    pom.setVersions(new ArrayList());
					}
					pom.addVersion(version);
					return version;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List versions = pom.getVersions();
					if (versions != null) {
						versions.remove(object);
						versions.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List versions = pom.getVersions();
					if (versions != null) {
						versions.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		versionTable.removeAll();
		List versions = pom.getVersions();
		List parentVersions = isInherited() ? getParentPom().getVersions() : null;
		if (versions != null && !versions.isEmpty()) {
			versionTable.addEntries(versions);
			versionTable.setInherited(false);
		}
		else if (parentVersions != null) {
			versionTable.addEntries(versions, true);
			versionTable.setInherited(true);
		}
		else {
			versionTable.setInherited(false);
		}
	}

    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        if (input != null && input instanceof Version) {
            Version version = (Version) input;
            TableItem[] items = versionViewer.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
                VersionPropertySource src = (VersionPropertySource) items[i].getData();
                if (src.getSource().equals(version)) {
                    ensureExpanded();
                    versionViewer.getTable().select(i);
                    return true;
                }
            }
        }
        return super.setFormInput(input);
    }
    
}
