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

import java.util.Set;

/**
 * information about the plugin.
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public interface IPluginInfo {
    
    /**
     * name of the plugin.
     */
    String getName();
    
    /**
     * the current version of the plugin installed.
     */
    String getVersion();
    
    /**
     * equals <name>-<version>
     */
    String getArtifactId();
    
    
    String getLongName();
    
    String getDescription();
    
    /**
     * Set of <code>String</code>, property keys of the plugin.
     */
    Set getPropertyKeys();
    
    /**
     * Set of <code>PluginProperty></code> instances
     */
    Set getEnhancedPropertyInfo();
    
}
