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

import org.apache.maven.project.Project;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.MevenideFonts;
import org.mevenide.ui.eclipse.editors.IPomEditorPage;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;
import org.mevenide.ui.eclipse.editors.PomXmlSourcePage;

/**
 * Abstract base class for a page in the POM editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class AbstractPomEditorPage 
	extends Composite 
	implements IPomEditorPage, IProjectChangeListener {

	private PageWidgetFactory factory;
    private MevenidePomEditor editor;
    private String heading;
	private Label headingLabel;

	private boolean updateNeeded;
    
    public AbstractPomEditorPage(String heading, MevenidePomEditor editor) {
        super(editor.getParentContainer(), SWT.NONE);
        this.editor = editor;
        this.heading = heading;
        this.factory = new PageWidgetFactory();
        init();
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
        if (headingLabel != null) {
        	headingLabel.setText(heading);
        }
    }

    public MevenidePomEditor getEditor() {
        return editor;
    }

    private void init() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		setBackground(MevenideColors.WHITE);

        // heading
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
        headingLabel = new Label(this, SWT.NONE);
        headingLabel.setFont(MevenideFonts.EDITOR_HEADER);
        headingLabel.setBackground(MevenideColors.WHITE);
		headingLabel.setText(getHeading());
		headingLabel.setLayoutData(data);
        
        // create parent for pages
		ScrolledComposite scroller = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scroller.setExpandHorizontal(true);
		scroller.setExpandVertical(true);
		scroller.setBackground(MevenideColors.WHITE);
    	
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		scroller.setLayoutData(data);

        Composite parent = new Composite(scroller, SWT.NONE);
        parent.setBackground(MevenideColors.WHITE);

        // now the rest of the page
        initializePage(parent);
        
		scroller.setContent(parent);
        scroller.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        update(getEditor().getPom());
    }

    protected abstract void initializePage(Composite parent);

	public void pageActivated(IPomEditorPage oldPage) {
		update();
		setFocus();
	}

	public void pageDeactivated(IPomEditorPage newPage) {
		if (newPage instanceof PomXmlSourcePage) {
			getEditor().updateDocument();
		}
	}
	
	public void projectChanged(ProjectChangeEvent e) {
		update(e.getPom());
	}

	public void update() {
		if (isUpdateNeeded()) {
			update(getEditor().getPom());
		}
	}

	public void update(Project pom) {
	}
	
    protected boolean isUpdateNeeded() {
        return updateNeeded;
    }

    protected void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public PageWidgetFactory getFactory() {
        return factory;
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.IPomEditorPage#isPropertySourceSupplier()
     */
    public boolean isPropertySourceSupplier() {
    	return true;
    }
    
}
