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

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.Execute;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: SysEnvLocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class SysEnvLocationFinder extends AbstractLocationFinder {
    private static Log log = LogFactory.getLog(SysEnvLocationFinder.class); 
    
    private static SysEnvLocationFinder locationFinder = new SysEnvLocationFinder();
    
	private Properties envProperties;
	
    private SysEnvLocationFinder() {
        loadEnvironment();
    }
    
    
	/**
	 * this is a slighty modified version of org.apache.tools.ant.taskdefs.Property#loadEnvironment()
	 * (c) ASF
	 */
	private void loadEnvironment() {
	   Properties props = new Properties();
	   Vector osEnv = Execute.getProcEnvironment();
	   log.debug("loading environment");
	   for (Enumeration e = osEnv.elements(); e.hasMoreElements();) {
		   String entry = (String) e.nextElement();
		   int pos = entry.indexOf('=');
		   if (pos == -1) {
			log.debug("Ignoring: " + entry);
		   } else {
			   props.put(entry.substring(0, pos),
			   entry.substring(pos + 1));
		   }
	   }
	   envProperties = props;
	   log.debug("environment loaded");
	}
    
    static SysEnvLocationFinder getInstance() {
        return locationFinder; 
    }
    
    public String getJavaHome() {
		return (String) envProperties.get("JAVA_HOME");
    }
    
    public String getMavenHome() {
		return (String) envProperties.get("MAVEN_HOME");
    }
    
    public String getMavenLocalHome() {
		return (String) envProperties.get("MAVEN_HOME_LOCAL");
    }
    
    public String getMavenLocalRepository() {
		return (String) envProperties.get("MAVEN_REPO_LOCAL");
    }
    
    public String getMavenPluginsDir() {
        return null;
    }
}
