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
package org.mevenide.project.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.util.StringUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public final class ResourceUtil {

	private ResourceUtil() { }

	/**
	 * construct a Resource from a given path
	 * 
	 * @param path
	 * @return
	 */
	public static Resource newResource(String path, String[] exclusionPatterns) {
	    Resource resource = new Resource();
		resource.setDirectory(path);
		for (int i = 0; i < exclusionPatterns.length; i++) {
			resource.addExclude(exclusionPatterns[i]);
        }
		
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
	
	/**
	 * 
	 * @return true if res1.directory == res2.directory in the StringUtils.relaxEquals sense
	 */
	public static boolean areEquivalent(Resource res1, Resource res2) {
		if ( res1 == null && res2 == null ) {
			return true;
		}
		if ( res1 != null ) {
			if ( res2 == null ) return false;
			return StringUtils.relaxEqual(res1.getDirectory(), res2.getDirectory());
		}
		return false;
	}
	
	public static void remove(List resources, Resource resource) {
		List resCopy = new ArrayList();
		resCopy.addAll(resources);
		for ( int u = 0; u < resCopy.size(); u++ ) {
			for (int i = 0; i < resources.size(); i++) {
				if ( ResourceUtil.areEquivalent((Resource) resCopy.get(u), (Resource) resources.get(i)) ) {
					resources.remove(i);
				}
			}
		}
	}
}
