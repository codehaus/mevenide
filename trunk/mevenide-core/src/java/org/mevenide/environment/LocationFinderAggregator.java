/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.environment;

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
		String javaHome = null;
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
		String mavenLocalHome = null;
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
		String mavenLocalRepository = null;
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
		String mavenPluginsDir = null;
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
