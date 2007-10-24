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
package org.mevenide.environment.sysenv;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: DefaultSysEnvProviderTest.java,v 1.1 2004/03/07 Exp gdodinet 
 * 
 */
public class DefaultSysEnvProviderTest extends TestCase {
	
    private DefaultSysEnvProvider finder;
    private String propertyKey, propertyValue;
    
    private String[] commandLine;
    
    protected void setUp() throws Exception {
        finder = new DefaultSysEnvProvider();
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
    		if ( key != null && value != null ) {
    			propertyKey = key;
    			propertyValue = value;
    			break;
    		}
    	}
    }
    
    protected void tearDown() throws Exception {
        finder = null;
        propertyKey = null;
        propertyValue = null;
    }
    
    public void testGetProperty() {
        assertEquals(propertyValue, finder.getProperty(propertyKey));
    }

    public void testMavenHome() {
        assertNotNull(finder.getProperty("MAVEN_HOME"));
    }
    
    /**
     * modified version of org.apache.tools.ant.taskdefs.condition.Os#isOs
     */
    private boolean isOsFamily(String family) {
    	boolean isFamily = false;
    	String osName = System.getProperty("os.name").toLowerCase();
    	String pathSeparator = System.getProperty("path.separator");
    	
    	if (family.equals("windows")) {
            isFamily = osName.indexOf("windows") > -1;
        } 
    	else if (family.equals("os/2")) {
            isFamily = osName.indexOf("os/2") > -1;
        } 
    	else if (family.equals("netware")) {
            isFamily = osName.indexOf("netware") > -1;
        } 
    	else if (family.equals("dos")) {
            isFamily = pathSeparator.equals(";") 
							&& !isOsFamily("netware");
        } 
    	else if (family.equals("mac")) {
            isFamily = osName.indexOf("mac") > -1;
        } 
    	else if (family.equals("tandem")) {
            isFamily = osName.indexOf("nonstop_kernel") > -1;
        } 
    	else if (family.equals("unix")) {
            isFamily = pathSeparator.equals(":")
			                && !isOsFamily("openvms")
			                && (!isOsFamily("mac") || osName.endsWith("x"));
        } 
    	else if (family.equals("win9x")) {
            isFamily = isOsFamily("windows")
			                && (osName.indexOf("95") >= 0
			                || osName.indexOf("98") >= 0
			                || osName.indexOf("me") >= 0
			                || osName.indexOf("ce") >= 0);
        } 
    	else if (family.equals("z/os")) {
            isFamily = osName.indexOf("z/os") > -1
                			|| osName.indexOf("os/390") > -1;
        } 
    	else if (family.equals("os/400")) {
            isFamily = osName.indexOf("os/400") > -1;
        } 
    	else if (family.equals("openvms")) {
            isFamily = osName.indexOf("openvms") > -1;
        } 
        return isFamily;
    }
    
    
    
    private String[] getProcEnvCommand() {
    	if (isOsFamily("os/2")) {
    		// OS/2 - use same mechanism as Windows 2000
    		String[] cmd = {"cmd", "/c", "set" };
    		return cmd;
    	} 
    	else if (isOsFamily("windows")) {
    		// Determine if we're running under XP/2000/NT or 98/95
    		if (!isOsFamily("win9x")) {
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
    	else if (isOsFamily("z/os")) {
    		String[] cmd = {"/bin/env"};
    		return cmd;
    	} 
    	else if (isOsFamily("unix")) {
    		// Generic UNIX
    		// Alternatively one could use: /bin/sh -c env
    		String[] cmd = {"/usr/bin/env"};
    		return cmd;
    	} 
    	else if (isOsFamily("netware")) {
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
