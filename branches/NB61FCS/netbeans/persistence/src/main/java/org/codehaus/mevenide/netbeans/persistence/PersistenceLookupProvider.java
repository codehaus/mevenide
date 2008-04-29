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

import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * extending the default maven project lookup.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class PersistenceLookupProvider implements LookupProvider {
    
    /** Creates a new instance of J2eeLookupProvider */
    public PersistenceLookupProvider() {
    }
    
    /**
     * 
     * @param context 
     * @return 
     */
    public Lookup createAdditionalLookup(Lookup context) {
//        // if there's more items later, just do a proxy..
        NbMavenProject prj = context.lookup(NbMavenProject.class);
        assert prj != null;
        return Lookups.fixed(new Object[] {
            new MavenPersistenceProvider(prj),
            new RecoTemp(),
            new CPExtender(prj)
        });
    }
    
    private static class RecoTemp implements RecommendedTemplates {
        public String[] getRecommendedTypes() {
            return new String[] {
                "persistence" //NOI18N
            };
        }
    }

}
