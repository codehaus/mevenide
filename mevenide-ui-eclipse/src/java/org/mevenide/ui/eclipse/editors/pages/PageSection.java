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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.parts.FormToolkit;
import org.eclipse.ui.forms.parts.SectionPart;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mevenide.ui.eclipse.editors.entries.IEntryChangeListener;
import org.mevenide.ui.eclipse.editors.entries.IOverrideAdaptor;
import org.mevenide.ui.eclipse.editors.entries.OverridableTextEntry;
import org.mevenide.ui.eclipse.editors.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.entries.TextEntry;

/**
 * Abstract base class for a section of a page in the POM Editor ui.
 * 
 * @refactor NEEDS MASSIVE OVERHAUL - basically a cut-n-paste from eclipse update.ui.forms
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class PageSection extends SectionPart {

	private static final Log log = LogFactory.getLog(PageSection.class);
    
	private Control client;
	private AbstractPomEditorPage page;
	private boolean inherited;
	private Project parentPom;

	class EntryChangeListenerAdaptor implements IEntryChangeListener {
		public void entryChanged(PageEntry entry) {
		}
		public void entryDirty(PageEntry entry) {
			getPage().getEditor().setModelDirty(true);
            if (log.isDebugEnabled()) {
                log.debug("entry was changed!");
            }
		}
	}
	
	abstract class OverrideAdaptor implements IOverrideAdaptor, IEntryChangeListener {
        public void refreshUI() {
        	redrawSection();
        }
		public void entryChanged(PageEntry entry) {
			if (log.isDebugEnabled()) {
				log.debug("overridable entry change committed! " + entry.getValue());
			}
			overrideParent(entry.getValue());
		}
		public void entryDirty(PageEntry entry) {
			getPage().getEditor().setModelDirty(true);
			if (log.isDebugEnabled()) {
				log.debug("overridable entry was changed!");
			}
		}
	}

    public PageSection(AbstractPomEditorPage containingPage) {
    	super(TITLE | DESCRIPTION | SEPARATOR | COLLAPSABLE);
        this.page = containingPage;
		
		this.parentPom = page.getEditor().getParentPom();
		if (parentPom != null) inherited = true;
    }

    protected Composite createClient(
    	Composite parent,
		FormToolkit toolkit) {
    	
    	client = createClient(parent, (PageWidgetFactory) toolkit);
    	return (Composite) client;
    }
    
    protected abstract Composite createClient(
		Composite parent,
		PageWidgetFactory factory);
		
	protected Label createSpacer(
		Composite parent, 
		PageWidgetFactory factory) {

		return createSpacer(parent, factory, 1);
	}
	
	protected Label createSpacer(
		Composite parent, 
		PageWidgetFactory factory,
		int span) {

		Label spacer = factory.createSpacer(parent);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		data.widthHint = 5;
		data.heightHint = 5;
		spacer.setLayoutData(data);
		return spacer;
	}
	
	protected Label createLabel(
		Composite parent, 
		String label,
		PageWidgetFactory factory) {

		return createLabel(parent, label, null, factory);
	}
	
	protected Label createLabel(
		Composite parent, 
		String label,
		String tooltip,
		PageWidgetFactory factory) {

		Label widget = factory.createLabel(parent, label);
		if (tooltip != null) {
			widget.setToolTipText(tooltip);
		}
		return widget;
	}
	
	protected Button createOverrideToggle(
		Composite parent, 
		PageWidgetFactory factory) {

		return createOverrideToggle(parent, factory, 1);
	}
	
	protected Button createOverrideToggle(
		Composite parent, 
		PageWidgetFactory factory,
		int span) {
		
		return createOverrideToggle(parent, factory, span, false);			
	}

	protected Button createOverrideToggle(
		Composite parent, 
		PageWidgetFactory factory,
		int span,
		boolean alignTop) {

		Button inheritanceToggle = null;
		if (isInherited()) {
			inheritanceToggle = factory.createButton(parent, " ", SWT.CHECK);
			int vAlign = alignTop 
				? GridData.VERTICAL_ALIGN_BEGINNING 
				: GridData.VERTICAL_ALIGN_CENTER;
			GridData data = new GridData(vAlign | GridData.HORIZONTAL_ALIGN_BEGINNING);
			data.horizontalSpan = span;
			data.widthHint = 12;
			data.heightHint = 12;
			inheritanceToggle.setLayoutData(data);
			inheritanceToggle.setSize(SWT.DEFAULT, SWT.DEFAULT);
		}
		return inheritanceToggle;
	}
	
	protected Button createBrowseButton(
		Composite parent, 
		PageWidgetFactory factory,
		String label,
		String tooltip,
		int span) {

		Composite buttonContainer = factory.createComposite(parent);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = span;
		buttonContainer.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);

		Button browseButton = factory.createButton(buttonContainer, label, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		browseButton.setLayoutData(data);
		browseButton.setToolTipText(tooltip);
		
		return browseButton;
	}

	protected Text createMultilineText(
		Composite parent,
		PageWidgetFactory factory) {

		Text text = factory.createText(parent, "", SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 80;
		text.setLayoutData(data);
		return text;
	}
		
	protected Text createText(
		Composite parent,
		PageWidgetFactory factory) {

		return createText(parent, factory, 1);
	}
		
	protected Text createText(
		Composite parent,
		PageWidgetFactory factory,
		int span) {

		return createText(parent, factory, span, SWT.NONE);
	}
	
	protected Text createText(
		Composite parent,
		PageWidgetFactory factory,
		int span,
		int style) {

		Text text = factory.createText(parent, "", style);
		int hfill = span == 1
			? GridData.FILL_HORIZONTAL
			: GridData.HORIZONTAL_ALIGN_FILL;
		GridData gd = new GridData(hfill | GridData.VERTICAL_ALIGN_CENTER);
		gd.horizontalSpan = span;
		text.setLayoutData(gd);
		return text;
	}
		
	protected TableViewer createTableViewer(
		Composite parent, 
		PageWidgetFactory factory,
		int style) {
			
		Table table = factory.createTable(parent, style);
		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);

		TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		
		return viewer;
	}

	protected abstract void update(Project pom);
	
	public void update() {
		redrawSection();
	}
	
	public void updateSection(Project pom) {
		update(pom);
		update();
	}
	
	protected void redrawSection() {
		Display display = getPage().getEditor().getSite().getShell().getDisplay();
		display.asyncExec(
			new Runnable() {
				public void run() {
					client.redraw();
				}
			}
		);
	}

    public AbstractPomEditorPage getPage() {
        return page;
    }
    
    protected void setIfDefined(TextEntry entry, String text) {
    	if (text != null) {
    		entry.setText(text, true);
    	}
    }
    
	protected void setIfDefined(OverridableTextEntry entry, String text, String parentText) {
		if (text != null) {
			entry.setText(text, true);
			entry.setInherited(false);
		}
		else if (parentText != null) {
			entry.setText(parentText, true);
			entry.setInherited(true);
		}
		else {
			entry.setInherited(false);
		}
	}
    
    protected boolean isDefined(String value) {
    	return (value != null && !"".equals(value.trim()));
    }

	protected boolean isInherited() {
        return inherited;
    }

    protected Project getParentPom() {
        return parentPom;
    }

    protected void setParentPom(Project newParentPom) {
        this.parentPom = newParentPom;
    }

}
