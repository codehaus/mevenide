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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.mevenide.project.ProjectConstants;

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
		Map sourceDirectories = projectReader.readSourceDirectories(pom);
		
//		Iterator it = sourceDirectories.keySet().iterator();
//		while (it.hasNext()) {
//			String sourceType = (String) it.next();
//			System.err.println(sourceType + " : " + sourceDirectories.get(sourceType));
//		}
		
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
