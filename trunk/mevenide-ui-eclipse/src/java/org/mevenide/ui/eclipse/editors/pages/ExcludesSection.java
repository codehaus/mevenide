/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundations.  All rights
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
import org.apache.maven.project.Project;
import org.apache.maven.project.UnitTest;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ExcludesSection extends PageSection {

	private ExcludesSubsection subsection;
	private IExcludesAdaptor excludesAdaptor;
	private TableEntry excludesTable;
	
	public ExcludesSection(UnitTestsPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("UnitTestExcludesSection.header"));
	}
	
	void setExcludesAdaptor(IExcludesAdaptor excludesAdaptor) {
		this.excludesAdaptor = excludesAdaptor;
	}

	public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		subsection = new ExcludesSubsection(this, excludesAdaptor);
		
		excludesTable = subsection.createWidget(container, factory, true);
		
		factory.paintBordersFor(container);

		return container;
	}

	public void update(Project pom) {
		subsection.updateTableEntries(excludesTable, getExcludes(pom), getInheritedExcludes(), false);
		
		super.update(pom);
	}

	public void setExcludes(Object target, List newExcludes) {
		List excludes = getOrCreateUnitTest(target).getExcludes();
		excludes.removeAll(excludes);
		excludes.addAll(newExcludes);
		getPage().getEditor().setModelDirty(true);
	}
	
	public void addExclude(Object target, String exclude) {
		getOrCreateUnitTest(target).addExclude(exclude);
		getPage().getEditor().setModelDirty(true);
	}
	
	public List getExcludes(Object source) {
		Project pom = (Project) source;
		return pom.getBuild() != null 
			? pom.getBuild().getUnitTest() != null
				? pom.getBuild().getUnitTest().getExcludes()
				: null
			: null;
	}
	
	public List getInheritedExcludes() {
		return isInherited() 
			? getExcludes(getParentPom())
			: null;
	}

	private UnitTest getOrCreateUnitTest(Object model) {
		Project pom = (Project) model;
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
