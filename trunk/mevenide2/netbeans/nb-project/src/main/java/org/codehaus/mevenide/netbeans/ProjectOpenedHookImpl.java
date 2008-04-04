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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.queries.MavenFileOwnerQueryImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * openhook implementation, register global classpath and also
 * register the project in the fileOwnerQuery impl, that's important for interproject
 * dependencies to work.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class ProjectOpenedHookImpl extends ProjectOpenedHook {
   
    private NbMavenProject project;
    private List<URI> uriReferences = new ArrayList<URI>();

    // ui logging
    static final String UI_LOGGER_NAME = "org.netbeans.ui.maven.project"; //NOI18N
    static final Logger UI_LOGGER = Logger.getLogger(UI_LOGGER_NAME);
    
    ProjectOpenedHookImpl(NbMavenProject proj) {
        project = proj;
    }
    
    protected void projectOpened() {
        attachUpdater();
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        if (q != null) {
            q.addMavenProject(project);
        } else {
            ErrorManager.getDefault().log("MavenFileOwnerQueryImpl not found..");  //NOI18N
        }
        Set<URI> uris = new HashSet<URI>();
        uris.addAll(Arrays.asList(project.getSourceRoots(false)));
        uris.addAll(Arrays.asList(project.getSourceRoots(true)));
        URI rootUri = FileUtil.toFile(project.getProjectDirectory()).toURI();
        File rootDir = new File(rootUri);
        for (URI uri : uris) {
            if (FileUtilities.getRelativePath(rootDir, new File(uri)) == null) {
                FileOwnerQuery.markExternalOwner(uri, project, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
                //TODO we do not handle properly the case when someone changes a
                // ../../src path to ../../src2 path in the lifetime of the project.
                uriReferences.add(uri);
            }
        }
        
        // register project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = project.getLookup().lookup(org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl.class);
        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
//        GlobalPathRegistry.getDefault().register(ClassPath.EXECUTE, cpProvider.getProjectClassPaths(ClassPath.EXECUTE));
        project.doBaseProblemChecks();
        
        //UI logging.. log what was the packaging type for the opened project..
        LogRecord record = new LogRecord(Level.INFO, "UI_MAVEN_PROJECT_OPENED");
        record.setLoggerName(UI_LOGGER_NAME); //NOI18N
        record.setParameters(new Object[] {project.getProjectWatcher().getPackagingType()});
        record.setResourceBundle(NbBundle.getBundle(ProjectOpenedHookImpl.class));
        UI_LOGGER.log(record);
    }
    
    protected void projectClosed() {
        uriReferences.clear();
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        if (q != null) {
            q.removeMavenProject(project);
        } else {
            ErrorManager.getDefault().log("MavenFileOwnerQueryImpl not found.."); //NOI18N
        }
        detachUpdater();
        // unregister project's classpaths to GlobalPathRegistry
        ClassPathProviderImpl cpProvider = project.getLookup().lookup(org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl.class);
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
        FileObject xml = fo.getFileObject("pom.xml"); //NOI18N
        if (userFo != null) {
            userFo.addFileChangeListener(project.getUserFolderUpdater());
            FileObject prop = userFo.getFileObject("settings.xml"); //NOI18N
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
            FileObject prop = userFo.getFileObject("settings.xml"); //NOI18N
            if (prop != null) {
                prop.removeFileChangeListener(project.getFileUpdater());
            }
        }
        FileObject xml = fo.getFileObject("pom.xml"); //NOI18N
        if (xml != null) {
            xml.removeFileChangeListener(project.getFileUpdater());
        }
    }        

}
