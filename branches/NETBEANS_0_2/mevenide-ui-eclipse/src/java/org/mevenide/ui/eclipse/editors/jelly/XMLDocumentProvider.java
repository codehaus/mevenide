package org.mevenide.ui.eclipse.editors.jelly;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.ui.eclipse.goals.outline.MavenXmlOutlinePage;

public class XMLDocumentProvider extends FileDocumentProvider {

    private AbstractJellyEditor editor;
	private IDocument document;
	private static RuleBasedPartitionScanner scanner;

	
	public XMLDocumentProvider(AbstractJellyEditor editor) {
        super();
        this.editor = editor;
    }
	
	protected IDocument createDocument(Object element) throws CoreException {
		document = super.createDocument(element);
        if (document != null) {
            if (scanner == null) {
                scanner = new RuleBasedPartitionScanner();
                scanner.setPredicateRules(new IPredicateRule[]{new TagRule()});
            }
            IDocumentPartitioner partitioner = new XMLDocumentPartitioner(scanner, ITypeConstants.TYPES);
            if (partitioner != null) {
                partitioner.connect(document);
                document.setDocumentPartitioner(partitioner);
            }
            XMLReconciler rec = new XMLReconciler(editor, (MavenXmlOutlinePage) editor.getAdapter(IContentOutlinePage.class));
            editor.setModel(rec);
            rec.createTree(document);
            document.addDocumentListener(rec);
        }
        return document;
	
	}
    
}