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
package org.mevenide.tags;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AdaptNbVersionTagTest extends TestCase {

    private AdaptNbVersionTag tag;
    
    protected void setUp() throws Exception {
        tag = new AdaptNbVersionTag();
    }

    protected void tearDown() throws Exception {
        tag = null;
    }

    /**
     * tests values for Openide-Specification-Version
     */
    public void testAdaptSpec() {
        tag.setType("spec");
        tag.setVersion("1.0.0");
        assertEquals("1.0.0", tag.adapt());
        
        tag.setVersion("1.00");
        assertEquals("1.0", tag.adapt());
        
        tag.setVersion("1..0");
        assertEquals("1.0", tag.adapt());
        
        tag.setVersion("1.0-rc1");
        assertEquals("1.0", tag.adapt());
        
        tag.setVersion("1.0.beta5");
        assertEquals("1.0", tag.adapt());
        
        tag.setVersion("SNAPSHOT");
        assertEquals("0.0.0", tag.adapt());
        
        tag.setVersion("1.0-SNAPSHOT");
        assertEquals("1.0", tag.adapt());

        tag.setVersion("1.5-BETA2-SNAPSHOT");
        assertEquals("1.5", tag.adapt());
        
        tag.setVersion("1.0.");
        assertEquals("1.0", tag.adapt());

        tag.setVersion("15.10-rc12");
        assertEquals("15.10", tag.adapt());
    }
    
   /**
     * tests values for Openide-Implementation-Version
     */    
    public void testAdaptImpl() {
        tag.setVersion("1.0.0");
        tag.setType("impl");
        assertEquals("1.0.0", tag.adapt());
        
        tag.setVersion("1.00");
        assertEquals("1.00", tag.adapt());
        
        tag.setVersion("1..0");
        assertEquals("1.0", tag.adapt());
        
        tag.setVersion("1.0-rc1");
        assertEquals("1.0-rc1", tag.adapt());
        
        tag.setVersion("1.0.beta5");
        assertEquals("1.0.beta5", tag.adapt());
        
        tag.setVersion("SNAPSHOT");
        assertEquals("0.0.0." + new SimpleDateFormat("yyyyMMdd").format(new Date()), tag.adapt());
        
        tag.setVersion("1.0-SNAPSHOT");
        assertEquals("1.0-" + new SimpleDateFormat("yyyyMMdd").format(new Date()), tag.adapt());

        tag.setVersion("1.5-BETA2-SNAPSHOT");
        assertEquals("1.5-BETA2-" + new SimpleDateFormat("yyyyMMdd").format(new Date()), tag.adapt());
        
        tag.setVersion("1.0.");
        assertEquals("1.0", tag.adapt());

        tag.setVersion("15.10-rc12");
        assertEquals("15.10-rc12", tag.adapt());
    }

}
