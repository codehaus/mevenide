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
package org.mevenide.properties.resolver;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;

import junit.framework.TestCase;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */

public class ProjectWalker2Test extends TestCase {
	private Project project ; 
	private ProjectWalker2 walker ; 
	
    protected void setUp() throws Exception {
        project = new Project();
        project.setGroupId("groupID");
        Build build = new Build(); 
        UnitTest test = new UnitTest();
        Resource res = new Resource();
        res.setDirectory("directory");
        res.addInclude("include1");
        res.addInclude("include2");
        test.addResource(res);
        build.setUnitTest(test);

        build.setSourceDirectory("mySourceDir");
        project.setBuild(build);
        
        Repository repository = new Repository();
        repository.setConnection("myCvsConnection");
        project.setRepository(repository);
        TestContext context = new TestContext(project);
        walker = new ProjectWalker2(context);
    }

    protected void tearDown() throws Exception {
        project = null;
    }

    public void testResolve() throws Exception {
        assertEquals("groupID", walker.getValue("pom.groupId"));
    	assertEquals("mySourceDir", walker.getValue("pom.build.sourceDirectory"));
        assertEquals("myCvsConnection", walker.getValue("pom.repository.connection"));
    }

    
    private class TestContext implements IQueryContext, IProjectContext {
        private Project  project;
        TestContext(Project proj) {
            project = proj;
        }
        public String getBuildPropertyValue(String key) {
            return null;
        }
        
        public org.mevenide.context.IProjectContext getPOMContext() {
            return this;
        }
        
        public java.io.File getProjectDirectory() {
            return null;
        }
        
        public String getProjectPropertyValue(String key) {
            return null;
        }
        
        public String getPropertyValue(String key) {
            return null;
        }
        
        public java.io.File getUserDirectory() {
            return null;
        }
        
        public String getUserPropertyValue(String key) {
            return null;
        }
        
        public Project getFinalProject() {
            return project;
        }
        
        public java.io.File[] getProjectFiles() {
            return null;
        }
        
        public Project[] getProjectLayers() {
            return null;
        }
        
    }
    
}
