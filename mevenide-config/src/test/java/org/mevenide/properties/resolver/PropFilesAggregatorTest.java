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
        assertEquals(right.getAbsolutePath().replaceAll("\\\\", "/"), home.replaceAll("\\\\", "/"));

        // value comes from locationfinder
        String home2 = def.getResolvedValue("maven.home");
        assertNotNull(home2);
        assertEquals(finder.getMavenHome().replaceAll("\\\\", "/"), home2.replaceAll("\\\\", "/"));
        
        // complex replacements from multiple properties files.
        String build = def.getResolvedValue("maven.build.src");
        assertNotNull(build);
        File correct = new File(projectDir.getAbsolutePath() + "/target_yyy/src2");
        assertEquals(correct.getAbsolutePath().replaceAll("\\\\", "/"), build.replaceAll("\\\\", "/"));
    }
    
    
    protected void tearDown() throws Exception {
        super.tearDown();
        def = null;
    }
    
}
