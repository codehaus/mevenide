/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.netbeans.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.classpath.ClassPathProviderImpl;
import org.mevenide.project.dependency.DefaultDependencyPathFinder;
import org.mevenide.project.io.JarOverrideReader2;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ProjectOpenedHookImpl extends ProjectOpenedHook {
    private static final Log logger = LogFactory.getLog(ProjectOpenedHookImpl.class);
   
    private MavenProject project;
    ProjectOpenedHookImpl(MavenProject proj) {
        project = proj;
    }
    
    protected void projectOpened() {
        logger.debug("Project opened.");
        // register project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)project.getLookup().lookup(ClassPathProviderImpl.class);
        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
        checkUnresolvedDependencies();
    }
    
    protected void projectClosed() {
        logger.debug("Project closed.");
        // unregister project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)project.getLookup().lookup(ClassPathProviderImpl.class);
        GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().unregister(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
    }
    
    private void checkUnresolvedDependencies() {
        List lst = new ArrayList();
        Project mavproj = project.getOriginalMavenProject();
        if (mavproj.getDependencies() == null) {
            return;
        }
        Iterator it = mavproj.getDependencies().iterator();
        
        while (it.hasNext()) {
            Dependency dep = (Dependency)it.next();
            if (!dependencyExists(dep)) {
                lst.add(dep);
            }
        }
        
        if (lst.size() > 0) {
            //TODO - show dialog offering to download dependencies.
        }
    }
    
    private boolean dependencyExists(Dependency dep) {
        if (dep.getType() == null || "jar".equals(dep.getType())) {
            // check override first
            String path = JarOverrideReader2.getInstance().processOverride(dep,
	            project.getPropertyResolver(),
	            project.getLocFinder());
            if (path == null) {
                DefaultDependencyPathFinder finder = new DefaultDependencyPathFinder(dep);
                path = finder.resolve();
            }
            logger.debug("dep path=" + path);
            File file = new File(path);
            return file.getName().endsWith(".jar") && file.exists();
        }
        return false;
    }
}
