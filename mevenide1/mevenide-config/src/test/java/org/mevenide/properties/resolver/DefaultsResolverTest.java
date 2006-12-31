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
        def = new DefaultsResolver(context);
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
    
    private class DummyPropFilesFinder implements IPropertyFinder {
        
        public String getValue(String key) {
            return null;
        }
        
        public void reload() {
        }
        
    }
}
