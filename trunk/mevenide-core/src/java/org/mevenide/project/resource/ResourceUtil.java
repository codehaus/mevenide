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
package org.mevenide.project.resource;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public final class ResourceUtil {

	private ResourceUtil() { }

	/**
	 * construct a Resource from a given path, including ALL children
	 * 
	 * @param path
	 * @return
	 */
	public static Resource newResource(String path) {
		Resource resource = new Resource();
		resource.setDirectory(path);
		resource.addInclude("**/*.*");
		
		return resource;
	}
	
	/**
	 * scans project.build and project.build.unitTest for resources which directory matches the directory parameter
	 * @return true if a resource denoted by the directory passed as parameter is found 
	 */
	public static boolean isResourcePresent(Project project, String directory) {
	    //if directory is null return
	    if ( directory == null ) {
	        return false;
	    }
	    
	    if ( project.getBuild() == null ) {
	        return false;
	    }
	    
	    //scan build.resources
	    List buildResources = project.getBuild().getResources();
	    for ( Iterator itr = buildResources.iterator(); itr.hasNext(); ) {
	        Resource resource = (Resource) itr.next();
	        if ( resource.getDirectory() != null && resource.getDirectory().equals(directory) ) {
	            return true;
	        }
	    }
	    
	    if ( project.getBuild().getUnitTest() == null ) {
	        return false;
	    }
	    
	    //scan build.unitTest.resources
	    List unitTestResources = project.getBuild().getUnitTest().getResources();
	    for ( Iterator itr = unitTestResources.iterator(); itr.hasNext(); ) {
	        Resource resource = (Resource) itr.next();
	        if ( resource.getDirectory() != null && resource.getDirectory().equals(directory) ) {
	            return true;
	        }
	    }
	    
	    //not found
	    return false;
	}
}
