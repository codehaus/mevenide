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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.mevenide.context.IQueryContext;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @author Milos Kleint (ca206216@tiscali.cz)
 * @version $Id: BuildPropertiesLocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class ProjectPropertiesLocationFinder extends CustomizablePropertiesLocationFinder {
   
    public ProjectPropertiesLocationFinder(IQueryContext context) {
        super(context);
    }
    
    public ProjectPropertiesLocationFinder(String effectiveWorkingDirectory) throws FileNotFoundException, IOException {
        super(effectiveWorkingDirectory);
    }
    
    protected String getRelativeFileName() {
        return "project.properties";
    }
    
    protected String getContextPropertyValue(String key) {
        if (getContext() == null) {
            throw new IllegalStateException("No context available.");
        }
        return getContext().getProjectPropertyValue(key);
    }    
}

