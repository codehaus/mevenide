/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.buildplan.nodes;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Anuradha G
 */
public class ElementChildren extends Children.Keys<Xpp3Dom> {

    private Xpp3Dom dom;

    public ElementChildren(Xpp3Dom dom) {
        this.dom = dom;
    }

    @Override
    protected Node[] createNodes(Xpp3Dom arg0) {
        return new Node[]{new ElementNode(arg0)};
    }

    @Override
    protected void addNotify() {
        setKeys(dom.getChildren());
    }
}
