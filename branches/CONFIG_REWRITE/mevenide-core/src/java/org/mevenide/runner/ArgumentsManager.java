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
package org.mevenide.runner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ArgumentsManager.java 8 mai 2003 17:12:5913:34:35 Exp gdodinet 
 * 
 */
public final class ArgumentsManager {

	private ArgumentsManager() {
	}

	/**
	 * This was causing our classloading problems :
	 * i included all jars under %MAVEN_HOME%/lib, tho we just needed forhead ! 
	 * 
	 * 
	 * @return String[] the classpath needed for Maven execution $maven_home/lib/*.jar
	 */
	public static String[] getMavenClasspath() {
//		File mavenLib = new File(Environment.getMavenHome(), "lib");
//		return new String[] { new File(mavenLib, "forehead-1.0-beta-5.jar").getAbsolutePath() };
		return new String[] { RunnerHelper.getHelper().getForeheadLibrary() };
	}

	/**
	 * @pre Environment has been configured
     *
	 * @return String[] the args passed to the VM during the Maven execution
	 */
	public static String[] getVMArgs(AbstractRunner runner) {
	    String[] properties = ArgumentsManager.getRawProperties(runner);
	    String[] vmArgs = new String[properties.length + 1];
	    vmArgs[0] = "-Xmx" + ConfigUtils.getHeapSize() +"M";
	    
	    for (int i = 1; i < properties.length + 1; i++) {
			vmArgs[i] = properties[i - 1];
		}
	    
	    return vmArgs;
	}

	static String[] getRawProperties(AbstractRunner runner) {
	    Map sysProps = ArgumentsManager.getSystemProperties(runner);
	
	    Set keys = sysProps.keySet();
	
	    String[] rawProps = new String[keys.size()];
	
	    Iterator iterator = keys.iterator();
	    int u = 0;
	    while ( iterator.hasNext() ) {
	        String key = (String) iterator.next(); 
	        rawProps[u] = "-D" + key + "=" + (String) sysProps.get(key);
	        u++;                 
	    }
	    
	    return rawProps;
	}

	/**
	 * in order to ensure that the basedir is correct we have to set 
	 * the user.dir property to the project home. bug in maven ? 
	 * 
	 * @param runner
	 * @return
	 */
	public static Map getSystemProperties(AbstractRunner runner) {
	    Map props = new HashMap();
	
	    props.put("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	    props.put("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
	    ILocationFinder config = ConfigUtils.getDefaultLocationFinder();
	    
	    props.put("maven.home", config.getMavenHome());
		props.put("maven.repo.local", config.getMavenLocalRepository());
	    props.put("forehead.conf.file", ConfigUtils.getConfigurationFile());
	    props.put("java.endorsed.dirs", ConfigUtils.getEndorsedDirs());
	    props.put("basedir", runner.getBasedir());
	    //props.put("user.dir", runner.getBasedir());
	    
	    return props;
	}
}
