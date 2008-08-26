/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.grammar;

import java.awt.Component;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.jdom.Document;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.spi.dom.AbstractNode;
import org.openide.ErrorManager;
import org.openide.util.Enumerations;
import org.openide.nodes.Node.Property;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author mkleint
 */
public abstract class AbstractSchemaBasedGrammar implements GrammarQuery {
    Document schemaDoc;

    private GrammarEnvironment environment;
    /** Creates a new instance of NewClass */
    public AbstractSchemaBasedGrammar(GrammarEnvironment env) {
        try {
            SAXBuilder builder = new SAXBuilder();
            InputStream stream = getSchemaStream();
            schemaDoc = builder.build(stream);
        } catch (Exception exc) {
            ErrorManager.getDefault().notify(exc);
        }
        environment = env;
    }
    
    protected final GrammarEnvironment getEnvironment() {
        return environment;
    }
    
    protected final MavenProject getMavenProject() {
        Project proj = FileOwnerQuery.getOwner(environment.getFileObject());
        if (proj != null) {
            NbMavenProject watch = proj.getLookup().lookup(NbMavenProject.class);
            assert watch != null;
            return watch.getMavenProject();
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "File " + environment.getFileObject() + " has maven2 code completion but doesn't belong to a maven2 project."); //NOI18N
        return null;
    }
    
    /**
     * the input stream of the xml schema document that describes the document elements.
     */
    protected abstract InputStream getSchemaStream();
    
    
    /**
     * to override by subclasses that want to provide some dynamic content un a specific subtree.
     * @param path is slash separated path string
     * @return the actual completion nodes or empty list
     */
    protected List getDynamicCompletion(String path, HintContext hintCtx, org.jdom.Element lowestParent) {
        return Collections.EMPTY_LIST;
    }

    /**
     * to override by subclasses that want to provide some dynamic content un a specific subtree.
     * @param path is slash separated path string
     * @return null, if no such offering exists or the actual completion nodes..
     */
    protected Enumeration getDynamicValueCompletion(String path, HintContext virtualTextCtx, org.jdom.Element el) {
        return null;
    }
    
    
    protected final org.jdom.Element findElement(org.jdom.Element parent, String name) {
        List<org.jdom.Element> childs = parent.getChildren("element", parent.getNamespace()); //NOI18N
        for (org.jdom.Element el : childs) {
            if (name.equals(el.getAttributeValue("name"))) { //NOI18N
                return el;
            }
        }
        return null;
    }

    
    protected final org.jdom.Element findNonTypedContent(org.jdom.Element root) {
        org.jdom.Element complex = root.getChild("complexType", root.getNamespace()); //NOI18N
        if (complex != null) {
            complex = complex.getChild("sequence", root.getNamespace()); //NOI18N
        }
        return complex;
        
    }

    
    protected final org.jdom.Element findTypeContent(final String type, org.jdom.Element docRoot) {
        List<org.jdom.Element> lst = docRoot.getContent(new Filter() {
            public boolean matches(Object match) {
                if (match instanceof org.jdom.Element) {
                    org.jdom.Element el = (org.jdom.Element)match;
                    if ("complexType".equals(el.getName()) && type.equals(el.getAttributeValue("name"))) { //NOI18N
                        return true;
                    }
                }
                return false;
            }
        });
        if (lst.size() > 0) {
            org.jdom.Element typeEl = lst.get(0);
            return typeEl.getChild("all", docRoot.getNamespace()); //NOI18N
        }
        return null;
    }
    

    
      private void processElement(String matches, org.jdom.Element childEl, Vector suggestions) {
        String childRefAttr = childEl.getAttributeValue("ref"); //NOI18N
        if (childRefAttr == null) {
            // if ref not defined, go check name attribute..
            childRefAttr = childEl.getAttributeValue("name"); //NOI18N
        }
        if (childRefAttr != null && childRefAttr.startsWith(matches)) {
            suggestions.add(new MyElement(childRefAttr));
        }
    }
    
    /**
     * filters out the child elements that are of of type 'element" or "group".
     */
    private class DefinitionContentElementFilter extends ElementFilter {
        public DefinitionContentElementFilter() {
        }
        
