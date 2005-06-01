/*
 * Created on 04.08.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.editors.jelly.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.mevenide.ui.eclipse.editors.jelly.AbstractJellyEditor;
import org.mevenide.ui.eclipse.editors.jelly.XMLNode;

/**
 * @author jll
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TextContentAssistProcessor extends JellyContentAssistProcessor {

    /**
     * @param editor
     */
    public TextContentAssistProcessor(AbstractJellyEditor editor) {
        super(editor);
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        if (getEditor() == null) {
            return new ICompletionProposal[0];
        }
        if (offset == 0) {
            String word = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            ICompletionProposal[] cp = new ICompletionProposal[1];
            cp[0] = new CompletionProposal(word, offset, 0, word.length(), null, word, null, null);
            return cp;
        }
        XMLNode node = null;
        node = getNodeAt(viewer.getDocument(), offset);
        return computeTags(viewer.getDocument(), node, offset);
    }

    public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        char[] c = new char[2];
        c[0] = ':';
        c[1] = '<';
        return c;
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    public String getErrorMessage() {
        return "No completions available.";
    }

    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }
}