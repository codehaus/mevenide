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

package org.mevenide.grammar;


/**
 * Utility methods that don't fit elsewhere
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class GrammarUtilities
{
    private static char[] nowordseparators = {':', '-', '.'}; 
    
    private GrammarUtilities() {
    }
    
    public static String extractLastWord(String text) {
        StringBuffer buff = new StringBuffer();
        if (text == null || text.length() == 0) {
            return "";
        }
        for (int i = text.length() - 1; i > -1; i--) {
            char currChar = text.charAt(i);
            boolean include = Character.isLetterOrDigit(currChar);
            if (!include) {
                for (int j = 0; j < nowordseparators.length; j++) {
                    if (currChar == nowordseparators[j]) {
                        include = true;
                        break;
                    }
                }
            }
            if (include) {
                buff.insert(0, currChar);
            } else {
                break;
            }
        }
        return buff.toString();
    }
    
}
