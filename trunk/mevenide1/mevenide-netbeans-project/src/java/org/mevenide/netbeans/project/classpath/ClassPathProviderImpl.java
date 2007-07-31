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

package org.mevenide.netbeans.project.classpath;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.api.project.MavenProject;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Defines class path for maven projects..
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class ClassPathProviderImpl implements ClassPathProvider {
    private static final Logger logger = Logger.getLogger(ClassPathProviderImpl.class.getName());
  
//    private static final String SRC = "src.dir";
//    private static final String TEST_SRC = "test.src.dir";    
//    private static final String BUILD = "build.classes.dir";    
//    private static final String TEST_BUILD = "build.test.classes.dir";    
//    private static final String ARTIFACT = "actifact";    
    
    private static final int TYPE_SRC = 0;
    private static final int TYPE_TESTSRC = 1;
    private static final int TYPE_TESTCLASS = 3;
    private static final int TYPE_CLASS = 2;
    private static final int TYPE_ARTIFACT = 4;
    private static final int TYPE_UNKNOWN = -1;
    
    
    
    private MavenProject project;
    private ClassPath[] cache = new ClassPath[7];
     
    public ClassPathProviderImpl(MavenProject proj) {
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
            logger.fine("get boot path");
            return new ClassPath[]{ getBootClassPath() };
        }
        logger.fine("getProjectClassPaths type =" + type);
        if (ClassPath.COMPILE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            logger.fine("COMPILEgetProjectClassPaths src");
            FileObject d = FileUtilities.convertURItoFileObject(project.getSrcDirectory());
            if (d != null) {
                logger.fine("COMPILEgetProjectClassPaths src adding1=" + d);
                l.add(getCompileTimeClasspath(d));
            }
            d = FileUtilities.convertURItoFileObject(project.getTestSrcDirectory());
            if (d != null) {
                logger.fine("COMPILEgetProjectClassPaths src adding2=" + d);
                l.add(getCompileTimeClasspath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        if (ClassPath.EXECUTE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            logger.fine("EXECUTEgetProjectClassPaths src");
            FileObject d = FileUtilities.convertURItoFileObject(project.getSrcDirectory());
            if (d != null) {
                logger.fine("EXECUTEgetProjectClassPaths src adding1=" + d);
                l.add(getRuntimeClasspath(d));
            }
            d = FileUtilities.convertURItoFileObject(project.getTestSrcDirectory());
            if (d != null) {
                logger.fine("EXECUTEgetProjectClassPaths src adding2=" + d);
                l.add(getRuntimeClasspath(d));
            }
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        
        if (ClassPath.SOURCE.equals(type)) {
            List/*<ClassPath>*/ l = new ArrayList(2);
            logger.fine("getProjectClassPaths src");
            FileObject d = FileUtilities.convertURItoFileObject(project.getSrcDirectory());
            if (d != null) {
                logger.fine("getProjectClassPaths src adding1=" + d);
                l.add(getSourcepath(d));
            }
            d = FileUtilities.convertURItoFileObject(project.getTestSrcDirectory());
            if (d != null) {
                logger.fine("getProjectClassPaths src adding2=" + d);
                l.add(getSourcepath(d));
            }
            logger.fine("getProjectClassPaths src lenght=" + l.size());
            return (ClassPath[])l.toArray(new ClassPath[l.size()]);
        }
        return new ClassPath[0];
    }
    
    public ClassPath findClassPath(FileObject file, String type) {
        logger.fine("findClassPath type =" + type);
        logger.fine("findClassPath file =" + file.getName());
        if (type.equals(ClassPath.COMPILE)) {
            return getCompileTimeClasspath(file);
        } else if (type.equals(ClassPath.EXECUTE)) {
            return getRuntimeClasspath(file);
        } else if (ClassPath.SOURCE.equals(type)) {
            return getSourcepath(file);
        } 
        else if (type.equals(ClassPath.BOOT)) {
            return getBootClassPath();
        } else {
            return null;
        }
    }

    
    private int getType(FileObject file) {
        FileObject dir = FileUtilities.convertURItoFileObject(project.getSrcDirectory());
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return TYPE_SRC;
        }
        // web app src also considered source..
        dir = FileUtilities.convertURItoFileObject(project.getWebAppDirectory());
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return TYPE_SRC;
        }
        dir = FileUtilities.convertURItoFileObject(project.getTestSrcDirectory());
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return TYPE_TESTSRC;
        }
        // cactus is also a test source..
        dir = FileUtilities.convertURItoFileObject(project.getCactusDirectory());
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return TYPE_TESTSRC;
        }
        dir = FileUtilities.convertURItoFileObject(project.getBuildClassesDir());
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return TYPE_CLASS;
        }
