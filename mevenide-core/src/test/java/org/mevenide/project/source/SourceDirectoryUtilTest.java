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
package org.mevenide.project.source;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.ProjectConstants;
//causes a cycle
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
		SourceDirectoryUtil.addSource(project, "src/pyo/iut/javaa", ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		
		assertEquals("src/pyo/javaa", project.getBuild().getSourceDirectory());
		assertEquals("src/pyo/teest/javaa", project.getBuild().getUnitTestSourceDirectory());
		assertEquals("src/pyo/aspeect", project.getBuild().getAspectSourceDirectory());
		assertEquals("src/pyo/iut/javaa", project.getBuild().getIntegrationUnitTestSourceDirectory());
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
		
		SourceDirectoryUtil.addSource(project, "src/pyo/iut", ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/iut"));
	}

}
