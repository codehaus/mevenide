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
package org.mevenide.ui.eclipse.util;

import java.util.HashMap;
import java.util.Map;

import org.mevenide.project.ProjectConstants;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryTypeUtil {

	private SourceDirectoryTypeUtil() {
	}
	
	private static Map sourceIndexMap;

	public final static String[] sourceTypes = {
		ProjectConstants.MAVEN_SRC_DIRECTORY,
		ProjectConstants.MAVEN_ASPECT_DIRECTORY,
		ProjectConstants.MAVEN_TEST_DIRECTORY,
		ProjectConstants.MAVEN_RESOURCE,
		ProjectConstants.MAVEN_TEST_RESOURCE
		//ProjectConstants.MAVEN_INTEGRATION_TEST_RESOURCE,		
	};

    public static Integer getSourceTypeIndex(String sourceType) {
		if ( sourceIndexMap == null ) {
			sourceIndexMap = new HashMap();
			for (int i = 0; i < sourceTypes.length; i++) {
				sourceIndexMap.put(sourceTypes[i], new Integer(i));
			}
		}
		return (Integer) sourceIndexMap.get(sourceType); 
	}

    public static String guessSourceType(String path) {
        if ( path.indexOf("java") != -1 ) { //$NON-NLS-1$
        	if ( path.indexOf("resources") != -1 || path.indexOf("conf") != -1 || path.indexOf("etc") != -1) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        		return ProjectConstants.MAVEN_RESOURCE;
        	}
        	if ( path.indexOf("test") != -1 ) { //$NON-NLS-1$
				return  ProjectConstants.MAVEN_TEST_DIRECTORY; 
	        } 
        	return ProjectConstants.MAVEN_SRC_DIRECTORY;
        }
        if ( path.indexOf("test") != -1 ) { //$NON-NLS-1$
			if ( path.indexOf("resources") != -1 || path.indexOf("conf") != -1 || path.indexOf("etc") != -1) {  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        		return ProjectConstants.MAVEN_TEST_RESOURCE;
        	}
			return  ProjectConstants.MAVEN_TEST_DIRECTORY; 
        }
        if ( path.indexOf("aspect") != -1 ) { //$NON-NLS-1$
        	if ( path.indexOf("resources") != -1 || path.indexOf("conf") != -1 || path.indexOf("etc") != -1) {  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        		return ProjectConstants.MAVEN_RESOURCE;
            }
 			return ProjectConstants.MAVEN_ASPECT_DIRECTORY;
        }
        if ( path.indexOf("resources") != -1 || path.indexOf("conf") != -1 || path.indexOf("etc") != -1) {   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    		return ProjectConstants.MAVEN_RESOURCE;
        } 
        if ( path.indexOf("src") != -1 || path.indexOf("source") != -1 ) { //$NON-NLS-1$ //$NON-NLS-2$
        	return ProjectConstants.MAVEN_SRC_DIRECTORY;
        }
        return null;
    }

	
    public static boolean isSource(String type) {
    	return ProjectConstants.MAVEN_SRC_DIRECTORY.equals(type)
					|| ProjectConstants.MAVEN_TEST_DIRECTORY.equals(type)
					|| ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(type);
    }
    
    public static boolean isResource(String type) {
    	return ProjectConstants.MAVEN_RESOURCE.equals(type);
    }
    
    public static boolean isUnitTestResource(String type) {
    	return ProjectConstants.MAVEN_TEST_RESOURCE.equals(type);
    }
	
}
