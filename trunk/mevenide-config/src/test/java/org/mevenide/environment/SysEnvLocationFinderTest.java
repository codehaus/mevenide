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
