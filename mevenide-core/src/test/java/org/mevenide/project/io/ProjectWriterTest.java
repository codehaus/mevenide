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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.ProjectConstants;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.IDependencyResolver;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectWriterTest extends AbstractMevenideTestCase {
	protected ProjectWriter pomWriter;
	protected DependencyFactory dependencyFactory;
	protected IDependencyResolver dependencyResolver;
	
	protected void setUp() throws Exception {
		super.setUp();
		pomWriter = ProjectWriter.getWriter();
		dependencyFactory = DependencyFactory.getFactory(); 
		dependencyResolver = dependencyFactory.getDependencyResolver();
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
		pomWriter.addSource(
			"src/test/java",
			projectFile,
			ProjectConstants.MAVEN_TEST_DIRECTORY);
		
		Map h = ProjectReader.getReader().getSourceDirectories(projectFile);
		
		assertEquals(3, h.size());
		
		assertTrue(h.containsValue("src/pyo/java"));
		assertTrue(h.containsValue("src/pyo/aspect"));
		assertTrue(h.containsValue("src/test/java"));
		
		assertEquals("src/pyo/java", h.get(ProjectConstants.MAVEN_SRC_DIRECTORY));
		assertEquals("src/test/java", h.get(ProjectConstants.MAVEN_TEST_DIRECTORY));
		assertEquals("src/pyo/aspect", h.get(ProjectConstants.MAVEN_ASPECT_DIRECTORY));
	}

	public void testAddDependency() throws Exception {
		pomWriter.addDependency("E:/bleeeaaaah/testo/ploufs/testo-0.0.1.plouf", projectFile);
		Project project = ProjectReader.getReader().read(projectFile);
		Dependency dep = dependencyFactory.getDependency("E:/bleeeaaaah/testo/ploufs/testo-0.0.1.plouf");
		assertTrue(dependencyResolver.isDependencyPresent(project, dep));
	}
	
	public void testAddResource() throws Exception {
		pomWriter.addResource("src/conf", projectFile);
		assertTrue(isResourcePresent("src/conf", new String[] {"**/*.*"}));
		
		pomWriter.addResource("etc", projectFile);
		assertTrue(isResourcePresent("etc", new String[] {"**/*.*", "fake.xml"}));
		
		
	}

	public void testAddProject() throws Exception {
		File referencedPom = new File(ProjectWriterTest.class.getResource("/project.xml").getFile());
		
		pomWriter.addProject(referencedPom, projectFile);
		
		Project project = ProjectReader.getReader().read(projectFile);
		Dependency dep = dependencyFactory.getDependency("X:/bleah/mevenide/mevenide-core-1.0.jar");
		assertTrue(dependencyResolver.isDependencyPresent(project, dep));
	}

	private boolean isResourcePresent(String testDirectory, String[] includes) throws FileNotFoundException, Exception, IOException {
		Project project = ProjectReader.getReader().read(projectFile);
		List resources = project.getBuild().getResources();
		boolean found = false;
		for (int i = 0; i < resources.size(); i++) {
			Resource resource = (Resource) resources.get(i);
			if ( resource.getDirectory() != null ) {
				boolean temp = resource.getDirectory().equals(testDirectory); 
				for (int j = 0; j < includes.length; j++) {
					temp &= resource.getIncludes().contains(includes[j]);
				} 
				if ( temp ) {
					found = true;	
				}
			}
		}
		return found;
	}

	public void testJarOverride() throws Exception {
		File propFile = new File(projectFile.getParent(), "project.properties");
		
		Project project = ProjectReader.getReader().read(projectFile);
		int prev = project.getDependencies().size();
		
		String path = "C:\\temp\\bleah\\fake.jar";
		String path2 = "C:\\temp\\bleah\\fake2.jar";
		String path3 = "C:\\temp space temp\\bleah\\fake fake2.jar";
		
		pomWriter.jarOverride(path, propFile, projectFile);
		pomWriter.jarOverride(path2, propFile, projectFile);
		pomWriter.jarOverride(path3, propFile, projectFile);
		pomWriter.jarOverride(path2, propFile, projectFile);
		
		project = ProjectReader.getReader().read(projectFile);
		
		assertEquals(prev + 3, project.getDependencies().size());
		
	}
	
	
	
}
