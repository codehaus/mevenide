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
import java.util.Arrays;
import java.util.Collection;
import org.codehaus.mevenide.netbeans.NbMavenProjectImpl;
import org.netbeans.spi.java.classpath.FilteringPathResourceImplementation;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class SourceClassPathImpl extends AbstractProjectClassPathImpl {
    
    /**
     * Creates a new instance of SourceClassPathImpl
     */
    public SourceClassPathImpl(NbMavenProjectImpl proj) {
        super(proj);
    }
    
    URI[] createPath() {
        Collection col = new ArrayList();
        col.addAll(Arrays.asList(getMavenProject().getSourceRoots(false)));
        //TODO temporary solution
        col.addAll(Arrays.asList(getMavenProject().getGeneratedSourceRoots()));
        URI webSrc = getMavenProject().getWebAppDirectory();
        if (new File(webSrc).exists()) {
            col.add(webSrc);
        }
        col.addAll(Arrays.asList(getMavenProject().getResources(false)));
        URI[] uris = new URI[col.size()];
        uris = (URI[])col.toArray(uris);
        return uris;        
    }

    @Override
    protected FilteringPathResourceImplementation getFilteringResources() {
        return null;
//        return new ExcludingResourceImpl(getMavenProject(), false);
    }
    
}
