/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.customizer;

import junit.framework.TestCase;

/**
 *
 * @author mkleint
 */
public class PropertySplitterTest extends TestCase {
    
    public PropertySplitterTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNextPair() {
        PropertySplitter instance = new PropertySplitter("exec=\"test1\"");
        String result = instance.nextPair();
        assertEquals("exec=test1", result);
        instance = new PropertySplitter("exec=test1 exec2=test2");
        result = instance.nextPair();
        assertEquals("exec=test1", result);
        result = instance.nextPair();
        assertEquals("exec2=test2", result);
        
        instance = new PropertySplitter("exec=\"test1 exec2=test2\"");
        result = instance.nextPair();
        assertEquals("exec=test1 exec2=test2", result);
        
        instance = new PropertySplitter("exec=\"test1 exec2=test2\" exec2=\"test3==test3\"");
        result = instance.nextPair();
        assertEquals("exec=test1 exec2=test2", result);
        result = instance.nextPair();
        assertEquals("exec2=test3==test3", result);
    }

}
