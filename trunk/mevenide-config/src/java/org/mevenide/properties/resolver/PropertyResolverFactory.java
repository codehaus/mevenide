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
package org.mevenide.properties.resolver;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.mevenide.context.IQueryContext;

import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.IPropertyResolver;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PropertyResolverFactory {
    
    private static PropertyResolverFactory factory = new PropertyResolverFactory();
    
    private Map resolvers = new TreeMap();
    
    private PropertyResolverFactory() {
    }
    
    public static PropertyResolverFactory getFactory() {
        return factory;
    }
    
    public IPropertyResolver getResolver(File projectDir, boolean forceRefresh) {
        PropertyFilesAggregator aggregator = (PropertyFilesAggregator) resolvers.get(projectDir.getAbsolutePath());
        
        if ( aggregator == null ) {
            String userHome = System.getProperty("user.home"); //NOI18N
            File userFile = new File(userHome);
            LocationFinderAggregator finder = new LocationFinderAggregator();
            finder.setEffectiveWorkingDirectory(projectDir.getAbsolutePath());
            aggregator = new PropertyFilesAggregator(projectDir, userFile, new DefaultsResolver(projectDir, userFile, finder));
            resolvers.put(projectDir.getAbsolutePath(), aggregator);
        }
        
        if ( forceRefresh ) {
            aggregator.reload();
        }
        
        return aggregator;
    }
    
    public IPropertyResolver createContextBasedResolver(IQueryContext context) {
        LocationFinderAggregator finder = new LocationFinderAggregator(context);
        return new PropertyFilesAggregator(context, 
            				new DefaultsResolver(context.getProjectDirectory(), 
                                                             context.getUserDirectory(), 
                                                             finder));
    }
    
    public IPropertyResolver getResolver(File projectDir) {
        return getResolver(projectDir, false);
    }
    
}
