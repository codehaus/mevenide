/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.queries;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.java.queries.BinaryForSourceQuery;
import org.netbeans.spi.java.queries.BinaryForSourceQueryImplementation;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class MavenBinaryForSourceQueryImpl implements BinaryForSourceQueryImplementation {
    private NbMavenProject project;
    private HashMap<URL, Res> results;
    
    /** Creates a new instance of MavenBinaryForSourceQueryImpl */
    public MavenBinaryForSourceQueryImpl(NbMavenProject prj) {
        project = prj;
        results = new HashMap<URL, Res>();
    }
    
    /**
     * Returns the binary root(s) for a given source root.
     * <p>
     * The returned BinaryForSourceQuery.Result must be a singleton. It means that for
     * repeated calling of this method with the same recognized root the method has to
     * return the same instance of BinaryForSourceQuery.Result.<br>
     * The typical implemantation of the findBinaryRoots contains 3 steps:
     * <ol>
     * <li>Look into the cache if there is already a result for the root, if so return it</li>
     * <li>Check if the sourceRoot is recognized, if not return null</li>
     * <li>Create a new BinaryForSourceQuery.Result for the sourceRoot, put it into the cache
     * and return it.</li>
     * </ol>
     * </p>
     * <p>
     * Any absolute URL may be used but typically it will use the <code>file</code>
     * protocol for directory entries and <code>jar</code> protocol for JAR entries
     * (e.g. <samp>jar:file:/tmp/foo.jar!/</samp>).
     * </p>
     * @param sourceRoot the source path root
     * @return a result object encapsulating the answer or null if the sourceRoot is not recognized
     */    
    public BinaryForSourceQuery.Result findBinaryRoots(URL url) {
        if (results.containsKey(url)) {
            return results.get(url);
        }
        if ("file".equals(url.getProtocol())) { //NOI18N
            try {
                Res toReturn = null;
                File fil = new File(url.toURI());
                fil = FileUtil.normalizeFile(fil);
                MavenProject mav = project.getOriginalMavenProject();
                String src = mav.getBuild() != null ? mav.getBuild().getSourceDirectory() : null;
                String testSrc = mav.getBuild() != null ? mav.getBuild().getTestSourceDirectory() : null;
                File srcFile = FileUtil.normalizeFile(new File(src));
                File testSrcFile = FileUtil.normalizeFile(new File(testSrc));
                if (srcFile.equals(fil) || testSrcFile.equals(fil)) {
                    toReturn = new Res(testSrcFile.equals(fil), project);
                } else {
                    URI[] gens = project.getGeneratedSourceRoots();
                    for (URI gen : gens) {
                        File genfil = new File(gen);
                        genfil = FileUtil.normalizeFile(genfil);
                        if (genfil.equals(fil)) {
                            // assume generated sources are not test..
                            toReturn = new Res(false, project);
                            break;
                        }
                    }
                }
                if (toReturn != null) {
                    results.put(url, toReturn);
                }
                return toReturn;
            }
            catch (URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }
    
    private static class Res implements BinaryForSourceQuery.Result {
        private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
        private NbMavenProject project;
        private boolean isTest;
        Res(boolean test, NbMavenProject prj) {
            isTest = test;
            project = prj;

        }
        
         /**
         * Get the binary roots.         
         * @return array of roots of compiled classes (may be empty but not null)
         */       
        public URL[] getRoots() {
            try         {
                String binary = isTest ? project.getOriginalMavenProject().getBuild().getTestOutputDirectory()
                                       : project.getOriginalMavenProject().getBuild().getOutputDirectory();
                File binFile = FileUtil.normalizeFile(new java.io.File(binary));

                return new java.net.URL[]{binFile.toURI().toURL()};
            }
            catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
            return new URL[0];
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
            List<ChangeListener> lists = new ArrayList<ChangeListener>();
            synchronized(listeners) {
                lists.addAll(listeners);
            }
            for (ChangeListener listen : lists) {
                listen.stateChanged(new ChangeEvent(this));
            }
        }
    }
    
}
