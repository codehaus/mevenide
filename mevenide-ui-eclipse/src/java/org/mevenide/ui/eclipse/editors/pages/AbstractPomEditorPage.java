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
import org.mevenide.ui.eclipse.editors.*;
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

}
