/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.properties.IPropertyFinder;

/**
 * a IPropertyFinder implementation that contains the default values.
 * Is to be used when none of the properties files defines the property.
 * Takes the values partly from ILocationFinder values and partly from the
 * a special prop file that contains the assumed maven defaults.
 *
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public final class DefaultsResolver implements IPropertyFinder {
    private static final Log logger = LogFactory.getLog(DefaultsResolver.class);
    
    private ILocationFinder locFinder;
    private IPropertyFinder pluginDefaults;
    private static Properties defaults;
    private File projectDir;
    private File userDir;
    //TODO lazy initialization
    static {
        try {
            InputStream stream = DefaultsResolver.class.getClassLoader().getResourceAsStream("org/mevenide/properties/maven_defaults.properties");
            defaults = new Properties();
            defaults.load(stream);
        } catch (Exception exc) {
            logger.error("Cannot load default properties.", exc);
        }
    } 
    /** Creates a new instance of DefaultsResolver */
    public DefaultsResolver(File projectFile, File userFile, ILocationFinder find) {
        userDir = userFile;
        projectDir = projectFile;
        locFinder = find;
    }
    
    
    /** Creates a new instance of DefaultsResolver */
    public DefaultsResolver(File projectFile, File userFile, ILocationFinder find, 
                            IPropertyFinder pluginDefaultsProps) {
        this(projectFile, userFile, find);
        pluginDefaults = pluginDefaultsProps;
    }
    
    public String getValue(String key) {
        return getDefault(key);
    }
    
    public void reload() {
    }
    
    
    private String getDefault(String key) {
        if ("basedir".equals(key)) { //NOI18N
            return projectDir.getAbsolutePath();
        }
        if ("user.home".equals(key)) { //NOI18N
            return userDir.getAbsolutePath();
        }
        if ("maven.home".equals(key)) { //NOI18N
            return locFinder.getMavenHome();
        }
        if ("maven.home.local".equals(key)) { //NOI18N
            return locFinder.getMavenLocalHome();
        }
        String toReturn = defaults.getProperty(key);
        if (toReturn == null && pluginDefaults != null) {
            toReturn = pluginDefaults.getValue(key);
        }
        return toReturn;
    }   
    
    public Set getDefaultKeys() {
        HashSet set = new HashSet(defaults.keySet());
        if (pluginDefaults != null && pluginDefaults instanceof PluginPropertiesFinder) {
            set.addAll(((PluginPropertiesFinder)pluginDefaults).getDefaultPluginKeys());
        }
        return set;
    }
    
}
