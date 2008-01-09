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
package org.codehaus.mevenide.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class IndexerUtil {

    public static List<GroupInfo> convertToInfos(List<StandardArtifactIndexRecord> indexRecords) {
        List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();

        //tempmaps
        Map<String, GroupInfo> groupMap = new HashMap<String, GroupInfo>();
        Map<String, ArtifactInfo> artifactMap = new HashMap<String, ArtifactInfo>();
        for (StandardArtifactIndexRecord sair : indexRecords) {
            String groupId = sair.getGroupId();
            String artId = sair.getArtifactId();
            String version = sair.getVersion();
            String type = sair.getType();
            GroupInfo ug = groupMap.get(groupId);
            if (ug == null) {
                ug = new GroupInfo(groupId);
                groupInfos.add(ug);
                groupMap.put(groupId, ug);
            }
            ArtifactInfo ua = artifactMap.get(artId);
            if (ua == null) {
                ua = new ArtifactInfo(artId);
                ug.addArtifactInfo(ua);
                artifactMap.put(artId, ua);
            }
            ua.addVersionInfo(new VersionInfo(groupId, artId, version, type));
        }
        return groupInfos;

    }
}
