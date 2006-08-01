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
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class GroupIdListChildren extends Children.Keys {

    private JTextField filter;
    private String currFilter;
    private List keys;
    private DocumentListener documentLister;
    
    /** Creates a new instance of GroupIdListChildren */
    public GroupIdListChildren(JTextField field) {
        filter = field;
        currFilter = filter.getText().trim();
        documentLister = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                checkCurrentFilter();
            }
            public void insertUpdate(DocumentEvent e) {
                checkCurrentFilter();
            }
            public void removeUpdate(DocumentEvent e) {
                checkCurrentFilter();
            }
        };
    }

    protected Node[] createNodes(Object key) {
        String groupId = (String)key;
        if (groupId.startsWith(currFilter)) {
            return new Node[] { new GroupIdNode(groupId) };
        }
        return new Node[0];
    }
    
    private void checkCurrentFilter() {
        String oldFilter = currFilter;
        currFilter = filter.getText().trim();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String elem = (String) it.next();
            if (elem.startsWith(oldFilter) != elem.startsWith(currFilter)) {
                refreshKey(elem);
            }
        }
    }

    protected void addNotify() {
        super.addNotify();
        try {
            keys = new ArrayList(CustomQueries.enumerateGroupIds());
            setKeys(keys);
            filter.getDocument().addDocumentListener(documentLister);
        } catch (IOException ex) {
            ex.printStackTrace();
            keys = new ArrayList();
            setKeys(Collections.EMPTY_LIST);
        }
    }

    protected void removeNotify() {
        super.removeNotify();
        keys = Collections.EMPTY_LIST;
        filter.getDocument().removeDocumentListener(documentLister);
        setKeys(Collections.EMPTY_LIST);
    }
    
}
