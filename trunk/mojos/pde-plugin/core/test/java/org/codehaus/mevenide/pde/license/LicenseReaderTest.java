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
package org.codehaus.mevenide.pde.license;

import java.io.File;
import junit.framework.TestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LicenseReaderTest extends TestCase {

    
    private LicenseReader tag;
    
    protected void setUp() throws Exception {
        tag = new LicenseReader();
    }

    protected void tearDown() throws Exception {
        tag = null;
    }

    public void testJar1() throws Exception {
    	File fl= new File(LicenseReaderTest.class.getResource("/license.jars/haslicense1.jar").getFile());
        tag.setJarFile(fl);
        String lic = tag.readLicense();
        assertNotNull(lic);
    }
    
    public void testJar2() throws Exception {
    	File fl= new File(LicenseReaderTest.class.getResource("/license.jars/haslicense2.jar").getFile());
        tag.setJarFile(fl);
        String lic = tag.readLicense();
        assertNotNull(lic);
    }
    
    public void testJar3() throws Exception {
    	File fl= new File(LicenseReaderTest.class.getResource("/license.jars/nolicense1.jar").getFile());
        tag.setJarFile(fl);
        String lic = tag.readLicense();
        assertNull(lic);
    }
}
