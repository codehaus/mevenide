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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.entries.TableEntry;
import org.mevenide.ui.eclipse.editors.properties.PropertyProxy;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DependenciesSection extends PageSection {

	private static final Log log = LogFactory.getLog(DependenciesSection.class);
    
	private TableEntry dependenciesTable;
	private TableEntry propertiesTable;
	
	public DependenciesSection(DependenciesPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("DependenciesSection.header"));
		setDescription(Mevenide.getResourceString("DependenciesSection.description"));
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
		
		// POM dependencies table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		dependenciesTable = new TableEntry(viewer, toggle, "Dependency", container, factory, this);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List dependencies = (List) value;
				pom.setDependencies(dependencies);
			}
			public Object acceptParent() {
				return getParentPom().getDependencies();
			}
		};
		dependenciesTable.addEntryChangeListener(adaptor);
		dependenciesTable.addOverrideAdaptor(adaptor);
		dependenciesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Dependency dependency = new Dependency();
					dependency.setArtifactId("<artifactId>");
					dependency.setGroupId("<groupId>");
					dependency.setVersion("<version>");
					dependency.setType("jar");
					pom.addDependency(dependency);
					return dependency;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List dependencies = pom.getDependencies();
					if (dependencies != null) {
						dependencies.remove(object);
						dependencies.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List dependencies = pom.getDependencies();
					if (dependencies != null) {
						dependencies.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
		
		// whitespace
		createSpacer(container, factory, isInherited() ? 3 : 2);
		
		// Dependency Property header label
		if (isInherited()) createSpacer(container, factory);
		factory.createLabel(container, Mevenide.getResourceString("DependenciesSection.properties.label"), SWT.BOLD);
		createSpacer(container, factory);
		
		// POM dependency properties table
		if (isInherited()) createSpacer(container, factory);
		viewer = createTableViewer(container, factory, 1);
		propertiesTable = new TableEntry(viewer, null, "Dependency Property", container, factory, this);
		dependenciesTable.addDependentTableEntry(propertiesTable);
		propertiesTable.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					List properties = (List) propertiesTable.getValue();
					if (log.isDebugEnabled()) {
						log.debug("properties = " + properties);
					}
					Dependency dependency = (Dependency) ((TableEntry) entry).getParentPomObject();
					dependency.setProperties(properties);
					dependency.resolvedProperties().clear();
					Iterator itr = properties.iterator();
					while (itr.hasNext()) {
						String[] property = MevenideUtils.resolveProperty((String) itr.next());
						dependency.resolvedProperties().put(property[0], property[1]);
					}
					if (log.isDebugEnabled()) {
						log.debug("resolved properties = " + dependency.resolvedProperties());
					}
				}
			}
		);
		propertiesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					String newPropertyStr = "unknown:unknown";
					PropertyProxy newProperty = new PropertyProxy(newPropertyStr);
					dependency.addProperty(newPropertyStr);
					return newProperty;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					List properties = dependency.getProperties();
					PropertyProxy propertyToMove = (PropertyProxy) object;
					String property = propertyToMove.toString();
					if (properties != null) {
						properties.remove(property);
						properties.add(index, property);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					List properties = dependency.getProperties();
					PropertyProxy propertyToMove = (PropertyProxy) object;
					String property = propertyToMove.toString();
					if (properties != null) {
						properties.remove(property);
					}
				}
				public List getDependents(Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					List properties = dependency.getProperties();
					List propertyProxies = new ArrayList(properties.size());
					Iterator itr = properties.iterator();
					while (itr.hasNext()) {
						propertyProxies.add(new PropertyProxy((String) itr.next()));
					}
					return propertyProxies;
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		dependenciesTable.removeAll();
		List dependencies = pom.getDependencies();
		List parentDependencies = isInherited() ? getParentPom().getDependencies() : null;
		if (dependencies != null && !dependencies.isEmpty()) {
		    for (Iterator iter = dependencies.iterator(); iter.hasNext(); ) {
		        Dependency element = (Dependency) iter.next();
		        log.debug(element);
		        if (element.getType() == null) {
		            element.setType("jar");
		        }
		    }
			dependenciesTable.addEntries(dependencies);
			dependenciesTable.setInherited(false);
		}
		else if (parentDependencies != null) {
			dependenciesTable.addEntries(dependencies, true);
			dependenciesTable.setInherited(true);
		}
		else {
			dependenciesTable.setInherited(false);
		}
		
		super.update(pom);
	}

}
