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
import java.util.logging.Logger;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;

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
            RepositoryInfo info = RepositoryPreferences.getInstance().getRepositoryInfoById(RepositoryPreferences.LOCAL_REPO_ID);
            if (info == null) {
                Logger.getLogger(LocalRepoProvider.class.getName()).fine("Local repository info cannot be found, how come?");
                return lst;
            }
                    
            List<NBVersionInfo> archs = RepositoryQueries.retrievePossibleArchetypes(info);
            if (archs == null) {
                return lst;
            }
            for (NBVersionInfo art : archs) {
               //TODO FINDout  how to get contain matadata 
               // boolean ng = artifact.getFiles().contains("META-INF/maven/archetype-metadata.xml");
                Archetype arch = ( "maven-archetype".equalsIgnoreCase(art.getPackaging())) ? //NOI18N
                                 new Archetype(true, true) : new Archetype();
                arch.setArtifactId(art.getArtifactId());
                arch.setGroupId(art.getGroupId());
                arch.setVersion(art.getVersion());
                arch.setName(art.getProjectName());
                arch.setDescription(art.getProjectDescription());
                
                lst.add(arch);
            }
       
        return lst;
    }
    
}
