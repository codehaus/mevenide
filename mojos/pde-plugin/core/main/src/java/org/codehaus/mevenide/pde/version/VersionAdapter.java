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
package org.codehaus.mevenide.pde.version;



/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class VersionAdapter {
    
    public String adapt(String version) {
        String validValues = "0123456789.";
        String newVersion = "";
        
        char lastConcatenatedChar = '-';
        
        for (int i = 0; i < version.length(); i++) {
			if ( newVersion.length() == 5 ) {
				break;
			}
			if ( validValues.indexOf(version.charAt(i)) >= 0 )  {
				if ( Character.isDigit(lastConcatenatedChar) ) {
					if ( Character.isDigit(version.charAt(i)) ) {
						newVersion += ".";
					}
					lastConcatenatedChar = version.charAt(i);
					newVersion += lastConcatenatedChar;
				}
				else {
					if ( Character.isDigit(version.charAt(i)) ) {
						lastConcatenatedChar = version.charAt(i);
						newVersion += lastConcatenatedChar;
					}
				}
			}
			else {
				break;
			}
        }
        
        if ( newVersion.endsWith(".") ) {
            newVersion = newVersion.substring(0, newVersion.length() - 1);
        }
        
		if ( newVersion.trim().equals("") ) {
			newVersion = "0.0.1";
		}
		else {
		    if ( !version.equals("SNAPSHOT") && version.indexOf("SNAPSHOT") >= 0 ) {
		        //newVersion += "." + new SimpleDateFormat("yyyyMMdd").format(new Date()); 
		    }
		}
		int digitNumber = countNumbers(newVersion);
		while ( digitNumber < 3 ) {
		    newVersion += ".0";
		    digitNumber++;
		}
	    return newVersion;
    }

    private int countNumbers(String version) {
        int u = 0;
        for (int i = 0; i < version.length(); i++) {
            if ( Character.isDigit(version.charAt(i)) ) {
            	u++;
            }
        }
        return u;
    }
    
}
