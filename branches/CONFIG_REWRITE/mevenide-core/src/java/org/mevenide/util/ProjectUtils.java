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

import java.io.File;
import org.apache.maven.project.Project;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectUtils {
    private ProjectUtils() {}
    
    /**
     * resolve a whole project including its ancestors. 
     * @param pomFile the POM to read
     */
    public static Project resolveProjectTree(File pomFile) {
        IQueryContext queryContext = new DefaultQueryContext(pomFile.getParentFile());
        
        IProjectContext projectContext = queryContext.getPOMContext();
        Project pom = projectContext.getFinalProject();
        pom.setFile(pomFile);
        return pom;
    }
}
