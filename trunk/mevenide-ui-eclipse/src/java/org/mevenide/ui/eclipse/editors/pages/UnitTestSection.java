/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.project.Build;
import org.apache.maven.project.Resource;
import org.apache.maven.project.Project;
import org.apache.maven.project.UnitTest;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.*;
import org.mevenide.ui.eclipse.editors.properties.ResourcePatternProxy;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class UnitTestSection extends PageSection {

	private TableEntry includesTable;
	private TableEntry excludesTable;
	private TableEntry resourcesTable;
	
	public UnitTestSection(UnitTestsPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("UnitTestSection.header"));
		setDescription(Mevenide.getResourceString("UnitTestSection.description"));
	}

	public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getEditor().getPom();
		
		// Unit test include header label
		if (isInherited()) createSpacer(container, factory);
		factory.createLabel(container, Mevenide.getResourceString("UnitTestSection.includes.label"), SWT.BOLD);
		createSpacer(container, factory);
		
		// Unit test include table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		includesTable = new TableEntry(viewer, toggle, "Include", container, factory, this);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List includes = (List) value;
				setIncludes(pom, includes);
			}
			public Object acceptParent() {
				return getIncludes(getParentPom());
			}
		};
		includesTable.addEntryChangeListener(adaptor);
		includesTable.addOverrideAdaptor(adaptor);
		includesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					String include = "[unknown]";
					ResourcePatternProxy includeProxy = new ResourcePatternProxy(include, true);
					addInclude(pom, include);
					return includeProxy;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List includes = getIncludes(pom);
					ResourcePatternProxy patternToMove = (ResourcePatternProxy) object;
					String pattern = (String) patternToMove.getSource();
					if (includes != null) {
						includes.remove(pattern);
						includes.add(index, pattern);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List includes = getIncludes(pom);
					ResourcePatternProxy patternToMove = (ResourcePatternProxy) object;
					String pattern = (String) patternToMove.getSource();
					if (includes != null) {
						includes.remove(pattern);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		// whitespace
		createSpacer(container, factory, isInherited() ? 3 : 2);
		
		// Unit test include header label
		if (isInherited()) createSpacer(container, factory);
		factory.createLabel(container, Mevenide.getResourceString("UnitTestSection.excludes.label"), SWT.BOLD);
		createSpacer(container, factory);
		
		// Unit test excludes table
		toggle = createOverrideToggle(container, factory, 1, true);
		viewer = createTableViewer(container, factory, 1);
		excludesTable = new TableEntry(viewer, toggle, "Exclude", container, factory, this);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List excludes = (List) value;
				setExcludes(pom, excludes);
			}
			public Object acceptParent() {
				return getExcludes(getParentPom());
			}
		};
		excludesTable.addEntryChangeListener(adaptor);
		excludesTable.addOverrideAdaptor(adaptor);
		excludesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					String exclude = "[unknown]";
					ResourcePatternProxy excludeProxy = new ResourcePatternProxy(exclude, false);
					addExclude(pom, exclude);
					return excludeProxy;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List excludes = getExcludes(pom);
					ResourcePatternProxy patternToMove = (ResourcePatternProxy) object;
					String pattern = (String) patternToMove.getSource();
					if (excludes != null) {
						excludes.remove(pattern);
						excludes.add(index, pattern);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List excludes = getExcludes(pom);
					ResourcePatternProxy patternToMove = (ResourcePatternProxy) object;
					String pattern = (String) patternToMove.getSource();
					if (excludes != null) {
						excludes.remove(pattern);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		// whitespace
		createSpacer(container, factory, isInherited() ? 3 : 2);
		
		// Unit test include header label
		if (isInherited()) createSpacer(container, factory);
		factory.createLabel(container, Mevenide.getResourceString("UnitTestSection.resources.label"), SWT.BOLD);
		createSpacer(container, factory);
		
		// Unit test resource table
		toggle = createOverrideToggle(container, factory, 1, true);
		viewer = createTableViewer(container, factory, 1);
		resourcesTable = new TableEntry(viewer, toggle, "Resource", container, factory, this);
		adaptor = new OverrideAdaptor() {
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
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		updateTableAndPomCollection(includesTable, getIncludes(pom), getInheritedIncludes());
		updateTableAndPomCollection(excludesTable, getExcludes(pom), getInheritedExcludes());
		updateTableAndPomCollection(resourcesTable, getResources(pom), getInheritedResources());
		
		super.update(pom);
	}

	private void updateTableAndPomCollection(
		TableEntry table,
		List pomCollection,
		List inheritedCollection
	) {
		table.removeAll();
		if (pomCollection != null && !pomCollection.isEmpty()) {
			table.addEntries(pomCollection);
			table.setInherited(false);
		}
		else if (inheritedCollection != null) {
			table.addEntries(pomCollection, true);
			table.setInherited(true);
		}
		else {
			table.setInherited(false);
		}
	}

	private void setIncludes(Project pom, List newIncludes) {
		List includes = getOrCreateUnitTest(pom).getIncludes();
		includes.removeAll(includes);
		includes.addAll(newIncludes);
	}
	
	private void addInclude(Project pom, String include) {
		getOrCreateUnitTest(pom).addInclude(include);
	}
	
	private List getIncludes(Project pom) {
		List includes = pom.getBuild() != null 
			? pom.getBuild().getUnitTest() != null
				? pom.getBuild().getUnitTest().getIncludes()
				: null
			: null;
		
		List includeProxies = null;
		if (includes != null) {
			includeProxies = new ArrayList(includes.size());
			Iterator itr = includes.iterator();
			while (itr.hasNext()) {
				includeProxies.add(new ResourcePatternProxy((String) itr.next(), true));
			}
		}
		return includeProxies;
	}
	
	private List getInheritedIncludes() {
		return isInherited() 
			? getIncludes(getParentPom())
			: null;
	}

	private void setExcludes(Project pom, List newExcludes) {
		List excludes = getOrCreateUnitTest(pom).getExcludes();
		excludes.removeAll(excludes);
		excludes.addAll(newExcludes);
	}
	
	private void addExclude(Project pom, String exclude) {
		getOrCreateUnitTest(pom).addExclude(exclude);
	}
	
	private List getExcludes(Project pom) {
		List excludes = pom.getBuild() != null 
			? pom.getBuild().getUnitTest() != null
				? pom.getBuild().getUnitTest().getExcludes()
				: null
			: null;
		
		List excludeProxies = null;
		if (excludes != null) {
			excludeProxies = new ArrayList(excludes.size());
			Iterator itr = excludes.iterator();
			while (itr.hasNext()) {
				excludeProxies.add(new ResourcePatternProxy((String) itr.next(), true));
			}
		}
		return excludeProxies;
	}
	
	private List getInheritedExcludes() {
		return isInherited() 
			? getExcludes(getParentPom())
			: null;
	}

	private void setResources(Project pom, List resources) {
		getOrCreateUnitTest(pom).setResources(resources);
	}
	
	private void addResource(Project pom, Resource resource) {
		getOrCreateUnitTest(pom).addResource(resource);
	}
	
	private List getResources(Project pom) {
		return pom.getBuild() != null 
			? pom.getBuild().getUnitTest() != null
				? pom.getBuild().getUnitTest().getResources()
				: null
			: null;
	}
	
	private List getInheritedResources() {
		return isInherited() 
			? getResources(getParentPom())
			: null;
	}

	private UnitTest getOrCreateUnitTest(Project pom) {
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
