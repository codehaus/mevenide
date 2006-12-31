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

import java.io.File;
import junit.framework.*;
import org.mevenide.netbeans.project.output.JavaOutputListenerProvider;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ViewInBrowseProcessorTest extends TestCase {
    private ViewInBrowseProcessor provider;
    public ViewInBrowseProcessorTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(JavaOutputListenerProviderTest.class);
        return suite;
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public void testIsInWatchedGoals() throws Exception {
        provider = new ViewInBrowseProcessor(new String[] {"site:"}, new File("/home"), "question", 10);
        assertFalse(provider.isInWatchedGoals("   hello"));
        assertFalse(provider.isInWatchedGoals("java:compile"));
        assertFalse(provider.isInWatchedGoals(" test:single:"));
        assertTrue(provider.isInWatchedGoals("site:"));
        assertTrue(provider.isInWatchedGoals("   hello"));
        assertTrue(provider.isInWatchedGoals("untest:my"));
        assertTrue(provider.isInWatchedGoals(" untest:my"));
        assertFalse(provider.isInWatchedGoals("untest:my:"));
        assertFalse(provider.isInWatchedGoals(" hello"));
    }

    
    public void testIsInWatchedGoals2() throws Exception {
        provider = new ViewInBrowseProcessor(new String[] {"maven-javadoc-plugin:report:"}, new File("/home"), "question", 10);
        assertFalse(provider.isInWatchedGoals("   hello"));
        assertFalse(provider.isInWatchedGoals("java:compile"));
        assertFalse(provider.isInWatchedGoals(" test:single:"));
        assertTrue(provider.isInWatchedGoals("maven-javadoc-plugin:report:"));
        assertTrue(provider.isInWatchedGoals("   hello"));
        assertTrue(provider.isInWatchedGoals("untest:my"));
        assertTrue(provider.isInWatchedGoals(" untest:my"));
        assertFalse(provider.isInWatchedGoals("untest:my:"));
        assertFalse(provider.isInWatchedGoals(" hello"));
    }
    
}
