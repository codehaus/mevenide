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
package org.mevenide.runner;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mevenide.Environment;

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
		return RunnerHelper.getHelper().getMainClass();
	}

	/**
	 * @pre Environment has been configured
     *
	 * @return String[] the args passed to the VM during the Maven execution
	 */
	public static String[] getVMArgs(AbstractRunner runner) {
	    String[] properties = ArgumentsManager.getRawProperties(runner);
	    String[] vmArgs = new String[properties.length + 1];
	    vmArgs[0] = "-Xmx" + Environment.getHeapSize() +"m";
	    
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
	static Map getSystemProperties(AbstractRunner runner) {
	    Map props = new HashMap();
	
	    props.put("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	    props.put("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
	    props.put("tools.jar" , Environment.getJavaHome() + File.separator + "lib" + File.separator + "tools.jar");
	    props.put("maven.home", Environment.getMavenHome());
		props.put("maven.repo.local", Environment.getMavenRepository());
	    props.put("forehead.conf.file", Environment.getConfigurationFile());
	    props.put("java.endorsed.dirs", Environment.getEndorsedDirs());
	    props.put("basedir", runner.getBasedir());
	    props.put("user.dir", runner.getBasedir());
	    
	    return props;
	}
}