//        dir = getBuildJar();
//        if (dir != null && (dir.equals(file))) {     //TODO When MasterFs check also isParentOf
//            return TYPE_ARTIFACT;
//        }
        dir = FileUtilities.convertURItoFileObject(project.getTestBuildClassesDir());
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir,file))) {
            return TYPE_TESTCLASS;
        }
        return TYPE_UNKNOWN;
    }
    
    
//    private ClassPath getRunTimeClasspath(FileObject file) {
//        int type = getType(file);
//        if (type < 0 || type > 3) {
//            return null;
//        } else if (type > 1) {
//            type-=2;            //Compiled source transform into source
//        }
//        ClassPath cp = null;
//        if (cache[4+type] == null || (cp = (ClassPath)cache[4+type].get())== null) {
//            if (type == 0) {
//                cp = ClassPathFactory.createClassPath(
//                new ProjectClassPathImplementation(helper,"run.classpath")); // NOI18N
//            }
//            else if (type == 1) {
//                cp = ClassPathFactory.createClassPath(
//                new ProjectClassPathImplementation(helper,"run.test.classpath")); // NOI18N
//            }
//            cache[4+type] = new SoftReference(cp);
//        }
//        return cp;
//    }
    
    private ClassPath getSourcepath(FileObject file) {
        int type = getType(file);
        logger.fine("getSourcepath type=" + type);
        if (type != TYPE_SRC &&  type != TYPE_TESTSRC ) {
            return null;
        }
        ClassPath cp = cache[type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                logger.fine("create Sourcepath src for " + file);
                cp = ClassPathFactory.createClassPath(new SrcClassPathImpl(project));
            }
            else {
                logger.fine("create Sourcepath testsrc");
                cp = ClassPathFactory.createClassPath(new TestSrcClassPathImpl(project));
            }
            cache[type] = cp;
        }
        return cp;
    }

    private ClassPath getCompileTimeClasspath(FileObject file) {
        int type = getType(file);
        logger.fine("getCompileTimeClasspath type=" + type);
        if (type != TYPE_SRC &&  type != TYPE_TESTSRC) {
            return null;
        }
        ClassPath cp = cache[2+type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                logger.fine("create CompileTimeClasspath src");
                cp = ClassPathFactory.createClassPath(new SrcBuildClassPathImpl(project));
            }
            else {
                logger.fine("create CompileTimeClasspath testsrc");
                cp = ClassPathFactory.createClassPath(new TestSrcBuildClassPathImpl(project));
            }
            cache[2+type] = cp;
        }
        return cp;
    }
    
    private ClassPath getRuntimeClasspath(FileObject file) {
        int type = getType(file);
        logger.fine("getRuntimeTimeClasspath type=" + type);
        if (type != TYPE_SRC &&  type != TYPE_TESTSRC) {
            return null;
        }
        ClassPath cp = cache[4+type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                logger.fine("create RumtimeClasspath src");
                cp = ClassPathFactory.createClassPath(new SrcRuntimeClassPathImpl(project));
            }
            else {
                logger.fine("create RumtimeClasspath testsrc");
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

