/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.editors.jelly.contentassist;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.part.FileEditorInput;
import org.mevenide.grammar.AttributeCompletion;
import org.mevenide.grammar.impl.EmptyAttributeCompletionImpl;
import org.mevenide.grammar.impl.GoalsAttributeCompletionImpl;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.jelly.AbstractJellyEditor;
import org.mevenide.ui.eclipse.editors.jelly.ITypeConstants;
import org.mevenide.ui.eclipse.editors.jelly.Namespace;
import org.mevenide.ui.eclipse.editors.jelly.XMLNode;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;

/**
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet </a>
 * @version $Id: MavenXmlContentAssistProcessor.java,v 1.1 18 avr. 2004 Exp gdodinet
 *  
 */
public abstract class JellyContentAssistProcessor implements IContentAssistProcessor {

    private static final  Log log = LogFactory.getLog(JellyContentAssistProcessor.class);
    
    private AbstractJellyEditor editor;

    private PreferencesManager preferencesManager;
    
    private AttributeCompletion attributeCompletor ; 
    
    public JellyContentAssistProcessor(AbstractJellyEditor editor) {
        this.editor = editor;
        preferencesManager = PreferencesManager.getManager();
        preferencesManager.loadPreferences();
        try {
            attributeCompletor = new GoalsAttributeCompletionImpl();
            String basedir = new File(((FileEditorInput) editor.getEditorInput()).getFile().getLocation().toOSString()).getParent();
            setBasedir(basedir);
        }
        catch (Exception e) {
            log.error("unable to create GoelasAttributeCompletionImpl", e);
            attributeCompletor = new EmptyAttributeCompletionImpl("NullCompletor");
        }
    }

	public void setBasedir(String basedir) {
		if ( attributeCompletor instanceof GoalsAttributeCompletionImpl ) {
			((GoalsAttributeCompletionImpl) attributeCompletor).setBasedir(basedir);
		}
	}
	
    protected XMLNode getNodeAt(IDocument doc, int offset) {
        try {
            Position[] pos = doc.getPositions("__content_types_category");
            for (int i = 0; i < pos.length; i++) {
                if (offset >= pos[i].getOffset() && offset <= pos[i].getOffset() + pos[i].getLength()) {
                    return (XMLNode) pos[i];
                }
            }
        }
        catch (BadPositionCategoryException e) {
        }
        return null;
    }

