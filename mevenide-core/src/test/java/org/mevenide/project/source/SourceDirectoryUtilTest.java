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
package org.mevenide.project.source;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.AbstractMevenideTestCase;
//causes a cycle
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.io.ProjectReader;


/**
 * 
 * This test introduces a cycle in package dependencies -__-; see imports
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryUtilTest extends AbstractMevenideTestCase {

	
	private ProjectReader reader; 
	protected Project project; 
	
	protected void setUp() throws Exception {
		super.setUp();
		reader  = ProjectReader.getReader();
		project = reader.read(projectFile);	
	}

	public void testAddSource() {
		SourceDirectoryUtil.addSource(project, "src/pyo/javaa", ProjectConstants.MAVEN_SRC_DIRECTORY);
		SourceDirectoryUtil.addSource(project, "src/pyo/teest/javaa", ProjectConstants.MAVEN_TEST_DIRECTORY);
		SourceDirectoryUtil.addSource(project, "src/pyo/aspeect", ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		
		assertEquals("src/pyo/javaa", project.getBuild().getSourceDirectory());
		assertEquals("src/pyo/teest/javaa", project.getBuild().getUnitTestSourceDirectory());
		assertEquals("src/pyo/aspeect", project.getBuild().getAspectSourceDirectory());
		
	}
	
	public void testIsDirectoryPresent() {
		project.setBuild(null);
		assertFalse(SourceDirectoryUtil.isSourceDirectoryPresent(project, "bleah"));
		project.setBuild(new Build());
		assertFalse(SourceDirectoryUtil.isSourceDirectoryPresent(project, "bleah"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/javaa", ProjectConstants.MAVEN_SRC_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/javaa"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/teest/javaa", ProjectConstants.MAVEN_TEST_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/teest/javaa"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/aspeect", ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/aspeect"));
		
	}

}
