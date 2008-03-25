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
import java.util.List;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class PhaseNode extends AbstractNode{

    public PhaseNode(String name,List<MojoBinding> bindings) {
        super(new MojoChildern(bindings));
        setDisplayName(NbBundle.getMessage(PhaseNode.class, "LBL_Phase", new Object[] {name}));
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/phase.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public String getHtmlDisplayName() {
        return getDisplayName();
    }
  
    private static class MojoChildern extends Children.Keys<MojoBinding>{
        private List<MojoBinding> bindings;

        public MojoChildern(List<MojoBinding> bindings) {
            this.bindings = bindings;
        }
        
        
        @Override
        protected Node[] createNodes(MojoBinding arg0) {
           return new Node[]{new MojoNode(arg0)};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(bindings);
        }
    
       
    
    }
    
}
