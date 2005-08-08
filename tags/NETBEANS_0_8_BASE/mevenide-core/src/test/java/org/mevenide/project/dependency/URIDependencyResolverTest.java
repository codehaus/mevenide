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
import java.net.URI;

/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class URIDependencyResolverTest extends TestCase {
    
    public URIDependencyResolverTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(URIDependencyResolverTest.class);
        
        return suite;
    }

    /**
     * Test of guessArtifactId method, of class org.mevenide.project.dependency.URIDependencyResolver.
     */
    public void testGuessArtifactId() {
        URIDependencyResolver resolver = new URIDependencyResolver();
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/txts/foo+joe-test2.-bar-1.0.7-beta-1.txt"));
        assertEquals("foo+joe-test2.-bar", resolver.guessArtifactId());
        assertEquals("1.0.7-beta-1", resolver.guessVersion());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/javadoc.jars/foo+joe-test2.-bar-1.0.7-beta-1.javadoc.jar"));
        assertEquals("foo+joe-test2.-bar", resolver.guessArtifactId());
        assertEquals("1.0.7-beta-1", resolver.guessVersion());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/plugins/foo+joe-test2.-bar-1.0.7-beta-1.jar"));
        assertEquals("foo+joe-test2.-bar", resolver.guessArtifactId());
        assertEquals("1.0.7-beta-1", resolver.guessVersion());

        resolver.setURI(URI.create("http://mevenide.codehaus.org/repository/netbeans/jars/nbs-core-release40b2.jar"));
        assertEquals("nbs-core", resolver.guessArtifactId());
        assertEquals("release40b2", resolver.guessVersion());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/commons-logging/jars/commons-logging-snapshot-version"));
        assertEquals("commons-logging-snapshot", resolver.guessArtifactId());
        assertEquals("version", resolver.guessVersion());
        assertEquals(null, resolver.guessExtension());
        
    }

    /**
     * Test of guessExtension method, of class org.mevenide.project.dependency.URIDependencyResolver.
     */
    public void testGuessExtension() {
        URIDependencyResolver resolver = new URIDependencyResolver();
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/txts/foo+joe-test2.-bar-1.0.7-beta-1.txt"));
        String ext = resolver.guessExtension();
        assertEquals("txt", ext);
        assertEquals("txt", resolver.guessType());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/jars/foo+joe-test2.-bar-1.0.7-beta-1.jar"));
        ext = resolver.guessExtension();
        assertEquals("jar", ext);
        assertEquals("jar", resolver.guessType());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/javadoc.jars/foo+joe-test2.-bar-1.0.7-beta-1.javadoc.jar"));
        ext = resolver.guessExtension();
        assertEquals("javadoc.jar", ext);
        assertEquals("javadoc.jar", resolver.guessType());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/plugins/foo+joe-test2.-bar-1.0.7-beta-1.jar"));
        ext = resolver.guessExtension();
        assertEquals("jar", ext);
        assertEquals("plugin", resolver.guessType());
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/distributions/foo+joe-test2.-bar-1.0.7-beta-1.zip"));
        ext = resolver.guessExtension();
        assertEquals("zip", ext);
        assertEquals("distribution", resolver.guessType());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/distributions/foo+joe-test2.-bar-1.0.7-beta-1.tar.gz"));
        ext = resolver.guessExtension();
        assertEquals("tar.gz", ext);
        assertEquals("distribution", resolver.guessType());
    }

    /**
     * Test of guessGroupId method, of class org.mevenide.project.dependency.URIDependencyResolver.
     */
    public void testGuessGroupId() {
        URIDependencyResolver resolver = new URIDependencyResolver();
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/maven/txts/foo+joe-test2.-bar-1.0.7-beta-1.txt"));
        assertEquals("maven", resolver.guessGroupId());
        
        resolver.setURI(URI.create("http://www.ibiblio.org/maven/hello/jars/foo+joe-test2.-bar-1.0.7-beta-1.jar"));
        assertEquals("hello", resolver.guessGroupId());
    }

    
}
