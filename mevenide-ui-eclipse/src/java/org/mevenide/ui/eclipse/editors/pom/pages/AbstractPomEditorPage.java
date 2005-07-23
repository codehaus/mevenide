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

import org.apache.maven.project.Project;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.IPomEditorPage;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;
import org.mevenide.ui.eclipse.editors.pom.PomXmlSourcePage;

/**
 * Abstract base class for a page in the POM editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class AbstractPomEditorPage 
	extends FormPage 
	implements IPomEditorPage, IProjectChangeListener {
	
	private String heading;
	private boolean updateNeeded;
	
	public AbstractPomEditorPage(MevenidePomEditor pomEditor, String pageId, String tabTitle, String pageHeading) {
		this(pomEditor, pageId, tabTitle);
		this.heading = pageHeading;
	}

    public AbstractPomEditorPage(MevenidePomEditor pomEditor, String pageId, String tabTitle) {
		super(pomEditor, pageId, tabTitle);
	}
	
	public MevenidePomEditor getPomEditor() {
		return (MevenidePomEditor) getEditor();
	}

	protected void addSection(PageSection section) {
      IManagedForm form = getManagedForm();

      if (Mevenide.ECLIPSE_FORMS_3_1_0.isGreaterThan(Mevenide.getEclipseFormsVersion())) {
         section.initialize(form);
      }

      form.addPart(section);
	}
	
	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.ManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
	    ScrolledForm parent = managedForm.getForm();
		parent.setText(getHeading());
		
		createPageContent(parent.getBody());
		setUpdateNeeded(true);
		update();
	}
	
	protected abstract void createPageContent(Composite parent);
	
	public void pageActivated(IPomEditorPage oldPage) {
		update();
		setFocus();
		setActive(true);
	}
	
	public void pageDeactivated(IPomEditorPage newPage) {
		setActive(false);
		if (newPage instanceof PomXmlSourcePage) {
			getPomEditor().updateDocument();
		}
	}
	
	public void projectChanged(ProjectChangeEvent e) {
		update(e.getPom());
	}
	
	/**
	 * @see org.eclipse.swt.widgets.Control#update()
	 */
	private void update() {
		if (isUpdateNeeded()) {
			update(getPomEditor().getPom());
		}
	}
	
	protected void update(Project pom) {
	    if ( getManagedForm() != null ) {
		    IFormPart[] sections = getManagedForm().getParts();
			for (int i = 0; i < sections.length; i++) {
                if (sections[i] instanceof PageSection) {
                    PageSection section = (PageSection) sections[i];
                    section.updateSection(pom);
                }
			}
			setUpdateNeeded(false);
	    }
	    else {
	        //mark as allRefreshNeeded so that when page activates later it will update
	    }
	}
    
    /**
     * @return <tt>true</tt> If the form's sections need updating.
     */
    protected boolean isUpdateNeeded() {
        return updateNeeded;
    }

    /**
     * @param updateNeeded Indicates that the form's sections need updating.
     */
    protected void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }
	
	/**
	 * @see org.mevenide.ui.eclipse.editors.pom.IPomEditorPage#isPropertySourceSupplier()
	 */
	public boolean isPropertySourceSupplier() {
		return true;
	}
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		update();
		super.setFocus();
	}
	
    /**
     * @return Returns the heading.
     */
    public String getHeading() {
        return heading;
    }
    /**
     * @param heading The heading to set.
     */
    public void setHeading(String pageHeading) {
        this.heading = pageHeading;
    }
}
