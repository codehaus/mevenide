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
import junit.framework.TestCase;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class PdeArtifactMojoTest extends TestCase {
    
    protected PdeArtifactMojo mojo;
    
    protected File eclipseHome;
    
    protected File commonBasedir;
    
    protected void setUp() throws Exception {
        super.setUp();
        mojo = getMojo() == null ? newMojoStub() : getMojo();
        eclipseHome = new File(getClass().getResource("/eclipse.home").getFile());
        commonBasedir = new File(getClass().getResource("/basedir.common").getFile());
        mojo.eclipseHome = eclipseHome;
    }

    protected abstract PdeArtifactMojo getMojo();
    
    protected PdeArtifactMojo newMojoStub() {
        mojo = new PdeArtifactMojo() { 
            public void execute(PluginExecutionRequest arg0, PluginExecutionResponse arg1) throws Exception { }
        };
        return mojo;
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        mojo = null;
    }
    
}

