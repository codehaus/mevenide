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
package org.mevenide;

import java.io.File;

/**
 * 
 * @todo use a n-singleton instead - singleton PER launchConfig 
 * Q: how to identify launch configuration ? whats a launch configuration ?
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Environment.java,v 1.1 21 avr. 2003 10:42:2213:34:35 Exp gdodinet
 * 
 */
public class Environment {
	private Environment()  {
	}
	
    /** maven home directory */
    private static String mavenHome;
    
	/** maven repository */
	private static String mavenRepository;
    
    /** java home directory */
    private static String javaHome;
    
    /** classworlds.conf file location */
    private static String configurationFile;

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
    
    /**
     * @return the configuration file. for now the value of forehead.conf.file 
     */
	public static String getConfigurationFile() {
		return configurationFile;
	}
    
    /**
     * @return java home directory (e.g. C:/jdk1.4.1/)
     */
	public static String getJavaHome() {
		return javaHome;
	}

    /** 
     * @return maven installation directory
     */
	public static String getMavenHome() {
		return mavenHome;
	}

    /**
     * constructs the endorsedDirs property needed for Maven execution
     * @return "JAVA_HOME/lib/endorsed:MAVEN_HOME/lib/endorsed"
     */
	public static String getEndorsedDirs() {
		return Environment.getJavaHome() + File.separatorChar 
		          + "lib" + File.separatorChar + "endorsed"
		          + File.pathSeparator + Environment.getMavenHome()
		          + File.separator + "lib" + File.separator + "endorsed";
	}

	public static String getMavenRepository() {
		return mavenRepository;
	}

	public static void setMavenRepository(String repo) {
		mavenRepository = repo;
	}

}
