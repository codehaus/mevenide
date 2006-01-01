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

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.mevenide.ui.eclipse.editors.pom.IPomEditorPage;
import org.mevenide.ui.eclipse.editors.pom.entries.IEntryChangeListener;
import org.mevenide.ui.eclipse.editors.pom.entries.IOverrideAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.OverridableTextEntry;
import org.mevenide.ui.eclipse.editors.pom.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.pom.entries.TextEntry;
import org.mevenide.util.MevenideUtils;
import org.mevenide.util.StringUtils;

/**
 * Abstract base class for a section of a page in the POM Editor ui.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class PageSection extends SectionPart {

    private static final Log log = LogFactory.getLog(PageSection.class);

    private IPomEditorPage page;
    private Project parentPom;
    private boolean inherited;

    class EntryChangeListenerAdaptor implements IEntryChangeListener {

        public void entryChanged(PageEntry entry) {
        }

        public void entryDirty(PageEntry entry) {
            getPage().getPomEditor().setModelDirty(true);
            if (log.isDebugEnabled()) {
                log.debug("entry was changed!"); //$NON-NLS-1$
            }
        }
    }

    abstract class OverrideAdaptor implements IOverrideAdaptor, IEntryChangeListener {
        public void refreshUI() {
            redrawSection();
        }

        public void entryChanged(PageEntry entry) {
            if (log.isDebugEnabled()) {
                log.debug("overridable entry change committed! " + entry.getValue()); //$NON-NLS-1$
            }
            overrideParent(entry.getValue());
        }

        public void entryDirty(PageEntry entry) {
            getPage().getPomEditor().setModelDirty(true);
            if (log.isDebugEnabled()) {
                log.debug("overridable entry was changed!"); //$NON-NLS-1$
            }
        }
    }

    public PageSection(IPomEditorPage containingPage, Composite parent, FormToolkit toolkit) {
        this(
                containingPage, parent, toolkit, 
                Section.DESCRIPTION | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.FOCUS_TITLE | ExpandableComposite.TITLE_BAR
             );
    }
    
    public PageSection(IPomEditorPage containingPage, Composite parent, FormToolkit toolkit, int style) {
        super(parent, toolkit, style);
        this.page = containingPage;

        this.parentPom = page.getPomEditor().getParentPom();
        if (parentPom != null) {
            inherited = true;
        }
    }
    
    public void setTitle(String title) {
        getSection().setText(title);
    }

    public void setDescription(String description) {
        getSection().setDescription(description);
    }

    protected Label createSpacer(Composite parent, FormToolkit factory) {
        return createSpacer(parent, factory, 1);
    }

    protected Label createSpacer(Composite parent, FormToolkit factory, int span) {
        Label spacer = factory.createLabel(parent, ""); //$NON-NLS-1$
        GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.HORIZONTAL_ALIGN_BEGINNING);
        data.horizontalSpan = span;
        data.widthHint = 5;
        data.heightHint = 5;
        spacer.setLayoutData(data);
        return spacer;
    }

    protected Label createLabel(Composite parent, String label, FormToolkit factory) {
        return createLabel(parent, label, null, factory);
    }

    protected Label createLabel(Composite parent, String label, String tooltip, FormToolkit factory) {
        Label widget = factory.createLabel(parent, label);
        if (tooltip != null) {
            widget.setToolTipText(tooltip);
        }
        return widget;
    }

    protected Button createOverrideToggle(Composite parent, FormToolkit factory) {
        return createOverrideToggle(parent, factory, 1);
    }

    protected Button createOverrideToggle(Composite parent, FormToolkit factory, int span) {
        return createOverrideToggle(parent, factory, span, false);
    }

    protected Button createOverrideToggle(Composite parent, FormToolkit factory, int span, boolean alignTop) {
        Button inheritanceToggle = null;
        if (isInherited()) {
            inheritanceToggle = factory.createButton(parent, " ", SWT.CHECK); //$NON-NLS-1$
            int vAlign = alignTop ? GridData.VERTICAL_ALIGN_BEGINNING : GridData.VERTICAL_ALIGN_CENTER;
            GridData data = new GridData(vAlign | GridData.HORIZONTAL_ALIGN_BEGINNING);
            data.horizontalSpan = span;
            data.widthHint = 12;
            data.heightHint = 12;
            inheritanceToggle.setLayoutData(data);
            inheritanceToggle.setSize(SWT.DEFAULT, SWT.DEFAULT);
        }
        return inheritanceToggle;
    }

    protected Button createBrowseButton(Composite parent, FormToolkit factory, String label, String tooltip, int span) {
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

    protected Text createMultilineText(Composite parent, FormToolkit factory) {

        Text text = factory.createText(parent, "", SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL); //$NON-NLS-1$
        GridData data = new GridData(GridData.FILL_BOTH);
        text.setLayoutData(data);
        GC gc = new GC(text);
		try {
			Point extent = gc.textExtent("X");//$NON-NLS-1$
			data.heightHint = 15 * extent.y;
		} 
		finally {
			gc.dispose();
		}
        return text;
    }

    protected Text createText(Composite parent, FormToolkit factory) {

        return createText(parent, factory, 1);
    }

    protected Text createText(Composite parent, FormToolkit factory, int span) {

        return createText(parent, factory, span, SWT.NONE);
    }

    protected Text createText(Composite parent, FormToolkit factory, int span, int style) {

        Text text = factory.createText(parent, "", style); //$NON-NLS-1$
        int hfill = span == 1 ? GridData.FILL_HORIZONTAL : GridData.HORIZONTAL_ALIGN_FILL;
        GridData gd = new GridData(hfill | GridData.VERTICAL_ALIGN_CENTER);
        gd.horizontalSpan = span;
        text.setLayoutData(gd);
        return text;
    }

    protected TableViewer createTableViewer(Composite parent, FormToolkit factory, int span, int style) {

        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = span;

        Table table = factory.createTable(parent, style);
        table.setLayoutData(data);

        TableViewer viewer = new TableViewer(table);
        viewer.setContentProvider(new WorkbenchContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());

        return viewer;
    }

    protected abstract Composite createSectionContent(Composite parent, FormToolkit factory);

    protected abstract void update(Project pom);

    public void updateSection(Project pom) {
        update(pom);
        redrawSection();
    }

    protected void redrawSection() {
        Display display = getPage().getPomEditor().getSite().getShell().getDisplay();
        display.asyncExec(new Runnable() {
	            public void run() {
	                if ( !getSection().isDisposed() ) {
	                    getSection().redraw();
	                }
	            }
	        }
        );
	}

    public IPomEditorPage getPage() {
        return page;
    }

    protected void setIfDefined(TextEntry entry, String text) {
        if (text != null) {
            entry.setText(text, true);
        }
    }

    protected void setIfDefined(OverridableTextEntry entry, String text, String parentText) {
        if ( entry != null ) {
	        if (text != null) {
	            entry.setText(text, true);
	            entry.setInherited(false);
	        } else if (parentText != null) {
	            entry.setText(parentText, true);
	            entry.setInherited(true);
	        } else {
	            //required otherwise removal of a complex type doesnot seem to affect pages
	            //however this causes the pom editor to be marked as dirty as soon as it is open
	            entry.setText(null, true);
	            entry.setInherited(false);
	        }
        }
    }

    protected boolean isDefined(String value) {
        return (!StringUtils.isNull(value));
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

    /**
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        if (log.isDebugEnabled()) {
            log.debug("initializing the section and its client..."); //$NON-NLS-1$
        }
        super.initialize(form);
        FormToolkit toolkit = form.getToolkit();
        Section section = getSection();
//        toolkit.createCompositeSeparator(section);
        
        Composite client = createSectionContent(section, toolkit);

        section.setClient(client);
        section.setExpanded(true);
        
    }

    protected String getRelativePath(final String directory) throws IOException {
        String basedir = ((FileEditorInput) getPage().getPomEditor().getEditorInput()).getFile().getLocation().toOSString();
        return MevenideUtils.makeRelativePath(new File(basedir).getParentFile(), directory).replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    protected File getPomFile() {
         return new File(((FileEditorInput) getPage().getPomEditor().getEditorInput()).getFile().getLocation().toOSString());
    }
    
    public void ensureExpanded() {
        if (! getSection().isExpanded()) {
            getSection().setExpanded(true);
        }
    }
    
}
