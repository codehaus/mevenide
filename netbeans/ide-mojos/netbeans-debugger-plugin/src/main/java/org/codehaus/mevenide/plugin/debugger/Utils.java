/*
 * Utils.java
 *
 * Created on March 26, 2006, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.plugin.debugger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
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
 *
 * @author mkleint
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    static ClassPath createSourcePath(Project proj, MavenProject mproject) {
        FileObject[] roots;
        ClassPath cp;
        try {
            roots = FileUtilities.convertStringsToFileObjects(mproject.getRuntimeClasspathElements());
            cp = convertToSourcePath(roots);
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
            cp = ClassPathSupport.createClassPath(new FileObject[0]);
        }
        
        roots = FileUtilities.convertStringsToFileObjects(mproject.getCompileSourceRoots());
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
    
    private static ClassPath convertToClassPath(FileObject[] roots) {
        List l = new ArrayList();
        for (int i = 0; i < roots.length; i++) {
            URL url = fileToURL(FileUtil.toFile(roots[i]));
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
    private static ClassPath convertToSourcePath(FileObject[] fos) {
        List lst = new ArrayList();
        Set existingSrc = new HashSet();
        for (int i = 0; i < fos.length; i++) {
            URL url = fileToURL(FileUtil.toFile(fos[i]));
            try {
                FileObject[] srcfos = SourceForBinaryQuery.findSourceRoots(url).getRoots();
                for (int j = 0; j < srcfos.length; j++) {
                    if (FileUtil.isArchiveFile(srcfos[j]))
                        srcfos [j] = FileUtil.getArchiveRoot(srcfos [j]);
                    try {
                        url = srcfos[j].getURL();
                    } catch (FileStateInvalidException ex) {
                        ErrorManager.getDefault().notify
                                (ErrorManager.EXCEPTION, ex);
                        continue;
                    }
                    if (url == null) continue;
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
    
    
    private static URL fileToURL(File file) {
        try {
            FileObject fileObject = FileUtil.toFileObject(file);
            if (fileObject == null) return null;
            if (FileUtil.isArchiveFile(fileObject))
                fileObject = FileUtil.getArchiveRoot(fileObject);
            return fileObject.getURL();
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
            return null;
        }
    }
    
}
