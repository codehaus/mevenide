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
package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.idea.global.MavenManager;

import java.io.File;

/**
 * A location finder which uses an IDEA module to find the Java home and uses the IDEA settings to find the
 * Maven home.
 *
 * @author Arik
 */
public class ModuleLocationFinder extends LocationFinderAggregator {
    private final Module module;

    /**
         * Creates an instance using the given query context and module. The given module will be used to locate
         * the appropriate Java home. The Maven home will be taken from the settings defined by the user.
         *
         * @param queryContext the query context to use
         * @param pModule
         */
    public ModuleLocationFinder(final IQueryContext queryContext, final Module pModule) {
        super(queryContext);
        module = pModule;
    }

    /**
     * Use the Java home defined for the specified module.
     *
     * @return the Java home defined for the module, or the default Java home, if not defined for it
     */
    public String getJavaHome() {
        if(module == null)
            return super.getJavaHome();

        final ProjectJdk jdk = ModuleSettings.getInstance(module).getJdk();
        if (jdk == null)
            return super.getJavaHome();
        else
            return jdk.getHomeDirectory().getPath();
    }

    /**
     * Return the Maven home defined by the user in IDEA settings. If not defined there, lets the superclass
     * try to guess its location.
     *
     * @return maven home directory
     */
    public String getMavenHome() {
        final MavenManager mgr = MavenManager.getInstance();
        final File mavenHome = mgr.getMavenHome();
        if(mavenHome == null || !mavenHome.exists())
            return super.getMavenHome();
        else
            return mavenHome.getAbsolutePath();
    }
}
