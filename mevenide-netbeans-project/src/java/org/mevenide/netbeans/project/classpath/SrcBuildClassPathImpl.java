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

package org.mevenide.netbeans.project.classpath;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.dependency.DefaultDependencyPathFinder;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.mevenide.project.io.JarOverrideReader2;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class SrcBuildClassPathImpl extends AbstractProjectClassPathImpl {
    private static final Log logger = LogFactory.getLog(SrcBuildClassPathImpl.class);
    
    IDependencyResolver resolver;
    /** Creates a new instance of SrcClassPathImpl */
    public SrcBuildClassPathImpl(MavenProject proj) {
        super(proj);
        
    }
    
    URI[] createPath() {
        List lst = new ArrayList();
        Project mavproj = getMavenProject().getOriginalMavenProject();
        Iterator it = mavproj.getDependencies().iterator();
        
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
    
    private URI checkOneDependency(Dependency dep) {
        if (dep.getType() == null || "jar".equals(dep.getType())) {
            // check override first
            String path = JarOverrideReader2.getInstance().processOverride(dep,
            getMavenProject().getPropertyResolver(),
            getMavenProject().getLocFinder());
            if (path == null) {
                DefaultDependencyPathFinder finder = new DefaultDependencyPathFinder(dep);
                path = finder.resolve();
            }
            logger.debug("dep path=" + path);
            File file = new File(path);
            if (file.getName().endsWith(".jar")) {
                URI uri = file.toURI();
                if (uri != null) {
                    return uri;
                }
            }
        }
        return null;
    }
    
}
