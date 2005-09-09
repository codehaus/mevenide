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
package org.mevenide.plugins;

import java.io.File;

import junit.framework.TestCase;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class JarPluginInfoTest extends TestCase {
    
    public JarPluginInfoTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }


    /**
     * Test of readProjectValues method, of class org.mevenide.plugins.DefaultPluginInfo.
     */
    public void testReadProjectValues() throws Exception {
        File fil = new File(this.getClass().getResource("/maven-nbm-plugin-1.3.jar").getFile());
        
        JarPluginInfo info = new JarPluginInfo(fil);
        assertEquals("Maven NBM Plugin", info.getName());
        assertEquals("Maven NBM Plugin", info.getLongName());
        assertEquals("Creates ready to use Netbeans module distribution", info.getDescription());
        
    }
    
}
