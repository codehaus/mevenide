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
package org.mevenide.properties.resolver;

import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyLocator;

/**  
 * factory for creating new instances of IPropertyLocator
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * 
 */
public class PropertyLocatorFactory {
    
    private static PropertyLocatorFactory factory = new PropertyLocatorFactory();
 
    private PropertyLocatorFactory() {
    }
    
    public static PropertyLocatorFactory getFactory() {
        return factory;
    }
 
//
// querycontext based stuff..
//
    public IPropertyLocator createContextBasedLocator(IQueryContext context) {
        return new PropertyFilesAggregator(context, new DefaultsResolver(context));
    }
    
}   
 
