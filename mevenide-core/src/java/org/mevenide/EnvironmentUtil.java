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
package org.mevenide;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.Execute;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: EnvironmentUtil.java,v 1.1 11 nov. 2003 Exp gdodinet 
 * 
 */
public class EnvironmentUtil {
    private static Log log = LogFactory.getLog(EnvironmentUtil.class);

    private EnvironmentUtil() {
    }
    
    private static Properties envProperties;
    
    
    /**
     * this is a slighty modified version of org.apache.tools.ant.taskdefs.Property#loadEnvironment()
     * (c) ASF
     */
	public static void loadEnvironment() {
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
	   log.debug("Maven home = " + getMavenHome());	
	   log.debug("Java home = " + getJavaHome());
	   if ( Environment.getJavaHome() == null ) Environment.setJavaHome(getJavaHome());
	   if ( Environment.getMavenHome() == null ) Environment.setMavenHome(getMavenHome());
	}
	
	static String getMavenHome() {
	    return (String) envProperties.get("MAVEN_HOME");
	}

	static String getJavaHome() {
		return (String) envProperties.get("JAVA_HOME");
	}

	static String getMavenRepoLocal() {
		return (String) envProperties.get("MAVEN_REPO_LOCAL");
	}

	static String getMavenRepo() {
		return (String) envProperties.get("MAVEN_REPO");
	}
}