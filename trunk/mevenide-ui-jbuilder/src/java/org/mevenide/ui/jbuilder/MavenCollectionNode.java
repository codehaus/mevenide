/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.jbuilder;

import java.util.ArrayList;

import javax.swing.Icon;

import com.borland.primetime.ide.BrowserIcons;
import com.borland.primetime.node.LightweightNode;
import com.borland.primetime.node.Node;
import com.borland.primetime.node.Project;

/**
 * <p>Title: Abstract Collection Node used to organize project view</p>
 * <p>Description: This class serves as a non-persistant tree node for
 * the project view, so that we can organize large amount of goals in a
 * structure that makes it easier for the user to manage.</p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenCollectionNode extends LightweightNode {

    private ArrayList childNodes = new ArrayList();

    public MavenCollectionNode (Project project, Node parentNode, String name) {
        super(project, parentNode, name);
    }

    public Icon getDisplayIcon () {
        return BrowserIcons.ICON_FOLDER;
    }

    public void addChild (Node childNode) {
        childNodes.add(childNode);
    }

    public boolean hasDisplayChildren () {
        if (childNodes.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Node[] getDisplayChildren () {
        return (Node[]) childNodes.toArray(new Node[childNodes.size()]);
    }

    public boolean isPersistent () {
        return false;
    }

}
