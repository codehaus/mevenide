/* ==========================================================================
 * Copyright 2007 Mevenide Team
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
package org.codehaus.mevenide.plugins.nbmreload;

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.reloadnbm.MavenNBMReload;
import org.codehaus.plexus.util.DirectoryScanner;
import org.openide.util.Lookup;


/**
 * Reload the NetBeans module in the developer's IDE. Requires a project with "nbm" packaging.
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal reload
 * @requiresProject
 */
public class NbmReloadMojo extends AbstractMojo {
    
    /**
     * Netbeans module assembly build directory.
     * directory where the the netbeans jar and nbm file get constructed.
     * @parameter default-value="${project.build.directory}/nbm" expression="${maven.nbm.buildDir}"
     * @required
     */
    private String nbmBuildDir;
    
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!"nbm".equals(project.getPackaging())) {
            getLog().warn("You attempt to run the plugin on a project that might not be of nbm kind (packaging). The plugin might possibly fail.");
        }
        DirectoryScanner scanner = new DirectoryScanner();
        File basedir = new File(nbmBuildDir);
//        File basedir = new File(project.getBasedir(), "target" + File.separator + "nbm");
        scanner.setBasedir(basedir);
        scanner.setIncludes(new String[] {
            "**/modules/*.jar",
            "**/modules/eager/*.jar",
            "**/modules/autoload/*.jar"
        });
        scanner.scan();
        String[] incl = scanner.getIncludedFiles();
        if (incl != null && incl.length > 0) {
            if (incl[0].indexOf("eager") > -1 || incl[0].indexOf("autoload") > -1) {
                throw new MojoFailureException("Cannot reload 'autoload' or 'eager' modules.");
            }
            MavenNBMReload deployment = (MavenNBMReload)Lookup.getDefault().lookup(MavenNBMReload.class);
            if (deployment == null) {
                getLog().error("Cannot lookup the Maven-NetBeans bridge for NBM Reload.");
                throw new MojoExecutionException("Cannot lookup the Maven-NetBeans bridge for NBM Reload.");
            }
            deployment.doReload(project, getLog(), new File(basedir, incl[0]));
        } else {
            throw new MojoFailureException("Cannot find any built NetBeans Module artifacts for reload.");
        }
    }

    
    
}
