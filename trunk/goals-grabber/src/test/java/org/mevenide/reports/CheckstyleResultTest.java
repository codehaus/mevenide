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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;



/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class CheckstyleResultTest extends AbstractResultTest {
    
    public CheckstyleResultTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CheckstyleResultTest.class);
        
        return suite;
    }

    /**
     * Test of getFiles method, of class org.mevenide.reports.CheckstyleResult.
     */
    public void testGetFiles() {
        CheckstyleResult result = new CheckstyleResult(context);
        File[] names = result.getFiles();
        assertNotNull(names);
        assertEquals(2, names.length);
        assertTrue(Arrays.asList(names).contains(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/reports/CheckstyleResult.java")));
        assertTrue(Arrays.asList(names).contains(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/reports/PmdResult.java")));
    }

    /**
     * Test of getViolationsForFile method, of class org.mevenide.reports.CheckstyleResult.
     */
    public void testGetViolationsForFile() {
        CheckstyleResult result = new CheckstyleResult(context);
        List violations = result.getViolationsForFile(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/reports/CheckstyleResult.java"));
        assertNotNull(violations);
        assertEquals(8, violations.size());
        CheckstyleResult.Violation viol = (CheckstyleResult.Violation)violations.iterator().next();
        assertEquals("20", viol.getColumn());
        assertEquals("42", viol.getLine());
        assertEquals("Name 'LOCK' must match pattern '^[a-z][a-zA-Z0-9]*$'.", viol.getMessage());
        assertEquals("error", viol.getSeverity());
        assertEquals("com.puppycrawl.tools.checkstyle.checks.naming.MemberNameCheck", viol.getSource());
        assertEquals(new File("/home/cenda/mav_src/mevenide/goals-grabber/src/java/org/mevenide/reports/CheckstyleResult.java"), viol.getFile());
    }
    
}
