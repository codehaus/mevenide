/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.codehaus.mevenide.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.mevenide.properties.Comment;
import org.mevenide.properties.Element;
import org.mevenide.properties.KeyValuePair;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DefaultElementHandler implements IElementHandler {

	//jdom way of dealing with dtd
    private static final String DOCTYPE = "\t<!ELEMENT plugin (property*)>\n" +
						         		  "\t<!ATTLIST plugin name CDATA #IMPLIED> \n" +
						                  "\t<!ATTLIST property \n" + 
						                  "\t          name CDATA #REQUIRED \n" +
						                  "\t          label CDATA #IMPLIED \n" +
						                  "\t          default CDATA #IMPLIED \n" + 
						                  "\t          required (true|false) \"false\" \n" +
						                  "\t          description CDATA #IMPLIED \n" + 
						                  "\t          validator CDATA #IMPLIED \n" +
						                  "\t          validate (true|false) \"true\" \n" + 
						                  "\t          scope (project|global) \"project\" \n" +
						    			  "\t          category CDATA #IMPLIED>\n";
    
    private static final String INDENT = "    "; //$NON-NLS-1$
	private static final String DEFAULT_ATTR = "default"; //$NON-NLS-1$
	private static final String DESCRIPTION_ATTR = "description"; //$NON-NLS-1$
	private static final String NAME_ATTR = "name"; //$NON-NLS-1$
	private static final String PROPERTY_ELEMENT = "property"; //$NON-NLS-1$
	private static final String PLUGIN_ELEMENT = "plugin"; //$NON-NLS-1$

	//stack of the comments found so far
    private Stack contextStack = new Stack(); 

    //commentStack is copied to backupStack before being cleared
    //thus giving us a chance to categorize properties
    private Stack backupStack;
    
    //map hashed by the KeyValuePair
    private Map propertyMap = new HashMap();
    
    //map hashed by the KeyValuePair - should surely be possible to compute categories from # ---- comments
    //private Map categoryMap = new HashMap();
    
    
    public void handle(Element element) {
        if ( element instanceof Comment ) {
            handleComment((Comment) element);
        }
        if ( element instanceof KeyValuePair ) {
            handleKeyValuePair((KeyValuePair) element);
        }
    }

    private void handleKeyValuePair(KeyValuePair pair) {
        String associatedComment = ""; //$NON-NLS-1$
        while ( !contextStack.isEmpty() ) {
            Comment comment = (Comment) contextStack.pop();
            if ( comment.getValue().length() >= 2 ) {
                associatedComment = comment.getValue().substring(1) + associatedComment;
            }
        }
        propertyMap.put(pair, associatedComment);
    }


    private void handleComment(Comment comment) {
        if ( comment.getValue().startsWith("# ---") ) { //$NON-NLS-1$
            if ( contextStack.isEmpty() ) {
                contextStack.push(comment);
            }
            else {
                contextStack.clear();
            }
        }
        else {
            contextStack.push(comment);
        }
    }

    public String getXmlDescription() {
        Document document = new Document();
        
        setDocType(document);
        
        org.jdom.Element root = new org.jdom.Element(PLUGIN_ELEMENT);
        document.setRootElement(root);
        
        for (Iterator it = propertyMap.keySet().iterator(); it.hasNext();) {
            KeyValuePair pair = (KeyValuePair) it.next();
            String description = (String) propertyMap.get(pair);
            
            org.jdom.Element property = new org.jdom.Element(PROPERTY_ELEMENT);
            property.setAttribute(NAME_ATTR, pair.getKey());
            property.setAttribute(DESCRIPTION_ATTR, description != null ? description.trim() : "");
            property.setAttribute(DEFAULT_ATTR, pair.getValue());
            root.addContent(property);
        }
        
        return getString(document);
        
    }

	private void setDocType(Document document) {
        DocType doctype = new DocType(PLUGIN_ELEMENT);
        doctype.setInternalSubset(DOCTYPE);
        document.setDocType(doctype);
    }

    private String getString(Document document) {
	    XMLOutputter outputter = new XMLOutputter();
		outputter.setIndent(INDENT);
		outputter.setExpandEmptyElements(false);
		outputter.setNewlines(true);
		return outputter.outputString(document);
	}
}
