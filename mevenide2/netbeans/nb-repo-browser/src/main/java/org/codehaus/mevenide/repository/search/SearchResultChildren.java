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
package org.codehaus.mevenide.repository.search;

import java.util.Collections;

import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.codehaus.mevenide.repository.GroupIdListChildren;
import org.codehaus.mevenide.repository.VersionNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class SearchResultChildren extends Children.Keys {

    private String artifactId;
    private String groupId;

    /**
     * creates a new instance of SearchResultChildren from browsing interface
     */
    public SearchResultChildren(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    protected Node[] createNodes(Object key) {
        if (GroupIdListChildren.LOADING == key) {
            return new Node[]{GroupIdListChildren.createLoadingNode()};
        }
        NBVersionInfo record = (NBVersionInfo) key;

        boolean hasSources = record.isSourcesExists();
        boolean hasJavadoc = record.isJavadocExists();

        return new Node[]{new VersionNode(record, hasJavadoc, hasSources, groupId != null)};
    }

    @Override
    protected void addNotify() {
        super.addNotify();

        setKeys(Collections.singletonList(GroupIdListChildren.LOADING));
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {

                setKeys(RepositoryUtil.getDefaultRepositoryIndexer().getVersions(RepositoryPreferences.LOCAL_REPO_ID, groupId, artifactId));

            }
        });

    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.EMPTY_LIST);
    }
}
