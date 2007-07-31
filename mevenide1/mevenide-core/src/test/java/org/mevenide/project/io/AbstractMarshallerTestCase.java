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

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.mevenide.util.DefaultProjectUnmarshaller;
import org.apache.maven.project.Project;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractMarshallerTestCase extends TestCase {
    protected Project testProject;
    protected IProjectMarshaller marshaller;

    protected void setUp() throws Exception {
        String pomFile = DefaultProjectMarshallerTest.class.getResource("/project.xml").getFile();
        Reader reader = new FileReader(pomFile);
        testProject = new DefaultProjectUnmarshaller().parse(reader);
		marshaller = getMarshaller();
    }

	protected abstract IProjectMarshaller getMarshaller() throws Exception ;
	
    public void testMarshall() throws Exception {
        Writer writer = new StringWriter();
    	marshaller.marshall(writer, testProject);
    	Reader reader = new StringReader(writer.toString());
        Project result = new DefaultProjectUnmarshaller().parse(reader);

        assertEquals(testProject.getArtifactId(), testProject.getArtifactId());
        assertEquals(testProject.getArtifacts(), testProject.getArtifacts());
        assertEquals(testProject.getDevelopers(), testProject.getDevelopers());
        assertEquals(testProject.getExtend(), testProject.getExtend());
        assertEquals(testProject.getDescription(), testProject.getDescription());
        assertEquals(testProject.getBuild(), testProject.getBuild());
        assertEquals(testProject.getId(), testProject.getId());
        assertEquals(testProject.getInceptionYear(), testProject.getInceptionYear());
        assertEquals(testProject.getDistributionSite(), testProject.getDistributionSite());
        assertEquals(testProject.getDistributionDirectory(), testProject.getDistributionDirectory());
        assertEquals(testProject.getGroupId(), testProject.getGroupId());
        assertEquals(testProject.getLogo(), testProject.getLogo());
        assertEquals(testProject.getName(), testProject.getName());
        assertEquals(testProject.getOrganization(), testProject.getOrganization());
        assertEquals(testProject.getPomVersion(), testProject.getPomVersion());
        assertEquals(testProject.getGumpRepositoryId(), testProject.getGumpRepositoryId());
        assertEquals(testProject.getShortDescription(), testProject.getShortDescription());
        assertEquals(testProject.getUrl(), testProject.getUrl());
        assertEquals(testProject.getSiteDirectory(), testProject.getSiteDirectory());
        assertEquals(testProject.getSiteAddress(), testProject.getSiteAddress());
        assertEquals(testProject.getShortDescription(), testProject.getShortDescription());
        assertEquals(testProject.getPackage(), testProject.getPackage());
        assertEquals(testProject.getReports(), testProject.getReports());
        assertEquals(testProject.getRepository(), testProject.getRepository());
        assertEquals(testProject.getContributors(), testProject.getContributors());
    }
   
}
