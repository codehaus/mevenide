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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mevenide.ProjectConstants;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectReaderTest extends TestCase {
	
	private File pom ;
	private ProjectReader projectReader;
	
	protected void setUp() throws Exception {
		pom = new File(ProjectReaderTest.class.getResource("/project.xml").getFile());
		projectReader = ProjectReader.getReader();
	}

	protected void tearDown() throws Exception {
		pom = null;
		projectReader = null;
	}

	public void testGetSourceDirectories() throws Exception {
		Map sourceDirectories = projectReader.getSourceDirectories(pom);
		
		Iterator it = sourceDirectories.keySet().iterator();
		while (it.hasNext()) {
			String sourceType = (String) it.next();
			System.err.println(sourceType + " : " + sourceDirectories.get(sourceType));
		}
		
		assertEquals(3, sourceDirectories.size());
		
		List expectedSources = new ArrayList();
		expectedSources.add("src/aspect");
		expectedSources.add("src/java");
		expectedSources.add("src/test/java");
		
		List expectedTypes = new ArrayList();
		expectedTypes.add(ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		expectedTypes.add(ProjectConstants.MAVEN_SRC_DIRECTORY);
		expectedTypes.add(ProjectConstants.MAVEN_TEST_DIRECTORY);
		
		List resultSources = new ArrayList();
		List resultTypes = new ArrayList();
		
		Iterator iterator = sourceDirectories.keySet().iterator();
		while (iterator.hasNext()) {
			String sourceType = (String) iterator.next();
			resultTypes.add(sourceType);
			resultSources.add(sourceDirectories.get(sourceType));
		}
		
//		assertEquals(expectedSources, resultSources);
//		assertEquals(expectedTypes, resultTypes);
		
		assertEquals("src/aspect", sourceDirectories.get(ProjectConstants.MAVEN_ASPECT_DIRECTORY));
		assertEquals("src/java", sourceDirectories.get(ProjectConstants.MAVEN_SRC_DIRECTORY));
		assertEquals("src/test/java", sourceDirectories.get(ProjectConstants.MAVEN_TEST_DIRECTORY));
		
	}	

}
