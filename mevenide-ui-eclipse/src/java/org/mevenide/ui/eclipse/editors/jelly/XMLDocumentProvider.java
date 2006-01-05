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