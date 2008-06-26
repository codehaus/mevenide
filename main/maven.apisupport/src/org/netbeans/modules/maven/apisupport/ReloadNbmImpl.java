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

package org.netbeans.modules.maven.apisupport;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import org.netbeans.core.startup.TestModuleDeployer;
import org.netbeans.modules.maven.bridges.reloadnbm.MavenNBMReload;

/**
 *
 * @author mkleint
 */
public class ReloadNbmImpl implements MavenNBMReload {

    public void doReload(MavenProject project, Log log, File module) throws MojoExecutionException, MojoFailureException {
        try {
            TestModuleDeployer.deployTestModule(module);
        } catch (IOException ex) {
            new MojoExecutionException("Error redeploying NBM module in developer IDE.", ex);
        }
    }
}