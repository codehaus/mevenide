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
package org.codehaus.mevenide.pde.plugin;

import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.codehaus.mevenide.pde.EclipseArtifactMojo;


/**  
 * @open can tags be inherited ?
 * 
 * @goal plugin
 * @description builds an Eclipse plugin
 * @parameter name="eclipseHome" 
 *            type="String" 
 *            required="true" 
 *            validator="" 
 *            expression="#maven.pde.eclipse.home" 
 *            description="Location of Eclipse home directory"
 * @parameter name="eclipseConfigurationFolder" 
 *            type="String" 
 *            required="false" 
 *            validator="" 
 *            expression="#maven.pde.eclipse.configuration" 
 *            description="Eclipse configuration folder"
 * @parameter name="maxBuildId" 
 *            type="Long" 
 *            required="false" 
 *            validator="" 
 *            expression="#maven.pde.maxBuildId" 
 *            description="Max compatible platform buildId"
 * @parameter name="minBuildId" 
 *            type="Long" 
 *            required="false" 
 *            validator="" 
 *            expression="#maven.pde.minBuildId" 
 *            description="Min compatible platform buildId"           
 * @parameter name="outputDirectory" 
 *            type="String" 
 *            required="true" 
 *            validator="" 
 *            expression="#maven.build.dir/eclipse" 
 *            description="Directory where the generated artifact will be outputted"
 * @parameter name="basedir" 
 *            type="String" 
 *            required="true" 
 *            validator="" 
 *            expression="#basedir" 
 *            description="Base directory"
 * @parameter name="workspace" 
 *            type="String" 
 *            required="false" 
 *            validator="" 
 *            expression="#maven.pde.workspace" 
 *            description="Eclispe workspace Location"
 *                       
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class EclipsePluginMojo extends EclipseArtifactMojo {
    
    public void execute(PluginExecutionRequest request, PluginExecutionResponse response) throws Exception {
        initialize(request);
    }
}
