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

package org.codehaus.mevenide.plugin.debugger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.debugger.MavenDebugger2;
import org.openide.util.Lookup;

/**
 * Reload classes
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal reload
 * @requiresProject
 * @phase compile
 * @requiresDependencyResolution compile
 */
public class JPDAReloadMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * @parameter expression="${netbeans.debug.class}
     * @required
     */
    private String className;
    
    
    /** Creates a new instance of JPDAReloadMojo */
    public JPDAReloadMojo() {
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Reloading...");
        MavenDebugger2 debugger = (MavenDebugger2)Lookup.getDefault().lookup(MavenDebugger2.class);
        if (debugger == null) {
            throw new MojoFailureException("Incompatible version of Maven support in NetBeans.");
        }
        debugger.reload(project, getLog(), className);
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
    
}
