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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.sysenv.DefaultSysEnvProvider;
import org.mevenide.environment.sysenv.SysEnvProvider;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: SysEnvLocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class SysEnvLocationFinder extends AbstractLocationFinder {
    private static Log log = LogFactory.getLog(SysEnvLocationFinder.class); 
    
    private static SysEnvLocationFinder locationFinder;
    private static SysEnvProvider defaultProvider = null;
    private SysEnvProvider provider = null;
    private static Object LOCK = new Object();
	
    private SysEnvLocationFinder() {
    }
    
    // needs to be public because of the property resolver..
    public static SysEnvLocationFinder getInstance() {
        if (locationFinder == null) {
            synchronized (LOCK) {
                if (locationFinder == null) {
                    if (defaultProvider == null) {
                        defaultProvider = new DefaultSysEnvProvider();
                    }
                    locationFinder = new SysEnvLocationFinder();
                    locationFinder.setSysEnvProvider(defaultProvider);
                }
            }
        }
        return locationFinder; 
    }
    
    /**
     * Sets the SysEnv provider instance for the locationFinder. Please not that if you
     * define a custom one impl, you should set it *before* the singleton SysEnvLocationFinder instance is 
     * created, thus before the getInstance() method is called for the first time. Best place is during the
     * startup sequence of your IDE. (For performance reasons your provider impl should be lazy initialized).
     */
    public static void setDefaultSysEnvProvider(SysEnvProvider prov) {
        synchronized (LOCK) {
            defaultProvider = prov;
            if (locationFinder != null) {
                // if setting provider later in the game, do discard the created LocationFinder??
                // or just ignore? or set to the current singleton?
                locationFinder = null;
                log.warn("Setting defaultSysEnvProvider while the singleton isntance of SysEnvLocationFinder exists");
            }
        }
    }
    
    private void setSysEnvProvider(SysEnvProvider prov)
    {
        provider = prov;
    }
    
    public String getJavaHome() {
		return provider.getProperty("JAVA_HOME");
    }
    
    public String getMavenHome() {
		return provider.getProperty("MAVEN_HOME");
    }
    
    public String getMavenLocalHome() {
		return provider.getProperty("MAVEN_HOME_LOCAL");
    }
    
    public String getMavenLocalRepository() {
		return provider.getProperty("MAVEN_REPO_LOCAL");
    }
    
    public String getMavenPluginsDir() {
        // makes no sense
        return null;
    }
    
    public String getUserHome() {
        return System.getProperty("user.home");
    }

    public String getUserPluginsDir() {
        // makes no sense
        return null;
    }

    public String getPluginJarsDir() {
        // makes no sense
        return null;
    }
}
