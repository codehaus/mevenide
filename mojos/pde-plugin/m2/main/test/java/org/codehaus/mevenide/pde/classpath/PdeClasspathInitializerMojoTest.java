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
package org.codehaus.mevenide.pde.classpath;

import java.util.HashMap;
import java.util.Map;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdeArtifactMojo;
import org.codehaus.mevenide.pde.PdeArtifactMojoTest;



/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdeClasspathInitializerMojoTest extends PdeArtifactMojoTest {
    
    private PluginExecutionRequest request;
    private PluginExecutionResponse response;
    
    private MavenProject project;

    protected void setUp() throws Exception {
        super.setUp();
        
        Model model = new Model();
        project = new MavenProject(model);
        
        Map params = new HashMap();
        params.put("project", project);
       
        params.put("eclipseHome", eclipseHome.getAbsolutePath());
        params.put("basedir", commonBasedir.getAbsolutePath());
        
        request = new PluginExecutionRequest( params );
        response = new PluginExecutionResponse();
    }
    
    protected PdeArtifactMojo getMojo() {
        return new PdeClasspathInitializerMojo();
    }
    
    public void testExecute() throws Exception {
        
        mojo.execute(request, response);
        assertEquals(3, project.getArtifacts().size());
        
    }
    
}
