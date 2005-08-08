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
package org.mevenide.properties.resolver;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;

import junit.framework.TestCase;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: MevenideUtilTest.java 30 2003 Exp gdodinet 
 * 
 */
public class ProjectWalkerTest extends TestCase {
	private Project project ; 
	private ProjectWalker walker ; 
	
    protected void setUp() throws Exception {
        project = new Project();
        
        Build build = new Build(); 
        build.setSourceDirectory("mySourceDir");
        project.setBuild(build);
        
        Repository repository = new Repository();
        repository.setConnection("myCvsConnection");
        project.setRepository(repository);
        
        walker = new ProjectWalker(project);
    }

    protected void tearDown() throws Exception {
        project = null;
    }

    public void testResolve() throws Exception {
    	assertEquals("mySourceDir", walker.resolve("${pom.build.sourceDirectory}"));
		assertEquals("this is myCvsConnection test", walker.resolve("this is ${pom.repository.connection} test"));
    	
    }

}
