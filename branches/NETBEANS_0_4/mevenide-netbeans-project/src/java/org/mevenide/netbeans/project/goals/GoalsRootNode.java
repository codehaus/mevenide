/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.netbeans.project.goals;

import java.util.Collections;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class GoalsRootNode extends AbstractNode
{
    private IGoalsGrabber grabber;
    /** Creates a new instance of PluginNode */
    public GoalsRootNode(IGoalsGrabber grabber)
    {
        super(new GoalsChildren(grabber));
        setName("availableGoals");
        setDisplayName("Available Goals");
        setShortDescription("Available Maven Plugins/Goals");
        this.grabber = grabber;
    }
    
    
    private static class GoalsChildren extends Children.Keys
    {
        private IGoalsGrabber grabber;
        public GoalsChildren(IGoalsGrabber grabber)
        {
            this.grabber = grabber;
        }
        
        public void addNotify()
        {
            super.addNotify();
            setKeys(grabber.getPlugins());
        }
        
        protected Node[] createNodes(Object obj)
        {
            String plugin = (String)obj;
            return new Node[] { new PluginNode(plugin, grabber) 
            };
        }

        public void removeNotify()
        {
            setKeys(Collections.EMPTY_LIST);
            super.removeNotify();
        }
        
    }
}
