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
package org.mevenide.properties.resolver.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.ProjectWalker;
import org.mevenide.properties.resolver.PropertyResolverFactory;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ResolverUtils {
    private static final Log log = LogFactory.getLog(ResolverUtils.class.getName());
    
    private ResolverUtils() {
    }
    
    public static String resolve(Project project, String value) {
        if ( value == null ) {
            return value;
        }
        IPropertyResolver resolver = PropertyResolverFactory.getFactory().getResolver(project.getFile().getParentFile());
        String workingValue = value;
        
        if  ( value.indexOf("${") > -1 ) {
            workingValue = resolver.resolveString(value);
        }
        if ( workingValue == null ) {
            //value not resolved. reinitialize workingValue
            workingValue = value; 
        }
        ProjectWalker walker = new ProjectWalker(project);
        try {
            return walker.resolve(workingValue, false);
        }
        catch (Exception e) {
            String message = "Unable to resolve " + value + " by walking through pom"; 
            log.error(message, e);
            return value;
        }
    	
    }
}
