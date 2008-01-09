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

import org.codehaus.mevenide.indexer.GroupInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.IndexerUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha G
 */
public class FindUsagesUtil {

    private FindUsagesUtil() {
    }

    public static List<GroupInfo> findUsages(Artifact a) {

        List<GroupInfo> usedGroups = new ArrayList<GroupInfo>();

        try {

            List<StandardArtifactIndexRecord> indexRecords = 
                    CustomQueries.findDependencyUsage(a.getGroupId(),
                    a.getArtifactId(), a.getVersion());
           usedGroups.addAll(IndexerUtil.convertToInfos(indexRecords));
 
            
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return usedGroups;
    }
}
