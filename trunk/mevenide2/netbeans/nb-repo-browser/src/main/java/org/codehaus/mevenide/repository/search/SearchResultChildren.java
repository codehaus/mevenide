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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.indexer.CustomQueries;
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
    
    private List<StandardArtifactIndexRecord> keys;
    
    private ArrayList<StandardArtifactIndexRecord> mainkeys;
    
    private ArrayList<StandardArtifactIndexRecord> attachedkeys;
    
    private String artifactId;
    private String groupId;
    
    /**
     * Creates a new instance of SearchResultChildren from search results.
     */
    public SearchResultChildren(List<StandardArtifactIndexRecord> results) {
        keys = results;
    }
    
    /**
     * creates a new instance of SearchResultChildren from browsing interface
     */
    public SearchResultChildren(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }
    
    protected Node[] createNodes(Object key) {
        if (GroupIdListChildren.LOADING == key) {
                return new Node[] { GroupIdListChildren.createLoadingNode() };
        }
        StandardArtifactIndexRecord record = (StandardArtifactIndexRecord)key;
        Iterator<StandardArtifactIndexRecord> it = attachedkeys.iterator();
        boolean hasSources = false;
        boolean hasJavadoc = false;
        while (it.hasNext() && (!hasJavadoc || !hasSources)) {
            StandardArtifactIndexRecord elem = it.next();
            if (elem.getGroupId().equals(record.getGroupId()) &&
                    elem.getArtifactId().equals(record.getArtifactId()) &&
                    elem.getVersion().equals(record.getVersion())) {
                hasSources = hasSources || "sources".equals(elem.getClassifier());
                hasJavadoc = hasJavadoc || "javadoc".equals(elem.getClassifier());
            }
        }
        return new Node[] { new VersionNode(record, hasJavadoc, hasSources, groupId != null) };
    }
    
    
    protected void addNotify() {
        super.addNotify();
        if (keys == null) {
            setKeys(Collections.singletonList(GroupIdListChildren.LOADING));
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        sortOutKeys(CustomQueries.getVersions(groupId, artifactId));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        setKeys(Collections.EMPTY_LIST);
                    }
                }
            });
        } else {
            sortOutKeys(keys);
        }
    }
    private void sortOutKeys(List<StandardArtifactIndexRecord> keys) {
        mainkeys = new ArrayList<StandardArtifactIndexRecord>(keys.size());
        attachedkeys = new ArrayList<StandardArtifactIndexRecord>(keys.size());
        for (StandardArtifactIndexRecord record : keys) {
            if (record.getClassifier() != null && (record.getClassifier().equals("javadoc")
                    || record.getClassifier().equals("sources"))) {
                attachedkeys.add(record);
            } else {
                mainkeys.add(record);
            }
        }
        setKeys(mainkeys);
    }
    
    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.EMPTY_LIST);
    }
    
}
