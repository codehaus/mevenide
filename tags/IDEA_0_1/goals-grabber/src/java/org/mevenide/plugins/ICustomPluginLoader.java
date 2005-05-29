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


package org.mevenide.plugins;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public interface ICustomPluginLoader {
    
    /**
     * loads the enhanced properties for the given plugin.
     * @param plugin name of the plugin
     * @param version version of the plugin
     * @param exactMatch if true, shall return anything only if the Loader has the exact version available. 
     *                        else it's free to attempt a best match.
     * @returns null if such info doesn't exist, or an array of property descriptions
     */
    PluginProperty[] loadProperties(String plugin, String version, boolean exactMatch);
    
}
