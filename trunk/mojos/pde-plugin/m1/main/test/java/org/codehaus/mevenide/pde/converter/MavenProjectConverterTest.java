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
package org.codehaus.mevenide.pde.converter;

import java.io.File;
import org.apache.maven.jelly.MavenJellyContext;
import org.apache.maven.project.Build;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import junit.framework.TestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenProjectConverterTest extends TestCase {

    private MavenProjectConverter converter;
    
    protected void setUp() throws Exception {
        Project project = new Project(); 
        project.setOrganization(new Organization());
        project.setBuild(new Build());
        Resource r = new Resource();
        r.setDirectory("test");
        r.setFiltering(false);
        project.getBuild().addResource(r);
        UnitTest unitTest = new UnitTest();
        unitTest.addResource(r);
        project.getBuild().setUnitTest(unitTest);
        project.setFile(new File(getClass().getResource("/project.xml").getFile()));
        converter = new MavenProjectConverter(project, new MavenJellyContext());
    }

    protected void tearDown() throws Exception {
        converter = null;
    }
    
    public void testConvert() throws Exception {
        converter.convert();
    }
}
