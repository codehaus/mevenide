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
import java.io.FileOutputStream;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.project.DependencyUtil;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectWriterTest extends TestCase {
	private ProjectWriter pomWriter;
	private File projectFile;
	
	protected void setUp() throws Exception {
		pomWriter = ProjectWriter.getWriter();
		File src = new File(ProjectWriterTest.class.getResource("/fixtures/project-fixture.xml").getFile());
		projectFile = new File(src.getParentFile().getParent(), "project-fixture.xml") ; 
		copy(src.getAbsolutePath(), projectFile.getAbsolutePath());
	}

	protected void tearDown() throws Exception {
		projectFile.delete();
	}

	public void testAddSource() throws Exception {
//		pomWriter.addSource(
//			"src/pyo/java",
//			projectFile,
//			BuildConstants.MAVEN_SRC);
//		pomWriter.addSource(
//			"src/pyo/aspect",
//			projectFile,
//			BuildConstants.MAVEN_ASPECT);
//		Hashtable h = ProjectReader.getAllSourceDirectories(projectFile);
//		assertEquals(2, h.size());
//		assertTrue(h.containsKey("src/pyo/java"));
//		assertTrue(h.containsKey("src/pyo/aspect"));
	}

	public void testIsDependencyPresent()throws Exception {
		 Project project = ProjectReader.getReader().read(projectFile);
		 List dependencies = project.getDependencies();
		
		 Dependency dep = DependencyUtil.getDependency("E:/maven/repository/junit/jars/junit-3.8.1.jar");
		 assertTrue(pomWriter.isDependencyPresent(project, dep));
		 
		 dep = DependencyUtil.getDependency("E:/bleeeaaaah/junit/jars/junit-3.8.1.jar");
		 assertTrue(pomWriter.isDependencyPresent(project, dep));
		 
		dep = DependencyUtil.getDependency("E:/bleeeaaaah/plouf/jars/junit-3.8.1.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
		
		dep = DependencyUtil.getDependency("E:/bleeeaaaah/plouf/junit-3.8.1.jar");
		assertTrue(pomWriter.isDependencyPresent(project, dep));
	}
	
	public void testAddDependency() {
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
