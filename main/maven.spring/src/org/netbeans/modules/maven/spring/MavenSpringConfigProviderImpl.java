/*
 *  Copyright 2008 Mevenide Team.
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

package org.netbeans.modules.maven.spring;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.spring.api.beans.SpringConstants;
import org.netbeans.modules.spring.spi.beans.SpringConfigFileLocationProvider;
import org.netbeans.modules.spring.spi.beans.SpringConfigFileProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbCollections;

/**
 *
 * @author mkleint
 */
public class MavenSpringConfigProviderImpl implements SpringConfigFileLocationProvider, SpringConfigFileProvider {
    private Project prj;
    
    MavenSpringConfigProviderImpl(Project project) {
        prj = project;
    }

    /**
     * Can return {@code null} if the location is unknown.
     *
     * @return the location.
     */

    public FileObject getLocation() {
        System.out.println("get location");
        NbMavenProject project = prj.getLookup().lookup(NbMavenProject.class);
        if (NbMavenProject.TYPE_WAR.equals(project.getPackagingType())) {
            FileObject fo = FileUtilities.convertURItoFileObject(project.getWebAppDirectory());
            if (fo != null) {
                return fo;
            }
        }
        URI[] res = project.getResources(false);
        for (URI resource : res) {
            FileObject fo = FileUtilities.convertURItoFileObject(resource);
            if (fo != null) {
                return fo;
            }
        }
        return null;
    }

    public Set<File> getConfigFiles() {
        System.out.println("get config files");
        Set<File> result = new HashSet<File>();
        NbMavenProject project = prj.getLookup().lookup(NbMavenProject.class);
        if (NbMavenProject.TYPE_WAR.equals(project.getPackagingType())) {
            FileObject fo = FileUtilities.convertURItoFileObject(project.getWebAppDirectory());
            if (fo != null) {
                addFilesInRoot(fo, result);
            }
        }
        URI[] res = project.getResources(false);
        for (URI resource : res) {
            FileObject fo = FileUtilities.convertURItoFileObject(resource);
            if (fo != null) {
                addFilesInRoot(fo, result);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    
    private static void addFilesInRoot(FileObject root, Set<File> result) {
        for (FileObject fo : NbCollections.iterable(root.getChildren(true))) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            if (!SpringConstants.CONFIG_MIME_TYPE.equals(fo.getMIMEType())) {
                continue;
            }
            File file = FileUtil.toFile(fo);
            if (file == null) {
                continue;
            }
            result.add(file);
        }
    }
    
}
