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
package org.netbeans.modules.maven.newproject;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.indexer.api.RepositoryQueries;
import org.netbeans.modules.maven.api.archetype.Archetype;
import org.netbeans.modules.maven.api.archetype.ArchetypeProvider;

/**
 *
 * @author mkleint
 */
public class RemoteRepoProvider implements ArchetypeProvider {

    public List<Archetype> getArchetypes() {
        List<Archetype> lst = new ArrayList<Archetype>();
        List<RepositoryInfo> infos = RepositoryPreferences.getInstance().getRepositoryInfos();
        for (RepositoryInfo info : infos) {
            if (RepositoryPreferences.LOCAL_REPO_ID.equals(info.getId())) {
                continue;
            }
            List<NBVersionInfo> archs = RepositoryQueries.findArchetypes(info);
            if (archs == null) {
                continue;
            }
            for (NBVersionInfo art : archs) {
                Archetype arch = new Archetype();
                arch.setArtifactId(art.getArtifactId());
                arch.setGroupId(art.getGroupId());
                arch.setVersion(art.getVersion());
                arch.setName(art.getProjectName());
                arch.setDescription(art.getProjectDescription());
                arch.setRepository(info.getRepositoryUrl());
                lst.add(arch);
            }
        }
        return lst;
    }
}
