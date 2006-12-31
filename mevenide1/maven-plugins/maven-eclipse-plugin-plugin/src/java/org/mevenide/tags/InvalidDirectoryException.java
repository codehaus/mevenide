/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags;

import org.apache.commons.jelly.JellyTagException;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class InvalidDirectoryException extends JellyTagException {
    private String directory;
    private boolean doesExist;
    private String property;
    
    public InvalidDirectoryException(String directory, boolean doesExist, String property) {
        this.directory = directory;
        this.doesExist = doesExist;
        this.property = property;
    }
    
    public String toString() {
        return property + " property should " + (doesExist ? "not" : "") + " be absolute (" + directory + ")";
    }
    
    public String getMessage() {
        return toString();
    }
}
