/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.grammar;

import java.awt.Component;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.spi.dom.AbstractNode;
import org.openide.util.Enumerations;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 */
public class MavenProjectGrammar implements GrammarQuery {
 
   private static Log logger = LogFactory.getLog(MavenProjectGrammar.class); 

   private Document schemaDoc3;
   private Document schemaDoc4;
   
   
   public MavenProjectGrammar()
   {
       try {
           SAXBuilder builder = new SAXBuilder();
           InputStream stream = getClass().getResourceAsStream("/org/mevenide/netbeans/grammar/resources/maven-project-3.xsd");
           logger.debug("stream is not null=" + (stream != null));
           schemaDoc3 = builder.build(stream);
           stream = getClass().getResourceAsStream("/org/mevenide/netbeans/grammar/resources/maven-project-4.xsd");
           schemaDoc4 = builder.build(stream);
       } catch (Exception exc)
       {
           logger.error("cannot read schema for maven project", exc);
       }
   }
   
    public Component getCustomizer(HintContext nodeCtx)
    {
        return null;
    }    
   /**
     * Allows Grammars to supply properties for the HintContext
     * @param ctx the hint context node
     * @return an array of properties for this context
     */        
    public org.openide.nodes.Node.Property[] getProperties(HintContext nodeCtx)
    {
        return new org.openide.nodes.Node.Property[0];
    }
    
    public boolean hasCustomizer(HintContext nodeCtx)
    {
        return false;
    }
   /**
     * Distinquieshes between empty enumaration types.
     * @return <code>true</code> there is no known result 
     *         <code>false</code> grammar does not allow here a result
     */    
    public boolean isAllowed(Enumeration en)
    {
        return true;
    }
    
    /**
     * Query attribute options for given context. All implementations must handle
     * queries based on owner element context.
     * @stereotype query
     * @output list of results that can be queried on name, and attributes
     * @time Performs fast up to 300 ms. 
     * @param ownerElementCtx represents owner <code>Element</code> that will host result.
     * @return enumeration of <code>GrammarResult</code>s (ATTRIBUTE_NODEs) that can be queried on name, and attributes.
     *         Every list member represents one possibility.  
     */    
    public Enumeration queryAttributes(HintContext ownerElementCtx)
    {
        return Enumerations.empty();
    }
   /**
     * query default value for given context. Two context types must be handled:
     * an attribute and an element context.
     * @param parentNodeCtx context for which default is queried
     * @return default value or <code>null</code>
     */    
    public GrammarResult queryDefault(HintContext parentNodeCtx)
    {
        return null;
    }
    /**
     * @semantics Navigates through read-only Node tree to determine context and provide right results.
     * @postconditions Let ctx unchanged
     * @time Performs fast up to 300 ms.
     * @stereotype query
     * @param virtualElementCtx represents virtual element Node that has to be replaced, its own attributes does not name sense, it can be used just as the navigation start point.
     * @return enumeration of <code>GrammarResult</code>s (ELEMENT_NODEs) that can be queried on name, and attributes.
     *         Every list member represents one possibility.  
     */    
    public Enumeration queryElements(HintContext virtualElementCtx)
    {
        String start = virtualElementCtx.getCurrentPrefix();
        logger.debug("start=" + start);
        
        Document schemaDoc = (findPomVersion(virtualElementCtx) == 4 ? schemaDoc4 : schemaDoc3);
        Node parentNode = virtualElementCtx.getParentNode();
        if (parentNode != null && schemaDoc != null)
        {
            String parentName = parentNode.getNodeName();
            logger.debug("parent name=" + parentName);
            org.jdom.Element schemaRoot = schemaDoc.getRootElement();
            List content = schemaRoot.getContent(new RootDefinitionElementFilter(parentName));
            logger.debug("returned items=" + content.size());
            Vector toReturn = new Vector();
            Iterator it = content.iterator();
            while (it.hasNext())
            {
                // should be just one..
                org.jdom.Element parentEl = (org.jdom.Element)it.next();
                org.jdom.Element seq = findSequenceElement(parentEl);
                if (seq != null)
                {
                    processSequence(start, seq, toReturn, schemaRoot);
                } else {
                    logger.warn("no complexType/sequence subelements defined in the found element");
                }
            }
            return toReturn.elements();
        } else {
            return Enumerations.empty();
        }
    }
    
