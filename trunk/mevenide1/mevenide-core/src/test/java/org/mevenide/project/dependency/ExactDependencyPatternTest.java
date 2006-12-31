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

package org.mevenide.project.dependency;

import junit.framework.*;
import org.apache.maven.project.Dependency;
import org.mevenide.TestQueryContext;

/**
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ExactDependencyPatternTest extends TestCase {
    
    private TestQueryContext context;
    
    public ExactDependencyPatternTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        context = new TestQueryContext();
    }
    
    protected void tearDown() throws Exception {
    }
    
    
    /**
     * Test of matches method, of class org.mevenide.project.dependency.ExactDependencyPattern.
     */
    public void testMatches() {
        
        ExactDependencyPattern pattern = new ExactDependencyPattern("artId", "grId", "0.1");
        Dependency dep = new Dependency();
        dep.setArtifactId("artId");
        dep.setGroupId("grId");
        dep.setVersion("0.1");
        assertTrue(pattern.matches(dep, context));
        
        dep = new Dependency();
        dep.setArtifactId("artId");
        dep.setGroupId("grId");
        dep.setVersion("0.1.1");
        assertFalse(pattern.matches(dep, context));
        
        dep = new Dependency();
        dep.setArtifactId("artId");
        dep.setGroupId("grId");
        assertFalse(pattern.matches(dep, context));
        
        dep = new Dependency();
        dep.setId("grId:artId");
        dep.setVersion("0.1");
        assertTrue(pattern.matches(dep, context));
        
        dep = new Dependency();
        assertFalse(pattern.matches(dep, context));
        
        
        pattern = new ExactDependencyPattern("myid", "myid", "0.1");
        dep = new Dependency();
        dep.setId("myid");
        dep.setVersion("0.1");
        assertTrue(pattern.matches(dep, context));
        
        
        pattern = new ExactDependencyPattern("artId", "grId", "${project.version}");
        context.addBuildPropertyValue("project.version", "10.1");
        
        // shall these match? mkleint
        
        dep = new Dependency();
        dep.setArtifactId("artId");
        dep.setGroupId("grId");
        dep.setVersion("${project.version}");
        assertTrue(pattern.matches(dep, context));
        
        dep = new Dependency();
        dep.setArtifactId("artId");
        dep.setGroupId("10.1");
        assertFalse(pattern.matches(dep, context));
        
        
    }
}
    
