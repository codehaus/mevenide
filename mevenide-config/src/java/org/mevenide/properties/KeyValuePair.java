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
package org.mevenide.properties;

import org.mevenide.properties.util.Utils;

/**
 * 
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class KeyValuePair extends Element {
    private String key;
    private StringBuffer buf;
    private char sepChar;

    protected KeyValuePair(String newKey, char separator) {
        key = newKey;
        buf = new StringBuffer(100);
        sepChar = separator;
    }

    public void addToValue(String value) {
        buf.append(value);
    }

    public void setValue(String value) {
        buf = new StringBuffer(value);
    }

    /**
     * for comparisons, trim the leaning and trailing whitespace
     */
    public String getKey() {
        return key.trim();
    }

    public String toString() {
        return key + sepChar + buf.toString();
    }
    
    protected void addLine(String line)
    {
        addToValue(line);
    }
    
    public String getValue() 
    {
        return buf.toString();
    }
    
    public boolean equals(Object obj) {
        if ( !(obj instanceof KeyValuePair) ) {
			return false;
		}
		KeyValuePair kvp = (KeyValuePair) obj;
		return Utils.areEqual(key, kvp.key) 
					&& Utils.areEqual(buf, kvp.buf)
					&& sepChar == kvp.sepChar;
    }

}