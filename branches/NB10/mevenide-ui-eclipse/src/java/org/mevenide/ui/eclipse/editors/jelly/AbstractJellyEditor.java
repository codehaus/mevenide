/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.editors.jelly;

import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.mevenide.ui.eclipse.Mevenide;


public abstract class AbstractJellyEditor extends TextEditor {
	private ColorManager colorManager;
	
	private Map namespaces;
	private XMLReconciler reconciler;
	
	public AbstractJellyEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager, this));
		setDocumentProvider(new XMLDocumentProvider(this));
	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public static final String CONTENTASSIST_PROPOSAL_ID = "org.mevenide.ui.eclipse.editors.mavenxml.ContentAssistProposal";
	
	protected void createActions() {
		super.createActions();
		
		IAction action = new TextOperationAction(
				Mevenide.getInstance().getResourceBundle(), 
				"MavenXml.ContentAssistProposal", 
				this, 
				ISourceViewer.CONTENTASSIST_PROPOSALS);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		
		setAction(CONTENTASSIST_PROPOSAL_ID, action);
		
	}

    public Map getNamespaces() {
        return namespaces;
    }
    
    public void setNamespaces(Map namespaces) {
        this.namespaces = namespaces;
    }
    public XMLReconciler getModel() {
        return reconciler;
    }
    public void setModel(XMLReconciler reconciler) {
        this.reconciler = reconciler;
    }
    
}
