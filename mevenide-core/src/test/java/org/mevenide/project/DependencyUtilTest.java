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
public class DependencyUtilTest extends TestCase {
	
	private File rootDirectory;
	private File artefact;
	
	private File mevenideHome; 
	
	protected void setUp() throws Exception {
		mevenideHome = new File(System.getProperty("user.home"), ".mevenide");
		rootDirectory = new File(mevenideHome, "repository");
		rootDirectory.mkdirs();
		
 		File testArtifactDirectory = new File(rootDirectory, "mevenide"); 
 		File testTypeDirectory = new File(testArtifactDirectory, "txts");
 		testTypeDirectory.mkdirs();
 		
 		artefact = new File(testTypeDirectory, "mevenide-test.txt");
		artefact.createNewFile();
		
	}

	protected void tearDown() throws Exception {
		rootDirectory.delete();
	}
	
	public void testGetGroupId() {
		Environment.setMavenHome(mevenideHome.getAbsolutePath());
		Dependency dep = DependencyUtil.getDependency(artefact.getAbsolutePath());
		assertEquals("mevenide", dep.getGroupId());
	}

	public void testGuessGroupiD() {
		Environment.setMavenHome(System.getProperty("user.home"));
		Dependency dep = DependencyUtil.getDependency(artefact.getAbsolutePath());
		assertEquals("mevenide", dep.getGroupId());
	}
}
