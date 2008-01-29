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
package org.codehaus.mevenide.indexer.api;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.apache.maven.artifact.Artifact;

/**
 *
 * @author Anuradha G
 */
public interface RepositoryIndexer {

    void indexRepo(String repoId);//default repo id for local repo is "local"
    void indexRepo(String repoId, File repoDir, File indexDir);
    void updateIndexWithArtifacts(String repoId, Collection<Artifact> artifacts);

    void deleteArtifactFromIndex(String repoId, Artifact artifact);

    Set<java.lang.String> getGroups(String repoId);

    Set<String> filterGroupIds(String repoId, String prefix);

    List<NBVersionInfo> getRecords(String repoId, String groupId, String artifactId, String version);

    Set<String> getArtifacts(String repoId, String groupId);

    List<NBVersionInfo> getVersions(String repoId, String groupId, String artifactId);

    List<NBGroupInfo> findDependencyUsage(String repoId, String groupId, String artifactId, String version);

    List<NBVersionInfo> findByMD5(String repoId, File file);

    List<NBVersionInfo> findByMD5(String repoId, String md5);

    List<NBVersionInfo> retrievePossibleArchetypes(String repoId);

    Set<String> filterPluginArtifactIds(String repoId, String groupId, String prefix);

    Set<String> filterPluginGroupIds(String repoId, String prefix);

    Set<String> filterArtifactIdForGroupId(String repoId, String groupId, String prefix);

    void addIndexChangeListener(ChangeListener cl);

    void removeIndexChangeListener(ChangeListener cl);
}
