/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
	
	
	/**
	 * iterate ${pom.build.resources} and merge those whose directory is equal to 
	 * the directory of resource passed as parameter with the later.
	 * 
	 * @param project
	 * @param resource
	 * @return boolean
	 */
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
