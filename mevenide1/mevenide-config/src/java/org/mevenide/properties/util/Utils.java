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
package org.mevenide.properties.util;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class Utils {
    private static final String WS = " \t\r\n\f";
    
    private Utils() { }
    
    public static boolean areEqual(Object o1, Object o2) {
        if ( o1 == null && o2 == null ) {
            return true;
        }
        if ( o1 == null ) {
            return false;
        }
        return o1.equals(o2);
    }
    
    public static String removeTrailingWhitespaces(String strg) {
        String localCopy = strg;
        while ( localCopy.length() > 0 && WS.indexOf(localCopy.charAt(localCopy.length() - 1)) != -1 ) {
            localCopy = localCopy.substring(0, localCopy.length() - 1);
        }
        return localCopy;
    }
    
    public static String removeTrailingSlash(String strg) {
        if ( strg.charAt(strg.length() - 1) == '\\' ) {
            return strg.substring(0, strg.length() - 1);   
        }
        return strg;
    }
}
