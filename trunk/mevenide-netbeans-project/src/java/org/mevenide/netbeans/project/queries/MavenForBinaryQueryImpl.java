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

package org.mevenide.netbeans.project.queries;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.netbeans.api.java.queries.SourceForBinaryQuery.Result;
import org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;


/**
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenForBinaryQueryImpl implements SourceForBinaryQueryImplementation, 
                                                      JavadocForBinaryQueryImplementation {
    private static final Log logger = LogFactory.getLog(MavenForBinaryQueryImpl.class);
                                                          
    private MavenProject project;
    /** Creates a new instance of MavenSourceForBinaryQueryImpl */
    public MavenForBinaryQueryImpl(MavenProject proj) {
        project = proj;
    }
    /**
     * Returns the source root(s) for a given binary root.
     * <p>
     * Any absolute URL may be used but typically it will use the <code>file</code>
     * protocol for directory entries and <code>jar</code> protocol for JAR entries
     * (e.g. <samp>jar:file:/tmp/foo.jar!/</samp>).
     * </p>
     * @param binaryRoot the class path root of Java class files
     * @return a list of source roots; may be empty but not null
     */   
    public Result findSourceRoots(URL url) {
        logger.warn("MavenSourceForBinaryQueryImpl project=" + project.getDisplayName() + "url=" + url);
        return new BinResult(url);
    }
    
    /**
     * Find any Javadoc corresponding to the given classpath root containing
     * Java classes.
     * <p>
     * Any absolute URL may be used but typically it will use the <code>file</code>
     * protocol for directory entries and <code>jar</code> protocol for JAR entries
     * (e.g. <samp>jar:file:/tmp/foo.jar!/</samp>).
     * </p>
     * @param binaryRoot the class path root of Java class files
     * @return a result object encapsulating the roots and permitting changes to
     *         be listened to, or null if the binary root is not recognized
     */    
    public org.netbeans.api.java.queries.JavadocForBinaryQuery.Result findJavadoc(URL url) {
        logger.warn("JavadocForBinaryQueryImplementation project=" + project.getDisplayName() + "url=" + url);
        URL binRoot = FileUtil.getArchiveFile(url);
        if (checkURL(binRoot)) {
        }
        return null;
    }
    
    
    private boolean checkURL(URL url) {
        logger.debug("checkurl=" + url);
        FileObject fo = URLMapper.findFileObject(url);
        if (fo != null) {
            logger.debug("checkurl fo=" + fo);
            Project mavproj = project.getOriginalMavenProject();
            if ("file".equals(url.getProtocol())) {
                File file = FileUtil.toFile(fo);
                logger.debug("jar protocol file=" + file);
                try {
                    IDependencyResolver depRes = DependencyResolverFactory.getFactory().
                                                            newInstance(file.getAbsolutePath());
                    return  (doCompare(depRes.guessArtifactId(), mavproj.getArtifactId()) && 
                             doCompare(depRes.guessGroupId(), mavproj.getGroupId()) &&
                             doCompare(depRes.guessVersion(), mavproj.getCurrentVersion()));
                } catch (Exception exc) {
                    logger.error("exception", exc);
                    return false;
                }
            }
            if ("jar".equals(url.getProtocol())) {
                
            }
        } 
        return false;
    }
    
    private FileObject[] getSrcRoot() {
        logger.debug("getsrcRoot");
        URI uri = project.getSrcDirectory();
        if (uri != null) {
            try {
                logger.debug("getsrcRoot uri=" + uri);
                FileObject foRoot = URLMapper.findFileObject(uri.toURL());
                if (foRoot != null) {
                    logger.debug("returning fo=" + foRoot);
                    return new FileObject[] {foRoot};
                }
            } catch (MalformedURLException exc) {
                logger.warn("malforrmed uri=" + uri);
            }
        }
        return new FileObject[0];
    }

    private boolean doCompare(String one, String two) {
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }
    
    
    private class BinResult implements Result  {
       private URL url;
        public BinResult(URL urlParam) {
            url = urlParam;
        }
       public void addChangeListener(ChangeListener changeListener) {
           //TODO
       }
       
       public FileObject[] getRoots() {
            URL binRoot = FileUtil.getArchiveFile(url);
            if (checkURL(binRoot)) {
                return getSrcRoot();
            }
            return new FileObject[0];
       }
       
       public void removeChangeListener(ChangeListener changeListener) {
           //TODO
       }
       
   }
    
}
