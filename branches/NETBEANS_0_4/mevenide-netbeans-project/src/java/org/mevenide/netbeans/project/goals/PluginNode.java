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
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PluginNode extends AbstractNode implements GoalNameCookie {
    /** Creates a new instance of PluginNode */
    public PluginNode(String plugin, IGoalsGrabber grabber) {
        super(new PluginChildren(plugin, grabber));
        setName(plugin);
        String displayName = plugin;
        if (IGoalsGrabber.ORIGIN_PROJECT.equals(grabber.getOrigin(plugin))) {
            displayName = displayName + NbBundle.getMessage(PluginNode.class, "PluginNode.projectSpecific");
        }
        setDisplayName(displayName);
        setShortDescription(grabber.getDescription(plugin));
        setIconBase("org/mevenide/netbeans/project/goals/PluginIcon");
    }
    
    public Node.Cookie getCookie(Class clazz) {
        Node.Cookie retValue;
        if (GoalNameCookie.class.isAssignableFrom(clazz)) {
            return this;
        }
        retValue = super.getCookie(clazz);
        return retValue;
    }
    
    /**
     * goalNameCookie method
     */
    public String getGoalName() {
        return getName();
    }
    
    private static class PluginChildren extends Children.Keys {
        private IGoalsGrabber grabber;
        private String plugin;
        
        public PluginChildren(String plug, IGoalsGrabber grab) {
            plugin = plug;
            grabber = grab;
        }
        
        public void addNotify() {
            super.addNotify();
            setKeys(grabber.getGoals(plugin));
        }
        
        protected Node[] createNodes(Object obj) {
            String goal = (String)obj;
            return new Node[] { new GoalNode(plugin, grabber, goal)
            };
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
            super.removeNotify();
        }
        
    }
}
