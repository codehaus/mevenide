/*
 * JPDAReloadMojo.java
 *
 * Created on January 13, 2006, 10:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.plugin.debugger;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Reload the JPDA debugger
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal jpdareload
 * @requiresProject
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class JPDAReloadMojo {
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /** Creates a new instance of JPDAReloadMojo */
    public JPDAReloadMojo() {
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
    }
    
}
