/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.netbeans.output;

import junit.framework.*;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;

/**
 * testing test output processing
 * @author  Milos Kleint (mkleint@codehaus.org)
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

    public void testSeparateTestOuput() {
        OutputVisitor visitor = new OutputVisitor();
        visitor.resetVisitor();
        provider.sequenceStart("mojo-execute#surefire:test", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("Surefire report directory: /home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/target/surefire-reports", visitor);
        assertNull(visitor.getOutputListener());
        assertEquals(provider.outputDir, "/home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/target/surefire-reports");
        visitor.resetVisitor();
        provider.processLine("Running org.codehaus.mevenide.netbeans.output.JavaOutputListenerProviderTest", visitor);
        assertEquals(provider.runningTestClass, "org.codehaus.mevenide.netbeans.output.JavaOutputListenerProviderTest");
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 0.038 sec", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("Running org.codehaus.mevenide.netbeans.execute.OutputHandlerTest", visitor);
        assertEquals(provider.runningTestClass, "org.codehaus.mevenide.netbeans.execute.OutputHandlerTest");
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.057 sec <<< FAILURE!        ", visitor);
        assertNotNull(visitor.getOutputListener());
        provider.sequenceFail("mojo-execute#surefire:test", visitor);
        
    }
}
