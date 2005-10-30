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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class TestSrcClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of TestSrcClassPathImpl */
    public TestSrcClassPathImpl(NbMavenProject proj) {
        super(proj);
    }
    
    URI[] createPath() {
        Collection col = new ArrayList();
        List srcs = getMavenProject().getOriginalMavenProject().getTestCompileSourceRoots();
        Iterator it = srcs.iterator();
        while (it.hasNext()) {
            String str = (String)it.next();
            File fil = new File(str);
            col.add(fil.toURI());
        }
        URI[] uris = new URI[col.size()];
        uris = (URI[])col.toArray(uris);
        return uris;        
    }
    
}
