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

import org.apache.maven.plugin.Plugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.verifier.CompatibilityChecker;


/**  
 * 
 * base pde-mojo class 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class PdeArtifactMojo implements Plugin {
    
    /** eclipse home directory */
    protected File eclipseHome;  
    
    /** Eclipse configuration Folder. It is exposed as a property because it is user configurable through the <code>-configuration</code> flag */
    protected File configurationFolder;
    
    /** The workspace un which Eclipse project exists. Required if project has not been created in the 'default location' */
    protected File workspace;
    
    /** directory where generated artifacts are outputted */
    protected File outputDirectory;
    
    /** base working directory */
    protected File basedir;
    
    /** project model */
    protected MavenProject project;
    
    /**
     * extract common parameters from request
     * @param request 
     */
    protected void initialize(PluginExecutionRequest request) throws ConfigurationException {
        checkBuildId(request);

        project = (MavenProject) request.getParameter( "project" );

        String eclipseHomeLocation = (String) request.getParameter("eclipseHome");
        eclipseHome = new File(eclipseHomeLocation);
        
        String outputDirectoryLocation = (String) request.getParameter("outputDirectory");
        if ( outputDirectoryLocation != null ) {
            outputDirectory = new File(outputDirectoryLocation);
        }
        
        String workspaceLocation = (String) request.getParameter("workspace");
        if ( workspaceLocation != null ) {
            workspace = new File(workspaceLocation);
        }

        String basedirLocation = (String) request.getParameter("basedir");
        if ( basedirLocation == null ) {
            throw new ConfigurationException("Configuration.BaseDir.Empty");
        }
        basedir = new File(basedirLocation);
        
    }

    private void checkBuildId(PluginExecutionRequest request) throws ConfigurationException {
        String eclipseConfigurationFolder = (String) request.getParameter("eclipseConfigurationFolder");
        String maxBuildIdParameter = (String) request.getParameter("maxBuildId");
        String minBuildIdParameter = (String) request.getParameter("minBuildId");
		new CompatibilityChecker(eclipseHome, configurationFolder).checkBuildId(maxBuildIdParameter, minBuildIdParameter);
    }

}
