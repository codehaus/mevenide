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
package org.codehaus.mevenide.pde.verifier;

import java.io.File;
import junit.framework.TestCase;
import org.codehaus.mevenide.pde.ConfigurationException;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CompatibilityCheckerTest extends TestCase {
    
    private CompatibilityChecker checker;
    
    private File eclipseHome;
    
    protected void setUp() throws Exception {
        super.setUp();
        eclipseHome = new File(getClass().getResource("/eclipse.home").getFile());
        checker = new CompatibilityChecker(eclipseHome, null);
    }

    
    protected void tearDown() throws Exception {
        super.tearDown();
        checker = null;
    }
    
    public void testGetBuildId() throws Exception {
        assertEquals(200409240800l, checker.getBuildId());
        
        checker.setConfigurationFolder(new File(eclipseHome, "configuration"));
        assertEquals(200409240800l, checker.getBuildId());
        
        checker.setConfigurationFolder(new File(eclipseHome, "nofolder"));
        try {
            checker.getBuildId();
            fail("expected ConfigurationException : configuration set to an invalid path");
        }
        catch (ConfigurationException e) { }
    }

    public void testCheckMaxBuildId() throws Exception {
        checker.checkMaxBuildId(200505061930l);
        try {
            checker.checkMaxBuildId(200305061930l);
            fail("expected ConfigurationException : buildId too high");
        }
        catch (ConfigurationException e) { }
    }

    public void testCheckMinBuildId() throws Exception {
        checker.checkMinBuildId(200305061930l);
        try {
            checker.checkMinBuildId(200505061930l);
            fail("expected ConfigurationException : buildId too low");
        }
        catch (ConfigurationException e) { }
    }

    public void testCheckBuildId() throws Exception {
        checker.checkBuildId(200305061930l, 200505061930l);
        checker.checkBuildId(200505061930l, 200305061930l);
        try {
            checker.checkBuildId(200505061930l, 200507061930l);
            fail("expected ConfigurationException : buildId too high");
        }
        catch (ConfigurationException e) { }
    }
    
}
