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

import java.util.List;

import org.apache.maven.project.Build;
import org.apache.maven.project.Resource;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ResourcesSection extends PageSection {

	private TableEntry resourcesTable;
	
	public ResourcesSection(BuildPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("ResourcesSection.header"));
		setDescription(Mevenide.getResourceString("ResourcesSection.description"));
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
		
		// Build resources table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		resourcesTable = new TableEntry(viewer, toggle, container, factory, this);
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
				public Object addNewObject() {
					Resource resource = new Resource();
					addResource(pom, resource);
					return resource;
				}
				public void moveObjectTo(int index, Object object) {
					List resources = getResources(pom);
					if (resources != null) {
						resources.remove(object);
						resources.add(index, object);
					}
				}
				public void removeObject(Object object) {
					List resources = getResources(pom);
					if (resources != null) {
						resources.remove(object);
					}
				}
			}
		);
		
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
			resourcesTable.addEntries(resources, true);
			resourcesTable.setInherited(true);
		}
		else {
			resourcesTable.setInherited(false);
		}
		
		super.update(pom);
	}

	private void setResources(Project pom, List resources) {
		getOrCreateBuild(pom).setResources(resources);
	}
	
	private void addResource(Project pom, Resource resource) {
		getOrCreateBuild(pom).addResource(resource);
	}
	
	private List getResources(Project pom) {
		return pom.getBuild() != null ? pom.getBuild().getResources() : null;
	}
	
	private List getInheritedResources() {
		return isInherited() 
			? getResources(getParentPom())
			: null;
	}

	private Build getOrCreateBuild(Project pom) {
		Build build = pom.getBuild();
		if (build == null) {
			build = new Build();
			pom.setBuild(build);
		}
		return build;
	}

}
