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

package org.codehaus.mevenide.netbeans.classpath;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Defines class path for maven2 projects..
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class ClassPathProviderImpl implements ClassPathProvider {
    
    private static final int TYPE_SRC = 0;
    private static final int TYPE_TESTSRC = 1;
    private static final int TYPE_WEB = 5;
    private static final int TYPE_UNKNOWN = -1;
    
    private NbMavenProject project;
    private ClassPath[] cache = new ClassPath[7];
    
    public ClassPathProviderImpl(NbMavenProject proj) {
        project = proj;
    }
    
    /**
     * Returns array of all classpaths of the given type in the project.
     * The result is used for example for GlobalPathRegistry registrations.
     */
    public ClassPath[] getProjectClassPaths(String type) {
        if (ClassPath.BOOT.equals(type)) {
            //TODO
            return new ClassPath[]{ getBootClassPath() };
        }
        if (ClassPath.COMPILE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            l.add(getCompileTimeClasspath(TYPE_SRC));
            l.add(getCompileTimeClasspath(TYPE_TESTSRC));
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        if (ClassPath.EXECUTE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            l.add(getRuntimeClasspath(TYPE_SRC));
            l.add(getRuntimeClasspath(TYPE_TESTSRC));
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        
        if (ClassPath.SOURCE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            l.add(getSourcepath(TYPE_SRC));
            l.add(getSourcepath(TYPE_TESTSRC));
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        return new ClassPath[0];
    }
    
    public ClassPath findClassPath(FileObject file, String type) {
        int fileType = getType(file);
        if (fileType != TYPE_SRC &&  fileType != TYPE_TESTSRC && fileType != TYPE_WEB) {
            Logger.getLogger(ClassPathProviderImpl.class.getName()).log(Level.INFO, " bad type=" + type + " for " + file); //NOI18N
            return null;
        }
        if (type.equals(ClassPath.COMPILE)) {
            return getCompileTimeClasspath(fileType);
        } else if (type.equals(ClassPath.EXECUTE)) {
            return getRuntimeClasspath(fileType);
        } else if (ClassPath.SOURCE.equals(type)) {
            return getSourcepath(fileType);
        } else if (type.equals(ClassPath.BOOT)) {
            return getBootClassPath();
        } else {
            return null;
        }
    }
    
    /**
     * @param child fileobject for the child..
     * @param possibleParents List of Strings denoting files..
     */
    private boolean isChildOf(FileObject child, List<String> possibleParents) {
        FileObject[] dirs = FileUtilities.convertStringsToFileObjects(possibleParents);
        for (int i =0; i < dirs.length; i++) {
            if (dirs[i] != null  && dirs[i].isFolder() && (dirs[i].equals(child) || FileUtil.isParentOf(dirs[i], child))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isChildOf(FileObject child, URI[] uris) {
        for (int i = 0; i < uris.length; i++) {
            FileObject fo = FileUtilities.convertURItoFileObject(uris[i]);
            if (fo != null  && fo.isFolder() && (fo.equals(child) || FileUtil.isParentOf(fo, child))) {
                return true;
            }
        }
        return false;
    }
    
    private int getType(FileObject file) {
        if (isChildOf(file, project.getOriginalMavenProject().getCompileSourceRoots()) ||
            isChildOf(file, project.getGeneratedSourceRoots()) ||
            isChildOf(file, project.getResources(false))) {
            return TYPE_SRC;
        }
        if (isChildOf(file, project.getOriginalMavenProject().getTestCompileSourceRoots()) ||
            isChildOf(file, project.getResources(true))) {
            return TYPE_TESTSRC;
        }
        
        URI web = project.getWebAppDirectory();
        FileObject fo = FileUtil.toFileObject(new File(web));
        if (fo != null && (fo.equals(file) || FileUtil.isParentOf(fo, file))) {
            return TYPE_WEB;
        }
        
        return TYPE_UNKNOWN;
    }
    
    
    
    private ClassPath getSourcepath(int type) {
        if (type == TYPE_WEB) {
            type = TYPE_SRC;
        }
        ClassPath cp = cache[type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new SourceClassPathImpl(project));
            } else {
                cp = ClassPathFactory.createClassPath(new TestSourceClassPathImpl(project));
            }
            cache[type] = cp;
        }
        return cp;
    }
    
    private ClassPath getCompileTimeClasspath(int type) {
        if (type == TYPE_WEB) {
            type = TYPE_SRC;
        }
        ClassPath cp = cache[2+type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new CompileClassPathImpl(project));
            } else {
                cp = ClassPathFactory.createClassPath(new TestCompileClassPathImpl(project));
            }
            cache[2+type] = cp;
        }
        return cp;
    }
    
    private ClassPath getRuntimeClasspath(int type) {
        if (type == TYPE_WEB) {
            type = TYPE_SRC;
        }
        ClassPath cp = cache[4+type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new RuntimeClassPathImpl(project));
            } else {
                cp = ClassPathFactory.createClassPath(new TestRuntimeClassPathImpl(project));
            }
            cache[4+type] = cp;
        }
        return cp;
    }
    
    private ClassPath getBootClassPath() {
        ClassPath cp = cache[6];
        if (cp == null) {
            cp = ClassPathFactory.createClassPath(new BootClassPathImpl(project));
            cache[6] = cp;
        }
        return cp;
    }
}

