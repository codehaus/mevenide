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
package org.mevenide.environment;

/**  
 * Finder for important maven locations.
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ILocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public interface ILocationFinder {
    
	String getMavenHome();
    
	String getJavaHome();
        
    String getMavenLocalHome();
    
    String getMavenLocalRepository();
    
    /**
     * directory with the expanded plugin cache, basically value of 
     * maven.plugin.unpacked.dir property
     */
	String getMavenPluginsDir();
    /**
     * directory with user's custom plugins, denoted by maven.plugin.user.dir property
     */    
        String getUserPluginsDir();
    /**
     * the directory containing maven plugin jars, denoted by maven.plugin.dir propertu
     */   
        String getPluginJarsDir();
    
    String getConfigurationFileLocation();
        
    String getUserHome();

}
