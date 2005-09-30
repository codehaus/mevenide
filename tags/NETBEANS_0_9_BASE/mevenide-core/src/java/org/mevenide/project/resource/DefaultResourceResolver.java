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
import java.util.List;

import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultResourceResolver implements IResourceResolver  {
	
	public void mergeSimilarResources(Project project, Resource resource) {
		List resources = project.getBuild().getResources();
		
		prepareResource(resource, resources);
		
		project.getBuild().addResource(resource);
	}
	
	public void mergeSimilarUnitTestResources(Project project, Resource resource) {
		List resources = project.getBuild().getUnitTest().getResources();
	
		prepareResource(resource, resources);
		
		project.getBuild().getUnitTest().addResource(resource);
	}

	private void prepareResource(Resource resource, List resources) {
		List similar = getSimilarResources(resources, resource);
		
		for (int i = 0; i < similar.size(); i++) {
			Resource similarResource = (Resource) similar.get(i);
			mergeIncludes(resource, similarResource);
			mergeExcludes(resource, similarResource);
			resources.remove(similarResource);
		}
	}

	private void mergeExcludes(Resource resource, Resource similarResource) {
		for (int j = 0; j < similarResource.getExcludes().size(); j++) {
			if ( !resource.getExcludes().contains(similarResource.getExcludes().get(j)) ) {
				resource.getExcludes().add(similarResource.getExcludes().get(j));
			}
		}
	}

	private void mergeIncludes(Resource resource, Resource similarResource) {
		for (int j = 0; j < similarResource.getIncludes().size(); j++) {
			if ( !resource.getIncludes().contains(similarResource.getIncludes().get(j)) ) {
				resource.getIncludes().add(similarResource.getIncludes().get(j));
			}
		}
	}

	private List getSimilarResources(List resources, Resource resource) {
		List similar = new ArrayList();
		
		for (int i = 0; i < resources.size(); i++) {
			Resource declaredResource = (Resource) resources.get(i);
			if ( declaredResource.getDirectory().equals(resource.getDirectory()) ) {
				similar.add(declaredResource);
			}
		}
		return similar;
	}
	
}
