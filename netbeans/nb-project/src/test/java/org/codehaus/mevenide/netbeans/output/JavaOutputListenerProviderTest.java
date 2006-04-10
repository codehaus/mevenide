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
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class JavaOutputListenerProviderTest extends TestCase {
    private JavaOutputListenerProvider provider;
    public JavaOutputListenerProviderTest(java.lang.String testName) {
        super(testName);
    }
   
    public static Test suite() {
        TestSuite suite = new TestSuite(JavaOutputListenerProviderTest.class);
        return suite;
    }

    protected void setUp() throws java.lang.Exception {
        provider = new JavaOutputListenerProvider(null);
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public void testRecognizeLine() {
        OutputVisitor visitor = new OutputVisitor();
        visitor.resetVisitor();
        provider.sequenceStart("mojoexecute#compiler:testCompile", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("Compiling 1 source file to /home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/target/test-classes", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("/home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/src/test/java/org/codehaus/mevenide/netbeans/output/JavaOutputListenerProviderTest.java:[31,1] illegal start of type", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("C:\\lfo\\pers\\projects\\mojos\\maven-hello-plugin\\src\\main\\java\\org\\laurentforet\\mojos\\hello\\GreetingMojo.java:[14,8] cannot find symbol", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.sequenceFail("mojoexecute#compiler:testCompile", visitor);
    }
}
