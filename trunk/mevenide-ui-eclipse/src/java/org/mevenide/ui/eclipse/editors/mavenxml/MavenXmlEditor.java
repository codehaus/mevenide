package org.mevenide.ui.eclipse.editors.mavenxml;

import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.ui.eclipse.goals.outline.MavenXmlOutlinePage;

public class MavenXmlEditor extends TextEditor {

	private ColorManager colorManager;
	private MavenXmlOutlinePage outlinePage;
	
	public MavenXmlEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
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
}
