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
package org.codehaus.mevenide.pde;

import java.io.File;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import junit.framework.TestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class EclipseArtifactMojoTest extends TestCase {
    
    private EclipseArtifactMojo mojo;
    private File eclipseHome;
    
    protected void setUp() throws Exception {
        super.setUp();
        mojo = new EclipseArtifactMojo() { 
            public void execute(PluginExecutionRequest arg0, PluginExecutionResponse arg1) throws Exception { }
        };
        eclipseHome = new File(getClass().getResource("/eclipse.home").getFile());
        mojo.eclipseHome = eclipseHome;
    }

    
    protected void tearDown() throws Exception {
        super.tearDown();
        mojo = null;
    }
    
    public void testGetBuildId() throws Exception {
        assertEquals(200409240800l, mojo.getBuildId());
        
        mojo.configurationFolder = new File(eclipseHome, "configuration");
        assertEquals(200409240800l, mojo.getBuildId());
        
        mojo.configurationFolder = new File(eclipseHome, "nofolder");
        try {
            mojo.getBuildId();
            fail("expected ConfigurationException : configuration set to an invalid path");
        }
        catch (ConfigurationException e) { }
    }

    public void testCheckMaxBuildId() throws Exception {
        mojo.checkMaxBuildId(200505061930l);
        try {
            mojo.checkMaxBuildId(200305061930l);
            fail("expected ConfigurationException : buildId too high");
        }
        catch (ConfigurationException e) { }
    }

    public void testCheckMinBuildId() throws Exception {
        mojo.checkMinBuildId(200305061930l);
        try {
            mojo.checkMinBuildId(200505061930l);
            fail("expected ConfigurationException : buildId too low");
        }
        catch (ConfigurationException e) { }
    }

    public void testCheckBuildId() throws Exception {
        mojo.checkBuildId(200305061930l, 200505061930l);
        mojo.checkBuildId(200505061930l, 200305061930l);
        try {
            mojo.checkBuildId(200505061930l, 200507061930l);
            fail("expected ConfigurationException : buildId too high");
        }
        catch (ConfigurationException e) { }
    }
}