    private int getIndexOf(Position[] pos, XMLNode node) {
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] == node) {
                return i;
            }
        }
        return -1;
    }

    protected ICompletionProposal[] computeTags(IDocument doc, XMLNode node, int offset) {
        ICompletionProposal[] cp = null;
        Map namespaces = editor.getNamespaces();
        Collection words = new ArrayList();
        String start = node == null ? "" : node.getContentTo(offset);
        String outerTag = null;
        XMLNode lastOpenTag = findLastOpenTag(doc, node);
        if (lastOpenTag == null || lastOpenTag == editor.getModel().getRoot()) {
            outerTag = Namespace.TOPLEVEL;
        }
        else {
            outerTag = lastOpenTag.getName();
        }
        
        Collection rootTags = new TreeSet();
        Collection subTags = new TreeSet();
        if (namespaces != null) {
            for (Iterator it = namespaces.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                
                Namespace ns = (Namespace) namespaces.get(key);
                
                Collection nsSubTags = null;
                
	            //collect sub tags that can only appear under outerTag  
	            nsSubTags = ns.getSubTags(outerTag);
	          
                if (nsSubTags != null) {
                    subTags.addAll(nsSubTags);
                }
                
                Collection nsRootTags = null;
                
                //if ( ns.isGeneric() && !Namespace.TOPLEVEL.equals(outerTag) ) {
                    //collect root tags that can be used everywhere
                    nsRootTags = ns.getRootTags();
                //}
                if ( nsRootTags != null ) {
                	rootTags.addAll(nsRootTags);
                } 
            }
        }
        
        //sort the subtags
        //Collections.sort(new ArrayList(words));
        
        words.addAll(subTags);
     
        //add rootTags after subtags
        rootTags.removeAll(subTags);
        words.addAll(rootTags);

        if ( isProjectTagSet() ) {
            words.remove("project");
        }
       
        if (node != null && node.getType() != null && "TEXT".equalsIgnoreCase(node.getType())) {
            
            cp = new ICompletionProposal[words.size()];
            //for (int i = 0; i < cp.length; i++) {
            int i = 0;
            for (Iterator it = words.iterator(); it.hasNext();) {
                String text = (String) it.next();
                if (preferencesManager.getBooleanValue("InsertEndTag")) {
                    cp[i] = new CompletionProposal("<" + text + "></" + text + ">", offset, 0, text.length() + 2, Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_TAG_OBJ), text,
                            null, null);
                }
                else {
                    cp[i] = new CompletionProposal("<" + text + ">", offset, 0, text.length() + 2, Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_TAG_OBJ), text, null, null);
                }
                i++;
            }
        }
        else {
            
            if (start.length() == 0) {
                boolean isAfterLesserThan = false;
                cp = new ICompletionProposal[words.size()];
                if (offset > 0) {
                    try {
                        isAfterLesserThan = "<".equals(doc.get(offset - 1, 1));
                    }
                    catch (BadLocationException e) {
                    }
                }
                
                //for (int i = 0; i < cp.length; i++) {
                int i = 0;
                for ( Iterator it = words.iterator(); it.hasNext();) {
                    String text = (String) it.next();
                    
                    if (preferencesManager.getBooleanValue("InsertEndTag")) {
                        if (isAfterLesserThan) {
                            cp[i] = new CompletionProposal(text, offset, 0, text.length() + 1, Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_TAG_OBJ), text,
                                    null, null);
                        }
                        else {
                            cp[i] = new CompletionProposal("<" + text + "></" + text + ">", offset, 0, text.length() + 2, Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_TAG_OBJ),
                                    text, null, null);
                        }
                    }
                    else {
                        cp[i] = new CompletionProposal(text, offset, 0, text.length(), Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_TAG_OBJ), text, null, null);
                    }
                    
                    i++;
                }
            }
            else {
                
                if (start.startsWith("/")) {
                    
                    if (lastOpenTag != editor.getModel().getRoot() && lastOpenTag.getName().startsWith(start.substring(1))) {
                        cp = new CompletionProposal[1];
                        XMLNode currentNode = getNodeAt(doc, offset);
                        char endChar = '#';
                        try {
                            endChar = doc.getChar(node.getOffset() + currentNode.getName().length() + 2);
                        }
                        catch (BadLocationException e) {
                            String message = "Unable to get endChar"; 
                            log.error(message, e);
                        }
                       
                        cp[0] = new CompletionProposal(lastOpenTag.getName() + (endChar != '>' ? ">" : ""), 
                                                       node.getOffset() + 2, 
                                                       lastOpenTag.getName().startsWith(currentNode.getName()) ? currentNode.getName().length() : currentNode.getOffset() - offset + 2, 
                                                       lastOpenTag.getName().length() + 1, 
                                                       Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_END_TAG_OBJ), 
                                                       "</" + lastOpenTag.getName() + ">", 
                                                       null, 
                                                       null);
                    }
                }
                else {
                    ArrayList cpL = new ArrayList();
                    int i = 0;
                    for (Iterator it = words.iterator(); it.hasNext();) {
                    //for (int i = 0; i < words.size(); i++) {
                        String text = (String) it.next();
                        //if (text.startsWith(start)) {
                        if (text.regionMatches(true, 0, start, 0, start.length())) {
                            //if (preferencesManager.getBooleanValue("InsertEndTag")) {
                            
                                cpL.add(new CompletionProposal(text, node.getOffset() + 1, node.getName().length(), text.length(), Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_TAG_OBJ), text, null, null));
                            //}
                            //else {
                            //    cpL.add(new CompletionProposal(text, node.getOffset() + 1, offset - node.getOffset() - 1, text .length()));
                            //}
                        }
                        i++;
                    }
                    cp = new ICompletionProposal[cpL.size()];
                    for (int u = 0; u < cp.length; u++) {
                        cp[u] = (ICompletionProposal) cpL.get(u);
                    }
                }
            }
        }
       
        return cp;
    }
    
    

    private XMLNode findLastOpenTag(IDocument doc, XMLNode node) {
        XMLNode lastOpenTag = null;
        if (node == null || node.getParent() == null) {
            try {
                Position[] pos = doc.getPositions("__content_types_category");
                if (pos.length > 0) {
                    if (ITypeConstants.TAG.equals(((XMLNode) pos[pos.length - 1]).getType())) {
                        lastOpenTag = (XMLNode) pos[pos.length - 1];
                    }
                    else {
                        lastOpenTag = ((XMLNode) pos[pos.length - 1]).getParent();
                    }
                }
            }
            catch (BadPositionCategoryException e) {
            }
        }
        else if (ITypeConstants.ENDTAG.equals(node.getType())) {
            try {
                Position[] pos = doc.getPositions("__content_types_category");
                int index = getIndexOf(pos, node);
                if (index > 0) {
                    if (ITypeConstants.TAG.equals(((XMLNode) pos[index - 1]).getType())) {
                        lastOpenTag = (XMLNode) pos[index - 1];
                    }
                    else {
                        lastOpenTag = ((XMLNode) pos[index - 1]).getParent();
                    }
                }
            }
            catch (BadPositionCategoryException e) {
            }
        }
        else {
            lastOpenTag = node.getParent();
        }
        return lastOpenTag;
    }

    private boolean isProjectTagSet() {
        List children = editor.getModel().getRoot().getChildren();
        for ( int y = 0; y < children.size(); y++) {
            if ( "project".equals(((XMLNode) children.get(y)).getName()) ) {
                return true;
            }
        }
        return false;
    }
    
    private String getAttributeStart(XMLNode node, int offset) {
        String start = "";
        String content = node.getContentTo(offset);
        int index = content.length() - 1;
        while (index >= 0 && !Character.isWhitespace(content.charAt(index))) {
            start = content.charAt(index) + start;
            index--;
        }
        return start;
    }

    protected ICompletionProposal[] computeAttributes(IDocument doc, XMLNode node, int offset) {
        List attrs = node.getAttributes();
        List nodeAttributes = new ArrayList(attrs.size());
        for (Iterator iter = attrs.iterator(); iter.hasNext();) {
            XMLNode element = (XMLNode) iter.next();
            nodeAttributes.add(element.getName());
        }
        
        ICompletionProposal[] cp = null;
        List words = new ArrayList();
        Set collectedWords = new TreeSet();
        
        Map namespaces = editor.getNamespaces();
        
        String start = getAttributeStart(node, offset);
        if (namespaces != null) {
            for (Iterator it = namespaces.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                Namespace ns = (Namespace) namespaces.get(key);
                Map attributeMap = ns.getAttributes();
                List candidates = (List) attributeMap.get(node.getName());
                if (candidates != null) {
                    collectedWords.addAll(candidates);
                }
            }
        }
        
        collectedWords.removeAll(nodeAttributes);
        words.addAll(collectedWords);
        
        Collections.sort(words);
        for (Iterator it = words.iterator(); it.hasNext();) {
            String s = (String) it.next();
            //if (!s.startsWith(start)) {
            if (!s.regionMatches(true, 0, start, 0, start.length())) {
                it.remove();
            }
        }
        cp = new ICompletionProposal[words.size()];
        for (int i = 0; i < cp.length; i++) {
            String displayText = (String) words.get(i) ;
            String text = displayText ;//+ "=\"\"";
            //IContextInformation contextInformation = createAttributeContextInformation(node.getName(), displayText);
            cp[i] = new CompletionProposal(text, offset - start.length(), start.length(), text.length(), Mevenide.getInstance().getImageRegistry().get(IImageRegistry.XML_ATTR_OBJ), displayText, null, null);
        }
        return cp;
    }

    private String getAttributeValueStart(XMLNode node, int offset, char quote) {
        String start = "";
        String content = node.getContentTo(offset);
        int index = content.length() - 1;
        if (quote == '\0') {
            while (index >= 0 && !Character.isWhitespace(content.charAt(index)) && content.charAt(index) != '=') {
                start = content.charAt(index) + start;
                index--;
            }
        }
        else {
            while (index >= 0 && content.charAt(index) != quote) {
                start = content.charAt(index) + start;
                index--;
            }
        }
        return start;
    }

    protected ICompletionProposal[] computeAttributeValues(IDocument doc, XMLNode node, int offset, char quote) {
        ICompletionProposal[] cp = null;
        List words = new ArrayList();
        String start = getAttributeValueStart(node, offset, quote);
        XMLNode attribute = node.getAttributeAt(offset);
		
		XMLNode parentNode = getNodeAt(doc, offset);
		
        //if ( Namespace.getWerkzList().contains(parentNode.getName()) && !("goal".equals(parentNode.getName()) && !"prereqs".equals(attribute.getName())) || "attainGoal".equals(parentNode.getName()) ) {
        if ( Namespace.getWerkzList().contains(parentNode.getName()) && !"goal".equals(parentNode.getName()) || "attainGoal".equals(parentNode.getName()) ) {
	        words = new ArrayList(attributeCompletor.getValueHints(start));
        }
        
        Collections.sort(words);
        for (Iterator it = words.iterator(); it.hasNext();) {
            String s = (String) it.next();
            if (!s.startsWith(start)) {
                it.remove();
            }
        }
        cp = new ICompletionProposal[words.size()];
        for (int i = 0; i < cp.length; i++) {
            String text = (String) words.get(i);
            if (quote == '\'' || quote == '"') {
                boolean hasComma = false;
                int replacementLength = 0;
                if ( attribute.getContent().indexOf(',') > -1 ) {
                    replacementLength = attribute.getContent().indexOf(',');
                    hasComma = true;
                }
                else if ( attribute.getContent().indexOf(' ') > -1 ) {
                    replacementLength = attribute.getContent().indexOf(' ');
                }
                else {
                    replacementLength = attribute.getContent().length();
                }
                
                cp[i] = new CompletionProposal(text + (hasComma ? "" : "" + quote), 
                                               offset - start.length(), 
                                               replacementLength - 6, 
                                               text.length() + 1, 
                                               Mevenide.getInstance().getImageRegistry().get(IImageRegistry.GOAL_OBJ),
                                               text, 
                                               null, 
                                               null);
            }
            else {
                cp[i] = new CompletionProposal('"' + text + '"', 
                                               offset - start.length(), 
                                               0, 
                                               text.length() + 2,
                                               Mevenide.getInstance().getImageRegistry().get(IImageRegistry.GOAL_OBJ), 
                                               text, 
                                               null, 
                                               null);
            }
        }
        return cp;
    }

    protected String normalizeWhitespaces(String s) {
        return s.replace((char) 10, ' ');
    }

    /*
     * @see IContentAssistProcessor#computeContextInformation(ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
        return null;
    }

    /*
     * @see IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    /*
     * @see IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage() {
        //        System.out.println("No completions available.");
        return "No completions available.";
    }

    /*
     * @see IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

    protected AbstractJellyEditor getEditor() {
        return editor;
    }
}