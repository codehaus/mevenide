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
package org.codehaus.mevenide.m2.embedder;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class M2Options {

    private static Map optionDescriptions = new TreeMap();
    
    private static ResourceBundle bundle = ResourceBundle.getBundle(M2Options.class.getPackage().getName() + ".Options"); 
    
    static {
        optionDescriptions.put(new Character('D'), "Define a system property");
        optionDescriptions.put(new Character('X'), "Produce execution debug output");
        optionDescriptions.put(new Character('o'), "Build is happening offline");
        optionDescriptions.put(new Character('r'), "Execute goals for project found in the reactor");
        //Following options make no sense in the context of the IDE
        //optionDescriptions.put(new Character('b'), "Suppress logo banner");
        //optionDescriptions.put(new Character('g'), "Display available mojoDescriptors");
	    //optionDescriptions.put(new Character('h'), "Display help information");
        //optionDescriptions.put(new Character('v'), "Display version information");
    }
	
	public static String getDescription(char option) {
	   return (String) optionDescriptions.get(new Character(option));
	}
	
    
}
