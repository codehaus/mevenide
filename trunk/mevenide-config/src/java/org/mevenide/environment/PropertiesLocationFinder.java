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
package org.mevenide.environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.mevenide.context.IQueryContext;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @author Milos Kleint (ca206216@tiscali.cz)
 * @version $Id: PropertiesLocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public abstract class PropertiesLocationFinder extends AbstractLocationFinder {
    
    private Properties properties;
    private IQueryContext context;

    protected PropertiesLocationFinder(IQueryContext querycontext)  {
        context = querycontext;
    }
    
    
    protected PropertiesLocationFinder() throws FileNotFoundException, IOException {
    }
    
    protected void loadProperties() throws IOException, FileNotFoundException {
        if (context != null) {
            throw new IllegalStateException("Using both context and internal property loading.");
        }
        String propertyFile = getPropertyFile();
        properties = new Properties();
        properties.load(new FileInputStream(propertyFile));
    }

    /**
     * IQueryContext of this instance, if available.
     */
    protected final IQueryContext getContext() {
        return context;
    }
    /**
     * subclasses delegate to a specific method in the context to retrieve the property.
     */
    protected abstract String getContextPropertyValue(String key);
    
    protected abstract String getPropertyFile();
    
    public String getJavaHome() {
        if (context != null) {
            return getContextPropertyValue("java.home");
        }
        return properties.getProperty("java.home");
    }
    public String getMavenHome() {
        if (context != null) {
            return getContextPropertyValue("maven.home");
        }
        return properties.getProperty("maven.home");
    }
    public String getMavenLocalHome() {
        if (context != null) {
            return getContextPropertyValue("maven.home.local");
        }
        return properties.getProperty("maven.home.local");
    }
    public String getMavenLocalRepository() {
        if (context != null) {
            return getContextPropertyValue("maven.repo.local");
        }
        return properties.getProperty("maven.repo.local");
    }
    public String getMavenPluginsDir() {
        if (context != null) {
            return getContextPropertyValue("maven.plugin.unpacked.dir");
        }
        return properties.getProperty("maven.plugin.unpacked.dir");
    }
}
