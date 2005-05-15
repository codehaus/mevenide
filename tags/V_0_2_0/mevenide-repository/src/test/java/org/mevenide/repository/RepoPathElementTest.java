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

package org.mevenide.repository;

import java.io.File;
import junit.framework.*;

/**
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RepoPathElementTest extends TestCase {
    
    public RepoPathElementTest(String testName) {
        super(testName);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RepoPathElementTest.class);
        
        return suite;
    }


    /**
     * Test of isLeaf method, of class org.mevenide.repository.RepoPathElement.
     */
    public void testIsLeaf() {
        RepoPathElement el = new RepoPathElement(RepositoryReaderFactory.createLocalRepositoryReader(new File("hello")), null);
        assertFalse(el.isLeaf());
        el.setGroupId("xxx");
        assertFalse(el.isLeaf());
        el.setType("jar");
        assertFalse(el.isLeaf());
        el.setArtifactId("art");
        assertFalse(el.isLeaf());
        el.setVersion("10.0");
        assertTrue(el.isLeaf());
    }


    /**
     * Test of getLevel method, of class org.mevenide.repository.RepoPathElement.
     */
    public void testGetLevel() {
        System.out.println("testGetLevel");
        
        // TODO add your test code below by replacing the default call to fail.
//        fail("The test case is empty.");
    }

    /**
     * Test of getPartialURIPath method, of class org.mevenide.repository.RepoPathElement.
     */
    public void testGetPartialURIPath() {
        System.out.println("testGetPartialURIPath");
        
        // TODO add your test code below by replacing the default call to fail.
//        fail("The test case is empty.");
    }


    
}
