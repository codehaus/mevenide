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
package org.mevenide.ui.eclipse.editors.pom;

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.mevenide.project.io.IProjectUnmarshaller;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.DefaultProjectUnmarshaller;

/**
 * Presents the raw POM source in a basic XML editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlSourcePage
	extends TextEditor 
	implements IPomEditorPage, IFormPage {

	private static final Log log = LogFactory.getLog(PomXmlSourcePage.class);
    private static final String ID = Mevenide.getResourceString("PomXMLSourcePage.id");
    private static final String TAB = Mevenide.getResourceString("PomXMLSourcePage.tab.label");
    
    private Composite control;
	private MevenidePomEditor editor;
	private IDocumentListener documentListener;
	private boolean modelNeedsUpdating;
	private boolean active = false;
	private int index;

	IProjectUnmarshaller unmarshaller;
	
	public PomXmlSourcePage(MevenidePomEditor pomEditor) {
		super();
		this.editor = pomEditor;
		unmarshaller = new DefaultProjectUnmarshaller();
		
		setSourceViewerConfiguration(new PomXmlConfiguration());
		initializeDocumentListener();
	}
	
    public void dispose() {
		super.dispose();
	}
	
	public void init(IEditorSite site, IEditorInput input) 
		throws PartInitException {
		
		setDocumentProvider(editor.getDocumentProvider());
		input = editor.getEditorInput();
        super.init(site, input);
    }

	private void initializeDocumentListener() {
		documentListener = new IDocumentListener() {
            public void documentAboutToBeChanged(DocumentEvent event) {
            }

            public void documentChanged(DocumentEvent event) {
                if (log.isDebugEnabled()) {
                    log.debug("document has been changed! active = " + isActive());
                }
                if (isActive()) {
					setModelNeedsUpdating(true);
                }
            }
		};
	}
	
    public void pageActivated(IPomEditorPage oldPage) {
        if (log.isDebugEnabled()) {
            log.debug("PomXmlSourcePage made active!");
        }
		setModelNeedsUpdating(false);
		setActive(true);
    }

	public void pageDeactivated(IPomEditorPage newPage) {
		if (log.isDebugEnabled()) {
			log.debug("PomXmlSourcePage made inactive!");
		}
		setActive(false);
    	if (isModelNeedsUpdating())
    	{
    		boolean cleanModel = getPomEditor().updateModel();
    		if (cleanModel) {
    			setModelNeedsUpdating(false);
    		}
    		
    		// FIXME: error marks for invalid model
    	}
	}

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        control = parent;
        
        IDocument document = getDocumentProvider().getDocument(getEditorInput());
        document.addDocumentListener(documentListener);
    }
    
    public void update(Project pom) {}

    private boolean isModelNeedsUpdating() {
        return modelNeedsUpdating;
    }

	private void setModelNeedsUpdating(boolean needsUpdating) {
        this.modelNeedsUpdating = needsUpdating;
    }

	/**
	 * @see org.mevenide.ui.eclipse.editors.pom.IPomEditorPage#getPomEditor()
	 */
	public MevenidePomEditor getPomEditor() {
        return editor;
    }
    
	/**
	 * @see org.mevenide.ui.eclipse.editors.pom.IPomEditorPage#isPropertySourceSupplier()
	 */
	public boolean isPropertySourceSupplier() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	public void initialize(FormEditor parent) {
		this.editor = (MevenidePomEditor) parent;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#getEditor()
	 */
	public FormEditor getEditor() {
		return editor;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#getManagedForm()
	 */
	public IManagedForm getManagedForm() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#setActive(boolean)
	 */
	public void setActive(boolean activeFlag) {
    	this.active = activeFlag;
	}

    public boolean isActive() {
        return active;
    }
    
	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#getPartControl()
	 */
	public Control getPartControl() {
		return control;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#getId()
	 */
	public String getId() {
		return ID;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#setIndex(int)
	 */
	public void setIndex(int pageIndex) {
		this.index = pageIndex;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#isSource()
	 */
	public boolean isSource() {
		return true;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.IFormPage#focusOn(java.lang.Object)
	 */
	public void focusOn(Object object) {
	}
	
    /**
     * @see org.eclipse.ui.part.WorkbenchPart#getTitle()
     */
    public String getTitle() {
        return TAB;
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#isEditor()
	 */
	public boolean isEditor() {
		//TODO Auto-generated method stub
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#selectReveal(java.lang.Object)
	 */
	public boolean selectReveal(Object object) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean canLeaveThePage() {
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
	    StringReader reader = new StringReader(document.get());
	    try {
		    Project pom = null;
	    	pom = unmarshaller.parse(reader);
	    	return true;
		}
	    catch ( Exception e ) {
	    	log.info("Cannot Leave Page due to parsing errors. reason : ", e);
	    	return false;
	    }
	}
}


