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

package org.mevenide.netbeans.project.libraries;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;


/**
 *
 * JavadocForBinaryQueryImplementation implementation
 * for items in the maven repository.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class RepositoryJavadocForBinaryQueryImpl implements JavadocForBinaryQueryImplementation {
    private static final Log logger = LogFactory.getLog(RepositoryJavadocForBinaryQueryImpl.class);
    
    public RepositoryJavadocForBinaryQueryImpl() {
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
        logger.debug("checkurl=" + url);
        URL binRoot = url;
        if ("jar".equals(url.getProtocol())) {
            binRoot = FileUtil.getArchiveFile(url);
        } else {
            // null for directories.
            return null;
        }
        FileObject jarFO = URLMapper.findFileObject(binRoot);
        if (jarFO != null) {
            File jarFile = FileUtil.toFile(jarFO);
            //            File repoFile = new File(repo);
            if (jarFile != null) {
                try {
                    IDependencyResolver resolver = DependencyResolverFactory.getFactory().newInstance(jarFile.getAbsolutePath());
                    String version = resolver.guessVersion();
                    String artifactid = resolver.guessArtifactId();
                    String groupid = resolver.guessGroupId();
                    String ext = resolver.guessExtension();
                    // maybe refine the condition??
                    if (version != null && artifactid != null && groupid != null && ext != null && "jar".equals(ext)) {
                        File groupDir = jarFile.getParentFile().getParentFile();
                        File javadocsDir = new File(groupDir, "javadocs"); //NOI18N
                        File javadocFile = new File(javadocsDir,
                        jarFile.getName().substring(0,  jarFile.getName().length() - ext.length()) + "javadoc");
                        if (javadocFile.exists()) {
                            return new DocResult(javadocFile);
                        }
                    }
                } catch (Exception exc) {
                    logger.debug("exception", exc);
                }
            }
        }
        return null;
        
    }
    
    private class DocResult implements JavadocForBinaryQuery.Result  {
        private File file;
        private List listeners;
        
        public DocResult(File javadoc) {
            file = javadoc;
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
            if (file.exists()) {
                try {
                    URL[] url = new URL[1];
                    url[0] = FileUtil.getArchiveRoot(file.toURI().toURL());
                    return url;
                } catch (MalformedURLException exc) {
                    logger.debug("exception", exc);
                }
            }
            return new URL[0];
        }
        
    }
    
}
