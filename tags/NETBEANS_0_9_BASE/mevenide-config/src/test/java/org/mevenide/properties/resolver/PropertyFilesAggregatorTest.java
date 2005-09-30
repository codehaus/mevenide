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
package org.mevenide.properties.resolver;

import java.io.File;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.IPropertyLocator;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropertyFilesAggregatorTest extends AbstractResolverTestCase {
    
    protected PropertyFilesAggregator def;
    protected ILocationFinder finder;
    /** Creates a new instance of DefaultsResolverTest */
    public PropertyFilesAggregatorTest() {
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        File buildprop = new File(AbstractResolverTestCase.class.getResource("/org/mevenide/properties/maven-test-plugin/plugin.properties").getFile());
        File repo = new File(projectDir, ".maven");
        if (!repo.exists()) {
            repo.mkdir();
        }
        File cache = new File(repo, "cache");
        if (!cache.exists()) {
            cache.mkdir();
        }
        File test = new File(cache, "maven-test-plugin");
        if (!test.exists()) {
            test.mkdir();
        }
        File copyTo = new File(test, "plugin.properties");
        copy(buildprop.getAbsolutePath(), copyTo.getAbsolutePath());
        
        def = new PropertyFilesAggregator(context, new DefaultsResolver(context));
        finder = new LocationFinderAggregator(context);
        
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
        
        String test = def.getValue("maven.test.dest");
        assertNotNull(test);
        assertEquals("${maven.build.dir}/test-classes", test);
        
        String parentValue = def.getValue("test1");
        assertNotNull(parentValue);
        assertEquals("parentbuild", parentValue);
        assertEquals(IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD, def.getPropertyLocation("test1"));
        
        parentValue = def.getValue("test2");
        assertNotNull(parentValue);
        assertEquals("parentproject", parentValue);
        assertEquals(IPropertyLocator.LOCATION_PARENT_PROJECT, def.getPropertyLocation("test2"));
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

        String test = def.getResolvedValue("maven.test.dest");
        assertNotNull(test);
        correct = new File(projectDir.getAbsolutePath() + "/target_yyy/test-classes");
        assertEquals(correct.getAbsolutePath().replaceAll("\\\\", "/"), test.replaceAll("\\\\", "/"));
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
 //       super.tearDown();
        def = null;
    }
    

}
