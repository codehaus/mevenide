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

import org.codehaus.mevenide.indexer.api.NBArtifactInfo;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;

import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 * @author Anuradha G
 */
public class ArtifactNode extends AbstractNode {
    private RepositoryInfo info;
    /** Creates a new instance of ArtifactNode */
    public ArtifactNode(RepositoryInfo info,String id, String art) {
        super(new ArtifactChildren(info,id, art));
        this.info=info;
        setName(art);
        setDisplayName(art);
    }

    public ArtifactNode(final RepositoryInfo info,final NBArtifactInfo artifactInfo) {
        super(new Children.Keys<NBVersionInfo>() {

            @Override
            protected Node[] createNodes(NBVersionInfo arg0) {


                return new Node[]{new VersionNode(info,arg0,arg0.isJavadocExists(),
                    arg0.isSourcesExists(), arg0.getGroupId() != null)
                };
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(artifactInfo.getVersionInfos());
            }
            });
        setName(artifactInfo.getName());
        setDisplayName(artifactInfo.getName());
    }

    @Override
    public Image getIcon(int arg0) {
        Image badge = Utilities.loadImage("org/codehaus/mevenide/repository/ArtifactBadge.png", true); //NOI18N
        return badge;
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }
}
