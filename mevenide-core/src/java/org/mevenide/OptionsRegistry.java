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

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;




/**
 * Maven options : Map(value, description) 
 * 
 * some options have been intentionally left out since they dont seem 
 * to make in the current context.
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class OptionsRegistry {
    
    private static Map options = new TreeMap();
    
    static {
        try {
			Properties props = new Properties();
			String src = OptionsRegistry.class.getResource("/mevenide.properties").getFile();
			props.load(new FileInputStream(src));
			Iterator keys = props.keySet().iterator();
			while ( keys.hasNext() ) {
			    String key = (String) keys.next();
			    Character optionChar = new Character(key.charAt(key.length() - 1));
			    options.put(optionChar, props.get(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
    }
    
   /** 
    * -D,--define arg   Define a system property
    * -E,--emacs        Produce logging information without adornments
    * -X,--debug        Produce execution debug output
    * -e,--exception    Produce exception stack traces
    * -o,--offline      Build is happening offline
    * -v,--version      Display version information
    */
	public static String getOptionDescription(char option) throws InvalidOptionException {
	   String description = (String) options.get(new Character(option));
       if ( description == null ) {
           throw new InvalidOptionException(option);
       }
       else return description;  	 
	}
	
	

}
