/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

	public PomXmlSourcePage(MevenidePomEditor editor) {
		super();
		this.editor = editor;
		
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
	
    private boolean isActive() {
        //return editor.getCurrentPage() == this;
        return active;
    }
    
    private void setActive(boolean active) {
    	this.active = active;
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

	private void setModelNeedsUpdating(boolean modelNeedsUpdating) {
        this.modelNeedsUpdating = modelNeedsUpdating;
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
