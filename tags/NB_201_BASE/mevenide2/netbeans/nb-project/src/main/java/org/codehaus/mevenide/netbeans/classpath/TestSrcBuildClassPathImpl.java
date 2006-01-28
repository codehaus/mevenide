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
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class TestSrcBuildClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of SrcClassPathImpl */
    public TestSrcBuildClassPathImpl(NbMavenProject proj) {
        super(proj);
        
    }
    
   URI[] createPath() {
        List lst = new ArrayList();
        //TODO we shall add the test class output as well. how?
        try {
            List srcs = getMavenProject().getOriginalMavenProject().getTestClasspathElements();
            Iterator it = srcs.iterator();
            List assemblies = new ArrayList();
            while (it.hasNext()) {
                String str = (String)it.next();
                File fil = FileUtil.normalizeFile(new File(str));
                // the assemblied jars go as last ones, otherwise source for binaries don't really work.
                // unless one has the assembled source jar s well?? is it possible?
                if (fil.getName().endsWith("-dep.jar")) {
                    assemblies.add(0, fil);
                } else {
                    lst.add(fil.toURI());
                }
            }
            it = assemblies.iterator();
            while (it.hasNext()) {
                File ass = (File)it.next();
                lst.add(ass.toURI());
            }
            File fil = new File(getMavenProject().getOriginalMavenProject().getBuild().getOutputDirectory());
            fil = FileUtil.normalizeFile(fil);
            lst.add(0, fil.toURI());
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
        }
        URI[] uris = new URI[lst.size()];
        uris = (URI[])lst.toArray(uris);
        return uris;
    }    
    
}
