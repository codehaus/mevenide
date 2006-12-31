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
import java.io.File;
import java.util.List;


/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class PmdResultTest extends AbstractResultTest {
    
    public PmdResultTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(PmdResultTest.class);
        
        return suite;
    }

    /**
     * Test of getFiles method, of class org.mevenide.reports.PmdResult.
     */
    public void testGetFiles() {
        PmdResult result = new PmdResult(context);
        File[] names = result.getFiles();
        assertNotNull(names);
        assertEquals(1, names.length);
        assertTrue(Arrays.asList(names).contains(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/genapp/TemplateInfo.java")));
    }

    /**
     * Test of getViolationsForFile method, of class org.mevenide.reports.PmdResult.
     */
    public void testGetViolationsForFile() {
        PmdResult result = new PmdResult(context);
        List violations = result.getViolationsForFile(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/genapp/TemplateInfo.java"));
        assertNotNull(violations);
        assertEquals(2, violations.size());
        PmdResult.Violation viol = (PmdResult.Violation)violations.iterator().next();
        assertEquals("138", viol.getLine());
        assertEquals("EmptyCatchBlock", viol.getViolationId());
        assertEquals("Avoid empty catch blocks", viol.getViolationText().trim());
        assertEquals(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/genapp/TemplateInfo.java"), viol.getFile());
    }
    
}
