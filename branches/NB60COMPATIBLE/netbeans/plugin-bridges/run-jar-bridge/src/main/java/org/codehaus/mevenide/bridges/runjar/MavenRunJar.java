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
package org.codehaus.mevenide.bridges.runjar;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author mkleint
 */
public interface MavenRunJar {
    
    /**
     * executes the jar project in the IDE
     * @returns the return exit code of the project execution
     */
    int runJarProject(MavenProject project, Log log, String finalName,
            File jarLocation, File workDirectory, String executable, String parameters,
            String jvmParameters, String debugJvmParameters, boolean waitForFinish) throws MojoFailureException, MojoExecutionException;
    
}
