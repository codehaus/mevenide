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

/**
 * 
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class Comment extends Element {
    private StringBuffer buf;
    
    protected Comment() {
        buf = new StringBuffer(100);
    }

    public void addToComment(String add) {
        buf.append(add);
    }

    public void setComment(String comment) {
        buf = new StringBuffer(comment);
    }

    public String toString() {
        return getValue();
    }
    
    public String getValue() {
        return buf.toString();
    }
    
    public boolean equals(Object obj) {
        if ( !(obj instanceof Comment) ) {
            return false;
        }
        return this.buf.equals(((Comment) obj).buf);
    }
}