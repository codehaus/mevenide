/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.netbeans.project;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;

/** 
 * Filesystem related utilities
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class FileSystemUtil 
{
    private static Log log = LogFactory.getLog(FileSystemUtil.class);

    /**
     * Netbeans filesystem attribute containing the id of the dependency
     */
    public static final String ATTR_MEVENIDE_ARTIFACT = "MevenIDE-Dependency-Artifact"; //NOI18N
    /**
     * Netbeans filesystem attribute identifying the filesystem as mounted by mevenide.
     */
    public static final String ATTR_MEVENIDE_SOURCE = "MevenIDE-Source-Path"; //NOI18N
    
    /**
     * mount a dependency jar to the netbeans repository, will start code-completion parsing
     * and other IDE related actions (automagically)
     */
    
    public static void mountDependency(Artifact artifact) throws IOException, PropertyVetoException
    {
        if (!alreadyMountedJar(artifact.getFile()))
        {
            JarFileSystem fs = new JarFileSystem();
            fs.setJarFile(artifact.getFile());
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Mount"); //NOI18N
            DataFolder folder = DataFolder.findFolder(fo);
            DataObject fsDobj = InstanceDataObject.create(folder, null, fs, null);
            // mark the fs as mounted automagically..
            fsDobj.getPrimaryFile().setAttribute(ATTR_MEVENIDE_ARTIFACT, artifact.getDependency().getId());
        }
    }
    
    /**
     * mount sources of the project as filesystems in IDe repository
     * TODO will need some way to indentify the types of sources, not sure if here is right place..
     * TODO - use "view Over Fs" instead of pure localfs, will get the advantage of having source control in..
     */
    public static void mountSources(File root) throws IOException, PropertyVetoException
    {
        if (!alreadyMountedFile(root))
        {
            LocalFileSystem fs = new LocalFileSystem();
            fs.setRootDirectory(root);
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Mount"); //NOI18N
            DataFolder folder = DataFolder.findFolder(fo);
            DataObject fsDobj = InstanceDataObject.create(folder, null, fs, null);
            // mark the fs as mounted automagically..
            fsDobj.getPrimaryFile().setAttribute(ATTR_MEVENIDE_SOURCE, "true");
            //                Repository.getDefault().addFileSystem(fs);
        }
    }
    
    /**
     * Same as the mountSources(File), will convert the FileObject to File first.
     */ 
    public static void mountSources(FileObject root) throws IOException, PropertyVetoException
    {
        File file = FileUtil.toFile(root);
        if (file != null) {
            mountSources(root);
        } else {
            throw new IOException("Cannot access file");
        }
    }
    
    private static boolean alreadyMountedJar(File jarFile)
    {
        Enumeration en = Repository.getDefault().getFileSystems();
        while (en.hasMoreElements())
        {
            FileSystem fs = (FileSystem)en.nextElement();
            if (fs.getSystemName().indexOf(jarFile.getName()) >= 0)
            {
                return true;
            }
        }
        return false;
    }    

    private static boolean alreadyMountedFile(File rootFile)
    {
        Enumeration en = Repository.getDefault().getFileSystems();
        while (en.hasMoreElements())
        {
            FileSystem fs = (FileSystem)en.nextElement();
            File rootFOFile = FileUtil.toFile(fs.getRoot());
            if (rootFOFile != null && rootFOFile.equals(rootFile))
            {
                return true;
            }
        }
        return false;
    }    
    
}
