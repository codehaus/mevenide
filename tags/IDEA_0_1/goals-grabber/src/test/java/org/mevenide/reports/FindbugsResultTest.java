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

package org.mevenide.reports;

import java.util.Arrays;
import junit.framework.*;
import java.util.List;


/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class FindbugsResultTest extends AbstractResultTest {
    
    public FindbugsResultTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FindbugsResultTest.class);
        
        return suite;
    }

    /**
     * Test of getClassNames method, of class org.mevenide.reports.FindbugsResult.
     */
    public void testGetClassNames() {
        
        FindbugsResult result = new FindbugsResult(context);
        String[] names = result.getClassNames();
        assertNotNull(names);
        assertEquals(2, names.length);
        assertTrue(Arrays.asList(names).contains("org.mevenide.plugins.DefaultPluginInfo"));
        assertTrue(Arrays.asList(names).contains("org.mevenide.genapp.TemplateInfo"));
    }

    /**
     * Test of getViolationsForClass method, of class org.mevenide.reports.FindbugsResult.
     */
    public void testGetViolationsForClass() {
        FindbugsResult result = new FindbugsResult(context);
        
        List violations = result.getViolationsForClass("org.mevenide.plugins.DefaultPluginInfo");
        assertNotNull(violations);
        assertEquals(1, violations.size());
        FindbugsResult.Violation viol = (FindbugsResult.Violation)violations.iterator().next();
        assertEquals("SBSC_USE_STRINGBUFFER_CONCATENATION", viol.getType());
        assertEquals("org.mevenide.plugins.DefaultPluginInfo", viol.getClassName());
        assertEquals("191", viol.getLine());
        assertEquals("SBSC: Method org.mevenide.plugins.DefaultPluginInfo.readProjectValues(java.io.File) concatenates strings using + in a loop", viol.getMessage());
        assertEquals("Normal", viol.getPriority());
    }
    
}
