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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.properties.IPropertyLocator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class FileUtilities
{
    
    /** Creates a new instance of FileUtilities */
    public FileUtilities()
    {
    }
    
    public static FileObject findFolder(FileObject rootFolder, String relPath)
    {
        StringTokenizer tok = new StringTokenizer(relPath, "/", false);
        FileObject folder = rootFolder;
        while (tok.hasMoreTokens() && folder != null)
        {
            folder = folder.getFileObject(tok.nextToken(), null);
        }
        return folder;
    }
    
    public static FileObject findOrCreate(FileObject rootFolder, String relPath) throws IOException
    {
        FileObject parentFolder = rootFolder;
        FileObject folder = rootFolder;
        StringTokenizer tok = new StringTokenizer(relPath, "/", false);
        while (tok.hasMoreTokens())
        {
            String name = tok.nextToken();
            folder = parentFolder.getFileObject(name, null);
            if (folder == null)
            {
                parentFolder.createFolder(name);
            }
            parentFolder = folder;
        }
        return folder;
    }
    
    public static FileObject getUserHome() {
        String home = System.getProperty("user.home");
        File file = new File(home);
        FileObject[] fo = FileUtil.fromFile(file);
        if (fo.length > 0) {
            return fo[0];
        }
        throw new IllegalStateException("Cannot find user.home fileobject (" + home + ")");
    }
    
    public static FileObject convertURItoFileObject(URI uri) {
        if (uri == null) {
            return null;
        }
        File fil = new File(uri);
        FileObject[] fos = FileUtil.fromFile(fil);
        if (fos.length > 0) {
            return fos[0];
        }
        return null;
    }
    
    /**
     * attempts to constuct the path to the given dependency in the current project constraints.
     * Does not resolve jar overriding.
     */
    public static URI getDependencyURI(Dependency dependency, MavenProject project) {
        ILocationFinder finder = project.getLocFinder();
        StringBuffer buff = new StringBuffer();
        File repo = new File(finder.getMavenLocalRepository());
        buff.append(dependency.getArtifactDirectory());
        buff.append("/");
        String type = dependency.getType();
        buff.append(type != null ? type : "jar");
        buff.append("s/");
        String id = dependency.getArtifactId();
        buff.append(id != null ? id : dependency.getId());
        buff.append("-");
        buff.append(dependency.getVersion());
        buff.append(".");
        String extension = dependency.getExtension();
        buff.append(extension != null ? extension : "jar");
        File file = new File(repo, buff.toString());
        file = FileUtil.normalizeFile(file);
        return file.toURI();
    }
    
    
    public static File locationToFile(int location, MavenProject project) {
        IQueryContext context = project.getContext();
        if (location == IPropertyLocator.LOCATION_PROJECT) {
            return new File(context.getProjectDirectory(), "project.properties");
        }
        if (location == IPropertyLocator.LOCATION_PROJECT_BUILD) {
            return new File(context.getProjectDirectory(), "build.properties");
        }
        if (location == IPropertyLocator.LOCATION_USER_BUILD) {
            return new File(context.getUserDirectory(), "build.properties");
        }
        throw new IllegalArgumentException("Wrong argument.");
    }
    
    /**
     * inspired by netbeans's org.apache.tools.ant.module.api.support.ActionUtils.findSelectedFiles
     */
    public static FileObject[] findSelectedFiles(Lookup context, FileObject dir, String suffix) {
        if (dir != null && !dir.isFolder()) {
            throw new IllegalArgumentException("Not a folder: " + dir); // NOI18N
        }
        if (suffix != null && suffix.indexOf('/') != -1) {
            throw new IllegalArgumentException("Cannot includes slashes in suffix: " + suffix); // NOI18N
        }
        List files = new ArrayList();
        Iterator it = context.lookup(new Lookup.Template(DataObject.class)).allInstances().iterator();
        while (it.hasNext()) {
            DataObject d = (DataObject)it.next();
            FileObject f = d.getPrimaryFile();
            boolean matches = FileUtil.toFile(f) != null;
            if (dir != null) {
                matches &= (FileUtil.isParentOf(dir, f) || dir == f);
            }
            if (suffix != null) {
                matches &= f.getNameExt().endsWith(suffix);
            }
            if (matches) {
                files.add(f);
            } 
        }
        return (FileObject[])files.toArray(new FileObject[files.size()]);
    }
}
