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

package org.mevenide.project.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

/**
 * provider of values for the CarefulProjectMarshaller, that iterates over JDOM's Elements to find the values.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class ElementContentProvider implements IContentProvider {
    
    private static final Log log = LogFactory.getLog(ElementContentProvider.class);
    
    private Element element;
    
    /** Creates a new instance of ElementContentProvider */
    public ElementContentProvider(Element parent) {
        element = parent;
    }

    public IContentProvider getSubContentProvider(String key) {
        Element child = element.getChild(key);
        return child != null ? new ElementContentProvider(child) : null;
    }

    public String getValue(String key) {
        Element child = element.getChild(key);
        return child != null ? child.getText() : null;
    }

    public List getSubContentProviderList(String parentKey, String childKey) {
       Element parent = element.getChild(parentKey);
       if (parent != null) {
            Collection list = parent.getChildren(childKey);
            if (list.size() > 0) {
                ArrayList toReturn = new ArrayList(list.size());
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Element obj2 = (Element)it.next();
                    toReturn.add(new ElementContentProvider(obj2));
                }
                return toReturn;
            }
        }
        return null;
    }
 
    public List getValueList(String parentKey, String childKey) {
       Element parent = element.getChild(parentKey);
       if (parent != null) {
            Collection list = parent.getChildren(childKey);
            if (list.size() > 0) {
                ArrayList toReturn = new ArrayList(list.size());
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Element obj2 = (Element)it.next();
                    toReturn.add(obj2.getText());
                }
                return toReturn;
            }
        }
        return null;
    }

    public List getProperties() {
        Element props = element.getChild("properties"); //NOI18N
        if (props != null) {
            List toReturn = new ArrayList();
            List childs = props.getChildren();
            Iterator it = childs.iterator();
            while (it.hasNext()) {
                Element el = (Element)it.next();
                String value = el.getText();
                String str = el.getName() + ":" + value; //NOI18N
                toReturn.add(str);
            }
            return toReturn;
        }
        return null;
    }
    
}
