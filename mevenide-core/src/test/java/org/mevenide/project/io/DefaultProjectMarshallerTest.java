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
package org.mevenide.project.io;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.maven.project.Project;
import org.mevenide.util.DefaultProjectUnmarshaller;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DefaultProjectMarshallerTest extends TestCase {

	private Writer writer;
	private Project project;
	private DefaultProjectMarshaller marshaller;
	
	protected void setUp() throws Exception {
		writer = new StringWriter();
		String pomFile = DefaultProjectMarshallerTest.class.getResource("/project.xml").getFile();
		Reader reader = new FileReader(pomFile);
		project = new DefaultProjectUnmarshaller().parse(reader);
		marshaller = new DefaultProjectMarshaller();
	}

	protected void tearDown() throws Exception {
		writer = null;
	}

	public void testMarshall() throws Exception {
		marshaller.marshall(writer, project);
		
		Reader reader = new StringReader(writer.toString());
		assertEquals(project, new DefaultProjectUnmarshaller().parse(reader));
		
		//System.out.print(writer.toString());
	}
	
	

}
 