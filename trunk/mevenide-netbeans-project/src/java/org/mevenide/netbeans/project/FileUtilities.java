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
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
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
public class FileUtilities {
    
    /** Creates a new instance of FileUtilities */
    public FileUtilities() {
    }
    
    public static FileObject findFolder(FileObject rootFolder, String relPath) {
        if (rootFolder != null) {
            File fl = FileUtil.toFile(rootFolder);
            if (fl != null) {
                File result = new File(fl, relPath);
                result = FileUtil.normalizeFile(result);
                return FileUtil.toFileObject(result);
            }
        }
        return null;
    }
    
    public static FileObject findOrCreate(FileObject rootFolder, String relPath) throws IOException {
        FileObject parentFolder = rootFolder;
        FileObject folder = rootFolder;
        StringTokenizer tok = new StringTokenizer(relPath, "/", false);
        while (tok.hasMoreTokens()) {
            String name = tok.nextToken();
            folder = parentFolder.getFileObject(name, null);
            if (folder == null) {
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
        File repo = new File(finder.getMavenLocalRepository());
        return getDependencyURI(dependency, repo);
    }
    
    static URI getDependencyURI(Dependency dependency, File repoFile) {
        StringBuffer buff = new StringBuffer();
        buff.append(dependency.getArtifactDirectory());
        buff.append(File.separator);
        String type = dependency.getType();
        buff.append(type != null ? type : "jar");
        buff.append("s");
        buff.append(File.separator);
        if (dependency.getJar() == null) {
            String id = dependency.getArtifactId();
            buff.append(id != null ? id : dependency.getId());
            buff.append("-");
            buff.append(dependency.getVersion());
            buff.append(".");
            String extension = dependency.getExtension();
            buff.append(extension != null ? extension : "jar");
        } else {
            buff.append(dependency.getJar());
        }
        File file = new File(repoFile, buff.toString());
        file = FileUtil.normalizeFile(file);
        return file.toURI();
    }    
    /**
     * find the right file for the given location and project. The returned file doesn't have to exist on disk.
     */
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
        if (location == OriginChange.LOCATION_POM) {
            File[] fls = context.getPOMContext().getProjectFiles();
            return fls[0];
        }
        if (location == OriginChange.LOCATION_POM_PARENT) {
            File[] fls = context.getPOMContext().getProjectFiles();
            if (fls.length > 1) {
                return fls[1];
            }
            return null;
        }
        throw new IllegalArgumentException("Wrong argument. is=" + location);
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
    
    /**
     * just gets the array of FOs from lookup.
     */
    public static FileObject[] extractFileObjectsfromLookup(Lookup lookup) {
        List files = new ArrayList();
        Iterator it = lookup.lookup(new Lookup.Template(DataObject.class)).allInstances().iterator();
        while (it.hasNext()) {
            DataObject d = (DataObject)it.next();
            FileObject f = d.getPrimaryFile();
            files.add(f);
        }
        return (FileObject[])files.toArray(new FileObject[files.size()]);
    }

    /**
     * delete a file or dir, recursively.
     */
    public static void delete(File file) {
        if ( file.isFile() ) {
            file.delete();
        }
        else {
            File[] files = file.listFiles();
            if ( files != null ) {
                for (int i = 0; i < files.length; i++) {
                    delete(files[i]);
                }
            }
            file.delete();
        }
        
    }
    
    /**
     * for source java files returns a their respective test (same name + Test) if it exists.
     * for test sources, returns the smae fileobject.
     *for anything else, returns null.
     *
     */
    public static FileObject findTestForFile(MavenProject project, FileObject f) {
        if (f == null || !"java".equals(f.getExt())) {
            return null;
        }
        File testRootFile = new File(project.getTestSrcDirectory());
        FileObject testroot = FileUtil.toFileObject(testRootFile);
        boolean matches = FileUtil.toFile(f) != null;
        if (testroot != null && FileUtil.isParentOf(testroot, f)) {
            return f;
        }
        FileObject srcroot = FileUtil.toFileObject(new File(project.getSrcDirectory()));
        if (srcroot != null && FileUtil.isParentOf(srcroot, f)) {
            String relative = FileUtil.getRelativePath(srcroot, f);
            relative = relative.substring(0, relative.length() - f.getNameExt().length());
            File testFile = new File(testRootFile, relative + f.getName() + "Test.java");
            System.out.println("testfile=" + testFile);
            if (testFile.exists()) {
                return FileUtil.toFileObject(testFile);
            }
        }
        return null;
    }
}
