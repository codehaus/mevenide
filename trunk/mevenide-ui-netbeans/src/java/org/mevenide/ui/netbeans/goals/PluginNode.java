/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package org.mevenide.ui.netbeans.goals;

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
public class PluginNode extends AbstractNode implements GoalNameCookie
{
    private IGoalsGrabber grabber;
    private GoalNameCookie cookie;
    /** Creates a new instance of PluginNode */
    public PluginNode(String plugin, IGoalsGrabber grabber)
    {
        super(new PluginChildren(plugin, grabber));
        setName(plugin);
        String displayName = plugin;
        if (IGoalsGrabber.ORIGIN_PROJECT.equals(grabber.getOrigin(plugin)))
        {
            displayName = displayName + NbBundle.getMessage(PluginNode.class, "PluginNode.projectSpecific"); 
        }
        setDisplayName(displayName);
        setShortDescription(grabber.getDescription(plugin));
        this.grabber = grabber;
        setIconBase("org/mevenide/ui/netbeans/goals/PluginIcon");
    }
    
    public Node.Cookie getCookie(Class clazz)
    {
        Node.Cookie retValue;
        if (GoalNameCookie.class.isAssignableFrom(clazz))
        {
            return this;
        }
        retValue = super.getCookie(clazz);
        return retValue;
    }    
    
/**
 * goalNameCookie method
 */    
    public String getGoalName()
    {
        return getName();
    }
    
    private static class PluginChildren extends Children.Keys
    {
        private IGoalsGrabber grabber;
        private String plugin;

        public PluginChildren(String plugin, IGoalsGrabber grabber)
        {
            this.plugin = plugin;
            this.grabber = grabber;
        }
        
        public void addNotify()
        {
            super.addNotify();
            setKeys(grabber.getGoals(plugin));
        }
        
        protected Node[] createNodes(Object obj)
        {
            String goal = (String)obj;
            return new Node[] { new GoalNode(plugin, grabber, goal) 
            };
        }

        public void removeNotify()
        {
            setKeys(Collections.EMPTY_LIST);
            super.removeNotify();
        }
        
    }
}
