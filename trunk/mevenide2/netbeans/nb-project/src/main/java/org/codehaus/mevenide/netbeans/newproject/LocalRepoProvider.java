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

package org.codehaus.mevenide.netbeans.newproject;

import org.codehaus.mevenide.netbeans.api.archetype.Archetype;
import org.codehaus.mevenide.netbeans.api.archetype.ArchetypeProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.indexer.CustomQueries;

/**
 * Lists archetypes found in local repository index. Will include both old archetypes
 * and archetypeng ones.
 * @author mkleint
 */
public class LocalRepoProvider implements ArchetypeProvider {
    
    /** Creates a new instance of LocalRepoProvider */
    public LocalRepoProvider() {
    }

    public List<Archetype> getArchetypes() {
        List<Archetype> lst = new ArrayList<Archetype>();
        try {
            List<StandardArtifactIndexRecord> archs = CustomQueries.retrievePossibleArchetypes();
            if (archs == null) {
                return lst;
            }
            for (StandardArtifactIndexRecord art : archs) {
                boolean ng = art.getFiles().contains("META-INF/maven/archetype-metadata.xml");
                Archetype arch = (ng || "maven-archetype".equalsIgnoreCase(art.getPackaging())) ? //NOI18N
                                 new Archetype(true, true) : new Archetype();
                arch.setArtifactId(art.getArtifactId());
                arch.setGroupId(art.getGroupId());
                arch.setVersion(art.getVersion());
                arch.setName(art.getProjectName());
                arch.setDescription(art.getProjectDescription());
                
                lst.add(arch);
            }
        } catch (RepositoryIndexSearchException ex) {
            ex.printStackTrace();
        }
        return lst;
    }
    
}
