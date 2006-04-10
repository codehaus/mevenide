/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.netbeans;

import java.io.File;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.queries.MavenFileOwnerQueryImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * openhook implementation, register global classpath and also
 * register the project in the fileOwnerQuery impl, that's important for interproject
 * dependencies to work.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class ProjectOpenedHookImpl extends ProjectOpenedHook {
   
    private NbMavenProject project;
    ProjectOpenedHookImpl(NbMavenProject proj) {
        project = proj;
    }
    
    protected void projectOpened() {
        attachUpdater();
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        if (q != null) {
            q.addMavenProject(project);
        } else {
            ErrorManager.getDefault().log("MavenFileOwnerQueryImpl not found..");
        }
        // register project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)project.getLookup().lookup(ClassPathProviderImpl.class);
        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
//        GlobalPathRegistry.getDefault().register(ClassPath.EXECUTE, cpProvider.getProjectClassPaths(ClassPath.EXECUTE));
    }
    
    protected void projectClosed() {
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        if (q != null) {
            q.removeMavenProject(project);
        } else {
            ErrorManager.getDefault().log("MavenFileOwnerQueryImpl not found..");
        }
        detachUpdater();
        // unregister project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)project.getLookup().lookup(ClassPathProviderImpl.class);
        GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().unregister(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
//        GlobalPathRegistry.getDefault().unregister(ClassPath.EXECUTE, cpProvider.getProjectClassPaths(ClassPath.EXECUTE));
    }
   
    private void attachUpdater() {
        //TODO the updating bussiness is somewhat more complex.
        // inheritance currently not taken into account
        
        FileObject fo = project.getProjectDirectory();
        FileObject userFo = project.getHomeDirectory();
        fo.addFileChangeListener(project.getProjectFolderUpdater());
        FileObject xml = fo.getFileObject("pom.xml");
        if (userFo != null) {
            userFo.addFileChangeListener(project.getUserFolderUpdater());
            FileObject prop = userFo.getFileObject("settings.xml");
            if (prop != null) {
                prop.addFileChangeListener(project.getFileUpdater());
            }
        }
        if (xml != null) {
            xml.addFileChangeListener(project.getFileUpdater());
        }
    }    
    
   private void detachUpdater() {
        FileObject fo = project.getProjectDirectory();
        FileObject userFo = project.getHomeDirectory();
        fo.removeFileChangeListener(project.getProjectFolderUpdater());
        if (userFo != null) {
            userFo.removeFileChangeListener(project.getUserFolderUpdater());
            FileObject prop = userFo.getFileObject("settings.xml");
            if (prop != null) {
                prop.removeFileChangeListener(project.getFileUpdater());
            }
        }
        FileObject xml = fo.getFileObject("pom.xml");
        if (xml != null) {
            xml.removeFileChangeListener(project.getFileUpdater());
        }
    }        

}
