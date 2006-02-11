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
package org.mevenide.netbeans.project;

import junit.framework.*;
import java.io.File;
import java.net.URI;
import org.apache.maven.project.Dependency;
import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */

public class FileUtilitiesTest extends TestCase {
    
    public FileUtilitiesTest(java.lang.String testName) {
        super(testName);
    }
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(FileUtilitiesTest.class);
        return suite;
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    /**
     * Test of getDependencyURI method, of class org.mevenide.netbeans.project.FileUtilities.
     */
    public void testGetDependencyURI() {
        IPropertyResolver res = new Res();
        File repo = new File("/home");
        Dependency dep = new Dependency();
        dep.setId("junit");
        dep.setVersion("3.8.1");
        URI uri = FileUtilities.getDependencyURI(dep, repo, res);
        File result = new File("/home/junit/jars/junit-3.8.1.jar");
        assertEquals(result.toURI(), uri);
        
        dep = new Dependency();
        dep.setId("springframework");
        dep.setVersion("1.0.2");
        dep.setType("jar");
        uri = FileUtilities.getDependencyURI(dep, repo, res);
        result = new File("/home/springframework/jars/springframework-1.0.2.jar");
        assertEquals(result.toURI(), uri);
        
        dep = new Dependency();
        dep.setId("ibatis");
        dep.setArtifactId("ibatis-sqlmap");
        dep.setVersion("2.0.2");
        dep.setType("jar");
        uri = FileUtilities.getDependencyURI(dep, repo, res);
        result = new File("/home/ibatis/jars/ibatis-sqlmap-2.0.2.jar");
        assertEquals(result.toURI(), uri);
        
        dep = new Dependency();
        dep.setId("tiles");
        dep.setJar("tiles.jar");
        dep.setType("jar");
        uri = FileUtilities.getDependencyURI(dep, repo, res);
        result = new File("/home/tiles/jars/tiles.jar");
        assertEquals(result.toURI(), uri);

        dep = new Dependency();
        dep.setGroupId("${pom.groupId}");
        dep.setArtifactId("${pom.groupId}");
        dep.setVersion("1.0.2");
        dep.setType("jar");
        uri = FileUtilities.getDependencyURI(dep, repo, res);
        result = new File("/home/haveit/jars/haveit-1.0.2.jar");
        assertEquals(result.toURI(), uri);
        
        
    }
    
    private static class Res implements IPropertyResolver {
        public String getResolvedValue(String key) {
            return key;
        }

        public String getValue(String key) {
            return key;
        }

        public void reload() {
        }

        public String resolveString(String original) {
            if ("${pom.groupId}".equals(original)) {
                return "haveit";
            }
            return original;
        }
        
    }
            
           
    
}
