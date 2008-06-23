/*
 *  Copyright 2008 mkleint.
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

package org.netbeans.modules.maven.gsf;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.maven.api.FileUtilities;
import org.netbeans.maven.api.NbMavenProject;
import org.netbeans.maven.api.PluginPropertyUtils;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathFactory;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint
 */
public class CPProvider implements ClassPathProvider {
    private static final int TYPE_SRC = 0;
    private static final int TYPE_TESTSRC = 1;
    private static final int TYPE_UNKNOWN = -1;

    private Project project;
    private ClassPath[] cache = new ClassPath[3];
    private NbMavenProject mavenProject;

    public CPProvider(Project prj) {
        this.project = prj;
        mavenProject = prj.getLookup().lookup(NbMavenProject.class);
    }

    public URI getScalaDirectory(boolean test) {
        //TODO hack, should be supported somehow to read this..
        String prop = PluginPropertyUtils.getPluginProperty(project, "org.scala.tools",
                "scala-maven-plugin", //NOI18N
                "sourceDir", //NOI18N
                "compile"); //NOI18N

        prop = prop == null ? (test ? "src/test/scala" : "src/main/scala") : prop; //NOI18N

        return FileUtilities.getDirURI(project.getProjectDirectory(), prop);
    }

    public URI getGroovyDirectory(boolean test) {
        String prop = test ? "src/test/groovy" : "src/main/groovy"; //NOI18N
        return FileUtilities.getDirURI(project.getProjectDirectory(), prop);
    }

    public URI[] getSourceRoots(boolean test) {
        List<URI> uris = new ArrayList<URI>();
        uris.add(getScalaDirectory(test));
        uris.add(getGroovyDirectory(test));
        uris.addAll(Arrays.asList(mavenProject.getResources(test)));
        uris.add(mavenProject.getWebAppDirectory());
        //TODO src/main/java as well?
        return uris.toArray(new URI[0]);
    }

    private ClassPath getSourcepath(int type) {
        ClassPath cp = cache[type];
        if (cp == null) {
            if (type == TYPE_SRC) {
                cp = ClassPathFactory.createClassPath(new SourcePathImpl(mavenProject, this, false));
            } else {
                cp = ClassPathFactory.createClassPath(new SourcePathImpl(mavenProject, this, true));
            }
            cache[type] = cp;
        }
        return cp;
    }




    public ClassPath findClassPath(FileObject file, String type) {
        int fileType = getType(file);
        if (fileType != TYPE_SRC &&  fileType != TYPE_TESTSRC) {
            Logger.getLogger(CPProvider.class.getName()).log(Level.FINEST, " bad type=" + type + " for " + file); //NOI18N
            return null;
//        }
//        if (type.equals(ClassPath.COMPILE)) {
//            return getSourcepath(fileType);
//        } else if (type.equals(ClassPath.EXECUTE)) {
//            return getSourcepath(fileType);
//        } else if (ClassPath.SOURCE.equals(type)) {
//            return getSourcepath(fileType);
        } else if (ClassPath.BOOT.equals(type)) {
            return null; //what is a boot classpath?   getBootClassPath();
        } else {
            return getSourcepath(fileType);
        }
    }

    private int getType(FileObject file) {
        if (isChildOf(file, getSourceRoots(false))) {
            return TYPE_SRC;
        }
        if (isChildOf(file, getSourceRoots(true))) {
            return TYPE_TESTSRC;
        }

//        //MEVENIDE-613, #125603 need to check later than the actual java sources..
//        // sometimes the root of resources is the basedir for example that screws up
//        // test sources.
//        if (isChildOf(file, project.getResources(false))) {
//            return TYPE_SRC;
//        }
//        if (isChildOf(file, project.getResources(true))) {
//            return TYPE_TESTSRC;
//        }
        return TYPE_UNKNOWN;
    }


    ClassPath[] getProjectSourcesClassPaths(String type) {
        ClassPath[] srcs = new ClassPath[2];
        srcs[0] = getSourcepath(TYPE_SRC);
        srcs[1] = getSourcepath(TYPE_TESTSRC);
        return srcs;
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

    public static FileObject[] convertStringsToFileObjects(List<String> strings) {
        FileObject[] fos = new FileObject[strings.size()];
        int index = 0;
        Iterator<String> it = strings.iterator();
        while (it.hasNext()) {
            String str = it.next();
            File fil = new File(str);
            fil = FileUtil.normalizeFile(fil);
            fos[index] = FileUtil.toFileObject(fil);
            index++;
        }
        return fos;
    }

}
