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
package org.codehaus.mevenide.pde.mojo;

import java.io.File;
import java.util.Collection;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.classpath.PluginClasspathResolver;

/**
 * 
 * @goal classpath
 * @phase compile
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * 
 */
public class PdeClasspathMojo extends AbstractMojo {
	/**
	 * @parameter 
	 * @description folder of the project under construction 
	 */
	private File basedir;
    
    /**
     * @parameter 
     * @description absolute path of eclipse home directory 
     */
    private String eclipseHome;
	
	/**
	 * @parameter
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	public void execute() throws MojoExecutionException {
		
        PluginClasspathResolver resolver = new PluginClasspathResolver(basedir, eclipseHome);
        try {
            Collection artifacts = resolver.extractEclipseDependencies();
			project.getCompileClasspathElements().addAll(artifacts);
        } 
		catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Unable to attach eclipse dependencies", e);
		}
        catch (PdePluginException e) {
            throw new MojoExecutionException("Unable to extract eclipse dependencies from descriptor", e);
        }  
		
	}
}
