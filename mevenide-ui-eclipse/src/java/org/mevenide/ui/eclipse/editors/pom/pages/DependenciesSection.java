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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.PropertyProxy;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;
import org.mevenide.ui.eclipse.wizard.NewDependencyWizard;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DependenciesSection extends PageSection {

	private static final Log log = LogFactory.getLog(DependenciesSection.class);
    
	private TableEntry dependenciesTable;
	private TableEntry propertiesTable;
	
	public DependenciesSection(
		DependenciesPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("DependenciesSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("DependenciesSection.description")); //$NON-NLS-1$
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
		
		// POM dependencies table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		TableViewer viewer = createTableViewer(container, factory, 1);
		dependenciesTable = new TableEntry(viewer, toggle, Mevenide.getResourceString("DependenciesSection.TableEntry.Tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List dependencies = (List) value;
				pom.setDependencies(dependencies);
				if ( dependencies != null ) {
				    for (int i = 0; i < dependencies.size(); i++) {
				        Artifact artifact = DefaultArtifactFactory.createArtifact((Dependency) dependencies.get(i));
				        if ( pom.getArtifacts() == null ) {
				            pom.setArtifacts(new ArrayList());
				        }
						pom.getArtifacts().add(artifact);
				    }
				}
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
					if ( pom.getDependencies() == null ) {
					    pom.setDependencies(new ArrayList());
					}
                    NewDependencyWizard wizard = new NewDependencyWizard();
					WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	            	dialog.create();
	            	int result = dialog.open();
	            	Dependency dependency = wizard.getDependency();
	            	if ( result == Window.OK && dependency != null ) {
	            		pom.addDependency(dependency);
						Artifact artifact = DefaultArtifactFactory.createArtifact(dependency);
						if ( pom.getArtifacts() == null ) {
						    pom.setArtifacts(new ArrayList());
						}
						pom.getArtifacts().add(artifact);	
	            	}
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
		factory.createLabel(container, Mevenide.getResourceString("DependenciesSection.properties.label"), SWT.BOLD); //$NON-NLS-1$
		createSpacer(container, factory);
		
		// POM dependency properties table
		if (isInherited()) createSpacer(container, factory);
		viewer = createTableViewer(container, factory, 1);
		propertiesTable = new TableEntry(viewer, null, Mevenide.getResourceString("DependencySection.Dependency.Property"), container, factory, this); //$NON-NLS-1$
		dependenciesTable.addDependentTableEntry(propertiesTable);
		propertiesTable.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					List properties = (List) propertiesTable.getValue();
					if (log.isDebugEnabled()) {
						log.debug("properties = " + properties); //$NON-NLS-1$
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
						log.debug("resolved properties = " + dependency.resolvedProperties()); //$NON-NLS-1$
					}
				}
			}
		);
		propertiesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					String newPropertyStr = Mevenide.getResourceString("AbstractPomEditorPage.Element.Unknown") + ":" + Mevenide.getResourceString("AbstractPomEditorPage.Element.Unknown"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					PropertyProxy newProperty = new PropertyProxy(newPropertyStr);
					if ( dependency.getProperties() == null ) {
					    pom.setProperties(new ArrayList());
					}
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
		        if (element.getType() == null) {
		            element.setType("jar"); //$NON-NLS-1$
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
	}

}
