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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;
import org.mevenide.util.StringUtils;

/**
 * Presents information on the identification and other core POM
 * information.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OverviewPage extends AbstractPomEditorPage implements IPropertyListener {

    private static final Log log = LogFactory.getLog(OverviewPage.class);
    
    private static final String ID = Mevenide.getResourceString("OverviewPage.id"); //$NON-NLS-1$
    private static final String TAB = Mevenide.getResourceString("OverviewPage.tab.label"); //$NON-NLS-1$
    private static final String HEADING = Mevenide.getResourceString("OverviewPage.heading"); //$NON-NLS-1$
    private static final String CHILD = Mevenide.getResourceString("OverviewPage.heading.child"); //$NON-NLS-1$
    private static final String UNNAMED = Mevenide.getResourceString("OverviewPage.heading.unnamed"); //$NON-NLS-1$
    
	private IdentificationSection idSection;
	private DescriptionSection descriptionSection;
	private FullDescriptionSection fullDesctiptionSection;
	
    public OverviewPage(MevenidePomEditor editor) {
        super(editor, ID, TAB);
        setHeading(editor.getPom());
    }

	protected void createPageContent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		FormToolkit factory = getEditor().getToolkit();

		idSection = new IdentificationSection(this, parent, factory);
		Control control = idSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(idSection);

		descriptionSection = new DescriptionSection(this, parent, factory);
		control = descriptionSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(descriptionSection);
		
		fullDesctiptionSection = new FullDescriptionSection(this, parent, factory);
		control = fullDesctiptionSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		addSection(fullDesctiptionSection);
	}

	protected void update(Project pom) {
	    if (log.isDebugEnabled()) {
	        log.debug("updating overview"); //$NON-NLS-1$
	    }
	    setHeading(pom);
		
		super.update(pom);
	}
	
	protected void setHeading(Project pom) {
		if ( !StringUtils.isNull(pom.getName()) ) {
		    setHeading(HEADING + pom.getName());
		}
		else if (getPomEditor().getParentPom() != null && !StringUtils.isNull(getPomEditor().getParentPom().getName()) ){
		    setHeading(HEADING + getPomEditor().getParentPom().getName() + CHILD);
		}
		else {
		    setHeading(HEADING + UNNAMED);
		}
	}
	
	/**
	 * @see org.mevenide.ui.eclipse.editors.pom.IPomEditorPage#isPropertySourceSupplier()
	 */
	public boolean isPropertySourceSupplier() {
		return false;
	}

	
    
    public void propertyChanged(Object arg0, int arg1) {
        if ( getPomEditor().equals(arg0) && arg1 == IWorkbenchPart.PROP_TITLE ) {
            ScrolledForm parent = getManagedForm().getForm();
    		parent.setText(getHeading());
    		parent.redraw();
        }
    }
}
