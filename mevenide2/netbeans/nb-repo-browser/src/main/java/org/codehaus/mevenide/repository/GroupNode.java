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

import org.codehaus.mevenide.repository.*;
import java.awt.Image;
import java.util.Collections;

import org.codehaus.mevenide.indexer.api.NBArtifactInfo;
import org.codehaus.mevenide.indexer.api.NBGroupInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 * @author Anuradha
 */
public class GroupNode extends AbstractNode {
    private RepositoryInfo info;
    /** Creates a new instance of GroupNode */
    public GroupNode(RepositoryInfo info,String id) {
        super(new GroupChildren(info,id));
        this.info=info;
        setName(id);
        setDisplayName(id);
    }

    public GroupNode( final RepositoryInfo info,final NBGroupInfo groupInfo) {
        super(new Children.Keys<NBArtifactInfo>() {

            @Override
            protected Node[] createNodes(NBArtifactInfo arg0) {
                return new Node[]{new ArtifactNode(info,arg0)};
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
        private RepositoryInfo info;
        /** Creates a new instance of GroupListChildren */
        public GroupChildren(RepositoryInfo info,String group) {
            this.info = info;
            id = group;
        }

        protected Node[] createNodes(Object key) {
            if (GroupListChildren.LOADING == key) {
                return new Node[]{GroupListChildren.createLoadingNode()};
            }
            String artifactId = (String) key;
            return new Node[]{new ArtifactNode(info,id, artifactId)};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(Collections.singletonList(GroupListChildren.LOADING));
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {

                    setKeys(RepositoryUtil.getDefaultRepositoryIndexer().getArtifacts(info.getId(), id));

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
