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

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractRunMojo extends AbstractMojo {
    /**
     * the java executable to use for starting the process
     * @parameter expression = "java"
     */
    protected String executable;
    
    /**
     * @parameter expression="${netbeans.jar.run.params}"
     */
    protected String parameters;

    /**
     * @parameter expression="${netbeans.jar.run.jvmparams}"
     */
    protected String jvmParameters;

    /**
     * @parameter expression="${netbeans.jar.run.debugparams}"
     */
    protected String debugJvmParameters;

    /**
     * @parameter expression="${project.build.directory}/executable-netbeans"
     */
    protected File workDirectory;
    
    /**
     * @parameter expression="${project.build.directory}/executable-netbeans"
     * @required
     */
    protected File jarLocation;
    
    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    protected String finalName;

    /**
     * maven project
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
}
