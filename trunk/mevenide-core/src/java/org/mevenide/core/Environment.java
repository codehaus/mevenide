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

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Environment.java 21 avr. 2003 10:42:2213:34:35 Exp gdodinet 
 * 
 */
public class Environment {
    /** maven home directory */
    private static String mavenHome;
    
    /** java home directory */
    private static String javaHome;
    
    /** classworlds.conf file location */
    private static String configurationFile;
    
    
    /**
     * prepare the environment for Maven execution : set system properties
     * @param effectiveDirectory the working directory
     */
    public static void prepareEnv(String effectiveDirectory) {
       
        initialize();

        System.setProperty("user.dir", effectiveDirectory);
    }

    /**
     * @refactor GETRID get rid of that method
     * initialize maven system properties
     */
	private static void initialize() {
		//System.setProperty("maven.home", mavenHome); 
		System.setProperty("tools.jar", javaHome + File.separatorChar + "lib" + File.separatorChar + "tools.jar");
		//System.setProperty("forehead.conf.file", configurationFile);
	}

	/**
     * set the jdk home directory
	 * @param string
	 */
	public static void setJavaHome(String jHome) {
		javaHome = jHome;
	}

	/**
     * set the maven home directory. also the classworlds.conf file 
     * is initialized to mavenHome/bin/classworlds.conf
	 * @param string
	 */
	public static void setMavenHome(String mHome) {
		mavenHome = mHome;
        File bin = new File(mavenHome, "bin");
        configurationFile = new File(bin, "forehead.conf").getAbsolutePath();
	}

	public static String getConfigurationFile() {
		return configurationFile;
	}

	public static String getJavaHome() {
		return javaHome;
	}

	public static String getMavenHome() {
		return mavenHome;
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
	 * @refactor EXTRACTME
	 * @return String[] the args passed to the VM during the Maven execution
	 */
	public static String[] getVMArgs(AbstractRunner runner) {
        String[] properties = getRawProperties(runner);
        String[] vmArgs = new String[properties.length + 1];
        vmArgs[0] = "-Xmx160m";
        for (int i = 1; i < properties.length + 1; i++) {
			vmArgs[i] = properties[i - 1];
		}
        
	    return vmArgs;
	}

    static String[] getRawProperties(AbstractRunner runner) {
        Map sysProps = getSystemProperties(runner);
    
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
        props.put("tools.jar" , System.getProperty("tools.jar"));
        props.put("maven.home", getMavenHome());
        props.put("forehead.conf.file", getConfigurationFile());
        props.put("java.endorsed.dirs", getEndorsedDirs());
        props.put("basedir", runner.getBasedir());
        props.put("user.dir", runner.getBasedir());
        return props;
    }
    
	private static String getEndorsedDirs() {
		return Environment.getJavaHome() + File.separatorChar 
		          + "lib" + File.separatorChar + "endorsed"
		          + File.pathSeparator + Environment.getMavenHome()
		          + File.separator + "lib" + File.separator + "endorsed";
	}

}
