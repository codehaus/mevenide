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
package org.mevenide.tags.eclipse;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AdaptVersionTag extends TagSupport {

    private String version;
    
    private String var;
    
    public void doTag(XMLOutput arg0) throws Exception {
		
		checkAttribute(version, "version");
		checkAttribute(var, "var");

        String newVersion = adapt();
        context.setVariable(var, newVersion);
    }
    

    protected String adapt() {
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
			newVersion = "0.0.0";
		}
		else {
		    if ( !version.equals("SNAPSHOT") && version.indexOf("SNAPSHOT") >= 0 ) {
		        newVersion += "." + new SimpleDateFormat("yyyyMMdd").format(new Date()); 
		    }
		}
        return newVersion;
    }


    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }

	private void checkAttribute(Object attribute, String attributeName) throws MissingAttributeException {
        if (attribute == null ) {
            throw new MissingAttributeException(attributeName + " should be defined.");
        }
    }

	private void checkAttribute(String attribute, String attributeName) throws MissingAttributeException {
		checkAttribute((Object) attribute, attributeName);
        if (attribute.trim().equals("") ) {
            throw new MissingAttributeException(attributeName + " should not be empty.");
        }
    }

}
