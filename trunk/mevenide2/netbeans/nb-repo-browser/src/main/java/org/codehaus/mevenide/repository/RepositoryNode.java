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

import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.openide.nodes.AbstractNode;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class RepositoryNode extends AbstractNode {
    private RepositoryInfo info;

    public RepositoryNode(RepositoryInfo info) {
        super(new GroupListChildren(info));
        this.info = info;
        setName(info.getId());
        setDisplayName(info.getName());
        
    }
   

    @Override
    public Image getIcon(int arg0) {
        if(info.isRemote()){
         return Utilities.loadImage("org/codehaus/mevenide/repository/remoterepo.png", true); //NOI18N
        }
        return Utilities.loadImage("org/codehaus/mevenide/repository/localrepo.png", true); //NOI18N
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }
}
