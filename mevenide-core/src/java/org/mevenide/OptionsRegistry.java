/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * Maven options : Map(value, description) 
 * 
 * some options have been intentionally left out since they dont seem 
 * to make in the current context.
 * 
 * @low use configuration (http://gdfact.fr.st/) instead of Properties  
 * 
 * make a singleton of it ?
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class OptionsRegistry {
    private static Log log = LogFactory.getLog(OptionsRegistry.class);
    
    private static Map options = new TreeMap();
    
    static {
        try {
			Properties props = new Properties();
			InputStream stream = OptionsRegistry.class.getResourceAsStream("/mevenide.properties");
			props.load(stream);
			Iterator keys = props.keySet().iterator();
			log.debug("Found " + props.keySet().size() + " total keys");
			while ( keys.hasNext() ) {
				String key = (String) keys.next();
				log.debug("current key = " + key);
			    Character optionChar = new Character(key.charAt(key.length() - 1));
			    log.debug("Found optionChar " + optionChar);
			    options.put(optionChar, props.get(key));
			}
		} catch (Exception e) {
			log.debug("Unable to init options map due to : " + e);
        }
    }
    
	private OptionsRegistry() {
	}

   /**
    * @return the description associated with the given option passed as a single character
    */
	public static String getDescription(char option) throws InvalidOptionException {
	   log.debug("Looking up through " + options.size() + " keys ");
	   String description = (String) options.get(new Character(option));
       if ( description == null ) {
           throw new InvalidOptionException(option);
       }
       else return description;  	 
	}
	
	

}
