/*
 *  Copyright 2008 Mevenide Team.
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

import java.awt.Image;
import javax.swing.Action;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.openide.nodes.AbstractNode;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class ElementNode extends AbstractNode {

    private Xpp3Dom dom;

    public ElementNode(Xpp3Dom dom) {
        super(new ElementChildren(dom));
        this.dom = dom;
        setDisplayName(dom.getName()+(dom.getValue()!=null ?
            (" : "+dom.getValue()) :""));
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/tag.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[0];
    }
    
    
}
