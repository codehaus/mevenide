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
package org.mevenide.genapp;

import junit.framework.*;
import java.util.ArrayList;
import java.util.List;
import org.mevenide.goals.TestQueryContext;
import java.io.File;

/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class GenAppTemplateFinderTest extends TestCase {
    
    public GenAppTemplateFinderTest(String testName) {
        super(testName);
    }

    private TestQueryContext context;
    private File rootDir;
    
    protected void setUp() throws java.lang.Exception {
        rootDir = new File(this.getClass().getResource("/templates").getFile());
        context = new TestQueryContext();
        // populate context with genapp plugin defaults..
        context.addUserPropertyValue("maven.genapp.template.repository", rootDir.getAbsolutePath());

    }


    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(GenAppTemplateFinderTest.class);
        
        return suite;
    }

    /**
     * Test of getTemplates method, of class org.mevenide.genapp.GenAppTemplateFinder.
     */
    public void testGetTemplates() {
        GenAppTemplateFinder finder = new GenAppTemplateFinder(context);
        TemplateInfo[] infos = finder.getTemplates(GenAppTemplateFinder.LOCATION_USER);
        assertNotNull(infos);
        assertEquals(2, infos.length);
        List lst = new ArrayList();
        lst.add(infos[1].getName());
        lst.add(infos[0].getName());
        assertTrue(lst.contains("default"));
        assertTrue(lst.contains("ejb"));
    }
    
    
}
