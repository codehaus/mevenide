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

import java.io.FileReader;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.builder.DefaultProjectUnmarshaller;
import org.mevenide.AbstractMevenideTestCase;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyResolverTest extends AbstractMevenideTestCase {
	
	protected DependencyFactory dependencyFactory;
	protected IDependencyResolver dependencyResolver;
	
	protected void setUp() throws Exception {
		super.setUp();
		dependencyFactory = DependencyFactory.getFactory();
		dependencyResolver = dependencyFactory.getDependencyResolver();
	}

	public void testIsDependencyPresent()throws Exception {
		Project project = new DefaultProjectUnmarshaller().parse(new FileReader(projectFile));
		List dependencies = project.getDependencies();
		
		Dependency dep = dependencyFactory.getDependency("E:/maven/repository/junit/jars/junit-3.8.1.jar");
		assertTrue(dependencyResolver.isDependencyPresent(project, dep));
		 
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/junit/jars/junit-3.8.1.jar");
		assertTrue(dependencyResolver.isDependencyPresent(project, dep));
		 
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/jars/junit-3.8.1.jar");
		assertTrue(dependencyResolver.isDependencyPresent(project, dep));
		
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/junit-3.8.1.jar");
		assertTrue(dependencyResolver.isDependencyPresent(project, dep));
		
		
	}

}
