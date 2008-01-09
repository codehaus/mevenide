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
import org.codehaus.mevenide.indexer.ArtifactInfo;
import org.codehaus.mevenide.indexer.VersionInfo;
import org.codehaus.mevenide.repository.search.SearchResultChildren;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class ArtifactIdNode extends AbstractNode {
    
    /** Creates a new instance of ArtifactIdNode */
    public ArtifactIdNode(String id, String art) {
        super(new SearchResultChildren(id, art));
        setName(art);
        setDisplayName(art);
    }

    public ArtifactIdNode(final ArtifactInfo artifactInfo) {
        super(new Children.Keys<VersionInfo>() {

                @Override
                protected Node[] createNodes(VersionInfo arg0) {
                   
        
                    return new Node[]{new VersionNode(arg0, 
                            "javadoc".equals(arg0.getClassifier()), 
                            "sources".equals(arg0.getClassifier()), arg0.getGroupId() != null)};
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
        return Utilities.mergeImages(super.getIcon(arg0), badge, 0, 0);
}

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }
    
}