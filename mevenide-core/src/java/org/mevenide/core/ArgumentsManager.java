/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mevenide.*;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ArgumentsManager.java 8 mai 2003 17:12:5913:34:35 Exp gdodinet 
 * 
 */
public class ArgumentsManager {

	private ArgumentsManager() {
	}

	/**
	 * @return String[] the classpath needed for Maven execution $maven_home/lib/*.jar
	 */
	public static String[] getMavenClasspath() {
		File mavenLib = new File(Environment.getMavenHome(), "lib");
	    
	    FilenameFilter filter = 
	        new FilenameFilter() {
	            public boolean accept(File parent, String fileName) {
	                return fileName.endsWith(".jar") || fileName.endsWith(".zip");
	            }
	        };
	    
	    File[] files = mavenLib.listFiles(filter);
	    
	    String[] cp = new String[files.length];
	    
	    for (int i = 0; i < files.length; i++) {
			cp[i] = files[i].getAbsolutePath();
		}
	    
		return cp;
	}

	/**
	 * @pre Environment has been configured
     *
	 * @return String[] the args passed to the VM during the Maven execution
	 */
	public static String[] getVMArgs(AbstractRunner runner) {
	    String[] properties = ArgumentsManager.getRawProperties(runner);
	    String[] vmArgs = new String[properties.length + 1];
	    vmArgs[0] = "-Xmx160m";
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
	 * @param runner
	 * @return
	 */
	static Map getSystemProperties(AbstractRunner runner) {
	    Map props = new HashMap();
	
	    props.put("javax.xml.parsers.DocumentBuilderFactory",
	                "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	    props.put("javax.xml.parsers.SAXParserFactory",
	                "org.apache.xerces.jaxp.SAXParserFactoryImpl");
	    props.put("tools.jar" , Environment.getJavaHome() + File.separator + "lib" + File.separator + "tools.jar");
	    props.put("maven.home", Environment.getMavenHome());
	    props.put("forehead.conf.file", Environment.getConfigurationFile());
	    props.put("java.endorsed.dirs", Environment.getEndorsedDirs());
	    props.put("basedir", runner.getBasedir());
	    props.put("user.dir", runner.getBasedir());
	    return props;
	}
}
