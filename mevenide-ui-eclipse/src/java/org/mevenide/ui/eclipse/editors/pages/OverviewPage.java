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
package org.mevenide.ui.eclipse.editors.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents information on the identification and other core POM
 * information.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OverviewPage extends AbstractPomEditorPage {

    private static final Log log = LogFactory.getLog(OverviewPage.class);
    
    public static final String HEADING = Mevenide.getResourceString("OverviewPage.heading");
	public static final String CHILD = Mevenide.getResourceString("OverviewPage.heading.child");
	public static final String UNNAMED = Mevenide.getResourceString("OverviewPage.heading.unnamed");
    
	private IdentificationSection idSection;
	private DescriptionSection descriptionSection;
	private FullDescriptionSection fullDesctiptionSection;
	
    public OverviewPage(MevenidePomEditor editor) {
        super(HEADING, editor);
		setHeading(editor.getPom());
    }

	protected void initializePage(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		PageWidgetFactory factory = getFactory();
		factory.setBackgroundColor(MevenideColors.WHITE);

		idSection = new IdentificationSection(this);
		Control control = idSection.createControl(parent, factory);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);

		descriptionSection = new DescriptionSection(this);
		control = descriptionSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);

		fullDesctiptionSection = new FullDescriptionSection(this);
		control = fullDesctiptionSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
	}

	public void update(Project pom) {
	    if (log.isDebugEnabled()) {
	        log.debug("updating overview");
	    }
		setHeading(pom);
		
		idSection.update(pom);
		descriptionSection.update(pom);
		fullDesctiptionSection.update(pom);
		
		setUpdateNeeded(false);
	}
	
	protected void setHeading(Project pom) {
		if (pom.getName() != null && !"".equals(pom.getName())) {
			setHeading(HEADING + pom.getName());
		}
		else if (getEditor().getParentPom() != null && !"".equals(getEditor().getParentPom().getName())){
			setHeading(HEADING + getEditor().getParentPom().getName() + CHILD);
		}
		else {
			setHeading(HEADING + UNNAMED);
		}
	}
	
	/**
	 * @see org.mevenide.ui.eclipse.editors.IPomEditorPage#isPropertySourceSupplier()
	 */
	public boolean isPropertySourceSupplier() {
		return false;
	}
	
}
