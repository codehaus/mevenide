/*
 *  Copyright 2008 Anuradha.
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
package org.codehaus.mevenide.netbeans.actions.usages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha G
 */
public class FindUsagesUtil {

    private FindUsagesUtil() {
    }

    public static List<UsedGroup> findUsages(Artifact a) {

        List<UsedGroup> usedGroups = new ArrayList<UsedGroup>();
        
        //tempmaps
        Map<String,UsedGroup> groupMap= new HashMap<String,UsedGroup>(); 
        Map<String,UsedArtifact> artifactMap= new HashMap<String,UsedArtifact>(); 
        try {

            List<StandardArtifactIndexRecord> indexRecords = 
                    CustomQueries.findDependencyUsage(a.getGroupId(),
                    a.getArtifactId(), a.getVersion());
            for (StandardArtifactIndexRecord sair : indexRecords) {
                String groupId = sair.getGroupId();
                String artId = sair.getArtifactId();
                String version = sair.getVersion();
                UsedGroup ug = groupMap.get(groupId);
                if(ug==null){
                 ug=new UsedGroup(groupId);
                 usedGroups.add(ug);
                 groupMap.put(groupId, ug);
                }
                UsedArtifact ua = artifactMap.get(artId);
                if(ua==null){
                    ua=new UsedArtifact(artId);
                  ug.addUsedArtifact(ua);
                  artifactMap.put(artId, ua);
                }
                ua.addUsedVersion(new UsedVersion(groupId, artId, version));
            }
 
            
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return usedGroups;
    }
}
