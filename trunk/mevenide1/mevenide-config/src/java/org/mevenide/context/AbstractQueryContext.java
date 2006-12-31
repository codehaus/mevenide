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

package org.mevenide.context;

import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyResolverFactory;


/**
 * utility abstract superclass, implementing getResolver() and getPropertyValue() methods.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public abstract class AbstractQueryContext implements IQueryContext {
    private IPropertyResolver resolver;
    
    protected AbstractQueryContext() {
    }
    
   /** 
     * the default property resolver for this instance of IQueryContext
     */
    public final IPropertyResolver getResolver() {
        if (resolver == null) {
            resolver = PropertyResolverFactory.getFactory().createContextBasedResolver(this);
        }
        return resolver;
    } 
    
   public String getPropertyValue(String key) {
        String toReturn = getUserPropertyValue(key);
        if (toReturn == null) {
            toReturn = getBuildPropertyValue(key);
        }
        if (toReturn == null) {
            toReturn = getProjectPropertyValue(key);
        }
        if (toReturn == null) {
            toReturn = getParentBuildPropertyValue(key);
        }
        if (toReturn == null) {
            toReturn = getParentProjectPropertyValue(key);
        }
        return toReturn;
    }    
}
