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

import java.awt.Image;
import org.apache.maven.lifecycle.MojoBindingUtils;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class MojoNode extends AbstractNode {

    private MojoBinding mb;

    public MojoNode(MojoBinding mb) {
        super(createChildren(mb));
        this.mb = mb;
        setDisplayName(MojoBindingUtils.toString(mb));
        setShortDescription(MojoBindingUtils.toString(mb));

    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/mojo.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");
        buffer.append(mb.getGroupId()).append(" : ").
                append(mb.getArtifactId()).append(" : ").
                append(mb.getVersion() == null ? "" : mb.getVersion() + ":").
                append("  <b>").append(mb.getGoal());
        return buffer.append("</html>").toString();
    }

    public static Children createChildren(MojoBinding mb) {
        Children.Array array = new Children.Array();
        Node[] nodes=new Node[mb.getConfiguration()==null? 1:2];
        nodes[0]=new ExecutionNode(mb);
        if(mb.getConfiguration()!=null){
         nodes[1]=new ConfigurationNode(mb);
        }
        array.add(nodes);
        return array;
    }
}
