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
package org.codehaus.mevenide.pde.version;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class VersionAdapterTest extends TestCase {

    private VersionAdapter adapter;
    protected void setUp() throws Exception {
        adapter = new VersionAdapter();
    }

    protected void tearDown() throws Exception {
        adapter = null;
    }

    public void testAdapt() {
        assertEquals("1.0.0", adapter.adapt("1.0.0"));
        assertEquals("1.0.0", adapter.adapt("1.00"));
        assertEquals("1.0.0", adapter.adapt("1..0"));
        assertEquals("1.0.0", adapter.adapt("1.0-rc1"));
        assertEquals("1.0.0", adapter.adapt("1.0.beta5"));
        assertEquals("0.0.1", adapter.adapt("SNAPSHOT"));
        assertEquals("1.0.0", adapter.adapt("1.0-SNAPSHOT"));
        assertEquals("1.0.0", adapter.adapt("1.0."));
        assertEquals("1.2.0", adapter.adapt("1.2"));
        assertEquals("1.0.0", adapter.adapt("1"));
		assertEquals("1.0.2", adapter.adapt("1.0.2-b1"));
    }

}
