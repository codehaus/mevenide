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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.grammar.TagLib;

/**
 * Container for tag library code completion data.
 * 
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public final class StaticTagLibImpl implements TagLib {

    private static final Logger LOGGER = Logger.getLogger(TagLib.class.getName());
    
	private static final String COMPLETION_ID_SEPARATOR = "|";

    private static final String WITHIN_ATTR = "within";
	private static final String ATTR_ELEM = "attr";
	private static final String TAG_ELEM = "tag";
	private static final String NAME_ATTR = "name";
    
	private String name;
    
    /** map - key String-tag name, value Collection of Strings (tag attrs) */
    private Map tags;
    private Map nestedtags;
    private Set roottags;
    private Map attrCompletions;
    
    private StaticTagLibImpl() {
        tags = new HashMap();
        nestedtags = new HashMap();
        roottags = new HashSet();
        attrCompletions = new HashMap();
    }
    
    public StaticTagLibImpl(String uri) throws Exception {
        this();
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("org/mevenide/grammar/resources/taglibs/" + uri.replaceFirst(":", "-") + ".xml");
            configure(is);
        }
        finally {
            if ( is != null ) {
            	is.close();
            }
        }
    }
    
    /** Creates a new instance of TagLib */
    public StaticTagLibImpl(InputStream libDef) throws Exception {
        this();
        configure(libDef);
    }

    private void configure(InputStream stream) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document schemaDoc = builder.build(stream);
        Element root = schemaDoc.getRootElement();
        setName(root.getAttributeValue(NAME_ATTR));
        Iterator it = root.getChildren(TAG_ELEM).iterator();
        while (it.hasNext()) {
            Element tagEl = (Element) it.next();
            String tagName = tagEl.getAttributeValue(NAME_ATTR);
            Iterator it2 = tagEl.getChildren(ATTR_ELEM).iterator();
            Collection col = new ArrayList(10);
            while (it2.hasNext()) {
                Element attrEl = (Element) it2.next();
                String attr = attrEl.getAttributeValue(NAME_ATTR);
                if (attr == null) {
                    throw new IOException("Badly formed document. Name attribute for 'attribute' element is mandatory.");
                }
                col.add(attr);
                String attrCompls = attrEl.getAttributeValue("cctypes");
                if (attrCompls != null) {
                    String completionID = tagName + COMPLETION_ID_SEPARATOR + attr;
                    Collection compl = new ArrayList();
                    attrCompletions.put(completionID, compl);
                    StringTokenizer tok = new StringTokenizer(attrCompls, ",", false);
                    while (tok.hasMoreTokens()) {
                        compl.add(tok.nextToken());
                    }
                }
            }
            addTag(tagName, col);
            String within = tagEl.getAttributeValue(WITHIN_ATTR);
            if (within != null) {
                StringTokenizer tok = new StringTokenizer(within, ",", false);
                while (tok.hasMoreTokens()) {
                    String parent = tok.nextToken();
                    Collection nest = (Collection) nestedtags.get(parent);
                    if (nest == null) {
                        nest = new ArrayList();
                        nestedtags.put(parent, nest);
                    }
                    nest.add(tagName);
                }
            }
            else {
                roottags.add(tagName);
            }
        }
    }

    private void setName(String libname) {
        name = libname;
    }

    private void addTag(String tagName, Collection attrs) {
        tags.put(tagName, attrs);
    }

    public String getName() {
        return name;
    }

    public Collection getTagAttrs(String tag) {
        Collection toReturn = (Collection) tags.get(tag);
        return toReturn == null ? Collections.EMPTY_LIST : toReturn;
    }

    public Collection getRootTags() {
        return roottags;
    }

    public Collection getSubTags(String tagName) {
        return (Collection) nestedtags.get(tagName);
    }

    public Collection getAttrCompletionTypes(String tag, String attribute) {
        String completionID = tag + COMPLETION_ID_SEPARATOR + attribute;
        Collection compls = (Collection) attrCompletions.get(completionID);
        return compls == null ? Collections.EMPTY_LIST : compls;
    }
   
}