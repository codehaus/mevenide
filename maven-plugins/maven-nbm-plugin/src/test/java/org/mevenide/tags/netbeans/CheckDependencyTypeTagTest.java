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
package org.mevenide.tags.netbeans;

import java.io.InputStream;
import java.util.jar.Manifest;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CheckDependencyTypeTagTest extends TestCase {

    private CheckDependencyTypeTag tag;
    
    protected void setUp() throws Exception {
        tag = new CheckDependencyTypeTag();
    }

    protected void tearDown() throws Exception {
        tag = null;
    }

    public void testSpec1() throws Exception {
        tag.resetExamination();
        tag.processValue("spec");
        assertEquals("spec", tag.type);
        assertEquals(false, tag.complete);
        
        tag.resetExamination();
        tag.processValue("spec=4.5");
        assertEquals("spec", tag.type);
        assertEquals("4.5", tag.dependencyValue);
        assertEquals(false, tag.complete);

        tag.resetExamination();
        tag.processValue("spec=org.milos &gt; 1.1");
        assertEquals("spec", tag.type);
        assertEquals("org.milos > 1.1", tag.dependencyValue);
        assertEquals(true, tag.complete);
        
        tag.resetExamination();
        tag.processValue("impl=org.milos/1 = 1.1");
        assertEquals("impl", tag.type);
        assertEquals("org.milos/1 = 1.1", tag.dependencyValue);
        assertEquals(true, tag.complete);
    }

}
