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

package org.mevenide.netbeans.project.nodes;

import org.apache.tools.ant.DirectoryScanner;

/**
 * a directory scanner subclass which only purposes is to return the default excludes..
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
final class DirScannerSubClass extends DirectoryScanner {
    
    /** Creates a new instance of DirScannerSubClass */
    public DirScannerSubClass() {
        super();
    }
    
    public static String[] getDefaultExcludesHack() {
        return DEFAULTEXCLUDES;
    }
}
