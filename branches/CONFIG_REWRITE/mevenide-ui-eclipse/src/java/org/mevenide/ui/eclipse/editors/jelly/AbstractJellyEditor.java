package org.mevenide.ui.eclipse.editors.jelly;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.mevenide.ui.eclipse.Mevenide;


public abstract class AbstractJellyEditor extends TextEditor {
    private static final Log log = LogFactory.getLog(AbstractJellyEditor.class);
    
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
