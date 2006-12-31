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
package org.mevenide.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.project.Project;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyLocatorFactory;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public final class ResolverUtils {
    
    private static class QueryContextObjects {
        IPropertyResolver resolver;
        IPropertyLocator locator;
        
        public QueryContextObjects(IPropertyResolver resolver, IPropertyLocator locator) {
            this.resolver = resolver;
            this.locator = locator;
        }
    }
    
    private static ResolverUtils instance = new ResolverUtils();
    
    private Map resolvers = new HashMap(); 
    private Map locators = new HashMap();
    
    private ResolverUtils() {
    }
    
    public static ResolverUtils getInstance() {
        return instance;
    }
    
    public String resolve(Project project, String value) {
        if ( value == null ) {
            return value;
        }
        
        IPropertyResolver resolver = getResolver(project.getFile());
        
        String workingValue = value;
        String tmp = "";
        
        while ( workingValue.indexOf("${") > -1 ) {
            int begin = workingValue.indexOf("${");
            tmp += workingValue.substring(0, begin);
            int end = workingValue.indexOf("}");
            if ( end > -1 ) {
                //String resolved = resolver.resolveString(workingValue.substring(begin + 2, end));
                String resolved = resolver.resolveString(workingValue.substring(begin, end + 1));
                tmp += (resolved != null) ? resolved : "${" + workingValue.substring(begin, end) + "}";
                workingValue = workingValue.substring(end + 1);
            }
        }
        
        return tmp + workingValue;
    }
    
    
    public IPropertyResolver getResolver(File projectFile) {
        IPropertyResolver resolver = null;
        if ( !resolvers.containsKey(projectFile) ) {
            QueryContextObjects objects = newQueryContextObjects(projectFile);
            resolver = objects.resolver;
            resolvers.put(projectFile, resolver);
            resolvers.put(projectFile, objects.locator);
        }
        return (IPropertyResolver) resolvers.get(projectFile);
    }
    
    public IPropertyLocator getLocator(File projectFile) {
        IPropertyLocator locator = null;
        if ( !resolvers.containsKey(projectFile) ) {
            QueryContextObjects objects = newQueryContextObjects(projectFile);
            locator = objects.locator;
            locators.put(projectFile, objects.resolver);
            locators.put(projectFile, locator);
        }
        return (IPropertyLocator) locators.get(projectFile);
    }
    
    private QueryContextObjects newQueryContextObjects(File projectFile) {
        IQueryContext queryContext = new DefaultQueryContext(projectFile.getParentFile());
        IPropertyResolver resolver = queryContext.getResolver();
        IPropertyLocator locator = PropertyLocatorFactory.getFactory().createContextBasedLocator(queryContext);
        return new QueryContextObjects(resolver, locator);
    }
    
}
