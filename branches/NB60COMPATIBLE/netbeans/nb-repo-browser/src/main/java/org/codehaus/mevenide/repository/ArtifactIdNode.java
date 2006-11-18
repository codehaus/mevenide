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

import java.io.IOException;
import java.util.Collections;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class ArtifactIdNode extends AbstractNode {
    
    /** Creates a new instance of ArtifactIdNode */
    public ArtifactIdNode(String id, String art) {
        super(new ArtifactChildren(id, art));
        setName(art);
        setDisplayName(art);
    }
    
    
    static class ArtifactChildren extends Children.Keys {
        private String groupId;
        private String artifactId;
        /** Creates a new instance of GroupIdListChildren */
        public ArtifactChildren(String group, String artifact) {
            groupId = group;
            artifactId = artifact;
        }
        
        protected Node[] createNodes(Object key) {
            if (GroupIdListChildren.LOADING == key) {
                return new Node[] { GroupIdListChildren.createLoadingNode() };
            }
            DefaultArtifactVersion version = (DefaultArtifactVersion)key;
            return new Node[] { new VersionNode(groupId, artifactId, version) };
        }
        
        protected void addNotify() {
            super.addNotify();
            setKeys(Collections.singletonList(GroupIdListChildren.LOADING));
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        setKeys(CustomQueries.getVersions(groupId, artifactId));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        setKeys(Collections.EMPTY_LIST);
                    }
                }
            });
        }
        
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }
        
    }
}