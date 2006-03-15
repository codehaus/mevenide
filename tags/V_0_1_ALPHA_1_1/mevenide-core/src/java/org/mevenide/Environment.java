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