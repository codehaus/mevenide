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

package org.mevenide.netbeans.grammar;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.spi.dom.AbstractNode;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.Utilities;
import org.openide.util.enum.EmptyEnumeration;
import org.openide.util.enum.SingletonEnumeration;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 */
public class MavenJellyGrammar implements GrammarQuery {
 
   private static Log logger = LogFactory.getLog(MavenJellyGrammar.class); 

   private Map tagLibs;
   
   public MavenJellyGrammar()
   {
       configure();
   }
  
    /**
     * This method is called only once before parsing occurs
     * which allows tag libraries to be registered and so forth
     * TODO - for performance reasons later read just the files that are used in the context.
     */
    protected void configure() 
    {
        tagLibs = new TreeMap();
        FileObject tagLibFolder = Repository.getDefault().getDefaultFileSystem().findResource("Plugins/Mevenide-Grammar");
        FileObject[] libFOs = tagLibFolder.getChildren();
        if (libFOs != null)
        {
            for (int i = 0; i < libFOs.length; i++)
            {
                try {
                    TagLib lib = new TagLib(libFOs[i].getInputStream());
                    tagLibs.put(lib.getName(), lib);
                } catch (Exception exc)
                {
                    logger.error("cannot read config file:" + libFOs[i].getPath(), exc);
                }
            }
        }
    }

    private Node findRootNode(HintContext virtualElementCtx) 
    {
        Node current = virtualElementCtx;
        while (current != null) 
        {
            if (current.getNodeName() != null && current.getNodeName().equals("project")) 
            {
                break;
            }
            logger.debug("findRootNode:curr rootNode is=" + current.getNodeName());
            current = current.getParentNode();
        }
        return current;
    }

    /**
     * will create a mapping table for 
     */
    private Map getNameSpaceMappings(Node projectNode)
    {
        HashMap toReturn = new HashMap();
        NamedNodeMap map = projectNode.getAttributes();
        if (map != null) 
        {
            for (int i = 0; i < map.getLength(); i++) 
            {
                Node attrNode = map.item(i);
                String attrName = attrNode.getNodeName();
                if (attrName.startsWith("xmlns:")) 
                {
                    toReturn.put(attrName.substring("xmlns:".length()), attrNode.getNodeValue());
                    logger.debug("namespace name=" + attrName.substring("xmlns:".length()) + " value=" + attrNode.getNodeValue());
                }
            }
        }
        return toReturn;
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
        String start = ownerElementCtx.getCurrentPrefix();
        logger.debug("start=" + start);
        Vector toReturn = new Vector();
        String elName = ownerElementCtx.getNodeName();
        int separ = elName.indexOf(':');
        if (separ > 0) 
        {
            //it's a namespace element..
            String ns = elName.substring(0, separ);
            String tag = (separ == elName.length() ? "" : elName.substring(separ + 1));
            TagLib lib = findTagLib(ns, ownerElementCtx);
            if (lib != null) {
                Iterator it = lib.getTagAttrs(tag).iterator();
                while (it.hasNext())
                {
                    String attr = (String)it.next();
                    if (attr.startsWith(start)) {
                        toReturn.add(new Attribute(attr));
                    }
                }
            }
        } else {
            //TODO what now? default namespace

        }
        return toReturn.elements();
    }
   /**
     * query default value for given context. Two context types must be handled:
     * an attribute and an element context.
     * @param parentNodeCtx context for which default is queried
     * @return default value or <code>null</code>
     */    
    public GrammarResult queryDefault(HintContext parentNodeCtx)
    {
        logger.debug("query default");
        return null;
    }
    
    private TagLib findTagLib(String ns, HintContext context)
    {
        Node rootNode = findRootNode(context);
        if (rootNode != null)
        {
            Map mapping = getNameSpaceMappings(rootNode);
            String jellyTag = (String)mapping.get(ns);
            if (jellyTag != null)
            {
                return  (TagLib)tagLibs.get(jellyTag);
            }
        }
        return null;
    }
    
