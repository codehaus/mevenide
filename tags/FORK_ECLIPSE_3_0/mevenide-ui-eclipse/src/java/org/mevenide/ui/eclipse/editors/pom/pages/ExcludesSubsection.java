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

import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.ResourcePatternProxy;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ExcludesSubsection extends AbstractResourcePatternSubsection {
	
	private PageSection section;
	private IExcludesAdaptor excluder;
	
	public ExcludesSubsection(PageSection parentSection, IExcludesAdaptor excludeAdaptor) {
		this.section = parentSection;
		this.excluder = excludeAdaptor;
	}
	
	public TableEntry createWidget(Composite container, FormToolkit factory, boolean isOverrideable) {
		final Project pom = section.getPage().getPomEditor().getPom();

		// Exclude table
		Button toggle = null;
		if (isOverrideable) {
			toggle = section.createOverrideToggle(container, factory, 1, true);
		} else {
			if (section.isInherited()) {
				section.createSpacer(container, factory);
			}
		}
		TableViewer viewer = section.createTableViewer(container, factory, 1);
		TableEntry excludesTable = new TableEntry(viewer, toggle, Mevenide.getResourceString("ExcludesSubsection.TableEntry.Tooltip"), container, factory, section); //$NON-NLS-1$
		PageSection.OverrideAdaptor adaptor = section.new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List excludes = (List) value;
				excluder.setExcludes(pom, excludes);
			}
			public Object acceptParent() {
				return excluder.getExcludes(section.getParentPom());
			}
		};

		excludesTable.addEntryChangeListener(adaptor);
		excludesTable.addOverrideAdaptor(adaptor);
		excludesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					String exclude = Mevenide.getResourceString("AbstractPomEditorPage.Element.Unknown"); //$NON-NLS-1$
					ResourcePatternProxy excludeProxy = new ResourcePatternProxy(exclude, false);
					excluder.addExclude(pom, exclude);
					return excludeProxy;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List excludes = excluder.getExcludes(pom);
					String pattern = (String) object;
					if (excludes != null) {
						excludes.remove(pattern);
						excludes.add(index, pattern);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List excludes = excluder.getExcludes(pom);
					String pattern = (String) object;
					if (excludes != null) {
						excludes.remove(pattern);
					}
				}
				// only ever called if this subsection belongs to a ResourceSection
				public List getDependents(Object parentObject) {
					Resource resource = (Resource) parentObject;
					List excludes = resource.getExcludes();
					List patternProxies = new ArrayList(excludes.size());
					Iterator itr = excludes.iterator();
					while (itr.hasNext()) {
						patternProxies.add(new ResourcePatternProxy((String) itr.next(), false));
					}
					return patternProxies;
				}
			}
		);
		return excludesTable;
	}

}
