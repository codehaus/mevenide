/*
 * LocalRepositoryReaderTest.java
 * JUnit based test
 *
 * Created on February 17, 2005, 7:06 PM
 */

package org.mevenide.repository;

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
public class LocalRepositoryReaderTest extends TestCase {
    private LocalRepositoryReader reader;
    public LocalRepositoryReaderTest(String testName) {
        super(testName);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    protected void setUp() throws Exception {
        File rootDir = new File(this.getClass().getResource("/repository").getFile());
        reader = new LocalRepositoryReader(rootDir);
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(LocalRepositoryReaderTest.class);
        
        return suite;
    }

    /**
     * Test of readElements method, of class org.mevenide.repository.LocalRepositoryReader.
     */
    public void testReadElements() throws Exception {
        RepoPathElement element = new RepoPathElement(reader);
        RepoPathElement[] groups = element.getChildren();
        assertNotNull(groups);
        assertEquals(2, groups.length);
        RepoPathElement group1 = null;
        RepoPathElement group2 = null;
        for (int i = 0; i < groups.length; i++) {
            if ("group1".equals(groups[i].getGroupId())) {
                group1 = groups[i];
            }
            if ("group2".equals(groups[i].getGroupId())) {
                group2 = groups[i];
            }
        }
        assertNotNull(group1);
        assertNotNull(group2);
        // now get children of group1
        RepoPathElement[] types = group1.getChildren();
        assertNotNull(types);
        assertEquals(3, types.length);
        
        RepoPathElement jarType = new RepoPathElement(reader);
        jarType.setGroupId("group1");
        jarType.setType("jar");
        RepoPathElement[] jars = jarType.getChildren();
        assertNotNull(jars);
        assertEquals(2, jars.length);
        
        RepoPathElement classworldType = new RepoPathElement(reader);
        classworldType.setGroupId("group1");
        classworldType.setType("jar");
        classworldType.setArtifactId("classworlds");
        RepoPathElement[] versions = classworldType.getChildren();
        assertNotNull(versions);
        assertEquals(3, versions.length);
        RepoPathElement ver1 = null;
        RepoPathElement ver2 = null;
        RepoPathElement ver3 = null;
        for (int i = 0; i < versions.length; i++) {
            if ("1.0-beta-5".equals(versions[i].getVersion())) {
                ver1 = versions[i];
            }
            if ("1.0-rc1".equals(versions[i].getVersion())) {
                ver2 = versions[i];
            }
            if ("1.1-SNAPSHOT".equals(versions[i].getVersion())) {
                ver3 = versions[i];
            }
        }
        assertNotNull(ver1);
        assertNotNull(ver2);
        assertNotNull(ver3);
        
        // check plugins
        RepoPathElement pluginType = new RepoPathElement(reader);
        pluginType.setGroupId("group1");
        pluginType.setType("plugin");
        RepoPathElement[] plugins = jarType.getChildren();
        assertNotNull(plugins);
        assertEquals(2, plugins.length);
        
    }
    
}