    /**
     * code could be probably reused with findTagLib?
     * @returns a map - key is namespace name, value -it's taglib instance
     */
    private Map findTagLibs(String nsStart, HintContext context)
    {
        Node rootNode = findRootNode(context);
        Map toReturn = new HashMap(10);
        if (rootNode != null)
        {
            Map mapping = getNameSpaceMappings(rootNode);
            Iterator it = mapping.keySet().iterator();
            while (it.hasNext())
            {
                String ns = (String)it.next();
                if (nsStart == null || nsStart.length() == 0 || ns.startsWith(nsStart)) {
                    String jellyTag = (String)mapping.get(ns);
                    if (jellyTag != null)
                    {
                        toReturn.put(ns, tagLibs.get(jellyTag));
                    }
                }
            }
        }
        return toReturn;
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
        Vector toReturn = new Vector();
        int separ = start.indexOf(':');
        if (separ > 0) 
        {
            //it's a namespace element..
            String ns = start.substring(0, separ);
            String tag = (separ == start.length() ? "" : start.substring(separ + 1));
            logger.debug("namespace is " + ns);
            TagLib lib = findTagLib(ns, virtualElementCtx);
            if (lib != null)
            {
                //we found the tag lib definition, can offer completion.
                Iterator it = lib.getTags().iterator();
                while (it.hasNext())
                {
                    String name = (String)it.next();
                    if (name.startsWith(tag))
                    {
                        toReturn.add(new MyElement(ns,name));
                    }
                }
            }
        } else {
            logger.debug("no namespace yet");
            Map libs = findTagLibs(start, virtualElementCtx);
            List singleTags = new ArrayList(30);
            if (libs.size() > 0) {
                Iterator it = libs.keySet().iterator();
                while (it.hasNext())
                {
                    String ns = (String)it.next();
                    TagLib lb = (TagLib)libs.get(ns);
                    logger.debug("adding namespace=" + ns);
                    toReturn.add(new TagLibElement(ns));
                    // add the tags as well, tothe end however.
                    logger.debug("adding lib=" + lb);
                    if (lb != null) {
                        // it can happen that we don't have the definition handy,
                        // that's why the null check..
                        Iterator it2 = lb.getTags().iterator();
                        while (it2.hasNext()) {
                            String tag = (String)it2.next();
                            singleTags.add(new MyElement(ns, tag));
                        }
                    }
                }
                toReturn.addAll(singleTags);
            }           
            //TODO add default elements..
        }
        return toReturn.elements();
    }
    /**
     * Allow to get names of <b>parsed general entities</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (ENTITY_REFERENCE_NODEs)
     */    
    public Enumeration queryEntities(String prefix)
    {
        logger.debug("query entities");
        return EmptyEnumeration.EMPTY;
    }
    /**
     * Allow to get names of <b>declared notations</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (NOTATION_NODEs)
     */        
    public Enumeration queryNotations(String prefix)
    {
        logger.debug("query notation");
        return EmptyEnumeration.EMPTY;
    }
     /**
     * Return options for value at given context.
     * It could be also used for completing of value parts such as Ant or XSLT property names (how to trigger it?).
     * @semantics Navigates through read-only Node tree to determine context and provide right results.
     * @postconditions Let ctx unchanged
     * @time Performs fast up to 300 ms.
     * @stereotype query
     * @param virtualTextCtx represents virtual Node that has to be replaced (parent can be either Attr or Element), its own attributes does not name sense, it can be used just as the navigation start point.
     * @return enumeration of <code>GrammarResult</code>s (TEXT_NODEs) that can be queried on name, and attributes.
     *         Every list member represents one possibility.  
     */   
    public Enumeration queryValues(HintContext virtualTextCtx)
    {
        logger.debug("query values..");
        Node parent = virtualTextCtx;
        String start = virtualTextCtx.getCurrentPrefix();
        Vector toReturn = new Vector();
        logger.debug("parent node=" + parent);
        logger.debug("context prefix= " + virtualTextCtx.getCurrentPrefix());
        logger.debug("context name=" + virtualTextCtx.getNodeName());
        logger.debug("context type=" + virtualTextCtx.getNodeType());
        if (parent != null && parent.getNodeType() == Node.ATTRIBUTE_NODE) {
            // completion for the namespaces..
            if (parent.getNodeName().startsWith("xmlns:")) {
                logger.debug(".. is the one");
                // now we offer jelly taglibs
                Iterator it = tagLibs.keySet().iterator();
                while (it.hasNext())
                {
                    String lib = (String)it.next();
                    logger.debug("lib=" + lib);
                    if (lib.startsWith(start)) {
                        logger.debug("adding lib");
                        toReturn.add(new TextNode(lib));
                    }
                }
            }
        }
        return toReturn.elements();
    }

    /**
     * a superclass for my node impls.
     */
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
    }
    
    private static class MyElement extends AbstractResultNode implements org.w3c.dom.Element {
        
        private String name;
        private String namespace;
        
        MyElement(String ns, String name) {
            this.name = name;
            namespace = ns;
        }
        
        public short getNodeType() {
            return Node.ELEMENT_NODE;
        }
        
        public String getNodeName() {
            return namespace + ":" + name;
        }
        
        public String getTagName() {
            return name;
        }
    }    

    private static class TagLibElement extends AbstractResultNode implements org.w3c.dom.Element {
        
        private String namespace;
        
        TagLibElement(String ns) {
            namespace = ns;
        }
        
        public short getNodeType() {
            return Node.ELEMENT_NODE;
        }
        
        public String getNodeName() {
            return namespace + ":";
        }
        
        public String getTagName() {
            return namespace;
        }
        
        public Icon getIcon(int kind) {
            return new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/grammar/resources/NamespaceIcon.gif", true));
        }        
    }        
    
    private static class Attribute extends AbstractResultNode implements org.w3c.dom.Attr {
        
        private String name;
        
        Attribute(String name) {
            this.name = name;
        }
        
        public short getNodeType() {
            return Node.ATTRIBUTE_NODE;
        }
        
        public String getNodeName() {
            return name;
        }
        
        public String getTagName() {
            return name;
        }
        
    }    

    private static class TextNode extends AbstractResultNode implements org.w3c.dom.Text {
        
        private String name;
        
        TextNode(String name) {
            this.name = name;
        }
        
        public short getNodeType() {
            return Node.TEXT_NODE;
        }
        
        public String getNodeName() {
            return name;
        }

        public String getNodeValue() {
            return name;
        }
        
        public String getTagName() {
            return name;
        }
        
    }    
    
    
}
