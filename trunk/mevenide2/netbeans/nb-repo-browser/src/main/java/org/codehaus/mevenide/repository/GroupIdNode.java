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
package org.codehaus.mevenide.repository;

import java.awt.Image;
import java.util.Collections;

import org.codehaus.mevenide.indexer.api.NBArtifactInfo;
import org.codehaus.mevenide.indexer.api.NBGroupInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class GroupIdNode extends AbstractNode {

    /** Creates a new instance of GroupIdNode */
    public GroupIdNode(String id) {
        super(new GroupChildren(id));
        setName(id);
        setDisplayName(id);
    }

    public GroupIdNode(final NBGroupInfo groupInfo) {
        super(new Children.Keys<NBArtifactInfo>() {

            @Override
            protected Node[] createNodes(NBArtifactInfo arg0) {
                return new Node[]{new ArtifactIdNode(arg0)};
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(groupInfo.getArtifactInfos());
            }
        });
        setName(groupInfo.getName());
        setDisplayName(groupInfo.getName());
    }

    static class GroupChildren extends Children.Keys {

        private String id;

        /** Creates a new instance of GroupIdListChildren */
        public GroupChildren(String group) {
            id = group;
        }

        protected Node[] createNodes(Object key) {
            if (GroupIdListChildren.LOADING == key) {
                return new Node[]{GroupIdListChildren.createLoadingNode()};
            }
            String artifactId = (String) key;
            return new Node[]{new ArtifactIdNode(id, artifactId)};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(Collections.singletonList(GroupIdListChildren.LOADING));
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {

                    setKeys(RepositoryUtil.getDefaultRepositoryIndexer().getArtifacts("local", id));

                }
            });
        }

        @Override
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }
    }

    @Override
    public Image getIcon(int arg0) {
        return NodeUtils.getTreeFolderIcon(false);
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return NodeUtils.getTreeFolderIcon(true);
    }
}
