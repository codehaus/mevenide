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
 * 
 */
package org.mevenide.project;

import java.io.File;

import org.apache.maven.project.Dependency;
import org.mevenide.Environment;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyFactoryTest extends TestCase {
	
	
	private File artefact;
	
	private File testTypeDirectory;
	private File mevenideHome; 
	
	private DependencyFactory dependencyFactory;
	
	protected void setUp() throws Exception {
		mevenideHome = new File(System.getProperty("user.home"), ".mevenide");
		File rootDirectory = new File(mevenideHome, "repository");
		rootDirectory.mkdirs();
		
 		File testArtifactDirectory = new File(rootDirectory, "mevenide"); 
 		testTypeDirectory = new File(testArtifactDirectory, "txts");
 		testTypeDirectory.mkdirs();
 		
 		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-dev.txt");
		artefact.createNewFile();
		
		dependencyFactory = DependencyFactory.getFactory();
		
		
	}

	protected void tearDown() throws Exception {
		//rootDirectory.delete();
	}
	
	public void testGetDependency() throws Exception {
		Environment.setMavenHome(mevenideHome.getAbsolutePath());
		Dependency dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("mevenide", dep.getGroupId());
		
		Environment.setMavenHome(System.getProperty("user.home"));
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		
		assertEquals("mevenide", dep.getGroupId());
		assertEquals("1.0.7-dev", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-beta1.txt");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.7-beta1", dep.getVersion());
		
		artefact = new File(testTypeDirectory, "junit-3.8.1.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("3.8.1", dep.getVersion());
		assertEquals("junit", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-beta-1.txt");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencyResolver_split-DEP_PATTERN $DEP-1
		assertEquals("1.0.7-beta-1", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File("c:/jdk1.4.1/jre/lib/rt.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencyResolver_getDependency-NOT_RECOGNIZED_PATTERN $DEP-2
		assertNull(dep.getVersion());	
		assertNull(dep.getArtifactId());
	}

	
}
