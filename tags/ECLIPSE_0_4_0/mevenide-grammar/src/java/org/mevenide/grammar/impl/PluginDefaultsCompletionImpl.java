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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.grammar.AttributeCompletion;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.resolver.PropertyLocatorFactory;

/**
 * Implementation of a attribute completion that gives the default plugin property keys
 * 
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class PluginDefaultsCompletionImpl implements AttributeCompletion {

    private static Log logger = LogFactory.getLog(PluginDefaultsCompletionImpl.class);
    private IPropertyLocator locator;
    private WeakHashMap subSetsMap;
    /** Creates a new instance of GoalsAttributeCompletionImpl */
    public PluginDefaultsCompletionImpl() {
        IQueryContext context = DefaultQueryContext.getNonProjectContextInstance();
        locator = PropertyLocatorFactory.getFactory().createContextBasedLocator(context);
        subSetsMap = new WeakHashMap();
    }

    public String getName() {
        return "pluginDefaults";
    }

    public Collection getValueHints(String start) {
        // consult cache first..
        Set result = (Set)subSetsMap.get(start);
        if (result != null) {
            return result;
        }
        Set startingSet = findClosestSubSet(start.substring(0, start.length() - 1));
        TreeSet toReturn = new TreeSet();
        Iterator it = startingSet.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            if (key.startsWith(start)) {
                toReturn.add(key);
            }
        }
        subSetsMap.put(start, toReturn);
        return toReturn;
    }
    
    private Set findClosestSubSet(String start) {
        String key = start;
        // 5 is magic number.. all maven props start with maven. no increated performance gained when we try caching those
        while (key.length() > 5) {
            Object obj = subSetsMap.get(key);
            if (obj != null) {
                return (Set)obj;
            }
            key = key.substring(0, key.length() - 1);
        }
        return locator.getKeysAtLocation(IPropertyLocator.LOCATION_DEFAULTS);
    }
}