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

import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyUtil {
	
	/**
	 * checks if a Dependency identified by its artifact path is present in the POM.
	 * default visibility for testing purpose
	 * 
	 * testing artifact doesnt seem to be a good solution since it is often omitted
	 * we rather have to test artifactId and version.
	 * 
	 * deeply depends upon a fully functionnal version of both methods :
	 * 
	 * org.mevenide.project.DependencyUtil#guessVersion() and
	 * org.mevenide.project.DependencyUtil#guessArtifactId()
	 * 
	 * @param project
	 * @param absoluteFileName
	 * @return
	 */
	public static boolean isDependencyPresent(Project project, Dependency dependency) {
		List dependencies = project.getDependencies();
		if ( dependencies == null ) {
			return false;
		}
		for (int i = 0; i < dependencies.size(); i++) {
			Dependency declaredDependency = (Dependency) dependencies.get(i);
		
			String version = declaredDependency.getVersion(); 
			String artifactId = declaredDependency.getArtifactId();
		
			if (  artifactId != null && artifactId.equals(dependency.getArtifactId()) 
				  && version != null && version.equals(dependency.getVersion())) {
				return true;
			}
		}
		return false;
	}
}
