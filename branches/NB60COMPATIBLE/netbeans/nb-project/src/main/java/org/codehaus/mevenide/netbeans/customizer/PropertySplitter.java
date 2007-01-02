/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.customizer;

/**
 *
 * @author mkleint
 */
public class PropertySplitter {
    
        private String line;
        private char[] quotes;
        private char separator;
        private char newline;
        private boolean trim = true;
        private char escape;
        
        private int location = 0;
        private char quoteChar = 0;
        private boolean inQuote = false;
        private boolean escapeNext = false;
        
        public PropertySplitter(String line) {
            this(line, new char[] { '"' } , '\\', ' ', '\n'); //NOI18N
        }
        
        private PropertySplitter(String line, char[] quotes, char escape, char separator, char nl) {
            this.line = line;
            this.quotes = quotes;
            this.separator = separator;
            this.trim = trim;
            this.escape = escape;
            newline = nl;
        }
        
        
        public String nextPair() {
            StringBuffer buffer = new StringBuffer();
            if (location >= line.length()) {
                return null;
            }
            //TODO should probably also handle (ignore) spaces before or after the = char somehow
            while (location < line.length()
                    && (line.charAt(location) != separator || line.charAt(location) != newline 
                                                           || inQuote || escapeNext)) {
                char c = line.charAt(location);
                if (escapeNext) {
                    buffer.append(c);
                    escapeNext = false;
                } else if (c == escape) {
                    escapeNext = true;
                } else if (inQuote) {
                    if (c == quoteChar) {
                        inQuote = false;
                    } else {
                        buffer.append(c);
                    }
                } else {
                    if (isQuoteChar(c)) {
                        inQuote = true;
                        quoteChar = c;
                    } else {
                        buffer.append(c);
                    }
                }
                location++;
            }
            location++;
            return trim ? buffer.toString().trim() : buffer.toString();
        }
        
        private boolean isQuoteChar(char c) {
            for (int i = 0; i < quotes.length; i++) {
                char quote = quotes[i];
                if (c == quote) return true;
            }
            return false;
        }
    
}
