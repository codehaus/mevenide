/*
 * DefaultsResolverTest.java
 *
 * Created on April 18, 2004, 3:14 PM
 */

package org.mevenide.properties.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import junit.framework.TestCase;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropFilesAggregatorTest extends AbstractResolverTestCase {
    
    protected IPropertyResolver def;
    /** Creates a new instance of DefaultsResolverTest */
    public PropFilesAggregatorTest() {
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        def = new PropFilesAggregator(projectDir, userHomeDir, new DefaultsResolver(projectDir, userHomeDir, finder));
    }
    
    public void testGetValue() throws Exception {
        String repo = def.getValue("maven.repo.remote");
        assertNotNull(repo);
        assertEquals("http://mevenide.codehaus.org", repo);
        
        // doesn't resolve values.
        String build = def.getValue("maven.build.src");
        assertNotNull(build);
        assertEquals("${maven.build.dir}/src2", build);
        
    }

    public void testGetResolvedValue() throws Exception {
        String home = def.getResolvedValue("maven.home.local");
        File right = new File(projectDir, ".maven");
        assertNotNull(home);
        assertEquals(right.getAbsolutePath(), home);

        // value comes from locationfinder
        String home2 = def.getResolvedValue("maven.home");
        assertNotNull(home2);
        assertEquals(finder.getMavenHome(), home2);
        
        // complex replacements from multiple properties files.
        String build = def.getResolvedValue("maven.build.src");
        assertNotNull(build);
        File correct = new File(projectDir.getAbsolutePath() + "/target_yyy/src2");
        assertEquals(correct.getAbsolutePath(), build);
    }
    
    
    protected void tearDown() throws Exception {
        super.tearDown();
        def = null;
    }
    
}
