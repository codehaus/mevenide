/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mevenide.project.io.PomSkeletonBuilder;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class FileUtils {

    private static Log log = LogFactory.getLog(FileUtils.class);

    private static final String POM_NAME = "project.xml"; //$NON-NLS-1$
    private static final String PROJECT_PROPERTIES_NAME = "project.properties"; //$NON-NLS-1$
    private static final String IGNORE_FILE_NAME = ".mvnignore"; //$NON-NLS-1$

    private FileUtils() {
    }

    public static boolean isClassFolder(String entryPath, IProject project) {
        return new File(project.getLocation().append(
                new Path(entryPath).removeFirstSegments(1)).toOSString())
                .isDirectory();
    }

    public static void createPom(IContainer folder, String file) throws Exception, CoreException {
        log.debug("Creating pom skeleton using template : " + file); //$NON-NLS-1$
        PomSkeletonBuilder pomSkeletonBuilder = PomSkeletonBuilder.getSkeletonBuilder(file);
        String referencedPomSkeleton = pomSkeletonBuilder.getPomSkeleton(folder.getName());
        IFile referencedProjectFile = folder.getFile(new Path(POM_NAME));
        referencedProjectFile.create(new ByteArrayInputStream(referencedPomSkeleton.getBytes()), false, null);
    }

    public static void createPom(IContainer folder) throws Exception, CoreException {
        createPom(folder, null);
    }

    public static void copyFile(File in, File out) throws Exception {
        FileChannel sourceChannel = new FileInputStream(in).getChannel();
        FileChannel destinationChannel = new FileOutputStream(out).getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
    }

    public static File getPom(IContainer container) {
        //weird trick to fix a NPE. dont know yet why we got that NPE
        if (container.exists()) {
            IPathResolver pathResolver = new DefaultPathResolver();
            IPath referencedProjectLocation = container.getLocation();
            return new File(pathResolver.getAbsolutePath(referencedProjectLocation.append(POM_NAME)));
        }
        return null;
    }

    public static IFile assertIgnoreFileExists(IContainer container) throws Exception {
        IFile file = container.getFile(new Path(IGNORE_FILE_NAME));
        if (!file.exists()) {
            InputStream is = new StringInputStream(""); //$NON-NLS-1$
            file.create(is, true, null);
        }
        return file;
    }

    public static IFile assertIgnoreFileExists(Project project) throws Exception {
        File systemFile = new File(project.getFile().getParent(), IGNORE_FILE_NAME);
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        IFile file = workspaceRoot.getFileForLocation(new Path(systemFile.getAbsolutePath()).makeAbsolute());
        if (!file.exists()) {
            InputStream is = new StringInputStream(""); //$NON-NLS-1$
            file.create(is, true, null);
        }
        return file;
    }

    private static List getIgnoredResources(IProject project) {
        try {
            IFile file = project.getFile(new Path(IGNORE_FILE_NAME));
            if (!file.exists()) {
                return getIgnoredResources(file);
            }
            return new ArrayList();
        } catch (Exception e) {
            log.debug("Cannot read ignored resources", e); //$NON-NLS-1$
            return new ArrayList();
        }
    }

    private static List getIgnoredResources(IFile ignoredResourceFile) throws Exception {
        RandomAccessFile raf = null;
        List ignoredLines = new ArrayList();

        try {
            raf = new RandomAccessFile(ignoredResourceFile.getLocation().toOSString(), "r"); //$NON-NLS-1$
            String line = null;
            while ((line = raf.readLine()) != null) {
                if (line.trim().length() > 0) {
                    ignoredLines.add(line.trim());
                }
            }
        } finally {
            if (raf != null) {
                raf.close();
            }
        }

        return ignoredLines;
    }

    public static boolean isArtifactIgnored(String ignoreLine, IProject project) {
        List ignoredResources = getIgnoredResources(project);
        for (int u = 0; u < ignoredResources.size(); u++) {
            if (((String) ignoredResources.get(u)).equals(ignoreLine)) {
                return true;
            }
        }
        return false;
    }

    public static IProject getParentProjectForFile(File f) {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(f.getName());
        if (project.exists()) {
            return project;
        }
        return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
                new Path(f.getAbsolutePath())).getProject();
    }

    public static void refreshProperties(IContainer eclipseContainer) {
        IFile file = eclipseContainer.getFile(new Path(PROJECT_PROPERTIES_NAME));
        try {
            if (file.exists()) {
                file.refreshLocal(IResource.DEPTH_ZERO, null);
            }
        } catch (CoreException e) {
            String message = "Unable to refresh project.properties"; //$NON-NLS-1$
            log.error(message, e);
        }
    }

    public static File getProjectPropertiesFile(File containerDir) throws IOException {
        File file = new File(containerDir, PROJECT_PROPERTIES_NAME);
        String errorMessage = "Unable to create project.properties in directory " + containerDir; //$NON-NLS-1$
        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                if (!result) {
                    log.warn(errorMessage);
                }
            } catch (IOException e) {
                log.error(errorMessage, e);
                throw e;
            }
        }
        return file;
    }
}
