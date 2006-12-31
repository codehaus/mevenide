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
public class TagContentAssistProcessor extends JellyContentAssistProcessor {

    public TagContentAssistProcessor(AbstractJellyEditor editor) {
        super(editor);
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        if (getEditor() == null) { // || editor.getNamespaces() == null) {
            return new ICompletionProposal[0];
        }
        if (offset == 0) {
            String word = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            ICompletionProposal[] cp = new ICompletionProposal[1];
            cp[0] = new CompletionProposal(word, offset, 0, word.length(), null, word, null, null);
            return cp;
        }
        XMLNode node = null;
        char quote = '\0';
        node = getNodeAt(viewer.getDocument(), offset);
        int state = node.getStateAt(offset);
        switch (state) {
            case XMLNode.ATTRIBUTE :
                return computeAttributes(viewer.getDocument(), node, offset);
            case XMLNode.TAG :
                return computeTags(viewer.getDocument(), node, offset);
            case XMLNode.SINGLEQUOTE :
                quote = '\'';
            case XMLNode.DOUBLEQUOTE : 
                quote = '"';
            case XMLNode.ATT_VALUE :
                return computeAttributeValues(viewer.getDocument(), node, offset, quote);
        }
        return computeTags(viewer.getDocument(), node, offset);
    }

    public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[]{' ', '/', '<', '=', '\'', '"'};
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