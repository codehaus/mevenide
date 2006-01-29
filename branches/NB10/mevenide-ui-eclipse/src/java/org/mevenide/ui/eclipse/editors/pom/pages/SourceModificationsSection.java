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

import java.util.List;

import org.apache.maven.project.Project;
import org.apache.maven.project.SourceModification;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.SourceModificationPropertySource;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SourceModificationsSection extends PageSection {

	private TableViewer sourceModificationsViewer;
	private TableEntry sourceModificationsTable;
	private TableEntry includesTable;
	private TableEntry excludesTable;
	private IncludesSubsection includesSubsection;
	private ExcludesSubsection excludesSubsection;
	
	private ISourceModificationAdaptor sourceModificationsAdaptor;
	
	private String sectionName;
	
	public SourceModificationsSection(
	    AbstractPomEditorPage page, 
		Composite parent, 
		FormToolkit toolkit,
		String name) 
   	{
        super(page, parent, toolkit);
		this.sectionName = name;
		setTitle(Mevenide.getResourceString(sectionName + ".header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString(sectionName + ".description")); //$NON-NLS-1$
	}
	
	void setSourceModificationAdaptor(ISourceModificationAdaptor adaptor) {
		this.sourceModificationsAdaptor = adaptor;
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
		
		// Build SourceModifications table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		sourceModificationsViewer = createTableViewer(container, factory, 1, SWT.MULTI);
		sourceModificationsTable = new TableEntry(sourceModificationsViewer, toggle, Mevenide.getResourceString("BuildSourceModificationsSection.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List sourceModifications = (List) value;
				setSourceModifications(pom, sourceModifications);
			}
			public Object acceptParent() {
				return getSourceModifications(getParentPom());
			}
		};
		sourceModificationsTable.addEntryChangeListener(adaptor);
		sourceModificationsTable.addOverrideAdaptor(adaptor);
		sourceModificationsTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
				    SourceModification sourceModification = new SourceModification();
				  	addSourceModification(pom, sourceModification);
					return sourceModification;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List sourceModifications = getSourceModifications(pom);
					if (sourceModifications != null) {
						sourceModifications.remove(object);
						sourceModifications.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List sourceModifications = getSourceModifications(pom);
					if (sourceModifications != null) {
					    sourceModifications.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		// Includes subsection
		createSpacer(container, factory, (isInherited() ? 3 : 2));
		if (isInherited()) {
			createSpacer(container, factory);
		}
		createLabel(container, Mevenide.getResourceString(sectionName + ".includes.header"), factory); //$NON-NLS-1$
		createSpacer(container, factory);
		
		IIncludesAdaptor includesAdaptor = new IIncludesAdaptor() {
			public void setIncludes(Object target, List newIncludes) {
			    SourceModification sourceModification = getSelectedSourceModification();
				List includes = sourceModification.getIncludes();
				includes.removeAll(includes);
				includes.addAll(newIncludes);
				getPage().getPomEditor().setModelDirty(true);
			}
			public void addInclude(Object target, String include) {
			    SourceModification sourceModification = getSelectedSourceModification();
			    sourceModification.addInclude(include);
				getPage().getPomEditor().setModelDirty(true);
			}
			public List getIncludes(Object source) {
			    SourceModification sourceModification = getSelectedSourceModification();
				return sourceModification.getIncludes();
			}
		};
		includesSubsection = new IncludesSubsection(this, includesAdaptor);
		includesTable = includesSubsection.createWidget(container, factory, false);
		sourceModificationsTable.addDependentTableEntry(includesTable);
		
		// Excludes subsection
		createSpacer(container, factory, (isInherited() ? 3 : 2));
		if (isInherited()) {
			createSpacer(container, factory);
		}
		createLabel(container, Mevenide.getResourceString(sectionName + ".excludes.header"), factory); //$NON-NLS-1$
		createSpacer(container, factory);
		
		IExcludesAdaptor excludesAdaptor = new IExcludesAdaptor() {
			public void setExcludes(Object target, List newExcludes) {
			    SourceModification sourceModification = getSelectedSourceModification();
				List excludes = sourceModification.getExcludes();
				excludes.removeAll(excludes);
				excludes.addAll(newExcludes);
				getPage().getPomEditor().setModelDirty(true);
			}
			public void addExclude(Object target, String exclude) {
			    SourceModification sourceModification = getSelectedSourceModification();
			    sourceModification.addExclude(exclude);
				getPage().getPomEditor().setModelDirty(true);
			}
			public List getExcludes(Object source) {
			    SourceModification sourceModification = getSelectedSourceModification();
				return sourceModification.getExcludes();
			}
		};
		excludesSubsection = new ExcludesSubsection(this, excludesAdaptor);
		excludesTable = excludesSubsection.createWidget(container, factory, false);
		sourceModificationsTable.addDependentTableEntry(excludesTable);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		sourceModificationsTable.removeAll();
		List sourceModifications = getSourceModifications(pom);
		List parentSourceModifications = getInheritedSourceModifications();
		if (sourceModifications != null && !sourceModifications.isEmpty()) {
			sourceModificationsTable.addEntries(sourceModifications);
			sourceModificationsTable.setInherited(false);
		}
		else if (parentSourceModifications != null) {
			sourceModificationsTable.addEntries(parentSourceModifications, true);
			sourceModificationsTable.setInherited(true);
		}
		else {
			sourceModificationsTable.setInherited(false);
		}
	}

	private void setSourceModifications(Project pom, List sourceModifications) {
		sourceModificationsAdaptor.setSourceModifications(pom, sourceModifications);
	}
	
	private void addSourceModification(Project pom, SourceModification sourceModification) {
	    sourceModificationsAdaptor.addSourceModification(pom, sourceModification);
	}
	
	private List getSourceModifications(Project pom) {
		return sourceModificationsAdaptor.getSourceModifications(pom);
	}
	
	private List getInheritedSourceModifications() {
		return isInherited() 
			? getSourceModifications(getParentPom())
			: null;
	}
	
	private SourceModification getSelectedSourceModification() {
	    IStructuredSelection selected = (IStructuredSelection) sourceModificationsViewer.getSelection();
		SourceModificationPropertySource source = (SourceModificationPropertySource) selected.getFirstElement();
		return (SourceModification) source.getSource();
	}
}