/*
 * ==========================================================================
 * Copyright 2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * =========================================================================
 */
package org.mevenide.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.properties.util.Utils;

/**
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a> 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *  
 */
public class PropertyModelFactory {
    private static final Log log = LogFactory.getLog(PropertyModelFactory.class);

    private static final PropertyModelFactory factory = new PropertyModelFactory();

    private final String whiteSpaceChars = " \t\r\n\f";

    private Map propertyFileMap = new HashMap();

    private PropertyModelFactory() {
    }

    public static PropertyModelFactory getFactory() {
        return factory;
    }

    /**
     * if searchMap is true then the PropertyModel associated to file is looked
     * up in the propertyFileMap cache, if not found a new one is created and
     * put in the Map. if searchMap is false then the call is equivalent to
     * newPropertyModel(InputStream) and newly instantiated PropertyModel is
     * not stored in the PropertyModel cache
     */
    public PropertyModel newPropertyModel(File file, boolean searchMap)
            throws IOException {
        if ( !searchMap ) { 
			return newPropertyModel(new FileInputStream(file)); 
		}
        if ( propertyFileMap.containsKey(file) ) { 
			return (PropertyModel) propertyFileMap.get(file); 
		}
        PropertyModel model = newPropertyModel(new FileInputStream(file));
        propertyFileMap.put(file, model);
        return model;
    }

    /**
     * equivalent to newPropertyModel(file, false)
     * 
     * @return the newly instantiated PropertyModel
     */
    public PropertyModel newPropertyModel(File file) throws IOException {
        return newPropertyModel(file, false);
    }

    /**
     * create a new PropertyModel from the InputStream parameter
     */
    public PropertyModel newPropertyModel(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "8859_1"));
        
        // according to Properties.java comment, it has to be 8859_1
        String line;
        
        PropertyModel model = new PropertyModel();
        
        Comment currComment = null;
        KeyValuePair currKeyPair = null;
        
        boolean appendingMode = false;
        
        while ( (line = reader.readLine()) != null ) {
			
            line = Utils.removeTrailingWhitespaces(line);
            
            // Find start of key first..
            // apparently doens't have to be the first in line..
            int len = line.length();
            int keyStart;
            for (keyStart = 0; keyStart < len; keyStart++) {
                if ( whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1 )
                        break;
            }
            if ( appendingMode ) {
                currComment = null;
				currKeyPair.setValue(Utils.removeTrailingSlash(currKeyPair.getValue()).trim());
                currKeyPair.addToValue(Utils.removeTrailingWhitespaces(line));
                appendingMode = continueLine(line);
                continue;
            }

            // Blank lines are added to comment (unless it's being added to the
            // previous value??
            if ( keyStart == len ) {
			    currKeyPair = null;
                model.addToComment(currComment, line);
                continue;
            }

            // Continue lines that end in slashes if they are not comments
            char firstChar = line.charAt(keyStart);
            if ( (firstChar == '#') || (firstChar == '!') ) {
				currKeyPair = null;
                model.addToComment(currComment, line);
                continue;
            }
            else {
				int sepIndex = Math.min(line.indexOf('='), line.indexOf(':'));
				//if only one of ':' or '=' sepIndex will be -1 so valid index might be the max
				if ( sepIndex < 0 ) {
					sepIndex = Math.max(line.indexOf('='), line.indexOf(':'));
				}
				if ( sepIndex > 0 ) {
  					//if we substring till sepIndex - 1 lines that dont put a space before key and separator are not well parsed
					String key = line.substring(0, sepIndex);
                    String value = line.substring(sepIndex + 1);
                    currComment = null;
                    if ( key.trim().length() == 0 ) {
                        log.warn("strange line - key is whitespace");
                        continue;
                    }
                    currKeyPair = model.newKeyPair(key, line.charAt(sepIndex), value);
	                appendingMode = continueLine(line);
                }
                else {
                    log.warn("A non-comment non key-pair line encountered:'" + line + "'");
                }
			}
        }
        return model;
    }

    /*
     * Returns true if the next line should be appended to this one..
     */
    private boolean continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ( (index >= 0) && (line.charAt(index--) == '\\') )
            slashCount++;
        return (slashCount % 2 == 1);
    }
}
