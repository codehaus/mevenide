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
import org.apache.maven.project.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.ResourcePropertySource;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ResourcesSection extends PageSection {

	private TableViewer resourcesViewer;
	private TableEntry resourcesTable;
	private TableEntry includesTable;
	private TableEntry excludesTable;
	private IncludesSubsection includesSubsection;
	private ExcludesSubsection excludesSubsection;
	
	private IResourceAdaptor resourceAdaptor;
	
	private String sectionName;
	
	public ResourcesSection(
	    AbstractPomEditorPage page, 
		Composite parent, 
		FormToolkit toolkit,
		String name) 
   	{
        super(page, parent, toolkit);
		this.sectionName = name;
		setTitle(Mevenide.getResourceString(sectionName + ".header"));
		setDescription(Mevenide.getResourceString(sectionName + ".description"));
	}
	
	void setResourceAdaptor(IResourceAdaptor adaptor) {
		this.resourceAdaptor = adaptor;
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
		
		// Build resources table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		resourcesViewer = createTableViewer(container, factory, 1);
		resourcesTable = new TableEntry(resourcesViewer, toggle, "Resource", container, factory, this);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List resources = (List) value;
				setResources(pom, resources);
			}
			public Object acceptParent() {
				return getResources(getParentPom());
			}
		};
		resourcesTable.addEntryChangeListener(adaptor);
		resourcesTable.addOverrideAdaptor(adaptor);
		resourcesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Resource resource = new Resource();
					addResource(pom, resource);
					return resource;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List resources = getResources(pom);
					if (resources != null) {
						resources.remove(object);
						resources.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List resources = getResources(pom);
					if (resources != null) {
						resources.remove(object);
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
		createLabel(container, Mevenide.getResourceString(sectionName + ".includes.header"), factory);
		createSpacer(container, factory);
		
		IIncludesAdaptor includesAdaptor = new IIncludesAdaptor() {
			public void setIncludes(Object target, List newIncludes) {
				Resource resource = getSelectedResource();
				List includes = resource.getIncludes();
				includes.removeAll(includes);
				includes.addAll(newIncludes);
				getPage().getPomEditor().setModelDirty(true);
			}
			public void addInclude(Object target, String include) {
				Resource resource = getSelectedResource();
				resource.addInclude(include);
				getPage().getPomEditor().setModelDirty(true);
			}
			public List getIncludes(Object source) {
				Resource resource = getSelectedResource();
				return resource.getIncludes();
			}
		};
		includesSubsection = new IncludesSubsection(this, includesAdaptor);
		includesTable = includesSubsection.createWidget(container, factory, false);
		resourcesTable.addDependentTableEntry(includesTable);
		
		// Excludes subsection
		createSpacer(container, factory, (isInherited() ? 3 : 2));
		if (isInherited()) {
			createSpacer(container, factory);
		}
		createLabel(container, Mevenide.getResourceString(sectionName + ".excludes.header"), factory);
		createSpacer(container, factory);
		
		IExcludesAdaptor excludesAdaptor = new IExcludesAdaptor() {
			public void setExcludes(Object target, List newExcludes) {
				Resource resource = getSelectedResource();
				List excludes = resource.getExcludes();
				excludes.removeAll(excludes);
				excludes.addAll(newExcludes);
				getPage().getPomEditor().setModelDirty(true);
			}
			public void addExclude(Object target, String exclude) {
				Resource resource = getSelectedResource();
				resource.addExclude(exclude);
				getPage().getPomEditor().setModelDirty(true);
			}
			public List getExcludes(Object source) {
				Resource resource = getSelectedResource();
				return resource.getExcludes();
			}
		};
		excludesSubsection = new ExcludesSubsection(this, excludesAdaptor);
		excludesTable = excludesSubsection.createWidget(container, factory, false);
		resourcesTable.addDependentTableEntry(excludesTable);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		resourcesTable.removeAll();
		List resources = getResources(pom);
		List parentResources = getInheritedResources();
		if (resources != null && !resources.isEmpty()) {
			resourcesTable.addEntries(resources);
			resourcesTable.setInherited(false);
		}
		else if (parentResources != null) {
			resourcesTable.addEntries(parentResources, true);
			resourcesTable.setInherited(true);
		}
		else {
			resourcesTable.setInherited(false);
		}
	}

	private void setResources(Project pom, List resources) {
		resourceAdaptor.setResources(pom, resources);
	}
	
	private void addResource(Project pom, Resource resource) {
		resourceAdaptor.addResource(pom, resource);
	}
	
	private List getResources(Project pom) {
		return resourceAdaptor.getResources(pom);
	}
	
	private List getInheritedResources() {
		return isInherited() 
			? getResources(getParentPom())
			: null;
	}
	
	private Resource getSelectedResource() {
		IStructuredSelection selected = (IStructuredSelection) resourcesViewer.getSelection();
		ResourcePropertySource source = (ResourcePropertySource) selected.getFirstElement();
		return (Resource) source.getSource();
	}

}
