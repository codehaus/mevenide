/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class IncludesSection extends PageSection {

	private IncludesSubsection subsection;
	private IIncludesAdaptor includesAdaptor;
	private TableEntry includesTable;
	
	public IncludesSection(UnitTestsPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("UnitTestIncludesSection.header"));
	}
	
	void setIncludesAdaptor(IIncludesAdaptor adaptor) {
		this.includesAdaptor = adaptor;
	}

	public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		subsection = new IncludesSubsection(this, includesAdaptor);
		
		includesTable = subsection.createWidget(container, factory, true);
		
		factory.paintBordersFor(container);
		
		return container;
	}

	public void update(Project pom) {
		subsection.updateTableEntries(includesTable, getIncludes(pom), getInheritedIncludes(), true);
		super.update(pom);
	}

	private List getIncludes(Project pom) {
		return includesAdaptor.getIncludes(pom);
	}
	
	private List getInheritedIncludes() {
		return isInherited() 
			? getIncludes(getParentPom())
			: null;
	}

}
