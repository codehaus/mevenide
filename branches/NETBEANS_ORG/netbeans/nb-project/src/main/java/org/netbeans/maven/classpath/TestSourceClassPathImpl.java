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

package org.netbeans.maven.classpath;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.maven.NbMavenProjectImpl;
import org.netbeans.spi.java.classpath.FilteringPathResourceImplementation;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class TestSourceClassPathImpl extends AbstractProjectClassPathImpl {
    
    /**
     * Creates a new instance of TestSourceClassPathImpl
     */
    public TestSourceClassPathImpl(NbMavenProjectImpl proj) {
        super(proj);
    }
    
    URI[] createPath() {
        Collection col = new ArrayList();
        col.addAll(Arrays.asList(getMavenProject().getSourceRoots(true)));
        col.addAll(Arrays.asList(getMavenProject().getResources(true)));
        
        URI[] uris = new URI[col.size()];
        uris = (URI[])col.toArray(uris);
        return uris;        
    }
    
    @Override
    protected FilteringPathResourceImplementation getFilteringResources() {
        return null;
//        return new ExcludingResourceImpl(getMavenProject(), true);
    }
    
    
}
