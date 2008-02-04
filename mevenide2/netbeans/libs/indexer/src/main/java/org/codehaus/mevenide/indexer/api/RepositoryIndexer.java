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

    void indexRepo(String repoId);//default repo id for local repo is RepositoryPreferences.LOCAL_REPO_ID
    void indexRepo(String repoId, File repoDir, File indexDir);
    void updateIndexWithArtifacts(String repoId, Collection<Artifact> artifacts);

    void deleteArtifactFromIndex(String repoId, Artifact artifact);

    Set<java.lang.String> getGroups(String... repoId);

    Set<String> filterGroupIds(String prefix, String... repoIds);

    List<NBVersionInfo> getRecords(String repoId, String groupId, String artifactId, String version);

    Set<String> getArtifacts(String groupId, String... repoId);

    List<NBVersionInfo> getVersions(String groupId, String artifactId, String... repoIds);

    List<NBGroupInfo> findDependencyUsage(String groupId, String artifactId, String version, String... repoIds);

    List<NBVersionInfo> findByMD5(File file, String... repoIds);

    List<NBVersionInfo> findByMD5(String md5, String... repoIds);

    List<NBVersionInfo> retrievePossibleArchetypes(String... repoIds);

    Set<String> filterPluginArtifactIds(String groupId, String prefix, String... repoIds);

    Set<String> filterPluginGroupIds(String prefix, String... repoIds);

    Set<String> filterArtifactIdForGroupId(String groupId, String prefix, String... repoIds);

    void addIndexChangeListener(ChangeListener cl);

    void removeIndexChangeListener(ChangeListener cl);
}
