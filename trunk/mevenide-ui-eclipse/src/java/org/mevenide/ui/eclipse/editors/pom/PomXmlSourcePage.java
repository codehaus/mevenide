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
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.project.ProjectComparator;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * Presents the raw POM source in a basic XML editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlSourcePage
	extends TextEditor 
	implements IPomEditorPage, IFormPage, IProjectChangeListener {

	private static final Log log = LogFactory.getLog(PomXmlSourcePage.class);
    private static final String ID = Mevenide.getResourceString("PomXMLSourcePage.id"); //$NON-NLS-1$
    private static final String TAB = Mevenide.getResourceString("PomXMLSourcePage.tab.label"); //$NON-NLS-1$
    
    private Composite control;
	private MevenidePomEditor editor;
	private IDocumentListener documentListener;
	private boolean modelNeedsUpdating;
	private boolean active = false;
	private int index;

	JDomProjectUnmarshaller unmarshaller;
	
	public PomXmlSourcePage(MevenidePomEditor pomEditor) {
		super();
		initialize(pomEditor);

		unmarshaller = new JDomProjectUnmarshaller();
		
		setSourceViewerConfiguration(new PomXmlConfiguration());
		initializeDocumentListener();
	}

    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
     */
    public void initialize(FormEditor editor) {
		this.editor = (MevenidePomEditor)editor;

        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.addProjectChangeListener(this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        ProjectComparator comparator = (ProjectComparator)getEditor().getAdapter(ProjectComparator.class);
        if (comparator != null) {
            comparator.removeProjectChangeListener(this);
        }

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
                    log.debug("document has been changed! active = " + isActive()); //$NON-NLS-1$
                }
                if (isActive()) {
					setModelNeedsUpdating(true);
                }
            }
		};
	}
	
    public void pageActivated(IPomEditorPage oldPage) {
        if (log.isDebugEnabled()) {
            log.debug("PomXmlSourcePage made active!"); //$NON-NLS-1$
        }
		setModelNeedsUpdating(false);
		getPomEditor().updateDocument();
		setActive(true);
    }

    
    public boolean isDirty() {
        return isModelNeedsUpdating();
    }
    
	public void pageDeactivated(IPomEditorPage newPage) {
		if (log.isDebugEnabled()) {
			log.debug("PomXmlSourcePage made inactive!"); //$NON-NLS-1$
		}
		setActive(false);
    	if (isModelNeedsUpdating())
    	{
    		boolean cleanModel = getPomEditor().updateModel();
    		if (cleanModel) {
    			setModelNeedsUpdating(false);
    		}
    		
    		// TODO: Add error marks for invalid model.
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
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#selectReveal(java.lang.Object)
	 */
	public boolean selectReveal(Object object) {
		return false;
	}
	
	public boolean canLeaveThePage() {
	    try {
	    	unmarshaller.parse(((IFileEditorInput) getEditorInput()).getFile().getRawLocation().toFile());
	    	return true;
		}
	    catch ( Exception e ) {
	    	log.info("Cannot Leave Page due to parsing errors. reason : ", e); //$NON-NLS-1$
	    	return false;
	    }
	}
	
	public void projectChanged(ProjectChangeEvent e) {
	    getPomEditor().updateDocument();
    }
}


