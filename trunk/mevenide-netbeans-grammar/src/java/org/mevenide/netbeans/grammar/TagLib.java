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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Container for tag library code completion data.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class TagLib
{
    private static Log logger = LogFactory.getLog(TagLib.class);
    
    private String name;
    /**
     * map - key String-tag name, value Collection of Strings (tag attrs)
     */
    private Map tags;
    /** Creates a new instance of TagLib */
    public TagLib(InputStream libDef) throws Exception
    {
        tags = new HashMap();
        configure(libDef);
    }
    
    private void configure(InputStream stream) throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        logger.debug("stream is not null=" + (stream != null));
        Document schemaDoc = builder.build(stream);
        logger.debug("stream parsed");
        Element root = schemaDoc.getRootElement();
        setName(root.getAttributeValue("name"));
        logger.debug("name=" + getName());
        Iterator it = root.getChildren("tag").iterator();
        while (it.hasNext())
        {
            Element tagEl = (Element)it.next();
            String tagName = tagEl.getAttributeValue("name");
            logger.debug("tagname=" + tagName);
            Iterator it2 = tagEl.getChildren("attr").iterator();
            Collection col = new ArrayList(10);
            while (it2.hasNext())
            {
                Element attrEl = (Element)it2.next();
                String attr = attrEl.getAttributeValue("name");
                logger.debug("attr=" + attr);
                if (attr != null)
                {
                    col.add(attr);
                }
            }
            addTag(tagName, col);
        }
    }

    private void setName(String name)
    {
        this.name = name;
    }
    
    private void addTag(String name, Collection attrs)
    {
        tags.put(name, attrs);
    }
    
    public String getName() {
        return name;
    }
    
    public Collection getTags() {
        return tags.keySet();
    }
    
    public Collection getTagAttrs(String tag)
    {
        return (Collection)tags.get(tag);
    }
}
