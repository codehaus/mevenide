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
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.TagLibManager;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.spi.dom.AbstractNode;
import org.openide.nodes.Node.Property;
import org.openide.util.Utilities;
import org.openide.util.enum.EmptyEnumeration;
import org.w3c.dom.*;

/**
 */
public class MavenJellyGrammar implements GrammarQuery {
 
   private static Log logger = LogFactory.getLog(MavenJellyGrammar.class); 

   private static TagLibManager manager; 
   
   public MavenJellyGrammar()
   {
   }
  
   
   private static TagLibManager getManager() {
       if (manager == null) {
           synchronized (MavenJellyGrammar.class) {
               if (manager == null) {
                   manager = new TagLibManager();
                   manager.setProvider(new NbTagLibProvider());
               }
           }
       }
       return manager;
   }
   
   
   private TagLib getDefaultTagLib() {
       //TODO define default taglib
       return getManager().getTagLibrary("default-maven");
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
    public Property[] getProperties(HintContext nodeCtx)
    {
        return new Property[0];
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
        Set toReturn = new TreeSet(new SimpleComparator());
        String elName = ownerElementCtx.getNodeName();
        int separ = elName.indexOf(':');
        TagLib lib = null;
        String tag = elName;
        if (separ > 0) 
        {
            //it's a namespace element..
            String ns = elName.substring(0, separ);
            tag = (separ == elName.length() ? "" : elName.substring(separ + 1));
            lib = findTagLib(ns, ownerElementCtx);
        } else {
            //TODO what now? default namespace
            lib = getDefaultTagLib();
        }
        if (lib != null) {
            Iterator it = lib.getTagAttrs(tag).iterator();
            while (it.hasNext()) {
                String attr = (String)it.next();
                if (attr.startsWith(start)) {
                    toReturn.add(new Attribute(attr));
                }
            }
        }
        return Collections.enumeration(toReturn);
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
                return  (TagLib)getManager().getTagLibrary(jellyTag);
            }
        }
        return null;
    }
    
    
    private Node findParentInSameNamespace(String ns, Node el) {
        Node parent = el.getParentNode();
        
        while (parent != null) {
            String name = parent.getNodeName();
            if (name != null)
            {
                int separ = name.indexOf(':');
                if (separ > 0)
                {
                    String nameSpace = name.substring(0, separ);
                    logger.debug("findParentInSameNamespace-" + nameSpace );
                    if (nameSpace.equals(ns))
                    {
                        return parent;
                    }
                } else
                {
                    // no namespace here,
                    if (ns == null)
                    {
                        return parent;
                    }
                }
            }
            parent = parent.getParentNode();
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
                        toReturn.put(ns, getManager().getTagLibrary(jellyTag));
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
        Collection toReturn = new TreeSet(new ElementComparator());
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
                Node parent = findParentInSameNamespace(ns, virtualElementCtx);
                if (parent != null) {
                    String parentTag = parent.getNodeName().substring(separ + 1);
                    Collection col = lib.getSubTags(parentTag);
                    if (col != null) {
                        createTagElements(col, toReturn, ns, tag);
                    }
                }
                createTagElements(lib.getRootTags(), toReturn, ns, tag);
            }
        } else {
            logger.debug("no namespace yet");
            Map libs = findTagLibs(start, virtualElementCtx);
            Collection singleTags = new ArrayList();
            if (libs.size() > 0) {
                Set sortedLibs = new TreeSet(libs.keySet());
                Iterator it = sortedLibs.iterator();
                while (it.hasNext())
                {
                    String ns = (String)it.next();
                    TagLib lb = (TagLib)libs.get(ns);
                    logger.debug("adding namespace=" + ns);
                    toReturn.add(new TagLibElement(ns));
                    // add the tags as well, tothe end however.
                    if (lb != null) {
                        logger.debug("adding lib=" + lb);
                        // add all elements for given namespace.. that's why the last parameter is null.
                        createTagElements(lb.getRootTags(), singleTags, ns, null);
                        //TODO add non root tags..
                    }
                }
            }
            // add default tags now..
            createTagElements(getDefaultTagLib().getRootTags(), toReturn, null, start);
            //TODO add non root tag feom default
            
            // add single tags from namespaces last, to have the default tags first (after namespaces)
            toReturn.addAll(singleTags);
        }
        return Collections.enumeration(toReturn);
    }
    
    
    private void createTagElements(Collection col, Collection elemList, String namespace, String start) {
        Iterator it = col.iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            if (start == null || name.startsWith(start)) {
                elemList.add(new MyElement(namespace, name));
            }
        }
        
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
        Set toReturn = new TreeSet(new SimpleComparator());
        logger.debug("parent node=" + parent);
        logger.debug("context prefix= " + virtualTextCtx.getCurrentPrefix());
        logger.debug("context name=" + virtualTextCtx.getNodeName());
        logger.debug("context type=" + virtualTextCtx.getNodeType());
        if (parent != null && parent.getNodeType() == Node.ATTRIBUTE_NODE) {
            // completion for the namespaces..
            if (parent.getNodeName().startsWith("xmlns:")) {
                logger.debug(".. is the one");
                // now we offer jelly taglibs
                String[] libs = getManager().getAvailableTagLibs();
                for (int i = 0; i < libs.length; i++)
                {
                    logger.debug("lib=" + libs[i]);
                    if (libs[i].startsWith(start)) {
                        logger.debug("adding lib");
                        toReturn.add(new TextNode(libs[i]));
                    }
                }
            }
        }
        return Collections.enumeration(toReturn);
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
        
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof AbstractResultNode)) {
                return false;
            }
            AbstractResultNode res = (AbstractResultNode)obj;
            int index1 = getNodeName().indexOf(':');
            int index2 = res.getNodeName().indexOf(':');
            if ( (index1 >= 0 && index2 < 0) || (index2 >= 0 && index1 < 0)) {
                return false;
            }
            return getNodeName().equals(res.getNodeName());
        }
    }
    
    private static class MyElement extends AbstractResultNode implements Element {
        
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
            return (namespace == null ? name : namespace + ":" + name);
        }
        
        public String getTagName() {
            return name;
        }
    }    

    private static class TagLibElement extends AbstractResultNode implements Element {
        
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
    
    private static class Attribute extends AbstractResultNode implements Attr {
        
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

    private static class TextNode extends AbstractResultNode implements Text {
        
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

    private static class SimpleComparator implements Comparator {
        
        public int compare(Object o1, Object o2)
        {
            AbstractResultNode nd1 = (AbstractResultNode)o1;
            AbstractResultNode nd2 = (AbstractResultNode)o2;
            return nd1.getTagName().compareTo(nd2.getTagName());
        }
        
    }

    private static class ElementComparator implements Comparator {
        
        public int compare(Object o1, Object o2)
        {
            AbstractResultNode nd1 = (AbstractResultNode)o1;
            AbstractResultNode nd2 = (AbstractResultNode)o2;
            if ((nd1 instanceof TagLibElement) && !(nd2 instanceof TagLibElement)) {
                return -1;
            }
            if (!(nd1 instanceof TagLibElement) && (nd2 instanceof TagLibElement)) {
                return 1;
            }
            String name1 = nd1.getNodeName();
            String name2 = nd2.getNodeName();
            int index1 = name1.indexOf(':');
            int index2 = name2.indexOf(':');
            if ( index1 >= 0 && index2 < 0) {
                return 1;
            }
            if ( index2 >= 0 && index1 < 0) {
                return -1;
            }
            return name1.compareTo(name2);
        }
        
    }
    
}
