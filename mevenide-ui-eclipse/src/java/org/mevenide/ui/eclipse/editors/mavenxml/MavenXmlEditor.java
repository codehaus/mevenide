package org.mevenide.ui.eclipse.editors.mavenxml;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.outline.MavenXmlOutlinePage;

public class MavenXmlEditor extends TextEditor {

	private ColorManager colorManager;
	private MavenXmlOutlinePage outlinePage;
	
	public MavenXmlEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager, this));
		setDocumentProvider(new XMLDocumentProvider());
	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outlinePage == null) {
				outlinePage= new MavenXmlOutlinePage((IFileEditorInput) getEditorInput());
			}
			return outlinePage;
		}
		return super.getAdapter(required);
	}
	
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		super.performSave(overwrite, progressMonitor);
		outlinePage.forceRefresh();
	}
	
	public static final String CONTENTASSIST_PROPOSAL_ID = "org.mevenide.ui.eclipse.editors.mavenxml.ContentAssistProposal";
	
	protected void createActions() {
		super.createActions();
		
		IAction action = new TextOperationAction(
				Mevenide.getPlugin().getResourceBundle(), 
				"MavenXml.ContentAssistProposal", 
				this, 
				ISourceViewer.CONTENTASSIST_PROPOSALS);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		
		setAction(CONTENTASSIST_PROPOSAL_ID, action);
		
	}


}
