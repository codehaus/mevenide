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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
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
	extends EditorPart
	implements IPomEditorPage, IProjectChangeListener {
	
	private static final Log log = LogFactory.getLog(AbstractPomEditorPage.class);

	private ScrolledComposite scroller;
	private Composite control;
	private PageWidgetFactory factory;
    private MevenidePomEditor editor;
    private String heading;
	private Label headingLabel;
	private List sections = new ArrayList(5);

	private boolean updateNeeded;
	private boolean active;
    
	class PageLayout extends Layout
	{
		protected Point computeSize(Composite composite, int widthHint, int heightHint, boolean flushCache)
		{
			if (widthHint != SWT.DEFAULT && heightHint != SWT.DEFAULT) {
				return new Point(widthHint, heightHint);
			}
			return computeMinimumSize(composite, heightHint, flushCache);
		}
		protected void layout(Composite composite, boolean flushCache)
		{
			Rectangle clientArea = composite.getClientArea();
			Control client = composite.getChildren()[0];
			if (client != null && !client.isDisposed())
			{
				client.setBounds(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
			}
		}
		
		private Point computeMinimumSize(Composite composite, int heightHint, boolean flushCache) {
			Control client = composite.getChildren()[0];
			Rectangle clientBounds = client.getBounds();
			int widthHint = clientBounds.x + clientBounds.width;
			return client.computeSize(widthHint, heightHint, flushCache);			
		}
	}
	
	public AbstractPomEditorPage(String mainHeading, MevenidePomEditor pomEditor) {
        this.editor = pomEditor;
        this.heading = mainHeading;
        this.factory = new PageWidgetFactory();
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String mainHeading) {
        this.heading = mainHeading;
        if (headingLabel != null) {
        	headingLabel.setText(mainHeading);
        }
    }

    public MevenidePomEditor getEditor() {
        return editor;
    }
    
    protected void addSection(PageSection section) {
    	sections.add(section);
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
	{
    	Composite pageContainer = new Composite(parent, SWT.NONE);
    	
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		pageContainer.setLayout(layout);
		pageContainer.setBackground(MevenideColors.WHITE);

        // heading
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
        headingLabel = new Label(pageContainer, SWT.NONE);
        headingLabel.setFont(MevenideFonts.EDITOR_HEADER);
        headingLabel.setBackground(MevenideColors.WHITE);
		headingLabel.setText(getHeading());
		headingLabel.setLayoutData(data);
        
        // create parent for pages
		scroller = new ScrolledComposite(pageContainer, SWT.H_SCROLL | SWT.V_SCROLL);
		scroller.setExpandHorizontal(true);
		scroller.setExpandVertical(true);
		scroller.setBackground(MevenideColors.WHITE);
    	
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		scroller.setLayoutData(data);

        Composite canvas = factory.createComposite(scroller);
        canvas.setBackground(MevenideColors.WHITE);
        canvas.setForeground(MevenideColors.BLACK);
        canvas.setLayout(new PageLayout());
        canvas.setMenu(parent.getMenu());
        
        Composite container = factory.createComposite(canvas);
        
        // now the rest of the page
        initializePage(container);
        
		scroller.setContent(container);
		this.control = canvas;
        
		setUpdateNeeded(true);
        update();
    }

    protected abstract void initializePage(Composite parent);

	public void pageActivated(IPomEditorPage oldPage) {
		update();
		setFocus();
		setActive(true);
	}

	public void pageDeactivated(IPomEditorPage newPage) {
		setActive(false);
		if (newPage instanceof PomXmlSourcePage) {
			getEditor().updateDocument();
		}
	}
	
	public void projectChanged(ProjectChangeEvent e) {
		update(e.getPom());
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#update()
	 */
	public void update() {
		log.debug("update called - resetting size on scoller");
		updateScrolledComposite();
		if (isUpdateNeeded()) {
			update(getEditor().getPom());
		}
	}

	private void updateScrolledComposite()
	{
		Point updatedSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		control.setSize(updatedSize);
		scroller.setMinSize(updatedSize);
	}

	protected void update(Project pom) {
		Iterator itr = sections.iterator();
		while (itr.hasNext()) {
			((PageSection) itr.next()).updateSection(pom);
		}
		setUpdateNeeded(false);
	}
	
    protected boolean isUpdateNeeded() {
        return updateNeeded;
    }

    /**
	 * FIXME: nothing calls this method currently - remove?
     */
    protected void setUpdateNeeded(boolean needsUpdate) {
        this.updateNeeded = needsUpdate;
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
    
    /**
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor)
	{
    	// no-op
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
	{
    	// no-op
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
    	setSite(site);
    	setInput(input);
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    public boolean isDirty()
	{
    	return false;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
	{
    	return false;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
	{
    	update();
    	control.setFocus();
    }
    
	public boolean isActive()
	{
		return active;
	}

	private void setActive(boolean activeFlag)
	{
		this.active = activeFlag;
	}

}
