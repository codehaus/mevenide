/* ==========================================================================
 * Copyright 2006 Mevenide Team
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


package org.codehaus.mevenide.netbeans.debug;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 * various debugger related utility methods.
 * @author mkleint
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    static MethodBreakpoint createBreakpoint(String stopClassName) {
        MethodBreakpoint breakpoint = MethodBreakpoint.create(
                stopClassName,
                "*"
                );
        breakpoint.setHidden(true);
        DebuggerManager.getDebuggerManager().addBreakpoint(breakpoint);
        return breakpoint;
    }
    
    public static File[] convertStringsToNormalizedFiles(List strings) {
        File[] fos = new File[strings.size()];
        int index = 0;
        Iterator it = strings.iterator();
        while (it.hasNext()) {
            String str = (String)it.next();
            File fil = new File(str);
            fil = FileUtil.normalizeFile(fil);
            fos[index] = fil;
            index++;
        }
        return fos;
    }
    
    
    static ClassPath createSourcePath(Project proj, MavenProject mproject) {
        File[] roots;
        ClassPath cp;
        try {
            //TODO this ought to be really configurable based on what class gets debugged.
            roots = convertStringsToNormalizedFiles(mproject.getTestClasspathElements());
            cp = convertToSourcePath(roots);
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
            cp = ClassPathSupport.createClassPath(new FileObject[0]);
        }
        
        roots = convertStringsToNormalizedFiles(mproject.getTestCompileSourceRoots());
        ClassPath sp = convertToClassPath(roots);
        
        ClassPath sourcePath = ClassPathSupport.createProxyClassPath(
                new ClassPath[] {cp, sp}
        );
        return sourcePath;
    }
    
    static ClassPath createJDKSourcePath(Project nbproject) {
        //TODO for now just assume the default platform...
        JavaPlatform jp = JavaPlatform.getDefault();
        if (jp != null) {
            return jp.getSourceFolders();
        } else {
            return ClassPathSupport.createClassPath(Collections.EMPTY_LIST);
        }
    }
    
    private static ClassPath convertToClassPath(File[] roots) {
        List l = new ArrayList();
        for (int i = 0; i < roots.length; i++) {
            URL url = Utils.fileToURL(roots[i]);
            l.add(url);
        }
        URL[] urls = (URL[]) l.toArray(new URL[l.size()]);
        return ClassPathSupport.createClassPath(urls);
    }
    
    /**
     * This method uses SourceForBinaryQuery to find sources for each
     * path item and returns them as ClassPath instance. All path items for which
     * the sources were not found are omitted.
     *
     */
    private static ClassPath convertToSourcePath(File[] fs)  {
        List lst = new ArrayList();
        Set existingSrc = new HashSet();
        for (int i = 0; i < fs.length; i++) {
            URL url = Utils.fileToURL(fs[i]);
            try {
                FileObject[] srcfos = SourceForBinaryQuery.findSourceRoots(url).getRoots();
                for (int j = 0; j < srcfos.length; j++) {
                    if (FileUtil.isArchiveFile(srcfos[j])) {
                        srcfos [j] = FileUtil.getArchiveRoot(srcfos [j]);
                    }
                    try {
                        url = srcfos[j].getURL();
                        if  (!url.toExternalForm().endsWith("/")) {
                            url = new URL(url.toExternalForm() + "/");
                        }
                    } catch (FileStateInvalidException ex) {
                        ErrorManager.getDefault().notify
                                (ErrorManager.EXCEPTION, ex);
                        continue;
                    } catch (MalformedURLException ex) {
                        ErrorManager.getDefault().notify
                                (ErrorManager.EXCEPTION, ex);
                        continue;
                    }
                    if (url == null)  {
                        continue;
                    }
                    if (!existingSrc.contains(url)) {
                        lst.add(ClassPathSupport.createResource(url));
                        existingSrc.add(url);
                    }
                } // for
            } catch (IllegalArgumentException ex) {
                //TODO??
            }
        }
        return ClassPathSupport.createClassPath(lst);
    }
    
    
    
    static URL fileToURL(File file) {
        try {
            URL url;
            url = file.toURI().toURL();
            if (FileUtil.isArchiveFile(url)) {
                url = FileUtil.getArchiveRoot(url);
            }
            if  (!url.toExternalForm().endsWith("/")) {
                url = new URL(url.toExternalForm() + "/");
            }
            return url;
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            return null;
        }
    }
    
}
