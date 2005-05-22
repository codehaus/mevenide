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
package org.mevenide.netbeans.project;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.classpath.ClassPathProviderImpl;
import org.mevenide.netbeans.project.queries.MavenFileOwnerQueryImpl;
import org.mevenide.project.io.JarOverrideReader2;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

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
        attachUpdater();
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        if (q != null) {
            q.addMavenProject(project);
        } else {
            logger.error("no query MavenFileOwnerQueryImpl :(");
        }
        // register project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)project.getLookup().lookup(ClassPathProviderImpl.class);
        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
//        GlobalPathRegistry.getDefault().register(ClassPath.EXECUTE, cpProvider.getProjectClassPaths(ClassPath.EXECUTE));
//        checkUnresolvedDependencies();
    }
    
    protected void projectClosed() {
        logger.debug("Project closed.");
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        if (q != null) {
            q.removeMavenProject(project);
        } else {
            logger.error("no query MavenFileOwnerQueryImpl :(");
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
        FileObject fo = FileUtil.toFileObject(project.getContext().getProjectDirectory());
        FileObject userFo = FileUtil.toFileObject(project.getContext().getUserDirectory());
        fo.addFileChangeListener(project.getProjectFolderUpdater());
        userFo.addFileChangeListener(project.getUserFolderUpdater());
        FileObject xml = fo.getFileObject("project.xml");
        FileObject prop = fo.getFileObject("project.properties");
        FileObject prop2 = fo.getFileObject("build.properties");
        FileObject prop3 = userFo.getFileObject("build.properties");
        if (xml != null) {
            xml.addFileChangeListener(project.getFileUpdater());
        }
        if (prop != null) {
            prop.addFileChangeListener(project.getFileUpdater());
        }
        if (prop2 != null) {
            prop2.addFileChangeListener(project.getFileUpdater());
        }
        if (prop3 != null) {
            prop3.addFileChangeListener(project.getFileUpdater());
        }
    }    
    
   private void detachUpdater() {
        FileObject fo = FileUtil.toFileObject(project.getContext().getProjectDirectory());
        FileObject userFo = FileUtil.toFileObject(project.getContext().getUserDirectory());
        fo.removeFileChangeListener(project.getProjectFolderUpdater());
        userFo.removeFileChangeListener(project.getUserFolderUpdater());
        FileObject xml = fo.getFileObject("project.xml");
        FileObject prop = fo.getFileObject("project.properties");
        FileObject prop2 = fo.getFileObject("build.properties");
        FileObject prop3 = userFo.getFileObject("build.properties");
        if (xml != null) {
            xml.removeFileChangeListener(project.getFileUpdater());
        }
        if (prop != null) {
            prop.removeFileChangeListener(project.getFileUpdater());
        }
        if (prop2 != null) {
            prop2.removeFileChangeListener(project.getFileUpdater());
        }
        if (prop3 != null) {
            prop3.removeFileChangeListener(project.getFileUpdater());
        }
    }        

    private boolean dependencyExists(Dependency dep) {
        // only those added to classpath matter..
        if (dep.getType() == null || dep.isAddedToClasspath()) {
            // check override first
            File file;
            String path = JarOverrideReader2.getInstance().processOverride(dep,
	            project.getPropertyResolver(),
	            project.getLocFinder());
           if (path != null) {
                file = new File(path);
            } else {
                file = new File(FileUtilities.getDependencyURI(dep, project));
            }
            logger.debug("dep path=" + path);
            return /*file.getName().endsWith(".jar") && */ file.exists();
        }
        return false;
    }
}
