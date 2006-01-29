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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.properties.IPropertyFinder;

/**
 * a IPropertyFinder implementation that iterates the installed plugin's 
 * plugin.properties files for the default values.
 * Is to be used when none of the normal properties files defines the property.
 *
 * TODO: logic for refresh, remember the plugins included, check for lastmodified of the files??
 * mkleint - not on my priority list, new plugin install or upgrade during IDE lifetime is out-of-scope for now.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public final class PluginPropertiesFinder implements IPropertyFinder {
    private static final Log logger = LogFactory.getLog(PluginPropertiesFinder.class);
    
    private File unpackedPluginDir;
    private File pluginDir;
    private File valid;
    private Properties props;
    private long lastModified = -1;
    private Object LOCK = new Object();
    /** Creates a new instance of DefaultsResolver */
    PluginPropertiesFinder(File unPlugDir, File packedDir) {
        unpackedPluginDir = unPlugDir;
        pluginDir = packedDir;
        valid = new File(pluginDir, "valid.cache");
    }
    
    public String getValue(String key) {
        synchronized (LOCK) {
            checkReload();
            return props.getProperty(key);
        }
    }
    
    public Set getDefaultPluginKeys() {
        synchronized (LOCK) {
            checkReload();
            return new HashSet(props.keySet());
        }
    }
    
    public void reload() {
        synchronized(LOCK) {
            checkReload();
        }
    }
    
    private void checkReload() {
        long validStamp = valid.exists() ? valid.lastModified() : 0;
        if (validStamp != lastModified) {
            loadAllProperties();
        }
        lastModified = validStamp;
    }
    
    private void loadAllProperties() {
        props = new Properties();
        if (valid.exists() && unpackedPluginDir.exists()) {
            File[] plugins = unpackedPluginDir.listFiles();
                for (int i = 0; i < plugins.length; i++) {
                    if (plugins[i].isDirectory()) {
                        loadDefaults(props, plugins[i]);
                    }
                }
        } else if (pluginDir.exists()) {
            // try loading from the jars in maven/lib
            File[] plugins = pluginDir.listFiles();
                for (int i = 0; i < plugins.length; i++) {
                    if (plugins[i].isFile() && plugins[i].getName().endsWith("jar")) {
                        loadJarDefaults(props, plugins[i]);
                    }
                }
        }
    }
    
    private void loadDefaults(Properties pros, File dir) {
        File propFile = new File(dir, "plugin.properties"); //NOI18N
        if (propFile.exists()) {
            try {
                pros.load(new BufferedInputStream(new FileInputStream(propFile)));
            } catch (IOException exc) {
                logger.warn("Cannot read defaults from file:" + propFile, exc); //NOI18N
            }
        }
    }
    
    private void loadJarDefaults(Properties pros, File jarFile) {
        JarFile jf = null;
        try {
            jf = new JarFile(jarFile);
            JarEntry je = jf.getJarEntry("plugin.properties");
            if (je != null) {
                pros.load(new BufferedInputStream(jf.getInputStream(je)));
            }
        } catch (IOException exc) {
            logger.warn("Cannot read defaults from file:" + jarFile, exc); //NOI18N
        } finally {
            if (jf != null) {
                try {
                    jf.close();
                } catch (IOException e) {
                    
                }
            }
        }
    }
}
