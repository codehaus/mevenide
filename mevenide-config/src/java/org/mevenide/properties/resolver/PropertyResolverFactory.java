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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;

import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.IPropertyResolver;


/**  
 * factory to create instances of IPropertyResolver
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PropertyResolverFactory {
    
    private static PropertyResolverFactory factory = new PropertyResolverFactory();
     /**
     * map (key=absolutepath of the plugindir, value PluginPropertiesFinder instance 
     */
    private HashMap pluginDirProps = new HashMap();
   
    private Map resolvers = new TreeMap();
    
    private PropertyResolverFactory() {
    }
    
    public static PropertyResolverFactory getFactory() {
        return factory;
    }
    /**
     * @deprecated.. for performance reasons, use IQueryContext based methods
     */
    public IPropertyResolver getResolver(File projectDir, boolean forceRefresh) {
        PropertyFilesAggregator aggregator = (PropertyFilesAggregator) resolvers.get(projectDir.getAbsolutePath());
        
        if ( aggregator == null ) {
            String userHome = System.getProperty("user.home"); //NOI18N
            File userFile = new File(userHome);
            aggregator = new PropertyFilesAggregator(projectDir, userFile, 
                             new DefaultsResolver(projectDir, userFile));
            resolvers.put(projectDir.getAbsolutePath(), aggregator);
        }
        
        if ( forceRefresh ) {
            aggregator.reload();
        }
        
        return aggregator;
    }
    
    /**
     * @deprecated.. for performance reasons, use IQueryContext based methods
     */
    public IPropertyResolver getResolver(File projectDir) {
        return getResolver(projectDir, false);
    }

//
// querycontext based stuff..
//
    public IPropertyResolver createContextBasedResolver(IQueryContext context) {
        return new PropertyFilesAggregator(context, new DefaultsResolver(context)); 
    }
    
    /**
     * returns a cached or newly created instance of IPropertyFinder that maps the
     * maven plugin defaults for a given maven.plugin.dir
     */
   PluginPropertiesFinder getPluginDefaultsPropertyFinder(File pluginDir) {
        synchronized (pluginDirProps) {
            PluginPropertiesFinder propfinder = (PluginPropertiesFinder)pluginDirProps.get(pluginDir);
            if (propfinder == null) {
                propfinder = new PluginPropertiesFinder(pluginDir);
                pluginDirProps.put(pluginDir, propfinder);
            }
            return propfinder;
        }
    }    
}
