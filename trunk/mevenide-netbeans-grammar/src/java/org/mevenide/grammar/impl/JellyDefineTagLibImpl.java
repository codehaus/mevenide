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

package org.mevenide.grammar.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.mevenide.grammar.TagLib;

/**
 * a TagLib implementation that parses the jelly.plugin files and extracts the 
 * dynamic taglibray definitions.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class JellyDefineTagLibImpl implements TagLib
{
    Element rootElement;
    /** Creates a new instance of JellyDefineTagLibImpl */
    public JellyDefineTagLibImpl(File taglibPluginJelly) throws Exception
    {
        this(new FileInputStream(taglibPluginJelly));
    }
    
    public JellyDefineTagLibImpl(InputStream pluginStream) throws Exception 
    {
        super();
        rootElement = findDynamicTagLibraryElement(pluginStream);
    }
    
    public static Element findDynamicTagLibraryElement(InputStream stream) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document schemaDoc = builder.build(stream);
        Element root = schemaDoc.getRootElement();
        List nsList = root.getAdditionalNamespaces();
        Iterator it = nsList.iterator();
        Namespace dynaNS = null;
        while (it.hasNext()) {
            Namespace ns = (Namespace)it.next();
            if (ns.getURI().equals("jelly:define")) {
                dynaNS = ns;
                break;
            }
        }
        if (dynaNS == null) {
            throw new Exception("Cannot find the jelly:define namespace in the document. Don't know what to do now.");
        }
        Element tagLibEl = root.getChild("taglib", dynaNS);
        return tagLibEl;
    }
    
    public String getName()
    {
        return rootElement.getAttributeValue("uri");
    }
    
    public Collection getRootTags()
    {
        List elements = rootElement.getChildren("tag", rootElement.getNamespace());
        Collection toReturn = new ArrayList(15);
        if (elements != null && elements.size() > 0) {
            Iterator it = elements.iterator();
            while (it.hasNext()) {
                Element el = (Element)it.next();
                toReturn.add(el.getAttributeValue("name"));
                //TODO?
            }
        }
        elements = rootElement.getChildren("jellybean", rootElement.getNamespace());
        if (elements != null && elements.size() > 0) {
            Iterator it = elements.iterator();
            while (it.hasNext()) {
                Element el = (Element)it.next();
                toReturn.add(el.getAttributeValue("name"));
            }
        }
        return toReturn;
    }
    
    public java.util.Collection getSubTags(String tagName)
    {
        return Collections.EMPTY_LIST;
    }
    
    public java.util.Collection getTagAttrs(String tag)
    {
        return Collections.EMPTY_LIST;
    }
    
}
