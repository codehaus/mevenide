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
package org.mevenide.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: BuildPropertiesLocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class BuildPropertiesLocationFinder extends PropertiesLocationFinder {
    
    private static BuildPropertiesLocationFinder finder;
    
    protected String getPropertyFile() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, "build.properties").getAbsolutePath();
    }
    
    private BuildPropertiesLocationFinder() throws FileNotFoundException, IOException {
		loadProperties();
    }
    
    public synchronized static BuildPropertiesLocationFinder getInstance() throws FileNotFoundException, IOException {
        if ( finder == null ) {
            finder = new BuildPropertiesLocationFinder();
        }
        return finder;
    }
}