    private org.jdom.Element findSequenceElement(org.jdom.Element parent) 
    {
        logger.debug("findSequence parent name=" + parent.getAttributeValue("name"));
        logger.debug("findSequence parent content size=" + parent.getChildren().size());
        org.jdom.Element complex = parent.getChild("complexType", parent.getNamespace());
        logger.debug("findSequence complex found" + (complex != null));
        if (complex != null) {
            return  complex.getChild("sequence", parent.getNamespace()); //NOI18N
        } 
        return null;
    }
    
    private void processElement(String matches, org.jdom.Element childEl, Vector suggestions, org.jdom.Element rootSchemaElement)
    {
        if (childEl.getName().equals("element")) //NOI18N
        {
            String childRefAttr = childEl.getAttributeValue("ref"); //NOI18N
            logger.debug("child ref attr=" + childRefAttr);
            if (childRefAttr == null)
            {
                // if ref not defined, go check name attribute..
                childRefAttr = childEl.getAttributeValue("name");
            }
            if (childRefAttr != null && childRefAttr.startsWith(matches))
            {
                suggestions.add(new MyElement(childRefAttr));
            }
        } else if (childEl.getName().equals("group"))
        {
            String grName = childEl.getAttributeValue("ref");
            if (grName != null) {
                List groupList = rootSchemaElement.getChildren("group", rootSchemaElement.getNamespace());
                Iterator it = groupList.iterator();
                boolean found = false;
                while (it.hasNext())
                {
                    org.jdom.Element grEl = (org.jdom.Element)it.next();
                    String grId = grEl.getAttributeValue("name");
                    if (grId != null && grId.equals(grName))
                    {
                        processGroup(matches, grEl, suggestions, rootSchemaElement);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    logger.warn("No schema group matches " + grName);
                }
            } else {
                logger.warn("No schema group reference");
                //TODO what to do if it's not a ref?
            }
        }
        
    }

    private void processGroup(String matches, org.jdom.Element groupEl, Vector suggestions, org.jdom.Element rootSchemaElement)
    {
        org.jdom.Element seq = groupEl.getChild("sequence", groupEl.getNamespace());
        if (seq != null) {
            processSequence(matches, seq, suggestions, rootSchemaElement);
        }
    }
    
    private void processSequence(String matches, org.jdom.Element seqEl, Vector suggestions, org.jdom.Element rootSchemaElement)
    {
        List availables = seqEl.getContent(new DefinitionContentElementFilter());
        logger.debug("content size=" + availables.size());
        Iterator availIt = availables.iterator();
        while (availIt.hasNext())
        {
            org.jdom.Element childEl = (org.jdom.Element)availIt.next();
            processElement(matches, childEl, suggestions, rootSchemaElement);
        }
    }

    
    /**
     * Allow to get names of <b>parsed general entities</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (ENTITY_REFERENCE_NODEs)
     */    
    public Enumeration queryEntities(String prefix)
    {
        return Enumerations.empty();
    }
    /**
     * Allow to get names of <b>declared notations</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (NOTATION_NODEs)
     */        
    public Enumeration queryNotations(String prefix)
    {
        return Enumerations.empty();
    }
    
    public Enumeration queryValues(HintContext virtualTextCtx)
    {
        return Enumerations.empty();
    }
    
    /**
     * TODO for some reason the pomversion element in the dom model, doens't contain the text value :(
     * so cannot retrieve the current pomversion to have different code completion
     *
     */
    private int findPomVersion(HintContext virtualElementCtx)
    {
        Node rootNode = null;
        // pomversion 3 is the default..
        int toReturn = 3;
//        Node current = virtualElementCtx.getParentNode();
//        while (current != null) {
//            if (current.getNodeName() != null && current.getNodeName().equals("project")) {
//                break;
//            }
//            logger.debug("findPomVersion:curr rootNode is=" + current.getNodeName());
//            current = current.getParentNode();
//        }
//        rootNode = current;
//        if (rootNode != null) {
//            logger.debug("findPomVersion:rootNode is=" + rootNode.getNodeName());
//            Node child = rootNode.getFirstChild();
//            while (child != null && !"pomVersion".equals(child.getNodeName())) {
//                child = child.getNextSibling();
//            }
//            if (child != null) {
//                org.w3c.dom.Element childEl = (org.w3c.dom.Element)child;
//                logger.debug("findPomVersion:has pomVersion subNode=" + child);
//                logger.debug("findPomVersion:pomversion content size=" + child.getChildNodes().getLength());
//                Node pomNodeContent = child.getChildNodes().getLength() > 0 ? child.getChildNodes().item(0) : null;
//                if (pomNodeContent != null && pomNodeContent instanceof Text) {
//                    // this is the text for the pom version?
//                    String pomVersion = pomNodeContent.getNodeValue();
//                    logger.debug("findPomVersion:getting text for pomversion=" + pomVersion);
//                    if (pomVersion != null) {
//                        try {
//                            toReturn = Integer.parseInt(pomVersion.trim());
//                        } catch (NumberFormatException exc)
//                        {
//                            logger.info("pom version text cannot be converted to number", exc);
//                        }
//                    }
//                }
//            }
//        }
        return toReturn;
    }
    
    private static abstract class AbstractResultNode extends AbstractNode implements GrammarResult {
        
        public Icon getIcon(int kind) {
            return null;
        }
        
        /**
         * @output provide additional information simplifiing decision
         */
        public String getDescription() {
            return getNodeName() + " desc";
        }
        
        /**
         * @output text representing name of suitable entity
         * //??? is it really needed
         */
        public String getText() {
            return getNodeName();
        }
        
        /**
         * @output name that is presented to user
         */
        public String getDisplayName() {
            return getNodeName() + " disp";
        }
        
    /**
     * For elements provide hint whether element has empty content model.
     * @return true element has empty content model (no childs) and can
     * be completed in empty element form i.e. <code>&lt;ement/></code>.
     * @since 6th Aug 2004
     */
        public boolean isEmptyElement() {
            return false;
       }
        
    }
    
    private static class MyElement extends AbstractResultNode implements Element {
        
        private String name;
        
        MyElement(String name) {
            this.name = name;
        }
        
        public short getNodeType() {
            return Node.ELEMENT_NODE;
        }
        
        public String getNodeName() {
            return name;
        }
        
        public String getTagName() {
            return name;
        }
        
    }    
    
    private class RootDefinitionElementFilter extends ElementFilter
    {
        private String text;
        public RootDefinitionElementFilter(String name) {
            text = name;
        }
        
        public boolean matches(Object obj)
        {
            boolean toReturn = super.matches(obj);
            if (toReturn)
            {
                org.jdom.Element el = (org.jdom.Element)obj;
                toReturn = false;
                if ("element".equals(el.getName()))
                {
                    String elName = el.getAttributeValue("name");
//                    logger.debug("it's name is=" + elName);
                    if (elName != null && text.equals(elName)) {
//                        logger.debug("including this one");
                        toReturn = true;
                    }
                }
            }
            return toReturn;
        }
        
    }
    
    /**
     * filters out the child elements that are of of type 'element" or "group".
     */
    private class DefinitionContentElementFilter extends ElementFilter
    {
        public DefinitionContentElementFilter() {
        }
        
        public boolean matches(Object obj)
        {
            boolean toReturn = super.matches(obj);
            if (toReturn)
            {
                org.jdom.Element el = (org.jdom.Element)obj;
                toReturn = false;
                if ("element".equals(el.getName()) || "group".equals(el.getName())) //NOI18N
                {
                        toReturn = true;
                }
            }
            return toReturn;
        }
        
    }
    
}
