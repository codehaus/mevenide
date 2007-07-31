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
package org.mevenide.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.mevenide.grammar.impl.EmptyAttributeCompletionImpl;
import org.mevenide.grammar.impl.EmptyTagLibImpl;

/**
 * Manager of TagLib instances. Works as a cache lazyily grabbing taglibs from
 * the TagLibProvider.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class TagLibManager {
    
    private static final Logger LOGGER = Logger.getLogger(TagLibManager.class.getName());
    
    private TagLibProvider provider;
    private AttrCompletionProvider attrComplProvider;
    private Map libcache;
    private Map attrCompletionCache;
    
    
    /** Creates a new instance of TagLibManager */
    public TagLibManager() {
        libcache = new HashMap();
        attrCompletionCache = new HashMap();
    }
    
    /**
     * sets a TagLibProvider instance that will be used to populate the cache.
     */
    public void setProvider(TagLibProvider prov) {
        provider = prov;
    }
    
    /**
     * sets the AttrCompletionProvider instance that will populate the cache.
     */
    public void setAttrCompletionProvider(AttrCompletionProvider prov) {
        attrComplProvider = prov;
    }
    
    /**
     * get available taglibnames. Returns only String[] so that they  don't need to be fully
     * initialized. At this point the provider has to be set.
     */
    public String[] getAvailableTagLibs() {
        assertHasProvider();
        return provider.getAvailableTags();
    }
    
    /**
     * Loads the tagLib instance by name. Consults the cache first, if not found, gets it from the 
     * provider. If not found there, it's not supported somehow, puts an instance of EmptyTagLibImpl in place.
     * Should never return null. At this point the provider has to be set.
     */
    public TagLib getTagLibrary(String name) {
        assertHasProvider();
        TagLib lib = (TagLib)libcache.get(name);
        if (lib == null) {
            lib = provider.retrieveTagLib(name);
            if (lib == null) {
                LOGGER.severe("No such taglibrary defined by provider:" + name);
                // create empty impl
                lib = new EmptyTagLibImpl(name);
            } 
            libcache.put(name, lib);
        }
        return lib;
    }

    public AttributeCompletion getAttributeCompletion(String name) {
        assertHasProvider();
        AttributeCompletion compl = (AttributeCompletion)attrCompletionCache.get(name);
        if (compl == null) {
            compl = attrComplProvider.retrieveAttributeCompletion(name);
            if (compl == null) {
                LOGGER.severe("No such attribute completion defined by provider:" + name);
                // create empty impl
                compl = new EmptyAttributeCompletionImpl(name);
            } 
            attrCompletionCache.put(name, compl);
        }
        return compl;
    }
    
    private void assertHasProvider() {
        if (provider == null) {
            LOGGER.severe("No taglib provider assigned.");
            throw new RuntimeException("No taglib provider assigned.");
        }
    }
}
