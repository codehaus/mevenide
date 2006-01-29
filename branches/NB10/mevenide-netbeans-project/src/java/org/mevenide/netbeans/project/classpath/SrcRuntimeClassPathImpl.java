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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.netbeans.api.project.MavenProject;


/**
 * class path def for runtime..
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class SrcRuntimeClassPathImpl extends AbstractProjectClassPathImpl {
    private static final Log logger = LogFactory.getLog(SrcBuildClassPathImpl.class);
    
//    IDependencyResolver resolver;
    /** Creates a new instance of SrcClassPathImpl */
    public SrcRuntimeClassPathImpl(MavenProject proj) {
        super(proj);
        
    }
    
    URI[] createPath() {
        List lst = new ArrayList();
        Project mavproj = getMavenProject().getOriginalMavenProject();
        lst.add(getMavenProject().getBuildClassesDir());
        
        // add resources to runtime path..
        Build build = mavproj.getBuild();
        if (build != null) {
            List resources = build.getResources();
            Iterator it = (resources != null ? resources.iterator() : Collections.EMPTY_LIST.iterator());
            while (it.hasNext()) {
                Resource res = (Resource)it.next();
                URI uri = checkOneResource(res);                    
                if (uri != null) {
                    lst.add(uri);
                }
            }
        }
        List deps = mavproj.getDependencies();
        Iterator it = (deps != null ? deps.iterator() : Collections.EMPTY_LIST.iterator());
        while (it.hasNext()) {
            Dependency dep = (Dependency)it.next();
            URI ur = checkOneDependency(dep);
            if (ur != null) {
                lst.add(ur);
            }
        }
        //        if (getMavenProject().getSrcDirectory() != null) {
        //            lst.add(getMavenProject().getSrcDirectory());
        //        }
        URI[] uris = new URI[lst.size()];
        uris = (URI[])lst.toArray(uris);
        return uris;
    }
    
}
