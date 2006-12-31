/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Adapt an artifact version to make it match Eclipse plugin expected version : 
 * only valid chars will be kept. <br/>Valid values are ones of ([0..9]|\.). Also if 
 * the version to adapt contains only two digits, ".0" will appended at the end so that 
 * it matches Eclipse Update Manager expectations (3 digits required).
 * 
 * <br/>
 * examples : 
 * <ul>
 * <li>a version such as 1.0-beta-1 will be transformed to 1.0.1</li>
 * <li>a version such as 1.1 (or 1) will be transformed to 1.1.0 (respectively 1.0.0)</li>
 * <li>a SNAPSHOT version will be cut to remove the SNAPSHOT, so 1.1-SNAPSHOT will resolved as 1.1.20040519, where 20040519 is the current date. however this may causes issues in regard to plugin version compatibility concerns</li>
 * <li>invalid version such as 1.0..1 (or 1.1.) will be transformed to 1.0.1 (respectively, 1.1.0)</li>
 * </ul> 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AdaptVersionTag extends AbstractMevenideTag {
	/** the version to adapt **/
    private String version;
    
    /** the name under which the adapted version will be put in the jelly context **/ 
    private String var;
    
    public void doTag(XMLOutput arg0) throws MissingAttributeException, JellyTagException {

		checkAttribute(version, "version");
		checkAttribute(var, "var");

        String newVersion = adapt();
        context.setVariable(var, newVersion);
    }

    public String adapt() {


        String validValues = "0123456789.";
        String newVersion = "";

        char lastConcatenatedChar = '-';

        for (int i = 0; i < version.length(); i++) {
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
		        //newVersion += ".0";
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
    
    public String getVersion() {
        return version;
    }
    
    /** the version to adapt **/
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getVar() {
        return var;
    }
    
    /** the name under which the adapted version will be put in the jelly context **/
    public void setVar(String var) {
        this.var = var;
    }

}
