/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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

import java.io.File;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.IPropertyLocator;

import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropertyFilesAggregatorTest extends AbstractResolverTestCase {
    
    protected PropertyFilesAggregator def;
    /** Creates a new instance of DefaultsResolverTest */
    public PropertyFilesAggregatorTest() {
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        def = new PropertyFilesAggregator(projectDir, userHomeDir, 
                new DefaultsResolver(projectDir, userHomeDir, finder, 
                    new DummyPropFilesFinder()));
    }
    
    public void testGetValue() throws Exception {
        String repo = def.getValue("maven.repo.remote");
        assertNotNull(repo);
        assertEquals("http://mevenide.codehaus.org", repo);
        assertEquals(IPropertyLocator.LOCATION_USER_BUILD, def.getPropertyLocation("maven.repo.remote"));
        
        // doesn't resolve values.
        String build = def.getValue("maven.build.src");
        assertNotNull(build);
        assertEquals("${maven.build.dir}/src2", build);
        assertEquals(IPropertyLocator.LOCATION_PROJECT, def.getPropertyLocation("maven.build.src"));
        
    }

    public void testGetResolvedValue() throws Exception {
        String home = def.getResolvedValue("maven.home.local");
        File right = new File(projectDir, ".maven");
        assertNotNull(home);
        assertEquals(right.getAbsolutePath().replaceAll("\\\\", "/"), home.replaceAll("\\\\", "/"));
        assertEquals(IPropertyLocator.LOCATION_USER_BUILD, def.getPropertyLocation("maven.home.local"));

        // value comes from locationfinder
        String home2 = def.getResolvedValue("maven.home");
        assertNotNull(home2);
        assertEquals(finder.getMavenHome().replaceAll("\\\\", "/"), home2.replaceAll("\\\\", "/"));
        
        // complex replacements from multiple properties files.
        String build = def.getResolvedValue("maven.build.src");
        assertNotNull(build);
        File correct = new File(projectDir.getAbsolutePath() + "/target_yyy/src2");
        assertEquals(correct.getAbsolutePath().replaceAll("\\\\", "/"), build.replaceAll("\\\\", "/"));
        assertEquals(IPropertyLocator.LOCATION_PROJECT, def.getPropertyLocation("maven.build.src"));
    }
    
    public void testResolveString() throws Exception {
        String home = def.resolveString("${maven.home.local}");
        File right = new File(projectDir, ".maven");
        assertNotNull(home);
        assertEquals(right.getAbsolutePath().replaceAll("\\\\", "/"), home.replaceAll("\\\\", "/"));

        // value comes from locationfinder
        String home2 = def.resolveString("${maven.home}");
        assertNotNull(home2);
        assertEquals(finder.getMavenHome().replaceAll("\\\\", "/"), home2.replaceAll("\\\\", "/"));
        
        // complex replacements from multiple properties files.
        String build = def.resolveString("${maven.build.src}");
        assertNotNull(build);
        File correct = new File(projectDir.getAbsolutePath() + "/target_yyy/src2");
        assertEquals(correct.getAbsolutePath().replaceAll("\\\\", "/"), build.replaceAll("\\\\", "/"));
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        def = null;
    }
    
    private class DummyPropFilesFinder implements IPropertyFinder {
        
        public String getValue(String key) {
            return null;
        }
        
        public void reload() {
        }
        
    }
}
