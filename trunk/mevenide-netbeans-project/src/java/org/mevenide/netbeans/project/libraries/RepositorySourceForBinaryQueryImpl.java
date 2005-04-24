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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/**
 *
 * SourceForBinaryQueryImplementation implementation
 * for items in the maven repository. It checks the artifact and
 * looks for the same artifact but of type "src.jar".
 * 
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RepositorySourceForBinaryQueryImpl implements SourceForBinaryQueryImplementation {
    
    /** Creates a new instance of RepositorySourceForBinaryQueryImpl */
    public RepositorySourceForBinaryQueryImpl() {
    }

    public SourceForBinaryQuery.Result findSourceRoots(URL url) {
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
                    if (version != null && artifactid != null && groupid != null 
                        && ext != null && "jar".equals(ext)) {
                        File groupDir = jarFile.getParentFile().getParentFile();
                        // new way.. type is javadoc.jar
                        File srcsDir = new File(groupDir, "src.jars"); //NOI18N
                        File srcFile = new File(srcsDir,
                                jarFile.getName().substring(0,  
                                   jarFile.getName().length() - ext.length()) 
                                + "src.jar");
                        if (srcFile.exists()) {
                            return new SrcResult(srcFile);
                        }
                    }
                } catch (Exception exc) {
                    
                }
            }
        }
        return null;
                
    }
    
    private class SrcResult implements SourceForBinaryQuery.Result  {
        private File file;
        private List listeners;
        
        public SrcResult(File src) {
            file = src;
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
        
        public FileObject[] getRoots() {
            if (file.exists()) {
                FileObject[] fos = new FileObject[1];
                fos[0] = FileUtil.getArchiveRoot(FileUtil.toFileObject(file));
                return fos;
            }
            return new FileObject[0];
        }
        
    }    
    
}
