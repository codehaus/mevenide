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

package org.mevenide.grammar;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manager of tagLib instances of TagLib instances.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */

public final class TagLibManager {
    
    private static Log logger = LogFactory.getLog(TagLibManager.class);
    
    private TagLibProvider provider;
    private Map libcache;
    
    
    /** Creates a new instance of TagLibManager */
    public TagLibManager() {
        libcache = new HashMap();
    }
    
    
    public void setProvider(TagLibProvider prov) {
        provider = prov;
    }
    
    public String[] getAvailableTagLibs() {
        assertHasProvider();
        return provider.getAvailableTags();
    }
    
    
    public TagLib getTagLibrary(String name) {
        TagLib lib = (TagLib)libcache.get(name);
        if (lib == null) {
            lib = provider.retrieveTagLib(name);
            if (lib == null) {
                logger.error("No such taglibrary defined:" + name);
            } else {
                libcache.put(name, lib);
            }
        }
        return lib;
    }
    
    
    private void assertHasProvider() {
        if (provider == null) {
            logger.fatal("No taglib provider assigned.");
            throw new RuntimeException("No taglib provider assigned.");
        }
    }
}
