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
package org.mevenide.runner;

import java.io.File;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RunnerUtils {

    private RunnerUtils() { }
    
    public static String getToolsJar() {
        ILocationFinder config = ConfigUtils.getDefaultLocationFinder();
	    return getToolsJar(config.getJavaHome());
    }

    public static String getToolsJar(String javaHome) {
        String toolsJar = javaHome + File.separator + "lib" + File.separator + "tools.jar";
	    if ( !new File(toolsJar).exists() ) {
	    	//mac os x..  
	        //convenient default for MacOSX 10.3.4 where classes.jar is JAVA_HOME/../Classes/classes.jar 
	    	toolsJar = new File(javaHome).getParent();
	    	String classesJarPart = "Classes/classes.jar";
	    	if ( toolsJar.endsWith("/") ) {
	    		toolsJar += classesJarPart;
	    	}
	    	else {
	    		toolsJar += "/" + classesJarPart;
	    	}
	    }
	    return toolsJar;
    }
}
