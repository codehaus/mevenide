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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * provider of values for the CarefulProjectMarshaller, 
 * that iterates over the org.apache.maven.Project bean using reflection.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class BeanContentProvider implements IContentProvider {
    private static final Log log = LogFactory.getLog(BeanContentProvider.class);
    
    private Object bean;
    
    /** Creates a new instance of BeanContentProvider */
    public BeanContentProvider(Object obj) {
        bean = obj;
    }

    public IContentProvider getSubContentProvider(String key) {
        Object toReturn = retrieveValue(key);
        return toReturn != null ? new BeanContentProvider(toReturn) : null;
    }

    public List getSubContentProviderList(String parentKey, String childKey) {
        Object obj = retrieveValue(parentKey);
        if (obj != null && obj instanceof Collection) {
            Collection list = (Collection)obj;
            ArrayList toReturn = new ArrayList(list.size());
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object obj2 = it.next();
                toReturn.add(new BeanContentProvider(obj2));
            }
            return toReturn;
        }
        return null;
    }
    
    public String getValue(String key) { 
        Object toReturn = retrieveValue(key);
        return toReturn != null ? toReturn.toString() : null;
    }
    
    private Object retrieveValue(String key) {
        if (bean == null) {
            return null;
        }
        try {
            Method method = bean.getClass().getMethod(createGetter(key), null);
            Object returned = method.invoke(bean, null);
            return returned;
        } catch (Exception exc) {
            String err = "Called " + createGetter(key) + " on " + bean.getClass().getName() + " and failed";
            log.error(err, exc);
        }
        return null;
    }
    
    private String createGetter(String key) {
        return "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    public List getValueList(String parentKey, String childKey) {
        Object toReturn = retrieveValue(parentKey);
        if (toReturn instanceof List) {
            return (List)toReturn;
        } else {
            if (toReturn != null) {
                log.error("Called " + createGetter(parentKey) + " on " + bean.getClass().getName() + " and failed returning " + toReturn.getClass());
            }
        }
        return null;
    }

    public List getProperties() {
        Object toReturn = retrieveValue("properties");
        if (toReturn instanceof List) {
            return (List)toReturn;
        } else {
            if (toReturn != null) {
                log.error("Called getProperties on " + bean.getClass().getName() + " and failed returning " + toReturn.getClass());
            }
        }
        return null;
    }

}
