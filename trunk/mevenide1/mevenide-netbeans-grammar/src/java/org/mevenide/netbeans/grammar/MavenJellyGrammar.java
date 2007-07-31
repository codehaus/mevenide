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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.mevenide.grammar.AttributeCompletion;
import org.mevenide.grammar.GrammarUtilities;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.TagLibManager;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.spi.dom.AbstractNode;
import org.openide.util.Utilities;
import org.openide.util.Enumerations;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 */
public class MavenJellyGrammar implements GrammarQuery {
    
    private static final Logger LOGGER = Logger.getLogger(MavenJellyGrammar.class.getName());
    
    private static TagLibManager manager;
    
    public MavenJellyGrammar() {
    }
    
    
    private static TagLibManager getManager() {
        if (manager == null) {
            synchronized (MavenJellyGrammar.class) {
                if (manager == null) {
                    manager = new TagLibManager();
                    NbTagLibProvider prov = new NbTagLibProvider();
                    manager.setProvider(prov);
                    manager.setAttrCompletionProvider(prov);
                }
            }
        }
        return manager;
    }
    
    
    private TagLib getDefaultTagLib() {
        //TODO define default taglib
        return getManager().getTagLibrary("default-maven");
    }
    
    private Node findRootNode(Node virtualElementCtx) {
        Node current = virtualElementCtx;
        while (current != null) {
            if (current.getNodeName() != null && current.getNodeName().equals("project")) {
                break;
            }
            LOGGER.fine("findRootNode:curr rootNode is=" + current.getNodeName());
            current = current.getParentNode();
        }
        return current;
    }
    
    /**
     * will create a mapping table for
     */
    private Map getNameSpaceMappings(Node projectNode) {
        HashMap toReturn = new HashMap();
        NamedNodeMap map = projectNode.getAttributes();
        if (map != null) {
            for (int i = 0; i < map.getLength(); i++) {
                Node attrNode = map.item(i);
                String attrName = attrNode.getNodeName();
                if (attrName.startsWith("xmlns:")) {
                    toReturn.put(attrName.substring("xmlns:".length()), attrNode.getNodeValue());
                    LOGGER.fine("namespace name=" + attrName.substring("xmlns:".length()) + " value=" + attrNode.getNodeValue());
                }
            }
        }
        return toReturn;
    }
    
    
    public Component getCustomizer(HintContext nodeCtx) {
        return null;
    }
    /**
     * Allows Grammars to supply properties for the HintContext
     * @param ctx the hint context node
     * @return an array of properties for this context
     */
    public org.openide.nodes.Node.Property[] getProperties(HintContext nodeCtx) {
        return new org.openide.nodes.Node.Property[0];
    }
    
