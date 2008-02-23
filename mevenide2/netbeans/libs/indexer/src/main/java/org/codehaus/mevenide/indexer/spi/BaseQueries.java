/*
 *  Copyright 2005-2008 Mevenide Team.
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
package org.codehaus.mevenide.indexer.spi;

import java.util.List;
import java.util.Set;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;

/**
 *
 * @author Milos Kleint
 */
public interface BaseQueries {

    Set<String> getGroups(List<RepositoryInfo> repos);

    Set<String> filterGroupIds(String prefix, List<RepositoryInfo> repos);

    List<NBVersionInfo> getRecords(String groupId, String artifactId, String version, List<RepositoryInfo> repos);

    Set<String> getArtifacts(String groupId, List<RepositoryInfo> repos);

    List<NBVersionInfo> getVersions(String groupId, String artifactId, List<RepositoryInfo> repos);

    Set<String> filterPluginArtifactIds(String groupId, String prefix, List<RepositoryInfo> repos);

    Set<String> filterPluginGroupIds(String prefix, List<RepositoryInfo> repos);

    Set<String> filterArtifactIdForGroupId(String groupId, String prefix, List<RepositoryInfo> repos);

}
