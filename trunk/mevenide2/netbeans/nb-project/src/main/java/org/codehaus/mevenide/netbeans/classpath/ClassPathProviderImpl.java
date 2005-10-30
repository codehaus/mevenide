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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
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
    private static final int TYPE_TESTCLASS = 3;
    private static final int TYPE_CLASS = 2;
    private static final int TYPE_ARTIFACT = 4;
    private static final int TYPE_UNKNOWN = -1;
    
    
    
    private NbMavenProject project;
    private ClassPath[] cache = new ClassPath[7];
    
    public ClassPathProviderImpl(NbMavenProject proj) {
        project = proj;
//        project.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                cache = new SoftReference[7];
//            }
//        });
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
            FileObject d = FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getSourceDirectory());
            if (d != null) {
                l.add(getCompileTimeClasspath(d));
            }
            d = FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getTestSourceDirectory());
            if (d != null) {
                l.add(getCompileTimeClasspath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        if (ClassPath.EXECUTE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            FileObject d = FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getSourceDirectory());
            if (d != null) {
                l.add(getRuntimeClasspath(d));
            }
            d = FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getTestSourceDirectory());
            if (d != null) {
                l.add(getRuntimeClasspath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        
        if (ClassPath.SOURCE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            FileObject d = FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getSourceDirectory());
            if (d != null) {
                l.add(getSourcepath(d));
            }
            d = FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getTestSourceDirectory());
            if (d != null) {
                l.add(getSourcepath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        return new ClassPath[0];
    }
    
    public ClassPath findClassPath(FileObject file, String type) {
        if (type.equals(ClassPath.COMPILE)) {
            return getCompileTimeClasspath(file);
        } else if (type.equals(ClassPath.EXECUTE)) {
            return getRuntimeClasspath(file);
        } else if (ClassPath.SOURCE.equals(type)) {
            return getSourcepath(file);
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
    private boolean isChildOf(FileObject child, List possibleParents) {
        FileObject[] dirs = FileUtilities.convertStringsToFileObjects(possibleParents);
        for (int i =0; i < dirs.length; i++) {
            if (dirs[i] != null  && dirs[i].isFolder() && (dirs[i].equals(child) || FileUtil.isParentOf(dirs[i], child))) {
                return true;
            }
        }
        return false;
    }
    
    private int getType(FileObject file) {
        if (isChildOf(file, project.getOriginalMavenProject().getCompileSourceRoots())) {
            return TYPE_SRC;
        }
//        // web app src also considered source..
//        dir = FileUtilities.convertURItoFileObject(project.getWebAppDirectory());
//        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
//            return TYPE_SRC;
//        }
        if (isChildOf(file, project.getOriginalMavenProject().getTestCompileSourceRoots())) {
            return TYPE_TESTSRC;
        }
//        // cactus is also a test source..
//        dir = FileUtilities.convertURItoFileObject(project.getCactusDirectory());
//        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
//            return TYPE_TESTSRC;
//        }
        try {
            if (isChildOf(file, project.getOriginalMavenProject().getCompileClasspathElements())) {
                return TYPE_CLASS;
            }
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
        }
        project.getOriginalMavenProject().getArtifact().getFile();
//        dir = getBuildJar();
//        if (dir != null && (dir.equals(file))) {     //TODO When MasterFs check also isParentOf
//            return TYPE_ARTIFACT;
//        }
        try {
            if (isChildOf(file, project.getOriginalMavenProject().getTestClasspathElements())) {
                return TYPE_TESTCLASS;
            }
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
        }
        return TYPE_UNKNOWN;
    }
    
    
    
    private ClassPath getSourcepath(FileObject file) {
        int type = getType(file);
        if (type != TYPE_SRC &&  type != TYPE_TESTSRC ) {
            return null;
        }
        ClassPath cp = cache[type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new SrcClassPathImpl(project));
            } else {
                cp = ClassPathFactory.createClassPath(new TestSrcClassPathImpl(project));
            }
            cache[type] = cp;
        }
        return cp;
    }
    
    private ClassPath getCompileTimeClasspath(FileObject file) {
        int type = getType(file);
        if (type != TYPE_SRC &&  type != TYPE_TESTSRC) {
            return null;
        }
        ClassPath cp = cache[2+type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new SrcBuildClassPathImpl(project));
            } else {
                cp = ClassPathFactory.createClassPath(new TestSrcBuildClassPathImpl(project));
            }
            cache[2+type] = cp;
        }
        return cp;
    }
    
    private ClassPath getRuntimeClasspath(FileObject file) {
        int type = getType(file);
        if (type != TYPE_SRC &&  type != TYPE_TESTSRC) {
            return null;
        }
        ClassPath cp = cache[4+type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new SrcRuntimeClassPathImpl(project));
            } else {
                cp = ClassPathFactory.createClassPath(new TestSrcRuntimeClassPathImpl(project));
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

