/* ==========================================================================
 * Copyright 2006 Mevenide Team
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
package org.codehaus.mevenide.netbeans.persistence;

import org.codehaus.mevenide.netbeans.AdditionalM2LookupProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * extending the default maven project lookup.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class PersistenceLookupProvider implements AdditionalM2LookupProvider {
    
    /** Creates a new instance of J2eeLookupProvider */
    public PersistenceLookupProvider() {
    }
    
    public Lookup createMavenLookup(NbMavenProject project) {
//        // if there's more items later, just do a proxy..
        return Lookups.fixed(new Object[] {
            new MavenPersistenceProvider(project)
        });
    }
    
}
