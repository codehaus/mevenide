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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 * @author Anuradha
 */
public class GroupListChildren extends Children.Keys {

    public static final Object LOADING = new Object();
   private RepositoryInfo info;
    public static Node createLoadingNode() {
        AbstractNode nd = new AbstractNode(Children.LEAF){

            @Override
            public Image getIcon(int arg0) {
                return Utilities.loadImage("org/codehaus/mevenide/repository/wait.gif");
            }
         
        };
        nd.setName("Loading"); //NOI18N
        nd.setDisplayName(NbBundle.getMessage(GroupListChildren.class, "Node_Loading"));
        return nd;
    }
    private List keys;

    public GroupListChildren(RepositoryInfo info) {
        this.info = info;
    }

    /** Creates a new instance of GroupListChildren */
    

    protected Node[] createNodes(Object key) {
        if (LOADING == key) {
            return new Node[]{createLoadingNode()};
        }
        String groupId = (String) key;
        return new Node[]{new GroupNode(info,groupId)};
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        setKeys(Collections.singletonList(LOADING));
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                keys = new ArrayList(RepositoryQueries.getGroups(info));
                setKeys(keys);
            }
        });
    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        keys = Collections.EMPTY_LIST;
        setKeys(Collections.EMPTY_LIST);
    }
}
