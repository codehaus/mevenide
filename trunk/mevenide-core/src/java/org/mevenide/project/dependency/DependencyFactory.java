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
package org.mevenide.project.dependency;


import org.apache.maven.project.Dependency;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyFactory {
	
	private DependencyFactory() throws Exception {
	}
	
	/** singleton related */
	private static DependencyFactory factory = null;
	private static Object lock = new Object();

	public static DependencyFactory getFactory() throws Exception {
		if (factory != null) {
			return factory;
		}
		synchronized (lock) {
			if (factory == null) {
				factory = new DependencyFactory();
			}
			return factory;
		}
	}
		
	/**
	 * return the Dependency instance associated with a given path.
	 * however this seems hard if not impossible to achieve in a 100% way.
	 * 
	 * Also if a file is found in local repo that match the fileName passed 
	 * as parameters, we'll use ${absoluteFileName.parent.parent.name} as 
	 * groupId. in either case we have to guess artifactId and version from the fileName.
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	public Dependency getDependency(String absoluteFileName) throws Exception {
		IDependencyResolver dependencyResolver = AbstractDependencyResolver.newInstance(absoluteFileName);
		
		String groupId = dependencyResolver.guessGroupId();
		
		String artifactId = dependencyResolver.guessArtifactId();
		String version = dependencyResolver.guessVersion();
		String extension = dependencyResolver.guessExtension();
		
		Dependency dependency = new Dependency();
		
		dependency.setGroupId(groupId == null ? "" : groupId); //?
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);
		//dependency.setArtifact(absoluteFileName);
		dependency.setJar(absoluteFileName);
		dependency.setType(extension);
		
		return dependency;
	}


}
