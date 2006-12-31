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
import org.mevenide.netbeans.project.output.JavaOutputListenerProvider;
import org.mevenide.netbeans.api.output.OutputVisitor;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
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
    
    public void testIsInWatchedGoals() throws Exception {
        assertFalse(provider.isInWatchedGoals("   hello"));
        assertFalse(provider.isInWatchedGoals("java:compile"));
        assertFalse(provider.isInWatchedGoals(" test:single:"));
        assertTrue(provider.isInWatchedGoals("java:compile:"));
        assertTrue(provider.isInWatchedGoals("   hello"));
        assertTrue(provider.isInWatchedGoals("untest:my"));
        assertTrue(provider.isInWatchedGoals(" untest:my"));
        assertFalse(provider.isInWatchedGoals("untest:my:"));
        assertFalse(provider.isInWatchedGoals(" hello"));
        assertTrue(provider.isInWatchedGoals("axis:compile:"));
        assertTrue(provider.isInWatchedGoals("untest:my"));
        assertFalse(provider.isInWatchedGoals("untest:my:"));
    }
    
    public void testRecognizeLine() {
        OutputVisitor visitor = new OutputVisitor();
        visitor.resetVisitor();
        provider.processLine("java:compile:", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("    [javac] Compiling 3 source files to /home/cenda/mav_src/mevenide/mevenide-netbeans-project/target/test-classes", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("/home/cenda/mav_src/mevenide/mevenide-netbeans-project/src/test/java/org/mevenide/netbeans/project/exec/JavaOutputListenerProviderTest.java:59: cannot resolve symbol", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("symbol  : method assertxxNull (org.openide.windows.OutputListener)", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("location: class org.mevenide.netbeans.project.exec.JavaOutputListenerProviderTest", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("test:me:", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("/home/cenda/mav_src/mevenide/mevenide-netbeans-project/src/test/java/org/mevenide/netbeans/project/exec/JavaOutputListenerProviderTest.java:59: cannot resolve symbol", visitor);
        assertNull(visitor.getOutputListener());
    }
    
    public void testRecognizeAxisLine() {
        
        OutputVisitor visitor = new OutputVisitor();
        visitor.resetVisitor();
        provider.processLine("axis:compile:", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("[echo] ...java:compile preGoal finished.", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("[echo] Compiling to /usr/local/src/schibsted/search-front-html/target/classes", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("[javac] Compiling 175 source files to /usr/local/src/schibsted/search-front-html/target/classes", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("/usr/local/src/schibsted/search-front-html/src/java/no/schibstedsok/front/searchportal/util/SearchConstants.java:65: warning: unmappable character for encoding UTF-8", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("public static final String OVERTURE_PPC_SEARCH_BASE_URL = \"http://?/\";", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("^", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("/usr/local/src/schibsted/search-front-html/src/java/no/schibstedsok/front/searchportal/result/Linkpulse.java:10: warning: unmappable character for encoding UTF-8", visitor);
        assertNotNull(visitor.getOutputListener());
    }
    
    
}
