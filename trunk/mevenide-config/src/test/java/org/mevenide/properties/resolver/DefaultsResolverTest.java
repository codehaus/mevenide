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

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DefaultsResolverTest extends AbstractResolverTestCase {
    
    protected IPropertyFinder def;
    /** Creates a new instance of DefaultsResolverTest */
    public DefaultsResolverTest() {
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        def = new DefaultsResolver(projectDir, userHomeDir, finder);
    }
    
    public void testDefaults1() throws Exception {
        String repo = def.getValue("maven.repo.remote");
        assertNotNull(repo);
        assertEquals("http://www.ibiblio.org/maven", repo);
        
        // doesn't resolve values.
        String build = def.getValue("maven.build.src");
        assertNotNull(build);
        assertEquals("${maven.build.dir}/src", build);
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        def = null;
    }
    
}
