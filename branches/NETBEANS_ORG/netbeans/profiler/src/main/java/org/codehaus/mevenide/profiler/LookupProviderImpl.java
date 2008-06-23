/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.profiler;

import org.codehaus.mevenide.netbeans.api.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.LookupProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author mkleint
 */
public class LookupProviderImpl implements LookupProvider {

    public Lookup createAdditionalLookup(Lookup baseContext) {
        
        NbMavenProject nbprj = baseContext.lookup(NbMavenProject.class);
        Project prj = baseContext.lookup(Project.class);
        assert prj != null;
        assert nbprj != null;
        
        return Lookups.fixed(new RunCheckerImpl(prj));
    }

}
