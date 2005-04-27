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
package org.mevenide.tags;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AdaptVersionTagTest extends TestCase {

    private AdaptVersionTag tag;
    protected void setUp() throws Exception {
        tag = new AdaptVersionTag();
    }

    protected void tearDown() throws Exception {
        tag = null;
    }

    public void testAdapt() {
        tag.setVersion("1.0.0");
        assertEquals("1.0.0", tag.adapt());
        
        tag.setVersion("1.00");
        assertEquals("1.0.0", tag.adapt());
        
        tag.setVersion("1..0");
        assertEquals("1.0.0", tag.adapt());

        tag.setVersion("1.0-rc1");
        assertEquals("1.0.1", tag.adapt());

        tag.setVersion("1.0.beta5");
        assertEquals("1.0.5", tag.adapt());

        tag.setVersion("SNAPSHOT");
        assertEquals("0.0.1", tag.adapt());

        tag.setVersion("1.0-SNAPSHOT");
        assertEquals("1.0." + /* new SimpleDateFormat("yyyyMMdd").format(new Date()) */ "0", tag.adapt());
        
        tag.setVersion("1.0.");
        assertEquals("1.0.0", tag.adapt());
        
        tag.setVersion("1.2");
        assertEquals("1.2.0", tag.adapt());
        
        tag.setVersion("1");
        assertEquals("1.0.0", tag.adapt());
    }

}
