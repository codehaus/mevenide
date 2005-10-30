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
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class TestSrcRuntimeClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of TestSrcRuntimeClassPathImpl */
    public TestSrcRuntimeClassPathImpl(NbMavenProject proj) {
        super(proj);
    }
    
   URI[] createPath() {
        List lst = new ArrayList();
        try {
            // TODO is it really the correct thing?
            List srcs = getMavenProject().getOriginalMavenProject().getTestClasspathElements();
            Iterator it = srcs.iterator();
            while (it.hasNext()) {
                String str = (String)it.next();
                File fil = new File(str);
                lst.add(fil.toURI());
            }
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
        }
        
        URI[] uris = new URI[lst.size()];
        uris = (URI[])lst.toArray(uris);
        return uris;
    }    
    
}
