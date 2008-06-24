/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.netbeans.modules.maven.gsf;

import org.netbeans.api.project.Project;
import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.api.classpath.GlobalPathRegistry;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author mkleint
 */
public class ProjectOpenedHookImpl extends ProjectOpenedHook {

    private Project project;

    ProjectOpenedHookImpl(Project prj) {
        this.project = prj;
    }

    @Override
    protected void projectOpened() {
        /**
         * @Note:
         * Register classpaths to GlobalPathRegistry will cause GSF indexer to monitor and indexing them.
         * 
         * Per org.netbeans.modules.gsfret.source.GlobalSourcePath#createResources(Request),
         * Tor' midifications: Treat bootCps as a source path, not a binary -  I want to scan directories.
         * 
         * We should here register boot source's classpath instead of binary boot classpath.
         * 
         * GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
         */
//mkleint: what is this? copied from scala project
//        FileObject scalaStubsFo = ScalaLanguage.getScalaStubFo();
//        if (scalaStubsFo != null) {
//            coreLibsCp = ClassPathSupport.createClassPath(new FileObject[]{scalaStubsFo});
//            GlobalPathRegistry.getDefault().register(ClassPath.BOOT, new ClassPath[]{coreLibsCp});
//        }
        
        CPProvider cpProvider = project.getLookup().lookup(CPProvider.class);

        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectSourcesClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectSourcesClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, cpProvider.getProjectSourcesClassPaths(ClassPath.COMPILE));
    }

    @Override
    protected void projectClosed() {
        CPProvider cpProvider = project.getLookup().lookup(CPProvider.class);
        //GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, cpProvider.getProjectSourcesClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().unregister(ClassPath.COMPILE, cpProvider.getProjectSourcesClassPaths(ClassPath.COMPILE));
    }
}