        public boolean matches(Object obj) {
            boolean toReturn = super.matches(obj);
            if (toReturn) {
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
      
    public Component getCustomizer(HintContext nodeCtx) {
        return null;
    }

    /**
     * Allows Grammars to supply properties for the HintContext
     * @param ctx the hint context node
     * @return an array of properties for this context
     */
    public Property[] getProperties(HintContext nodeCtx) {
        return new Property[0];
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

    
    protected final void processSequence(String matches, org.jdom.Element seqEl, Vector suggestions) {
        List<org.jdom.Element> availables = seqEl.getContent(new DefinitionContentElementFilter());
        for (org.jdom.Element childEl : availables) {
            processElement(matches, childEl, suggestions);
        }
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
        return Enumerations.empty();
    }

    /**
     * query default value for given context. Two context types must be handled:
     * an attribute and an element context.
     * @param parentNodeCtx context for which default is queried
     * @return default value or <code>null</code>
     */
    public GrammarResult queryDefault(HintContext parentNodeCtx) {
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
    public Enumeration queryElements(HintContext virtualElementCtx) {
        String start = virtualElementCtx.getCurrentPrefix();
        
        Node parentNode = virtualElementCtx.getParentNode();
        boolean hasSchema = false;
        if (parentNode != null && schemaDoc != null) {
            List<String> parentNames = new ArrayList<String>();
            while (parentNode != null & parentNode.getNodeName() != null) {
                parentNames.add(0, parentNode.getNodeName());
                if (parentNode.getParentNode() == null || parentNode.getParentNode().getNodeName() == null) {
                    NamedNodeMap nnm = parentNode.getAttributes();
                    hasSchema  = nnm.getNamedItemNS("xsi","schemaLocation") != null;
                }
                parentNode = parentNode.getParentNode();
            }
            org.jdom.Element schemaParent = schemaDoc.getRootElement();
            Iterator<String> it = parentNames.iterator();
            String path = ""; //NOI18N
            Vector toReturn = new Vector();
            while (it.hasNext() && schemaParent != null) {
                String str = it.next();
                path = path + "/" + str; //NOI18N
                org.jdom.Element el = findElement(schemaParent, str);
                if (!it.hasNext()) {
                    toReturn.addAll(getDynamicCompletion(path, virtualElementCtx, el));
                }
                if (el != null) {
                    String type = el.getAttributeValue("type"); //NOI18N
                    if (type != null) {
                        schemaParent = findTypeContent(type, schemaDoc.getRootElement());
                        if (schemaParent == null) {
                            System.err.println("no schema parent for " + str + " of type " + el.getAttributeValue("type")); //NOI18N
                        }
                    } else {
                        schemaParent = findNonTypedContent(el);
                    }
                } else {
                    System.err.println("cannot find element=" + str); //NOI18N
                }
            }
            if (schemaParent != null && !hasSchema) {
                processSequence(start, schemaParent, toReturn);
            }
            return toReturn.elements();
        } else {
            return Enumerations.empty();
        }
    }

    
    
    /**
     * Allow to get names of <b>parsed general entities</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (ENTITY_REFERENCE_NODEs)
     */
    public Enumeration queryEntities(String prefix) {
        return Enumerations.empty();
    }

    /**
     * Allow to get names of <b>declared notations</b>.
     * @param prefix prefix filter
     * @return enumeration of <code>GrammarResult</code>s (NOTATION_NODEs)
     */
    public Enumeration queryNotations(String prefix) {
        return Enumerations.empty();
    }

    
    public Enumeration queryValues(HintContext virtualTextCtx) {
        Node parentNode = virtualTextCtx.getParentNode();
        List<String> parentNames = new ArrayList<String>();
        if (virtualTextCtx.getCurrentPrefix().length() == 0) {
            parentNames.add(virtualTextCtx.getNodeName());
        }
        if (parentNode != null && schemaDoc != null) {
            while (parentNode != null & parentNode.getNodeName() != null) {
                parentNames.add(0, parentNode.getNodeName());
                parentNode = parentNode.getParentNode();
            }
            org.jdom.Element schemaParent = schemaDoc.getRootElement();
            Iterator<String> it = parentNames.iterator();
            String path = ""; //NOI18N
            while (it.hasNext() && schemaParent != null) {
                String str = it.next();
                path = path + "/" + str; //NOI18N
                org.jdom.Element el = findElement(schemaParent, str);
                if (!it.hasNext()) {
                    Enumeration en = getDynamicValueCompletion(path, virtualTextCtx, el);
                    if (en != null) {
                        return en;
                    }
                }
                if (el != null) {
                    String type = el.getAttributeValue("type"); //NOI18N
                    if (type != null) {
                        schemaParent = findTypeContent(type, schemaDoc.getRootElement());
                        if (schemaParent == null) {
                            System.err.println("no schema parent for " + str + " of type=" + el.getAttributeValue("type")); //NOI18N
                        }
                    } else {
                        schemaParent = findNonTypedContent(el);
                    }
                } else {
                    System.err.println("cannot find element=" + str); //NOI18N
                }                
            }
        }
        return Enumerations.empty();
    }


    /**
     * for subclasses that  have a given list of possible values in the element's text content. 
     */
    protected final Enumeration createTextValueList(String[] values, HintContext context) {
        Collection elems = new ArrayList();
        for (String value :  values) {
            if (value.startsWith(context.getCurrentPrefix())) {
                elems.add(new MyTextElement(value, context.getCurrentPrefix()));
            }
        }
        return Collections.enumeration(elems);
        
    }
    
    protected abstract static class AbstractResultNode extends AbstractNode implements GrammarResult {
        
        public Icon getIcon(int kind) {
            return null;
        }
        
        /**
         * @output provide additional information simplifiing decision
         */
        public String getDescription() {
            return getNodeName();
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
        
    }

    
    protected static class MyElement extends AbstractResultNode implements Element {
        
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

     protected static class MyTextElement extends AbstractResultNode implements Text {
        
        private String name;
        private String prefix;
        
        MyTextElement(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
        }
        
        public short getNodeType() {
            return Node.TEXT_NODE;
        }
        
        public String getNodeName() {
            return name;
        }
        
        public String getTagName() {
            return name;
        }
        
        public String getNodeValue() {
            return name.substring(prefix.length());
        }
        
    }
     
    protected static class ComplexElement extends AbstractResultNode implements Element {
        
        private String name;
        private String display;
        private NodeList list;
        
        ComplexElement(String tagName, String displayName, NodeList listimpl) {
            this.name = tagName;
            display = displayName;
            list = listimpl;
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
        
        public String getDisplayName() {
            return display;
        }

        public NodeList getChildNodes() {
            return list;
        }
     /**
     * @return false
     */
    public boolean hasChildNodes() {
        return true;
    }
    public org.w3c.dom.Node getLastChild() {
        return list.item(list.getLength() - 1);
    }
    /**
     * @return null
     */
    public org.w3c.dom.Node getFirstChild() {
        return list.item(0);
    }

       
        
    }
     
   
}
