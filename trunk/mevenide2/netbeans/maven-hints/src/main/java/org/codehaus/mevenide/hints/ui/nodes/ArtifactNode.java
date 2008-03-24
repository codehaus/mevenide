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
package org.codehaus.mevenide.hints.ui.nodes;

import java.awt.Image;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class ArtifactNode extends AbstractNode {
    private List<NBVersionInfo> versionInfos;
    public ArtifactNode(String name, final List<NBVersionInfo> list) {
        super(new Children.Keys<NBVersionInfo>() {

            @Override
            protected Node[] createNodes(NBVersionInfo arg0) {


                return new Node[]{new VersionNode(arg0, arg0.isJavadocExists(),
                            arg0.isSourcesExists())
                        };
            }

            @Override
            protected void addNotify() {

                setKeys(list);
            }
        });
        this.versionInfos=list;
        setName(name);
        setDisplayName(name);
    }

    @Override
    public Image getIcon(int arg0) {
        Image badge = Utilities.loadImage("org/codehaus/mevenide/hints/ArtifactBadge.png", true); //NOI18N

        return badge;
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    public List<NBVersionInfo> getVersionInfos() {
        return new ArrayList<NBVersionInfo>(versionInfos);
    }
    
    
}
