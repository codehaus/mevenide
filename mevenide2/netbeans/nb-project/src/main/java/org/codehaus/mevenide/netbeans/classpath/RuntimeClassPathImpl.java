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

package org.codehaus.mevenide.netbeans.classpath;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileUtil;

/**
 * class path def for runtime..
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RuntimeClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of SrcClassPathImpl */
    public RuntimeClassPathImpl(NbMavenProject proj) {
        super(proj);
        
    }
    
    URI[] createPath() {
        List lst = new ArrayList();
        MavenProject prj = getMavenProject().getOriginalMavenProject();
        if (prj != null && prj.getBuild() != null) {
            File fil = new File(prj.getBuild().getOutputDirectory());
            fil = FileUtil.normalizeFile(fil);
            lst.add(fil.toURI());
        }
        try {
            List srcs = getMavenProject().getOriginalMavenProject().getRuntimeClasspathElements();
            Iterator it = srcs.iterator();
            while (it.hasNext()) {
                String str = (String)it.next();
                File fil = FileUtil.normalizeFile(new File(str));
                lst.add(fil.toURI());
            }
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
        }
        URI[] res = getMavenProject().getResources(false);
        for (URI resource : res) {
            lst.add(resource);
        }
        URI[] uris = new URI[lst.size()];
        uris = (URI[])lst.toArray(uris);
        return uris;
    }
    
}
