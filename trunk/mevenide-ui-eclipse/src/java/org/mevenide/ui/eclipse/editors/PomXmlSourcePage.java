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
package org.mevenide.ui.eclipse.editors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * Presents the raw POM source in a basic XML editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlSourcePage
	extends TextEditor 
	implements IPomEditorPage {

	private static final Log log = LogFactory.getLog(PomXmlSourcePage.class);
    
	private MevenidePomEditor editor;
	private IDocumentListener documentListener;
	private boolean modelNeedsUpdating;
	private boolean active = false;

	public PomXmlSourcePage(MevenidePomEditor pomEditor) {
		super();
		this.editor = pomEditor;
		
		setSourceViewerConfiguration(new PomXmlConfiguration());
		initializeDocumentListener();
	}
	
    public void dispose() {
		super.dispose();
	}
	
	public void init(IEditorSite site, IEditorInput input) 
		throws PartInitException {
		
		setDocumentProvider(editor.getDocumentProvider());
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
	
    public boolean isActive() {
        //return editor.getCurrentPage() == this;
        return active;
    }
    
    private void setActive(boolean activeFlag) {
    	this.active = activeFlag;
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
    		boolean cleanModel = getEditor().updateModel();
    		if (cleanModel) {
    			setModelNeedsUpdating(false);
    		}
    		
    		// FIXME: error marks for invalid model
    	}
	}

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        
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

	public MevenidePomEditor getEditor() {
        return editor;
    }
    
    public void setHeading(String heading) {}

	/**
	 * @see org.mevenide.ui.eclipse.editors.IPomEditorPage#isPropertySourceSupplier()
	 */
	public boolean isPropertySourceSupplier() {
		return false;
	}
	
}
