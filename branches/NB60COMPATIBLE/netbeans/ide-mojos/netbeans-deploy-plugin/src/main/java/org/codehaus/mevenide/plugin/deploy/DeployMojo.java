
/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.plugin.deploy;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.deployment.MavenDeployment;
import org.openide.util.Lookup;


/**
 * Deploy a maven artifact..
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal deploy
 * @requiresProject
 */
public class DeployMojo extends AbstractMojo {
    
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    
    /**
     * Holds value of property debugmode.
     * @parameter expression = "${netbeans.deploy.debugmode}"
     *
     */
    private boolean debugmode = false;
    
    /**
     * @parameter expression = "${netbeans.deploy.forceRedeploy}"
     */
    private boolean forceRedeploy = true;
    
    /**
     *  URI of the web client or rich client in J2EE application to execute after deployment.
     * @parameter expression="${netbeans.deploy.clientModuleUri}"
     */
    private String clientModuleUri;
    
    /**
     * equivalent of netbeans-ant property client.url
     * @parameter expression="${netbeans.deploy.clientUrlPart}"
     */
    private String clientUrlPart;
    
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (clientUrlPart == null) {
            clientUrlPart = "";
        }
        Set allowedPackagings = new HashSet();
        allowedPackagings.add("war");
        allowedPackagings.add("ejb");
        allowedPackagings.add("ear");
        if (!allowedPackagings.contains(project.getPackaging())) {
            getLog().warn("You attempt to run run the plugin on a project that might not be of war, ear or ejb kind (packaging). The plugin might possibly fail.");
        }
        MavenDeployment deployment = (MavenDeployment)Lookup.getDefault().lookup(MavenDeployment.class);
        if (deployment == null) {
            getLog().error("Cannot lookup the Maven-NetBeans bridge for deployment.");
            throw new MojoExecutionException("Cannot lookup the Maven-NetBeans bridge for deployment.");
        }
        deployment.doDeployment(project, getLog(), debugmode, clientUrlPart, clientModuleUri, forceRedeploy);
    }

    
    
}
