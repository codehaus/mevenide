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

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class GoalNode extends AbstractNode implements GoalNameCookie {
    private IGoalsGrabber grabber;
    private String goal;
    private String plugin;
    /** Creates a new instance of GoalNode */
    public GoalNode(String plugin, IGoalsGrabber grabber, String goal) {
        super(Children.LEAF);
        setName(plugin + ":" + goal); //NOI18N
        this.goal = goal;
        this.plugin = plugin;
        setDisplayName(goal);
        String desc = grabber.getDescription(getName());
        if (desc == null || "null".equals(desc)) {
            desc = "<No description>";
        }
        setShortDescription(desc);
        this.grabber = grabber;
        setIconBase("org/mevenide/netbeans/project/goals/GoalIcon"); //NOI18N
    }
    
    public String getGoalName() {
        if ("(default)".equals(goal)) //NOI18N
        {
            return plugin;
        }
        return getName();
    }
    
    public Node.Cookie getCookie(Class clazz) {
        Node.Cookie retValue;
        if (GoalNameCookie.class.isAssignableFrom(clazz)) {
            return this;
        }
        retValue = super.getCookie(clazz);
        return retValue;
    }
    
    
}
