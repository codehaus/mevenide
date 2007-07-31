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

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mevenide.context.IQueryContext;
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
    private static final Logger LOGGER = Logger.getLogger(DefaultsResolver.class.getName());
    
    private PluginPropertiesFinder pluginDefaults;
    private static Properties defaults;
    private File projectDir;
    private File userDir;
    private IQueryContext context;
    private String lastUnpackedDir;
    private String lastPackedDir;
    private boolean reloading = false;
    //TODO lazy initialization
    static {
        try {
            InputStream stream = DefaultsResolver.class.getClassLoader().getResourceAsStream("org/mevenide/properties/maven_defaults.properties");
            defaults = new Properties();
            defaults.load(stream);
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "Cannot load default properties.", exc);
        }
    } 
    /** Creates a new instance of DefaultsResolver 
     * @deprecated
     */
    public DefaultsResolver(File projectFile, File userFile) {
        userDir = userFile;
        projectDir = projectFile;
    }
    
    public DefaultsResolver(IQueryContext cont) {
        context = cont;
    }
    
    void initPluginPropsFinder(PluginPropertiesFinder finder) {
        pluginDefaults = finder;
    }
    
    public String getValue(String key) {
        return getDefault(key);
    }
    
    public void reload() {
    }
    
    
    private String getDefault(String key) {
        if ("basedir".equals(key)) { //NOI18N
            if (context != null) {
                File proj = context.getProjectDirectory();
                return proj == null ? null : proj.getAbsolutePath();
            } else {
                return projectDir.getAbsolutePath();
            }
        }
        if ("user.home".equals(key)) { //NOI18N
            if (context != null) {
                return context.getUserDirectory().getAbsolutePath();
            }
            return userDir.getAbsolutePath();
        }
        checkReload();
        String toReturn = defaults.getProperty(key);
        if (toReturn == null && pluginDefaults != null) {
            toReturn = pluginDefaults.getValue(key);
        }
        return toReturn;
    }   
    
    public Set getDefaultKeys() {
        checkReload();
        HashSet set = new HashSet(defaults.keySet());
        if (pluginDefaults != null) {
            set.addAll(pluginDefaults.getDefaultPluginKeys());
        }
        return set;
    }
    
    private synchronized void checkReload() {
        if (reloading) {
            return;
        }
        reloading = true;
        String val = context.getResolver().getResolvedValue("maven.plugin.unpacked.dir"); //NOI18N
        String val2 = context.getResolver().getResolvedValue("maven.plugin.dir"); //NOI18N
        if (   (val != null && !val.equals(lastUnpackedDir)) 
            || (val2 != null && !val2.equals(lastPackedDir))) {
            pluginDefaults = PropertyResolverFactory.getFactory().getPluginDefaultsPropertyFinder(new File(val), new File(val2));
            lastUnpackedDir = val;
            lastPackedDir = val2;
        }
        reloading = false;
    }
}
