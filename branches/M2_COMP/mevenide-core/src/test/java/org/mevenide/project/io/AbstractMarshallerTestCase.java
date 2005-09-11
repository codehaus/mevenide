/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractMarshallerTestCase extends TestCase {
    protected MavenProject testProject;
    protected IProjectMarshaller marshaller;

    protected void setUp() throws Exception {
        String pomFile = DefaultProjectMarshallerTest.class.getResource("/project.xml").getFile();
        Reader reader = new FileReader(pomFile);
        testProject = new MavenProject();
        Model model = new MavenXpp3Reader().read(reader);
        testProject.setModel(model);
		marshaller = getMarshaller();
    }

	protected abstract IProjectMarshaller getMarshaller() throws Exception ;
	
    public void testMarshall() throws Exception {
        Writer writer = new StringWriter();
    	marshaller.marshall(writer, testProject);
    	Reader reader = new StringReader(writer.toString());
    	//assertEquals(testProject, new DefaultProjectUnmarshaller().unmarshall(reader));
    	//System.out.print(writer.toString());
    }
   
}