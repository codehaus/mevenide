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
package org.mevenide.project.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.ProjectConstants;
import org.mevenide.project.DependencyFactory;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectWriterTest extends TestCase {
	private ProjectWriter pomWriter;
	private File projectFile;
	private DependencyFactory dependencyFactory;
	
	protected void setUp() throws Exception {
		pomWriter = ProjectWriter.getWriter();
		File src = new File(ProjectWriterTest.class.getResource("/fixtures/project-fixture.xml").getFile());
		projectFile = new File(src.getParentFile().getParent(), "project-fixture.xml") ; 
		copy(src.getAbsolutePath(), projectFile.getAbsolutePath());
		dependencyFactory = DependencyFactory.getFactory();
		
	}

	protected void tearDown() throws Exception {
		projectFile.delete();
	}

	public void testAddSource() throws Exception {
		pomWriter.addSource(
			"src/pyo/java",
			projectFile,
		ProjectConstants.MAVEN_SRC_DIRECTORY);
		pomWriter.addSource(
			"src/pyo/aspect",
			projectFile,
			ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		
		Map h = ProjectReader.getReader().getSourceDirectories(projectFile);
		
		assertEquals(3, h.size());
		
		assertTrue(h.containsValue("src/pyo/java"));
		assertTrue(h.containsValue("src/pyo/aspect"));
		assertTrue(h.containsValue("src/test/java"));
		
		assertEquals("src/pyo/java", h.get(ProjectConstants.MAVEN_SRC_DIRECTORY));
		assertEquals("src/test/java", h.get(ProjectConstants.MAVEN_TEST_DIRECTORY));
		assertEquals("src/pyo/aspect", h.get(ProjectConstants.MAVEN_ASPECT_DIRECTORY));
	}

	public void testIsDependencyPresent()throws Exception {
		Project project = ProjectReader.getReader().read(projectFile);
		List dependencies = project.getDependencies();
		
		Dependency dep = dependencyFactory.getDependency("E:/maven/repository/junit/jars/junit-3.8.1.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
		 
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/junit/jars/junit-3.8.1.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
		 
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/jars/junit-3.8.1.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
		
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/junit-3.8.1.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
	}
	
	public void testAddDependency() throws Exception {
		pomWriter.addDependency("E:/bleeeaaaah/testo/ploufs/testo-0.0.1.plouf", projectFile);
		Project project = ProjectReader.getReader().read(projectFile);
		Dependency dep = dependencyFactory.getDependency("E:/bleeeaaaah/testo/ploufs/testo-0.0.1.plouf");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
	}
	
	public void testAddResource() throws Exception {
		String testDirectory = System.getProperty("user.dir");
		
		String testFile1 = new File(testDirectory, "fake1.xml").getAbsolutePath(); 
		String testFile2 = new File(testDirectory, "fake2.xml").getAbsolutePath();
		
		pomWriter.addResource(testFile1, projectFile);
		
		assertTrue(isResourcePresent(testDirectory, new String[] {"fake1.xml"}));
		
		pomWriter.addResource(testFile2, projectFile);
		assertTrue(isResourcePresent(testDirectory, new String[] {"fake1.xml", "fake2.xml"}));
		
		pomWriter.addResource(testDirectory, projectFile);
		assertTrue(isResourcePresent(testDirectory, new String[] {"fake1.xml", "fake2.xml", "**/*.*"}));
		
	}

	public void testAddProject() throws Exception {
		File referencedPom = new File(ProjectWriterTest.class.getResource("/project.xml").getFile());
		
		pomWriter.addProject(referencedPom, projectFile);
		
		Project project = ProjectReader.getReader().read(projectFile);
		Dependency dep = dependencyFactory.getDependency("X:/bleah/mevenide/mevenide-core-1.0.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
	}

	private boolean isResourcePresent(String testDirectory, String[] includes) throws FileNotFoundException, Exception, IOException {
		Project project = ProjectReader.getReader().read(projectFile);
		List resources = project.getBuild().getResources();
		boolean found = false;
		for (int i = 0; i < resources.size(); i++) {
			Resource resource = (Resource) resources.get(i);
			boolean temp = resource.getDirectory().equals(testDirectory); 
			for (int j = 0; j < includes.length; j++) {
				temp &= resource.getIncludes().contains(includes[j]);
			} 
			if ( temp ) {
				found = true;	
			}
		}
		return found;
	}

	private void copy(String sourceFile, String destFile) throws Exception {

		FileInputStream from = new FileInputStream(sourceFile);
		FileOutputStream to = new FileOutputStream(destFile);
		try {
			byte[] buffer = new byte[4096]; 
			int bytes_read; 
			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read);
			}
		} 
		finally {
			if (from != null) {
				from.close();
			}
			if (to != null) {
				to.close();
			}
		}

	}
	
}
