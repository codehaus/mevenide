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
package org.mevenide.project.dependency;


import java.io.File;

import org.apache.maven.project.Dependency;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public final class DependencyFactory {
	

	private DependencyFactory() {
	}
	
	private static DependencyFactory factory = new DependencyFactory();
	
	public static DependencyFactory getFactory() {
		return factory;
	}
		
	/**
	 * return the Dependency instance associated with a given path.
	 * however this seems hard if not impossible to achieve in a 100% way.
	 * 
	 * Also if a file is found in local repo that match the fileName passed 
	 * as parameters, we'll use ${absoluteFileName.parent.parent.name} as 
	 * groupId. in either case we have to guess artifactId and version from the fileName.
	 * 
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	public Dependency getDependency(String absoluteFileName) throws Exception {
		IDependencyResolver dependencyResolver = DependencyResolverFactory.getFactory().newInstance(absoluteFileName);
		
		String groupId = dependencyResolver.guessGroupId();
		
		String artifactId = dependencyResolver.guessArtifactId();
		String version = dependencyResolver.guessVersion();
		String extension = dependencyResolver.guessExtension();
		
		Dependency dependency = new Dependency();
		
        if (groupId == null) {
            dependency.setId(artifactId);
        } else {
    		dependency.setGroupId(groupId); 
        	dependency.setArtifactId(artifactId);
        }
		dependency.setVersion(version);
		dependency.setType(extension);

        String filename = new File(absoluteFileName).getName();
        if (!dependency.getArtifact().equals(filename)) {
            dependency.setJar(filename);
        }
		
		return dependency;
	}


}
