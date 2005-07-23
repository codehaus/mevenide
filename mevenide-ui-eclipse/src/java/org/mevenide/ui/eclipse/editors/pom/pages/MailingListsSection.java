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

import org.apache.maven.project.MailingList;
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
public class MailingListsSection extends PageSection {

	private TableEntry mailingListTable;
	
	public MailingListsSection(
	    TeamPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("MailingListsSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("MailingListsSection.description")); //$NON-NLS-1$
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
		
		// POM mailingLists table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		mailingListTable = new TableEntry(viewer, toggle, Mevenide.getResourceString("MailingListsSection.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List mailingLists = (List) value;
				pom.setMailingLists(mailingLists);
			}
			public Object acceptParent() {
				return getParentPom().getMailingLists();
			}
		};
		mailingListTable.addEntryChangeListener(adaptor);
		mailingListTable.addOverrideAdaptor(adaptor);
		mailingListTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					MailingList mailingList = new MailingList();
					if ( pom.getMailingLists() == null ) {
					    pom.setMailingLists(new ArrayList());
					}
					pom.addMailingList(mailingList);
					return mailingList;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List mailingLists = pom.getMailingLists();
					if (mailingLists != null) {
						mailingLists.remove(object);
						mailingLists.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List mailingLists = pom.getMailingLists();
					if (mailingLists != null) {
						mailingLists.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		mailingListTable.removeAll();
		List mailingLists = pom.getMailingLists();
		List parentMailingLists = isInherited() ? getParentPom().getMailingLists() : null;
		if (mailingLists != null && !mailingLists.isEmpty()) {
			mailingListTable.addEntries(mailingLists);
			mailingListTable.setInherited(false);
		}
		else if (parentMailingLists != null) {
			mailingListTable.addEntries(mailingLists, true);
			mailingListTable.setInherited(true);
		}
		else {
			mailingListTable.setInherited(false);
		}
	}

}
