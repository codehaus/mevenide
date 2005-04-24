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

import java.util.Collections;
import junit.framework.TestCase;
import org.apache.maven.project.Project;
import org.jdom.Element;
import org.jdom.DefaultJDOMFactory;
import org.jdom.JDOMFactory;
import org.mevenide.context.AbstractQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */

public class ProjectWalker2Test extends TestCase {
	private Element project ; 
	private ProjectWalker2 walker ; 
	
    protected void setUp() throws Exception {
    // is this necesaary, maybe just return null from getRootProjectElement()
        JDOMFactory factory = new DefaultJDOMFactory();           
        project = factory.element("project");
        Element gr = factory.element("groupId");
        gr.setText("MYgroupID");
        project.addContent(gr);
        Element build = factory.element("build");
        Element test = factory.element("unitTest");
        build.addContent(test);
        Element src = factory.element("sourceDirectory");
        src.setText("mySourceDir");
        build.addContent(src);
        project.addContent(build);
        Element repository = factory.element("repository");
        Element connection = factory.element("connection");
        connection.setText("myCvsConnection");
        repository.addContent(connection);
        project.addContent(repository);
        TestContext context = new TestContext(project);
        walker = new ProjectWalker2(context);
    }

    protected void tearDown() throws Exception {
        project = null;
    }

    public void testResolve() throws Exception {
        assertEquals("MYgroupID", walker.getValue("pom.groupId"));
    	assertEquals("mySourceDir", walker.getValue("pom.build.sourceDirectory"));
        assertEquals("myCvsConnection", walker.getValue("pom.repository.connection"));
    }

    
    private class TestContext extends AbstractQueryContext implements IProjectContext {
        private Element  projectElement;
        TestContext(Element proj) {
            projectElement = proj;
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
        
        public java.io.File getUserDirectory() {
            return null;
        }
        
        public String getUserPropertyValue(String key) {
            return null;
        }
        
        public Project getFinalProject() {
            return null;
        }
        
        public java.io.File[] getProjectFiles() {
            return null;
        }
        
        public int getProjectDepth() {
            return 0;
        }
        
        
        public Project[] getProjectLayers() {
            return null;
        }
        
        public org.jdom.Element[] getRootElementLayers() {
            return new Element[0];
        }
        
        public org.jdom.Element getRootProjectElement() {
            return projectElement;
        }

        public java.util.Set getBuildPropertyKeys() {
            return Collections.EMPTY_SET;
        }

        public java.util.Set getProjectPropertyKeys() {
            return Collections.EMPTY_SET;
        }

        public java.util.Set getUserPropertyKeys() {
            return Collections.EMPTY_SET;
        }

        public String getParentProjectPropertyValue(String key) {
            return null;
        }

        public java.util.Set getParentProjectPropertyKeys() {
            return Collections.EMPTY_SET;
        }

        public String getParentBuildPropertyValue(String key) {
            return null;
        }

        public java.util.Set getParentBuildPropertyKeys() {
            return Collections.EMPTY_SET;
        }

        public java.util.Set getPropertyKeysAt(int location) {
            return Collections.EMPTY_SET;
        }

        public String getPropertyValueAt(String key, int location) {
            return null;
        }
        
    }
    
}
