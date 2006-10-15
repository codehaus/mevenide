/* ==========================================================================
 * Copyright 2006 Mevenide Team
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
package org.codehaus.mevenide.plugin.run;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mevenide.bridges.runjar.MavenRunJar;
import org.openide.util.Lookup;

/**
 * goal for running the project's jar artifact through maven in netbeans.
 * runs java -jar <jar>, assuming the assembly's jar-with-dependencies to be run before this one.
 * also assuming a Main-Class: entry in the manifest.
 *
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal run-jar
 * @requiresDependencyResolution runtime
 * @requiresProject
 */
public class RunJarMojo extends AbstractRunMojo  {
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenRunJar runjar = (MavenRunJar)Lookup.getDefault().lookup(MavenRunJar.class);
        if (runjar == null) {
            throw new MojoExecutionException("Not running within Netbeans, cannot lookup MavenRunJar instance");
        }
        int retCode = runjar.runJarProject(project, getLog(), finalName, jarLocation,workDirectory, executable, parameters, jvmParameters, debugJvmParameters, true);
        getLog().info("Exited with return code=" + retCode);
    }

}
