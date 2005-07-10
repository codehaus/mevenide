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

package org.mevenide.netbeans.project.queries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.mevenide.properties.IPropertyResolver;
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
    private BinResult srcResult;
    private BinResult jarResult;
    private BinResult testResult;
    /** Creates a new instance of MavenSourceForBinaryQueryImpl */
    public MavenForBinaryQueryImpl(MavenProject proj) {
        project = proj;
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (srcResult != null) {
                    srcResult.fireChanged();
                }
                if (jarResult != null) {
                    jarResult.fireChanged();
                }
                if (testResult != null) {
                    testResult.fireChanged();
                }
            }
        });
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
        if (url.getProtocol().equals("jar") && checkURL(url) != -1) { //NOI18N
            if (jarResult == null) {
                logger.debug("MavenSourceForBinaryQueryImpl project=" + project.getDisplayName() + "url=" + url);
                jarResult = new BinResult(url);
            }
            return jarResult;
        }
        if (url.getProtocol().equals("file")) { //NOI18N
            int result = checkURL(url);
            if (result == 1) {
                if (testResult == null) {
                    testResult = new BinResult(url);
                }
                return testResult;
            }
            if (result == 0) {
                if (srcResult == null) {
                    srcResult = new BinResult(url);
                }
                return srcResult;
            }
        }
        return null;
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
        if (checkURL(url) != -1) {
            logger.debug("JavadocForBinaryQueryImplementation project=" + project.getDisplayName() + "url=" + url);
            return new DocResult(url);
        }
        return null;
    }
    
    /**
     * -1 - not found
     * 0 - source
     * 1 - test source
     */
    private int checkURL(URL url) {
        logger.debug("checkurl=" + url);
        if ("file".equals(url.getProtocol())) {
            // true for directories.
            try {
                URL srcUrl = project.getBuildClassesDir().toURL();
//                System.out.println("  check1=" + srcUrl);
//                System.out.println("  check1 uri=" + project.getBuildClassesDir());
                if (url.equals(srcUrl)) {
                    return 0;
                }
                URI uri = project.getTestBuildClassesDir();
                if (uri != null) {
                    // can be null in some obscrure cases.
                    URL testsrcUrl = uri.toURL();
                    if (url.equals(testsrcUrl)) {
                        return 1;
                    }
                }
            } catch (MalformedURLException exc) {
                logger.warn("exception maplformed url.", exc);
            }
            return -1;
        }
        if ("jar".equals(url.getProtocol())) {
            URL binRoot = FileUtil.getArchiveFile(url);
            FileObject fo = URLMapper.findFileObject(binRoot);
            if (fo != null) {
                logger.debug("checkurl fo=" + fo);
                Project mavproj = project.getOriginalMavenProject();
                File file = FileUtil.toFile(fo);
                try {
                    IDependencyResolver depRes = DependencyResolverFactory.getFactory().
                            newInstance(file.getAbsolutePath());
                    IPropertyResolver res = project.getPropertyResolver();
					// #MEVENIDE-287 SNAPSHOT handled in a special way
                    boolean found =  (doCompare(depRes.guessArtifactId(), res.resolveString(mavproj.getArtifactId())) &&
                            doCompare(depRes.guessGroupId(), res.resolveString(mavproj.getGroupId())) &&
                            ("SNAPSHOT".equals(depRes.guessVersion()) || doCompare(depRes.guessVersion(), res.resolveString(mavproj.getCurrentVersion()))));
                    return found ? 0 : -1;
                } catch (Exception exc) {
                    logger.error("exception", exc);
                    return -1;
                }
            }
        }
        return -1;
    }
    
    private FileObject[] getSrcRoot() {
        logger.debug("getsrcRoot");
        Collection toReturn = new ArrayList();
        Collection uris = new ArrayList();
        URI uri = project.getGeneratedSourcesDir();
        if (uri != null) {
            uris.add(uri);
        }
        uri = project.getSrcDirectory();
        if (uri != null) {
            uris.add(uri);
        }
        Collection adds = project.getAdditionalGeneratedSourceDirs();
        if (adds != null) {
            uris.addAll(adds);
        }
        Iterator it = uris.iterator();
        while (it.hasNext()) {
            uri = (URI)it.next();
            try {
                FileObject fo = URLMapper.findFileObject(uri.toURL());
                if (fo != null) {
                    toReturn.add(fo);
                }
            } catch (MalformedURLException exc) {
                logger.warn("malforrmed uri=" + uri);
            }
        }
        FileObject[] fos = new FileObject[toReturn.size()];
        fos = (FileObject[])toReturn.toArray(fos);
        return fos;
    }
    
    private FileObject[] getTestSrcRoot() {
        logger.debug("gettestsrcRoot");
        URI uri = project.getTestSrcDirectory();
        if (uri != null) {
            try {
                logger.debug("gettestsrcRoot uri=" + uri);
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
        return one.trim().equals(two.trim());
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
           int xxx = checkURL(url);
            if (xxx == 0) {
                results = getSrcRoot();
            } else if (xxx == 1) {
                results = getTestSrcRoot();
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
       
       void fireChanged() {
           List lists = new ArrayList();
           synchronized(listeners) {
               lists.addAll(listeners);
           }
           Iterator it = lists.iterator();
           while (it.hasNext()) {
               ChangeListener listen = (ChangeListener)it.next();
               listen.stateChanged(new ChangeEvent(this));
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
       
       void fireChanged() {
           List lists = new ArrayList();
           synchronized(listeners) {
               lists.addAll(listeners);
           }
           Iterator it = lists.iterator();
           while (it.hasNext()) {
               ChangeListener listen = (ChangeListener)it.next();
               listen.stateChanged(new ChangeEvent(this));
           }
       }
        
        public java.net.URL[] getRoots() {
            if (checkURL(url) != -1) {
                results = getJavadocRoot();
            } else {
                results = new URL[0];
            }
            return results;
        }
        
   }
    
}
