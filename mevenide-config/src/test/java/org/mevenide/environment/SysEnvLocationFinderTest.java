/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.tools.ant.taskdefs.condition.Os;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: SysEnvLocationFinderTest.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class SysEnvLocationFinderTest extends AbstractLocationFinderTest {
    
    private SysEnvLocationFinder finder;
    private String mavenHome, javaHome;
    
    private String[] commandLine;
    
    protected void setUp() throws Exception {
        finder = SysEnvLocationFinder.getInstance();
        setUpCommandLine();
        setUpExceptedEnvironmentVariables();
    }
    
    
    private void setUpCommandLine() {
    	commandLine = getProcEnvCommand();
    }
    
    private void setUpExceptedEnvironmentVariables() throws Exception {
    	Process p = Runtime.getRuntime().exec(commandLine);
    	BufferedReader br = new BufferedReader(new InputStreamReader( p.getInputStream() ));
    	String line = null;
    	while( (line = br.readLine()) != null ) {
    		int idx = line.indexOf( '=' );
    		String key = line.substring( 0, idx );
    		String value = line.substring( idx+1 );
    		if  ( key.equals("JAVA_HOME") ) {
    			javaHome = value;
    		}
    		if  ( key.equals("MAVEN_HOME") ) {
    			mavenHome = value;
    		}
    	}
    }
    
    protected void tearDown() throws Exception {
        finder = null;
        javaHome = null;
        mavenHome = null;
    }
    
    public void testGetJavaHome() {
        assertEquals(javaHome, finder.getJavaHome());
    }

    public void testGetMavenHome() {
		assertEquals(mavenHome, finder.getMavenHome());
    }

    //from org.apache.tools.ant.taskdefs.Execute
    private static String[] getProcEnvCommand() {
    	if (Os.isFamily("os/2")) {
    		// OS/2 - use same mechanism as Windows 2000
    		String[] cmd = {"cmd", "/c", "set" };
    		return cmd;
    	} 
    	else if (Os.isFamily("windows")) {
    		// Determine if we're running under XP/2000/NT or 98/95
    		if (!Os.isFamily("win9x")) {
    			// Windows XP/2000/NT
    			String[] cmd = {"cmd", "/c", "set" };
    			return cmd;
    		} 
    		else {
    			// Windows 98/95
    			String[] cmd = {"command.com", "/c", "set" };
    			return cmd;
    		}
    	} 
    	else if (Os.isFamily("z/os")) {
    		String[] cmd = {"/bin/env"};
    		return cmd;
    	} 
    	else if (Os.isFamily("unix")) {
    		// Generic UNIX
    		// Alternatively one could use: /bin/sh -c env
    		String[] cmd = {"/usr/bin/env"};
    		return cmd;
    	} 
    	else if (Os.isFamily("netware")) {
    		String[] cmd = {"env"};
    		return cmd;
    	} 
    	else {
    		// MAC OS 9 and previous
    		// TODO: I have no idea how to get it, someone must fix it
    		String[] cmd = null;
    		return cmd;
    	}
    }
}
