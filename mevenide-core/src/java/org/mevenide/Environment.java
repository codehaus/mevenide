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

import java.io.File;

/**
 * 
 * @todo make it non-static, non-final and abstract (f.i. abstract wrapper on subclasses defined in concrete mevenide implementations)  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Environment.java,v 1.1 21 avr. 2003 10:42:2213:34:35 Exp gdodinet
 * 
 */
public final class Environment {

	private Environment()  {
	}
	
	static {
	    EnvironmentUtil.loadEnvironment();
	}
	
    /** maven home directory */
    private static String mavenHome;
    
	/** maven home directory */
	private static String mavenLocalHome;
		
	/** maven repository */
	private static String mavenLocalRepository;
    
    /** java home directory */
    private static String javaHome;
    
    /** classworlds.conf file location */
    private static String configurationFile;

	/** heap size */
	private static int heapSize = 160;

	/** 
	 * maven plugins installation directory
	 * 
	 * default to ${user.home}/.maven/plugins
	 * 
	 */
	private static String mavenPluginsInstallDir;

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
		if ( configurationFile == null || javaHome.trim().equals("") )
		    return new File(new File(EnvironmentUtil.getMavenHome(), "bin"), "forehead.conf").getAbsolutePath();
	    return configurationFile;
	}
    
    /**
     * @return java home directory (e.g. C:/jdk1.4.1/)
     */
	public static String getJavaHome() {
		return javaHome == null || javaHome.trim().equals("") ? EnvironmentUtil.getJavaHome() : javaHome;
	}

    /** 
     * @return maven installation directory
     */
	public static String getMavenHome() {
	    return mavenHome == null || mavenHome.trim().equals("") ? EnvironmentUtil.getMavenHome() : mavenHome;
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
	
	/**
	 * @return maven local repository location
	 */
	public static String getMavenLocalRepository() {
		return mavenLocalRepository != null ? mavenLocalRepository : EnvironmentUtil.getMavenRepoLocal();
	}
	
	/**
	 * set maven local repository location
	 */
	public static void setMavenLocalRepository(String repo) {
		mavenLocalRepository = repo;
	}

	/** 
	 * @return maven plugins installation directory. default to mavenLocalHome/plugins if not set
	 */
    public static String getMavenPluginsInstallDir() {
    	if ( mavenPluginsInstallDir == null && getMavenLocalHome() != null ) {
    		mavenPluginsInstallDir = new File(getMavenLocalHome(), "plugins").getAbsolutePath(); 
    	}
        return mavenPluginsInstallDir;
    }
    
	/** 
	 * set maven plugins installation directory
	 * 
	 */
    public static void setMavenPluginsInstallDir(String pluginsInstallDir) {
        mavenPluginsInstallDir = pluginsInstallDir;
    }
    
    /**
     * @return maximum java heap size - passed as vm argument (Xmx) when launching maven
     */
    public static int getHeapSize() {
        return heapSize;
    }
	/**
	 * set maximum java heap size - passed as vm argument (Xmx) when launching maven
	 */
    public static void setHeapSize(int hSize) {
        Environment.heapSize = hSize;
    }

	/**
	 * @return maven local installation directory. default to ${user.home}/maven if not set
	 */
    public static String getMavenLocalHome() {
    	if ( mavenLocalHome == null ) {
    		mavenLocalHome = new File(System.getProperty("user.home"), ".maven").getAbsolutePath();
    	}
        return mavenLocalHome;
    }

	/**
	 * set maven local installation directory
	 */
    public static void setMavenLocalHome(String mLocalHome) {
        Environment.mavenLocalHome = mLocalHome;
    }
	
	
}
