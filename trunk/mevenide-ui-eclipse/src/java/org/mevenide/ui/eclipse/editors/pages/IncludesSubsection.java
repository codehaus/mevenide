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

import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.editors.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.entries.TableEntry;
import org.mevenide.ui.eclipse.editors.properties.ResourcePatternProxy;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class IncludesSubsection extends AbstractResourcePatternSubsection {
	
	private PageSection section;
	private IIncludesAdaptor includer;
	
	public IncludesSubsection(PageSection section, IIncludesAdaptor includer) {
		this.section = section;
		this.includer = includer;
	}
	
	public TableEntry createWidget(Composite container, PageWidgetFactory factory, boolean isOverrideable) {
		final Project pom = section.getPage().getEditor().getPom();

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
				public List getDependents(Object parentObject) { return null; }
			}
		);
		return includesTable;
	}

}
