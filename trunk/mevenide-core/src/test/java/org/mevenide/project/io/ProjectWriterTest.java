/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.project.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.GenericArtifact;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectWriterTest extends AbstractMevenideTestCase {
	protected ProjectWriter pomWriter;
	protected DependencyFactory dependencyFactory;
	
	protected void setUp() throws Exception {
		super.setUp();
		pomWriter = ProjectWriter.getWriter();
		dependencyFactory = DependencyFactory.getFactory(); 
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
		
		Map h = ProjectReader.getReader().readSourceDirectories(projectFile);
		
		assertEquals(3, h.size());
		
		assertTrue(h.containsValue("src/pyo/java"));
		assertTrue(h.containsValue("src/pyo/aspect"));
		assertTrue(h.containsValue("src/test/java"));
		
		assertEquals("src/pyo/java", h.get(ProjectConstants.MAVEN_SRC_DIRECTORY));
		assertEquals("src/test/java", h.get(ProjectConstants.MAVEN_TEST_DIRECTORY));
		assertEquals("src/pyo/aspect", h.get(ProjectConstants.MAVEN_ASPECT_DIRECTORY));
	}

	public void testSetArtifacts() throws Exception {
		Dependency dep = dependencyFactory.getDependency("E:/bleeeaaaah/testo/ploufs/testo-0.0.1.plouf");
		Artifact art = new GenericArtifact(dep);
		
		List l = new ArrayList();
		l.add(art);
		
		pomWriter.setArtifacts(l, ProjectReader.getReader().read(projectFile));
		Project project = ProjectReader.getReader().read(projectFile);
		
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
	}
	
	public void testAddResource() throws Exception {
		pomWriter.addResource("src/conf", projectFile, new String[0]);
		assertTrue(isResourcePresent("src/conf"));
		
		pomWriter.addResource("etc", projectFile, new String[0]);
		assertTrue(isResourcePresent("etc"));
		
	}

	public void testAddProject() throws Exception {
		File referencedPom = new File(ProjectWriterTest.class.getResource("/project.xml").getFile());
		
		pomWriter.addProject(referencedPom, projectFile);
		
		Project project = ProjectReader.getReader().read(projectFile);
		
		Dependency dep = dependencyFactory.getDependency("X:/bleah/mevenide/mevenide-core-1.0.jar");
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
	}

	private boolean isResourcePresent(String testDirectory) throws FileNotFoundException, Exception, IOException {
		Project project = ProjectReader.getReader().read(projectFile);
		List resources = project.getBuild().getResources();
		for (int i = 0; i < resources.size(); i++) {
			Resource resource = (Resource) resources.get(i);
			if ( resource.getDirectory() != null ) {
				if ( resource.getDirectory().equals(testDirectory) ) {
					return true;
				}
			}
		}
		return false;
	}
	
}
