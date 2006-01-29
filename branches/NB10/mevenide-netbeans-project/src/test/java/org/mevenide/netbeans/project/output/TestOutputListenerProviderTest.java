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

package org.mevenide.netbeans.project.output;

import junit.framework.*;
import org.mevenide.netbeans.project.output.TestOutputListenerProvider;
import org.mevenide.netbeans.api.output.OutputVisitor;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class TestOutputListenerProviderTest extends TestCase {
    private TestOutputListenerProvider provider;
    public TestOutputListenerProviderTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TestOutputListenerProviderTest.class);
        return suite;
    }

    protected void setUp() throws java.lang.Exception {
        provider = new TestOutputListenerProvider(null);
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public void testIsInWatchedGoals() throws Exception {
        assertFalse(provider.isInWatchedGoals("   hello"));
        assertFalse(provider.isInWatchedGoals("test:single"));
        assertFalse(provider.isInWatchedGoals(" test:single:"));
        assertTrue(provider.isInWatchedGoals("test:single:"));
        assertTrue(provider.isInWatchedGoals("   hello"));
        assertTrue(provider.isInWatchedGoals("untest:my"));
        assertTrue(provider.isInWatchedGoals(" untest:my"));
        assertFalse(provider.isInWatchedGoals("untest:my:"));
        assertFalse(provider.isInWatchedGoals(" hello"));
    }
    
    public void testRecognizeLine() {
        OutputVisitor visitor = new OutputVisitor();
        visitor.resetVisitor();
        provider.processLine("test:test:", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] Running org.mevenide.netbeans.project.nodes.DirScannerSubClassTest", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 0.586 sec", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] Running org.mevenide.netbeans.project.exec.TestOutputListenerProviderTest", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] Tests run: 1, Failures: 1, Errors: 0, Time elapsed: 0.027 sec", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] [ERROR] TEST org.mevenide.netbeans.project.exec.TestOutputListenerProviderTest FAILED        ", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] [ERROR] Test org.mevenide.netbeans.project.exec.TestOutputListenerProviderTest FAILED        ", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [junit] TEST org.mevenide.netbeans.project.exec.TestOutputListenerProviderTest FAILED        ", visitor);
        assertNotNull(visitor.getOutputListener());        
    }
    
}
