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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;

/**
 * A IPropertyFinder implementation that maps a single properties file.
 * The cache is based on PropertyModel.
 * @deprecated
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
final class SinglePropertyFileFinder implements IPropertyFinder {
    private static final Logger LOGGER = Logger.getLogger(SinglePropertyFileFinder.class.getName());
    
    private File propFile = null;
    private PropertyModel propModel = null;
    private boolean regenerate;
    private boolean skip = false;
    /** Creates a new instance of SinglePropFile */
    SinglePropertyFileFinder (File file) {
        propFile = file;
        regenerate = true;
        skip = false;
    }
    
    public String getValue(String key) {
        if (!skip) {
            try {
                if (regenerate) {
                    readModel();
                }
            } catch (IOException exc) {
                LOGGER.log(Level.SEVERE,"Cannot read file", exc);
                skip = true;
                return null;
            }
            KeyValuePair pair = propModel.findByKey(key);
            if (pair != null) {
                return pair.getValue();
            }
        }
        return null;
    }
    
    private void readModel() throws IOException {
        propModel = PropertyModelFactory.getFactory().newPropertyModel(new FileInputStream(propFile));
    }
    
    public void reload() {
        skip = false;
        regenerate = true;
    }
    
}

