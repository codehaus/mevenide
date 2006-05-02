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
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
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
    public SourceForBinaryQuery.Result findSourceRoots(URL url) {
        logger.debug("MavenSourceForBinaryQueryImpl project=" + project.getDisplayName() + "url=" + url);
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
    public JavadocForBinaryQuery.Result findJavadoc(URL url) {
        logger.debug("JavadocForBinaryQueryImplementation project=" + project.getDisplayName() + "url=" + url);
        return new DocResult(url);
    }
    
    
    private boolean checkURL(URL url) {
        logger.debug("checkurl=" + url);
        URL binRoot = url;
        if ("jar".equals(url.getProtocol())) {
            binRoot = FileUtil.getArchiveFile(url);
        } else {
            // true for directories.
            return true;
        }
        FileObject fo = URLMapper.findFileObject(binRoot);
        if (fo != null) {
            logger.debug("checkurl fo=" + fo);
            Project mavproj = project.getOriginalMavenProject();
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
    
    private URL[] getJavadocRoot() {
        logger.debug("getjavadocRoot");
        String destDir = project.getPropertyResolver().getResolvedValue("maven.javadoc.destdir");
        if (destDir != null) {
            File fil = new File(destDir);
            fil = FileUtil.normalizeFile(fil);
            try {
                return new URL[] {fil.toURI().toURL()};
            } catch (MalformedURLException exc) {
                logger.warn("malforrmed file uri=" + fil.toURI());
            }
        } else {
            logger.error("Cannot resolve maven.javadoc.destdir. How come?");
        }
        return new URL[0];
    }    

    private boolean doCompare(String one, String two) {
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }
    
    
    private class BinResult implements SourceForBinaryQuery.Result  {
       private URL url;
       private List listeners;
       private FileObject[] results;
        public BinResult(URL urlParam) {
            url = urlParam;
            listeners = new ArrayList();
        }
        
       
       public FileObject[] getRoots() {
            if (checkURL(url)) {
                results = getSrcRoot();
            } else {
                results = new FileObject[0];
            }
            return results;
       }
       
       public void addChangeListener(ChangeListener changeListener) {
           synchronized (listeners) {
               listeners.add(changeListener);
           }
       }
       
       public void removeChangeListener(ChangeListener changeListener) {
           synchronized (listeners) {
               listeners.remove(changeListener);
           }
       }
       
   }

    private class DocResult implements JavadocForBinaryQuery.Result  {
       private URL url;
       private URL[] results;
       private List listeners;
       
        public DocResult(URL urlParam) {
            url = urlParam;
            listeners = new ArrayList();
        }
       public void addChangeListener(ChangeListener changeListener) {
           synchronized (listeners) {
               listeners.add(changeListener);
           }
       }
       
       public void removeChangeListener(ChangeListener changeListener) {
           synchronized (listeners) {
               listeners.remove(changeListener);
           }
       }
        
        public java.net.URL[] getRoots() {
            URL binRoot = FileUtil.getArchiveFile(url);
            if (checkURL(url)) {
                results = getJavadocRoot();
            } else {
                results = new URL[0];
            }
            return results;
        }
        
   }
    
}