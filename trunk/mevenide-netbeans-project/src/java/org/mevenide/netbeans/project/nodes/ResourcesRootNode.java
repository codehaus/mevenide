/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.netbeans.project.nodes;

import javax.swing.Action;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class ResourcesRootNode extends AbstractNode
{
    private MavenProject project;
    private static Action actions[];
    
    ResourcesRootNode(MavenProject mavproject)
    {
        super(new ResourcesRootChildren(mavproject));
        setName("Resources"); //NOI18N
        setDisplayName("Resources");
        // can do so, since we depend on it..
        setIconBase("org/netbeans/modules/java/j2seproject/ui/resources/packageRoot"); //NOI18N
	project = mavproject;
    }
    
    public Action[] getActions( boolean context )
    {
       return super.getActions(context);
    }
    
}

