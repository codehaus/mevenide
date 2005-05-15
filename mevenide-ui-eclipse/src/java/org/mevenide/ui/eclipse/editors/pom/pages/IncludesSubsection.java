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
import org.mevenide.ui.eclipse.adapters.properties.ResourcePatternProxy;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class IncludesSubsection extends AbstractResourcePatternSubsection {
	
	private PageSection section;
	private IIncludesAdaptor includer;
	
	public IncludesSubsection(PageSection parentSection, IIncludesAdaptor includeAdaptor) {
		this.section = parentSection;
		this.includer = includeAdaptor;
	}
	
	public TableEntry createWidget(Composite container, FormToolkit factory, boolean isOverrideable) {
		final Project pom = section.getPage().getPomEditor().getPom();

		// Include table
		Button toggle = null;
		if (isOverrideable) {
			toggle = section.createOverrideToggle(container, factory, 1, true);
		} else {
			if (section.isInherited()) {
				section.createSpacer(container, factory);
			}
		}
		TableViewer viewer = section.createTableViewer(container, factory, 1);
		TableEntry includesTable = new TableEntry(viewer, toggle, "Include", container, factory, section);
		PageSection.OverrideAdaptor adaptor = section.new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List includes = (List) value;
				includer.setIncludes(pom, includes);
			}
			public Object acceptParent() {
				return includer.getIncludes(section.getParentPom());
			}
		};

		includesTable.addEntryChangeListener(adaptor);
		includesTable.addOverrideAdaptor(adaptor);
		includesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					String include = "unknown";
					ResourcePatternProxy includeProxy = new ResourcePatternProxy(include, true);
					includer.addInclude(pom, include);
					return includeProxy;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List includes = includer.getIncludes(pom);
					String pattern = (String) object;
					if (includes != null) {
						includes.remove(pattern);
						includes.add(index, pattern);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List includes = includer.getIncludes(pom);
					String pattern = (String) object;
					if (includes != null) {
						includes.remove(pattern);
					}
				}
				// only ever called if this subsection belongs to a ResourceSection
				public List getDependents(Object parentObject) {
					Resource resource = (Resource) parentObject;
					List includes = resource.getIncludes();
					List patternProxies = new ArrayList(includes.size());
					Iterator itr = includes.iterator();
					while (itr.hasNext()) {
						patternProxies.add(new ResourcePatternProxy((String) itr.next(), true));
					}
					return patternProxies;
				}
			}
		);
		return includesTable;
	}

}
