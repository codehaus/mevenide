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
package org.mevenide.project.dependency;

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
		File mavenLocal = new File(System.getProperty("user.home"), ".maven");
		File localRepo = new File(mavenLocal, "repository");
		Environment.setMavenRepository(localRepo.getAbsolutePath());
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
		//BUG-DependencySplitter_split-DEP_PATTERN $DEP-1
		assertEquals("1.0.7-beta-1", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "junit-1.0.rc3.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3", dep.getVersion());
		assertEquals("junit", dep.getArtifactId());
		
		artefact = new File("c:/jdk1.4.1/jre/lib/rt.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencyResolver_getDependency-NOT_RECOGNIZED_PATTERN $DEP-2
		assertNull(dep.getVersion());	
		assertNull(dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-1.0.rc3.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-1.0.rc3-SNAPSHOT.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3-SNAPSHOT", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-SNAPSHOT.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("SNAPSHOT", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "testo-0.0.1.plouf");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("0.0.1", dep.getVersion());
		assertEquals("testo", dep.getArtifactId());
		
		dep = dependencyFactory.getDependency(new File(Environment.getMavenRepository(), "commons-httpclient\\jars\\commons-httpclient-2.0alpha1-20020829.jar").getAbsolutePath());
		assertEquals("2.0alpha1-20020829", dep.getVersion());
		assertEquals("commons-httpclient", dep.getArtifactId());
		assertEquals("commons-httpclient", dep.getGroupId());
	}

	
}
