/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.grammar.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.mevenide.grammar.TagLib;

/**
 * a TagLib implementation that parses the jelly.plugin files and extracts the
 * dynamic taglibrary definitions.
 * 
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class JellyDefineTagLibImpl implements TagLib {

    private static Log logger = LogFactory.getLog(JellyDefineTagLibImpl.class);
    private Element rootElement;
    private URLClassLoader classLoader;
    private Map cachedTagAttributes;

    /** Creates a new instance of JellyDefineTagLibImpl */
    public JellyDefineTagLibImpl(File taglibPluginJelly, ClassLoader mavenClassLoader) throws Exception {
        this(new FileInputStream(taglibPluginJelly));
        File pluginDir = taglibPluginJelly.getParentFile();
        URL url = pluginDir.toURL();
        // needs to be rewritten once it's used outside netbeans.. dependence on
        // MavengrammarModule.
        classLoader = new URLClassLoader(new URL[]{url}, mavenClassLoader);
    }

    /**
     * primarily targetted at unit testing..
     */
    public JellyDefineTagLibImpl(InputStream pluginStream) throws Exception {
        super();
        rootElement = findDynamicTagLibraryElement(pluginStream);
        cachedTagAttributes = new TreeMap();
    }

    /**
     * finds the defition of the taglib in the plugin.jelly file..
     */
    public static Element findDynamicTagLibraryElement(InputStream stream) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document schemaDoc = builder.build(stream);
        Element root = schemaDoc.getRootElement();
        List nsList = root.getAdditionalNamespaces();
        Iterator it = nsList.iterator();
        Namespace dynaNS = null;
        while (it.hasNext()) {
            Namespace ns = (Namespace) it.next();
            if (ns.getURI().equals("jelly:define")) { //NOI18N
                dynaNS = ns;
                break;
            }
        }
        if (dynaNS == null) {
            throw new Exception("Cannot find the jelly:define namespace in the document. Don't know what to do now.");
        }
        Element tagLibEl = findTagLibElement(root, dynaNS); //NOI18N
        if (tagLibEl == null) {
            logger.error("The supposedly defined taglibrary was not found");
        }
        return tagLibEl;
    }

    private static Element findTagLibElement(Element root, Namespace ns) {
        Element toReturn = root.getChild("taglib", ns); //NOI18N
        if (toReturn == null) {
            // obviously the taglib is not defined at this level..
            // descend recursively down..
            // for example codeswitcher is defined like that
            Collection col = root.getChildren();
            if (col != null) {
                Iterator it = col.iterator();
                while (it.hasNext()) {
                    Element el = (Element) it.next();
                    toReturn = findTagLibElement(el, ns);
                    if (toReturn != null) {
                        return toReturn;
                    }
                }
            }
        }
        return toReturn;
    }

    public String getName() {
        return rootElement.getAttributeValue("uri"); //NOI18N
    }

    /**
     * TODO - rewrite, improve code..
     */
    public Collection getRootTags() {
        List elements = rootElement.getChildren("tag", rootElement.getNamespace()); //NOI18N
        Collection toReturn = new ArrayList(15);
        if (elements != null && elements.size() > 0) {
            Iterator it = elements.iterator();
            while (it.hasNext()) {
                Element el = (Element) it.next();
                toReturn.add(el.getAttributeValue("name")); //NOI18N
                //TODO?
            }
        }
        elements = rootElement.getChildren("jellybean", rootElement.getNamespace()); //NOI18N
        if (elements != null && elements.size() > 0) {
            Iterator it = elements.iterator();
            while (it.hasNext()) {
                Element el = (Element) it.next();
                String name = el.getAttributeValue("name"); //NOI18N
                toReturn.add(name);
            }
        }
        elements = rootElement.getChildren("bean", rootElement.getNamespace()); //NOI18N
        if (elements != null && elements.size() > 0) {
            Iterator it = elements.iterator();
            while (it.hasNext()) {
                Element el = (Element) it.next();
                String name = el.getAttributeValue("name"); //NOI18N
                toReturn.add(name);
            }
        }
        //        elements = rootElement.getChildren("dynabean",
        // rootElement.getNamespace()); //NOI18N
        //        if (elements != null && elements.size() > 0) {
        //            Iterator it = elements.iterator();
        //            while (it.hasNext()) {
        //                Element el = (Element)it.next();
        //                String name = el.getAttributeValue("name"); //NOI18N
        //                toReturn.add(name);
        //            }
        //        }
        return toReturn;
    }

    public java.util.Collection getSubTags(String tagName) {
        return Collections.EMPTY_LIST;
    }

    public java.util.Collection getTagAttrs(String tag) {
        Collection toReturn = (Collection) cachedTagAttributes.get(tag);
        if (toReturn == null) {
            toReturn = new HashSet();
            // find the element first and get Attribute children tags if
            // available..
            List elements = rootElement.getChildren();
            Element tagElement = null;
            if (elements != null && elements.size() > 0) {
                Iterator it = elements.iterator();
                while (it.hasNext()) {
                    Element el = (Element) it.next();
                    if (el.getNamespace().equals(rootElement.getNamespace())
                    // TODO - we shall check for tag/jellybean tags and also
                    // others..
                            && (el.getName().equals("tag") || el.getName().equals("jellybean") || el.getName().equals("bean") //||
                            //el.getName().equals("dynabean")
                            ) && tag.equals(el.getAttributeValue("name"))) //NOI18N
                    {
                        tagElement = el;
                        List attrElements = tagElement.getChildren("attribute", rootElement.getNamespace()); //NOI18N
                        if (attrElements != null && attrElements.size() > 0) {
                            Iterator it2 = elements.iterator();
                            while (it2.hasNext()) {
                                Element attrEl = (Element) it2.next();
                                String attrName = attrEl.getAttributeValue("name"); //NOI18N
                                if (attrName != null) {
                                    toReturn.add(attrName);
                                }
                            }
                        }
                        //was found -> break out of the loop..
                        break;
                    }
                }
            }
            // TODO where shall we check for the classloader tag.. to replace
            // the default one..
            if (tagElement != null) {
                if (tagElement.getName().equals("jellybean")) { //NOI18N
                    // do the jellybean stuff..
                    logger.debug("introspectJellyBean");
                    introspectBean(tagElement, toReturn, classLoader);
                }
                if (tagElement.getName().equals("bean")) { //NOI18N
                    // do the bean stuff..
                    logger.debug("introspectBean");
                    introspectBean(tagElement, toReturn, classLoader);
                }
                //                if (tagElement.getName().equals("dynabean")) { //NOI18N
                //                    // do the bean stuff..
                //                    logger.debug("introspectDynaBean");
                //                    introspectBean(tagElement, toReturn, classLoader);
                //                }
            }
        }
        return toReturn;
    }

    private void introspectBean(Element element, Collection attrSet, ClassLoader loader) {
        try {
            Class clazz = loader.loadClass(element.getAttributeValue("className")); //NOI18N
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("set") //NOI18N
                        && methods[i].getParameterTypes().length == 1 && methods[i].getModifiers() == Modifier.PUBLIC) {
                    try {
                        String attrName = methods[i].getName().substring(3);
                        // include only the ones with getters?..
                        if (clazz.getMethod("get" + attrName, null) != null) {
                            // make the first character lowercase..
                            attrSet.add(firstToLowerCase(attrName));
                        }
                    }
                    catch (Exception e) {
                        // no getter?? just ignore.. process the next one..
                        logger.warn("No getter, probably ignore", e);
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Cannot examine jelly bean", exc);
        }
    }

    private static final String firstToLowerCase(String input) {
        String first = input.substring(0, 1);
        first = first.toLowerCase();
        return first + input.substring(1);
    }

    // empty implementation, hard to guess what values can go into the dynamic
    // tag's attribute..
    public Collection getAttrCompletionTypes(String tag, String attribute) {
        return Collections.EMPTY_LIST;
    }
}