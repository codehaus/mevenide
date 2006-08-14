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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class GroupIdListChildren extends Children.Keys {

    private List keys;
    
    /** Creates a new instance of GroupIdListChildren */
    public GroupIdListChildren() {
    }

    protected Node[] createNodes(Object key) {
        String groupId = (String)key;
        return new Node[] { new GroupIdNode(groupId) };
    }
    
    protected void addNotify() {
        super.addNotify();
        try {
            keys = new ArrayList(CustomQueries.enumerateGroupIds());
            setKeys(keys);
        } catch (IOException ex) {
            ex.printStackTrace();
            keys = new ArrayList();
            setKeys(Collections.EMPTY_LIST);
        }
    }

    protected void removeNotify() {
        super.removeNotify();
        keys = Collections.EMPTY_LIST;
        setKeys(Collections.EMPTY_LIST);
    }
    
}
