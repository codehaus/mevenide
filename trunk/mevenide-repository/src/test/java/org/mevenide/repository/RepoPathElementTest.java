/*
 * RepoPathElementTest.java
 * JUnit based test
 *
 * Created on February 19, 2005, 1:00 PM
 */

package org.mevenide.repository;

import java.io.File;
import junit.framework.*;

/**
 *
 * @author cenda
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
        RepoPathElement el = new RepoPathElement(RepositoryReaderFactory.createLocalRepositoryReader(new File("hello")));
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
