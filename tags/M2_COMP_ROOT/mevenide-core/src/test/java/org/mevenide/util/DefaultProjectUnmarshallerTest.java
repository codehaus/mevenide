/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.util;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: DefaultProjectUnmarshallerTest.java 8 mai 2003 15:32:4913:34:35 Exp gdodinet 
 * 
 */
public class DefaultProjectUnmarshallerTest extends TestCase {

	private DefaultProjectUnmarshaller unmarshaller;
	private FileReader reader;
	
	protected void setUp() throws Exception {
		File pom = new File(DefaultProjectUnmarshallerTest.class.getResource("/project.xml").getFile());
		reader = new FileReader(pom);
		unmarshaller = new DefaultProjectUnmarshaller(); 
	}

	
	protected void tearDown() throws Exception {
		unmarshaller = null;
	}
	
	public void testUnmarshallProperties() throws Exception {
		Project project = unmarshaller.parse(reader);
		List deps = project.getDependencies();
		for (int i = 0; i < deps.size(); i++) {
			Dependency d = (Dependency) deps.get(i);
			if ( "maven".equals(d.getGroupId()) && "maven".equals(d.getArtifactId() ) ) {
				assertEquals(2, d.getProperties().size());
				assertEquals("true", d.getProperty("test.prop"));
				assertEquals("it worked", d.getProperty("anotherProp"));
			}
		}	
	}
	
}
