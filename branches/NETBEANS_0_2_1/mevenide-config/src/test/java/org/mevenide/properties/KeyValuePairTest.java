/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.properties;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class KeyValuePairTest extends TestCase {
    private KeyValuePair keyValuePair;
    
    protected void setUp() throws Exception {
        keyValuePair = new KeyValuePair("initialKey", '=');
    }

    protected void tearDown() throws Exception {
        keyValuePair = null;
    }

    public void testGetKey() {
        assertEquals("initialKey", keyValuePair.getKey());
    }
    
    public void testSetValue() {
        keyValuePair.setValue("my value");
        assertEquals("initialKey=my value", keyValuePair.toString());
    }
    
    public void testAddToValue() {
        keyValuePair.setValue("my value");
        keyValuePair.addToValue("value addition");
        assertEquals("initialKey=my valuevalue addition", keyValuePair.toString());
    }

    public void testAddLine() {
        keyValuePair.setValue("my value");
        keyValuePair.addLine("value addition");
        assertEquals("initialKey=my valuevalue addition", keyValuePair.toString());
    }

    public void testEquals() {
        //TODO
    }
}
