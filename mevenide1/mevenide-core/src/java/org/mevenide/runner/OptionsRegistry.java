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
package org.mevenide.runner;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;





/**
 * Maven options : Map(value, description) 
 * 
 * some options have been intentionally left out since they dont seem 
 * to make in the current context. 
 * 
 * it is still possible to register other options at rt using #registerCharOption()
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public final class OptionsRegistry {
    private final static Logger LOGGER = Logger.getLogger(OptionsRegistry.class.getName());
    
    private static OptionsRegistry registry = new OptionsRegistry();
    private Map options = new TreeMap();
    
	public static OptionsRegistry getRegistry() {
		return registry;
	}
   
    private OptionsRegistry() {
		try {
			Properties props = new Properties();
			InputStream stream = OptionsRegistry.class.getResourceAsStream("/mevenide.properties");
			props.load(stream);
			
			Iterator keys = props.keySet().iterator();
			LOGGER.fine("Found " + props.keySet().size() + " total keys");
			while ( keys.hasNext() ) {
				String key = (String) keys.next();
				LOGGER.fine("current key = " + key);
				
				Character optionChar = new Character(key.charAt(key.length() - 1));
				LOGGER.fine("Found optionChar " + optionChar);
				
				this.registerCharOption(optionChar.charValue(), (String) props.get(key));
			}
		} 
		catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to init options map due to : " + e);
		}
	}

	public void registerCharOption(char option, String optionDescription) {
		options.put(new Character(option), optionDescription);
	} 
	
	public void registerCharOption(char option) {
		registerCharOption(option, "No available description");
	}


   /**
    * @return the description associated with the given option passed as a single character
    */
	public String getDescription(char option) throws InvalidOptionException {
	   LOGGER.fine("Looking up through " + options.size() + " keys ");
	   String description = (String) options.get(new Character(option));
       if ( description == null ) {
           throw new InvalidOptionException(option);
       }
       else {
           return description;  	 
       }
	}
	
	

}
