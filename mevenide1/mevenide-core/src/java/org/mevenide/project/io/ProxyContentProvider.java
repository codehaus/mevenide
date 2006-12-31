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
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * provider of values for the CarefulProjectMarshaller, that proxies another content provider,
 * allowing to customize behaviour.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ProxyContentProvider implements IContentProvider {
    
    private static final Log log = LogFactory.getLog(ProxyContentProvider.class);
    
    protected IContentProvider provider;
    /** Creates a new instance of ElementContentProvider */
    public ProxyContentProvider(IContentProvider origin) {
        provider = origin;
    }

    protected IContentProvider createChildContentProvider(IContentProvider origin) {
        return new ProxyContentProvider(origin);
    }
    
    public IContentProvider getSubContentProvider(String key) {
        IContentProvider child = provider.getSubContentProvider(key);
        return child != null ? createChildContentProvider(child) : null;
    }

    public String getValue(String key) {
        return provider.getValue(key);
    }

    public List getSubContentProviderList(String parentKey, String childKey) {
        List orig = provider.getSubContentProviderList(parentKey, childKey);
        if (orig != null) {
            Iterator it = orig.iterator();
            List toReturn = new ArrayList();
            while (it.hasNext()) {
                IContentProvider obj = (IContentProvider)it.next();
                toReturn.add(createChildContentProvider(obj));
            }
            return toReturn;
        }
        return null;
    }
 
    public List getValueList(String parentKey, String childKey) {
        return provider.getValueList(parentKey, childKey);
    }

    public List getProperties() {
        return provider.getProperties();
    }
 
}
