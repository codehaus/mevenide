/*
 * LocalRepositoryReaderTest.java
 * JUnit based test
 *
 * Created on February 17, 2005, 7:06 PM
 */

package org.mevenide.repository;

import java.net.URI;
import junit.framework.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.mevenide.project.dependency.DefaultDependencyResolver;
import org.mevenide.project.dependency.IDependencyResolver;

/**
 *
 * @author cenda
 */
public class HttpRepositoryReaderTest extends TestCase {
    private HttpRepositoryReader reader;
    public HttpRepositoryReaderTest(String testName) {
        super(testName);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    protected void setUp() throws Exception {
        reader = new HttpRepositoryReader(URI.create("http://www.ibiblio.org/maven/"));
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(HttpRepositoryReaderTest.class);
        
        return suite;
    }

    /**
     * Test of readElements method, of class org.mevenide.repository.LocalRepositoryReader.
     */
    public void testReadElements() throws Exception {
        RepoPathElement element = new RepoPathElement(reader);
        RepoPathElement[] groups = element.getChildren();
        assertNotNull(groups);
        assertTrue(groups.length > 0);
        
        RepoPathElement jarType = new RepoPathElement(reader);
        jarType.setGroupId("commons-httpclient");
        jarType.setType("jar");
        RepoPathElement[] jars = jarType.getChildren();
        assertNotNull(jars);
        assertTrue(jars.length > 0);
        
//        RepoPathElement classworldType = new RepoPathElement(reader);
//        classworldType.setGroupId("group1");
//        classworldType.setType("jar");
//        classworldType.setArtifactId("classworlds");
//        RepoPathElement[] versions = classworldType.getChildren();
//        assertNotNull(versions);
//        assertEquals(3, versions.length);
//        RepoPathElement ver1 = null;
//        RepoPathElement ver2 = null;
//        RepoPathElement ver3 = null;
//        for (int i = 0; i < versions.length; i++) {
//            if ("1.0-beta-5".equals(versions[i].getVersion())) {
//                ver1 = versions[i];
//            }
//            if ("1.0-rc1".equals(versions[i].getVersion())) {
//                ver2 = versions[i];
//            }
//            if ("1.1-SNAPSHOT".equals(versions[i].getVersion())) {
//                ver3 = versions[i];
//            }
//        }
//        assertNotNull(ver1);
//        assertNotNull(ver2);
//        assertNotNull(ver3);
        
        // check plugins
        RepoPathElement pluginType = new RepoPathElement(reader);
        pluginType.setGroupId("mevenide");
        pluginType.setType("plugin");
        RepoPathElement[] plugins = pluginType.getChildren();
        assertNotNull(plugins);
        assertTrue(plugins.length > 0);
        
    }
    
}
