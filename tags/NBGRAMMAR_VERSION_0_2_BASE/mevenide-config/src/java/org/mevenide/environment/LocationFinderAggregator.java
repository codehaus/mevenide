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
package org.mevenide.environment;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: LocationFinderAggregator.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class LocationFinderAggregator implements ILocationFinder {
    
    private static Log log = LogFactory.getLog(LocationFinderAggregator.class);
    
    private UserRefinedPropertiesLocationFinder userRefinedPropertiesLocationFinder;
    private ProjectPropertiesLocationFinder projectPropertiesLocationFinder;
    private BuildPropertiesLocationFinder buildPropertiesLocationFinder;
    private SysEnvLocationFinder sysEnvLocationFinder;
    private CustomLocationFinder customLocationFinder;
    
    public LocationFinderAggregator() {
        sysEnvLocationFinder = SysEnvLocationFinder.getInstance();
        
        try {
            buildPropertiesLocationFinder = BuildPropertiesLocationFinder.getInstance();
        }
        catch ( Exception e ) { 
            log.debug("BuildPropertiesLocationFinder not created", e);
        }
       
    }
    
    public void setEffectiveWorkingDirectory(String effectiveWorkingDirectory) {
        try {
	        userRefinedPropertiesLocationFinder = new UserRefinedPropertiesLocationFinder(effectiveWorkingDirectory);
        }
		catch ( Exception e ) { 
			log.debug("UserRefinedPropertiesLocationFinder not created", e);
		}
        try {
            projectPropertiesLocationFinder = new ProjectPropertiesLocationFinder(effectiveWorkingDirectory);
        }
		catch ( Exception e ) { 
			log.debug("ProjectPropertiesLocationFinder not created", e);
		}
    }

    public String getConfigurationFileLocation() {
        String configurationFile = null;
        if ( projectPropertiesLocationFinder !=  null 
                && projectPropertiesLocationFinder.getConfigurationFileLocation() != null ) {
			configurationFile = projectPropertiesLocationFinder.getConfigurationFileLocation();
		}
		if ( userRefinedPropertiesLocationFinder !=  null 
		        && userRefinedPropertiesLocationFinder.getConfigurationFileLocation() != null ) {
			configurationFile = userRefinedPropertiesLocationFinder.getConfigurationFileLocation();
		}        
		if ( buildPropertiesLocationFinder !=  null 
		        && buildPropertiesLocationFinder.getConfigurationFileLocation() != null ) {
			configurationFile = buildPropertiesLocationFinder.getConfigurationFileLocation();
		}
		if ( sysEnvLocationFinder !=  null 
		        && sysEnvLocationFinder.getConfigurationFileLocation() != null ) {
		    configurationFile = sysEnvLocationFinder.getConfigurationFileLocation();
	    }
        return configurationFile;
    }

    public String getJavaHome() {
		String javaHome = System.getProperty("java.home");;
		if ( customLocationFinder !=  null 
				&& customLocationFinder.getJavaHome() != null ) {
			javaHome = customLocationFinder.getJavaHome();
		}
		if ( projectPropertiesLocationFinder !=  null 
				&& projectPropertiesLocationFinder.getJavaHome() != null ) {
			javaHome = projectPropertiesLocationFinder.getJavaHome();
		}
		if ( userRefinedPropertiesLocationFinder !=  null 
				&& userRefinedPropertiesLocationFinder.getJavaHome() != null ) {
			javaHome = userRefinedPropertiesLocationFinder.getJavaHome();
		}        
		if ( buildPropertiesLocationFinder !=  null 
				&& buildPropertiesLocationFinder.getJavaHome() != null ) {
			javaHome = buildPropertiesLocationFinder.getJavaHome();
		}
		if ( sysEnvLocationFinder !=  null 
				&& sysEnvLocationFinder.getJavaHome() != null ) {
			javaHome = sysEnvLocationFinder.getJavaHome();
		}
		return javaHome;
    }
    
    public String getMavenHome() {
		String mavenHome = null;
		if ( customLocationFinder !=  null 
				&& customLocationFinder.getMavenHome() != null ) {
			mavenHome = customLocationFinder.getMavenHome();
		}
		if ( projectPropertiesLocationFinder !=  null 
				&& projectPropertiesLocationFinder.getMavenHome() != null ) {
			mavenHome = projectPropertiesLocationFinder.getMavenHome();
		}
		if ( userRefinedPropertiesLocationFinder !=  null 
				&& userRefinedPropertiesLocationFinder.getMavenHome() != null ) {
			mavenHome = userRefinedPropertiesLocationFinder.getMavenHome();
		}        
		if ( buildPropertiesLocationFinder !=  null 
				&& buildPropertiesLocationFinder.getMavenHome() != null ) {
			mavenHome = buildPropertiesLocationFinder.getMavenHome();
		}
		if ( sysEnvLocationFinder !=  null 
				&& sysEnvLocationFinder.getMavenHome() != null ) {
			mavenHome = sysEnvLocationFinder.getMavenHome();
		}
		return mavenHome;
    }
    
    public String getMavenLocalHome() {
        String userHome = System.getProperty("user.home");
		String mavenLocalHome = new File(userHome, ".maven").getAbsolutePath();;
		if ( customLocationFinder !=  null 
				&& customLocationFinder.getMavenLocalHome() != null ) {
			mavenLocalHome = customLocationFinder.getMavenLocalHome();
		}
		if ( projectPropertiesLocationFinder !=  null 
				&& projectPropertiesLocationFinder.getMavenLocalHome() != null ) {
			mavenLocalHome = projectPropertiesLocationFinder.getMavenLocalHome();
		}
		if ( userRefinedPropertiesLocationFinder !=  null 
				&& userRefinedPropertiesLocationFinder.getMavenLocalHome() != null ) {
			mavenLocalHome = userRefinedPropertiesLocationFinder.getMavenLocalHome();
		}        
		if ( buildPropertiesLocationFinder !=  null 
				&& buildPropertiesLocationFinder.getMavenLocalHome() != null ) {
			mavenLocalHome = buildPropertiesLocationFinder.getMavenLocalHome();
		}
		if ( sysEnvLocationFinder !=  null 
				&& sysEnvLocationFinder.getMavenLocalHome() != null ) {
			mavenLocalHome = sysEnvLocationFinder.getMavenLocalHome();
		}
		return mavenLocalHome;
    }
    
    public String getMavenLocalRepository() {
		String mavenLocalRepository =  new File(getMavenLocalHome(), "repository").getAbsolutePath();;
		if ( customLocationFinder !=  null 
				&& customLocationFinder.getMavenLocalRepository() != null ) {
			mavenLocalRepository = customLocationFinder.getMavenLocalRepository();
		}
		if ( projectPropertiesLocationFinder !=  null 
				&& projectPropertiesLocationFinder.getMavenLocalRepository() != null ) {
			mavenLocalRepository = projectPropertiesLocationFinder.getMavenLocalRepository();
		}
		if ( userRefinedPropertiesLocationFinder !=  null 
				&& userRefinedPropertiesLocationFinder.getMavenLocalRepository() != null ) {
			mavenLocalRepository = userRefinedPropertiesLocationFinder.getMavenLocalRepository();
		}        
		if ( buildPropertiesLocationFinder !=  null 
				&& buildPropertiesLocationFinder.getMavenLocalRepository() != null ) {
			mavenLocalRepository = buildPropertiesLocationFinder.getMavenLocalRepository();
		}
		if ( sysEnvLocationFinder !=  null 
				&& sysEnvLocationFinder.getMavenLocalRepository() != null ) {
			mavenLocalRepository = sysEnvLocationFinder.getMavenLocalRepository();
		}
		return mavenLocalRepository;
    }
    
    public String getMavenPluginsDir() {
		String mavenPluginsDir = new File(getMavenLocalHome(), "plugins").getAbsolutePath();
		if ( customLocationFinder !=  null 
				&& customLocationFinder.getMavenPluginsDir() != null ) {
			mavenPluginsDir = customLocationFinder.getMavenPluginsDir();
		}
		if ( projectPropertiesLocationFinder !=  null 
				&& projectPropertiesLocationFinder.getMavenPluginsDir() != null ) {
			mavenPluginsDir = projectPropertiesLocationFinder.getMavenPluginsDir();
		}
		if ( userRefinedPropertiesLocationFinder !=  null 
				&& userRefinedPropertiesLocationFinder.getMavenPluginsDir() != null ) {
			mavenPluginsDir = userRefinedPropertiesLocationFinder.getMavenPluginsDir();
		}        
		if ( buildPropertiesLocationFinder !=  null 
				&& buildPropertiesLocationFinder.getMavenPluginsDir() != null ) {
			mavenPluginsDir = buildPropertiesLocationFinder.getMavenPluginsDir();
		}
		if ( sysEnvLocationFinder !=  null 
				&& sysEnvLocationFinder.getMavenPluginsDir() != null ) {
			mavenPluginsDir = sysEnvLocationFinder.getMavenPluginsDir();
		}
		return mavenPluginsDir;
    }
    
  
    public void setCustomLocationFinder(CustomLocationFinder customLocationFinder) {
        this.customLocationFinder = customLocationFinder;
    }
}