    public boolean hasCustomizer(HintContext nodeCtx) {
        return false;
    }
    /**
     * Distinquieshes between empty enumaration types.
     * @return <code>true</code> there is no known result
     *         <code>false</code> grammar does not allow here a result
     */
    public boolean isAllowed(Enumeration en) {
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
    public Enumeration queryAttributes(HintContext ownerElementCtx) {
        String start = ownerElementCtx.getCurrentPrefix();
        LOGGER.fine("start=" + start);
        Set toReturn = new TreeSet(new SimpleComparator());
        String elName = ownerElementCtx.getNodeName();
        int separ = elName.indexOf(':');
        TagLib lib = null;
        String tag = elName;
        if (separ > 0) {
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
    public GrammarResult queryDefault(HintContext parentNodeCtx) {
        LOGGER.fine("query default");
        return null;
    }
    
    private TagLib findTagLib(String ns, Node context) {
        Node rootNode = findRootNode(context);
        if (rootNode != null) {
            Map mapping = getNameSpaceMappings(rootNode);
            String jellyTag = (String)mapping.get(ns);
            if (jellyTag != null) {
                return  (TagLib)getManager().getTagLibrary(jellyTag);
            }
        }
        return null;
    }
    
    /**
     * iterates up the document hierarchy and returns the first parent node that has the same
     * namespace.
     * @param ns namespace to look for, if null, default namespace is searched.
     */
    private Node findParentInSameNamespace(String ns, Node el) {
        Node parent = el.getParentNode();
        
        while (parent != null) {
            String name = parent.getNodeName();
            if (name != null) {
                int separ = name.indexOf(':');
                if (separ > 0) {
                    String nameSpace = name.substring(0, separ);
                    LOGGER.fine("findParentInSameNamespace-" + nameSpace );
                    if (nameSpace.equals(ns)) {
                        return parent;
                    }
                } else {
                    // no namespace here,
                    if (ns == null) {
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
    private Map findTagLibs(String nsStart, HintContext context) {
        Node rootNode = findRootNode(context);
        Map toReturn = new HashMap(10);
        if (rootNode != null) {
            Map mapping = getNameSpaceMappings(rootNode);
            Iterator it = mapping.keySet().iterator();
            while (it.hasNext()) {
                String ns = (String)it.next();
                if (nsStart == null || nsStart.length() == 0 || ns.startsWith(nsStart)) {
                    String jellyTag = (String)mapping.get(ns);
                    if (jellyTag != null) {
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
    public Enumeration queryElements(HintContext virtualElementCtx) {
        String start = virtualElementCtx.getCurrentPrefix();
        LOGGER.fine("start=" + start);
        Collection toReturn = new TreeSet(new ElementComparator());
        int separ = start.indexOf(':');
        if (separ > 0) {
            //it's a namespace element..
            String ns = start.substring(0, separ);
            String tag = (separ == start.length() ? "" : start.substring(separ + 1));
            LOGGER.fine("namespace is " + ns);
            TagLib lib = findTagLib(ns, virtualElementCtx);
            createSubTagsElements(ns, virtualElementCtx, toReturn, lib, tag);
            createTagElements(lib.getRootTags(), toReturn, ns, tag);
        } else {
            LOGGER.fine("no namespace yet");
            Map libs = findTagLibs(start, virtualElementCtx);
            Collection singleTags = new ArrayList();
            if (libs.size() > 0) {
                Set sortedLibs = new TreeSet(libs.keySet());
                Iterator it = sortedLibs.iterator();
                while (it.hasNext()) {
                    String ns = (String)it.next();
                    TagLib lb = (TagLib)libs.get(ns);
                    LOGGER.fine("adding namespace=" + ns);
                    toReturn.add(new TagLibElement(ns));
                    // add the tags as well, tothe end however.
                    LOGGER.fine("adding lib=" + lb);
                    // add all elements for given namespace.. that's why the last parameter is null.
                    createTagElements(lb.getRootTags(), singleTags, ns, null);
                    createSubTagsElements(ns, virtualElementCtx, singleTags, lb, null);
                }
            }
            // add default tags now..
            createTagElements(getDefaultTagLib().getRootTags(), toReturn, null, start);
            createSubTagsElements(null, virtualElementCtx, toReturn, getDefaultTagLib(), start);
            
            // add single tags from namespaces last, to have the default tags first (after namespaces)
            toReturn.addAll(singleTags);
        }
        return Collections.enumeration(toReturn);
    }
    
    /**
     * creates a list of tag elements for the given collection of strings, will filter out
     * only those starting with value in parameter start. If that one is null, will add all.
     *
     */
    private void createTagElements(Collection col, Collection elemList, String namespace, String start) {
        Iterator it = col.iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            if (start == null || name.startsWith(start)) {
                elemList.add(new MyElement(namespace, name));
            }
        }
    }
    
    private void createSubTagsElements(String ns, Node virtualElementCtx, Collection ccList, TagLib lib, String start) {
        Node parent = findParentInSameNamespace(ns, virtualElementCtx);
        if (parent != null) {
            String parentTag = parent.getNodeName();
            int separ = parentTag.indexOf(':');
            if (separ > -1) {
                parentTag = parentTag.substring(separ + 1);
            }
            LOGGER.fine("parenttag=" + parentTag);
            Collection col = lib.getSubTags(parentTag);
            if (col != null) {
                createTagElements(col, ccList, ns, start);
            }
        }
    }
    /**
     * Allow to get names of <b>parsed general entities</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (ENTITY_REFERENCE_NODEs)
     */
    public Enumeration queryEntities(String prefix) {
        LOGGER.fine("query entities");
        return Enumerations.empty();
    }
    /**
     * Allow to get names of <b>declared notations</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (NOTATION_NODEs)
     */
    public Enumeration queryNotations(String prefix) {
        LOGGER.fine("query notation");
        return Enumerations.empty();
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
    public Enumeration queryValues(HintContext virtualTextCtx) {
        Node parent = virtualTextCtx;
        String start = virtualTextCtx.getCurrentPrefix();
        Set toReturn = new TreeSet(new SimpleComparator());
        //        logger.fine("parent node=" + parent);
        //        logger.fine("document=" + parent.getOwnerDocument());
        //        logger.fine("parent's parent=" + parent.getParentNode());
        //        logger.fine("document element=" + parent.getOwnerDocument().getDocumentElement());
        if (parent != null && parent.getNodeType() == Node.ATTRIBUTE_NODE) {
            // completion for the namespaces..
            if (parent.getNodeName().startsWith("xmlns:")) {
                // now we offer jelly taglibs
                String[] libs = getManager().getAvailableTagLibs();
                for (int i = 0; i < libs.length; i++) {
                    LOGGER.fine("lib=" + libs[i]);
                    if (libs[i].startsWith(start)) {
                        toReturn.add(new TextNode(libs[i]));
                    }
                }
            } else {
                // do attribute cc now..
                Attr attrParent  = (Attr)parent;
                Node elementParent = (Node)attrParent.getOwnerElement();
                int nsint = elementParent.getNodeName().indexOf(":");
                String namespace = (nsint > -1 ? elementParent.getNodeName().substring(0, nsint) : null);
                String tagName;
                if (nsint > -1 && nsint < elementParent.getNodeName().length() - 1) {
                    tagName = elementParent.getNodeName().substring(nsint + 1);
                } else {
                    tagName = elementParent.getNodeName();
                }
                TagLib lib;
                if (namespace == null) {
                    lib = getDefaultTagLib();
                } else {
                    lib = findTagLib(namespace, elementParent);
                }
                String lastWord = GrammarUtilities.extractLastWord(start);
                String initialPart = start.substring(0, start.length() - lastWord.length());
                if (lib != null) {
                    LOGGER.fine("found taglib" + lib.getName());
                    Collection types = lib.getAttrCompletionTypes(tagName, virtualTextCtx.getNodeName());
                    Iterator it = types.iterator();
                    while (it.hasNext()) {
                        String type = (String)it.next();
                        LOGGER.fine("attr compl type=" + type);
                        AttributeCompletion compl = getManager().getAttributeCompletion(type);
                        Collection col = compl.getValueHints(lastWord);
                        Iterator valIt = col.iterator();
                        while (valIt.hasNext()) {
                            toReturn.add(new TextNode((String)valIt.next(), initialPart));
                        }
                    }
                }
                // complete maven plugin default properties..
                if (lastWord.startsWith("maven.")) {
                    AttributeCompletion compl = getManager().getAttributeCompletion("pluginDefaults");
                    Collection col = compl.getValueHints(lastWord);
                    Iterator valIt = col.iterator();
                    while (valIt.hasNext()) {
                        toReturn.add(new TextNode((String)valIt.next(), initialPart));
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
            return getNodeName();
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
        private String start = "";
        
        TextNode(String name) {
            this.name = name;
        }
        
        TextNode(String name, String start) {
            this(name);
            this.start = start;
        }
        
        public short getNodeType() {
            return Node.TEXT_NODE;
        }
        
        public String getNodeName() {
            return name;
        }
        
        public String getNodeValue() {
            return start + name;
        }
        
        public String getTagName() {
            return name;
        }
    }
    
    private static class SimpleComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            AbstractResultNode nd1 = (AbstractResultNode)o1;
            AbstractResultNode nd2 = (AbstractResultNode)o2;
            return nd1.getTagName().compareTo(nd2.getTagName());
        }
        
    }
    
    private static class ElementComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
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
