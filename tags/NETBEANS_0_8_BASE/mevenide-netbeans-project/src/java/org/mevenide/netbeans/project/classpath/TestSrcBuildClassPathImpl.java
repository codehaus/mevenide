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

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class TestSrcBuildClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of SrcClassPathImpl */
    public TestSrcBuildClassPathImpl(MavenProject proj) {
        super(proj);
        
    }
    
   URI[] createPath() {
        List lst = new ArrayList();
        Project mavproj = getMavenProject().getOriginalMavenProject();
        lst.add(getMavenProject().getBuildClassesDir());
        URI tests = getMavenProject().getTestBuildClassesDir();
        if (tests != null) {
            lst.add(tests);
        }
        boolean junitIncluded = false;
        List deps = mavproj.getDependencies();
        if (deps != null) {
            Iterator it = deps.iterator();
            while (it.hasNext()) {
                Dependency dep = (Dependency)it.next();
                URI uri = checkOneDependency(dep);
                if (uri != null) {
                    lst.add(uri);
                    if ("junit".equals(dep.getId())) { //NOI18N
                        junitIncluded = true;
                    }
                }
            }
        }
        //Now add junit and related jars for tests..
        if (!junitIncluded) {
            String repo = getMavenProject().getLocFinder().getMavenLocalRepository();
            repo = ((repo.endsWith("\\") || repo.endsWith("/")) ? repo : (repo + File.separator));
            repo = repo + "junit/jars";
            File dir = new File(repo);
            if (dir.exists()) {
                String[] junitList = dir.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        // kind of simplification, assuming all artifacts in junit group are named
                        // junit..
                        if (name != null && name.startsWith("junit-") && name.endsWith(".jar")) {
                            return true;
                        }
                        return false;
                    }
                });
                if (junitList != null && junitList.length > 0) {
                    String latest = junitList[0];
                    // kind of simplified way of getting the latest version of junit..
                    if (junitList.length > 1) {
                        for (int i = 1; i < junitList.length; i++) {
                            if (latest.compareTo(junitList[i]) < 0) {
                                latest = junitList[i];
                            }
                        }
                    }
                    File fil = new File(dir, latest);
                    fil = FileUtil.normalizeFile(fil);
                    URI uri = fil.toURI();
                    if (uri != null) {
                        lst.add(uri);
                    }
                }
            }
        }
        URI[] uris = new URI[lst.size()];
        uris = (URI[])lst.toArray(uris);
        return uris;
    }    
    
}
