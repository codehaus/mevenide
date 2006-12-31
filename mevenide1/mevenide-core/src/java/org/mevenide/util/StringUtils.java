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
package org.mevenide.util;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class StringUtils {
	public static final String EMPTY_STR = "";
	
	/** 
	 * @param sourceDirectory
	 * @return false if source is null or does only contain whitespaces
	 */
	public static boolean isNull(String string) {
		return string == null 
		 		|| string.trim().equals(EMPTY_STR);
	}
	
	/**
	 * @return true if trimmed parameters are equal  
	 */
	public static boolean relaxEqual(String s1, String s2) {
		if ( s1 == null ) {
			return s2 == null;
		}
		else {
			if ( s2 == null ) {
				return false;
			}
			return s1.trim().equals(s2.trim());
		} 
	}
	
	public static String removeEndingSlash(String path) {
    	String result = path;
    	if ( result != null && result.endsWith("/") ) {
    		result = result.substring(0, result.length() - 1);
    	}
    	return result;
    }
	
}
