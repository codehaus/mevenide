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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.repository.VersionNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class SearchResultChildren extends Children.Keys {
    
    private List keys;
    
    private ArrayList mainkeys;
    
    private ArrayList attachedkeys;
    
    /**
     * Creates a new instance of SearchResultChildren
     */
    public SearchResultChildren(List results) {
        keys = results;
    }
    
    protected Node[] createNodes(Object key) {
        StandardArtifactIndexRecord record = (StandardArtifactIndexRecord)key;
        Iterator it = attachedkeys.iterator();
        boolean hasSources = false;
        boolean hasJavadoc = false;
        while (it.hasNext() && (!hasJavadoc || !hasSources)) {
            StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
            if (elem.getGroupId().equals(record.getGroupId()) &&
                    elem.getArtifactId().equals(record.getArtifactId()) &&
                    elem.getVersion().equals(record.getVersion())) {
                hasSources = hasSources || "sources".equals(elem.getClassifier());
                hasJavadoc = hasJavadoc || "javadoc".equals(elem.getClassifier());
            }
        }
        return new Node[] { new VersionNode(record, hasJavadoc, hasSources) };
    }
    
    
    protected void addNotify() {
        super.addNotify();
        Iterator it = keys.iterator();
        mainkeys = new ArrayList(keys.size());
        attachedkeys = new ArrayList(keys.size());
        while (it.hasNext()) {
            StandardArtifactIndexRecord record = (StandardArtifactIndexRecord) it.next();
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
