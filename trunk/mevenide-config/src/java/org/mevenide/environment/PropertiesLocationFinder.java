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

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: PropertiesLocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public abstract class PropertiesLocationFinder extends AbstractLocationFinder {
    
    private Properties properties;
    
    protected PropertiesLocationFinder() throws FileNotFoundException, IOException {
    }
    
    protected void loadProperties() throws IOException, FileNotFoundException {
        String propertyFile = getPropertyFile();
        properties = new Properties();
        properties.load(new FileInputStream(propertyFile));
    }

    protected abstract String getPropertyFile();
    
    public String getJavaHome() {
		return properties.getProperty("java.home");
    }
    
    public String getMavenHome() {
		return properties.getProperty("maven.home");
    }
    
    public String getMavenLocalHome() {
		return properties.getProperty("maven.home.local");
    }
    public String getMavenLocalRepository() {
		return properties.getProperty("maven.repo.local");
    }
    public String getMavenPluginsDir() {
		return properties.getProperty("maven.plugins.dir");
    }
}
