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
package org.mevenide.project;

import java.io.File;
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
public class ResourceUtil {
	
	
	/**
	 * iterate ${pom.build.resources} and merge those whose directory is equal to 
	 * the directory of resource passed as parameter with the later.
	 * 
	 * @param project
	 * @param resource
	 * @return boolean
	 */
	public static void mergeSimilarResources(Project project, Resource resource) {
		List similar = getSimilarResources(project, resource);
		
		for (int i = 0; i < similar.size(); i++) {
			Resource similarResource = (Resource) similar.get(i);
			resource.getIncludes().addAll(similarResource.getIncludes());
			resource.getExcludes().addAll(similarResource.getExcludes());
			project.getBuild().getResources().remove(similarResource);
		}
		
		project.getBuild().addResource(resource);
	}

	public static List getSimilarResources(Project project, Resource resource) {
		List similar = new ArrayList();
		
		List resources = project.getBuild().getResources();
		
		for (int i = 0; i < resources.size(); i++) {
			Resource declaredResource = (Resource) resources.get(i);
			if ( declaredResource.getDirectory().equals(resource.getDirectory()) ) {
				similar.add(declaredResource);
			}
		}
		return similar;
	}
	
	/**
	 * construct a Resource from a given path, including all children
	 * 
	 * @param path
	 * @return
	 */
	public static Resource newResource(String path) {
		boolean isDirectory = new File(path).isDirectory();
		String directory =  isDirectory ? path : new File(path).getParent();
		String singleInclude = isDirectory ? "**/*.*" : new File(path).getName();
		
		Resource resource = new Resource();
		resource.setDirectory(directory);
		resource.addInclude(singleInclude);
		
		return resource;
	}
}
