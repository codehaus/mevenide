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

import org.apache.maven.project.Project;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents a client control for editing information on the source 
 * configuration management system used by this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class RepositoryPage extends AbstractPomEditorPage {

	public static final String HEADING = Mevenide.getResourceString("RepositoryPage.heading");
    
	private ScmConnectionSection scmSection;
	private VersionsSection versionsSection;
	private BranchesSection branchesSection;

	public RepositoryPage(MevenidePomEditor editor) {
        super(HEADING, editor);
    }

	protected void initializePage(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		PageWidgetFactory factory = getFactory();
		factory.setBackgroundColor(MevenideColors.WHITE);

		scmSection = new ScmConnectionSection(this);
		Control control = scmSection.createControl(parent, factory);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);

		versionsSection = new VersionsSection(this);
		control = versionsSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);

		branchesSection = new BranchesSection(this);
		control = branchesSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
	}

	public void update(Project pom) {
		scmSection.update(pom);
		versionsSection.update(pom);
		branchesSection.update(pom);
		
		setUpdateNeeded(false);
	}

}
