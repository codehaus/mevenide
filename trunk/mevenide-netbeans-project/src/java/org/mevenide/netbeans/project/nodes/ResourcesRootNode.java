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

package org.mevenide.netbeans.project.nodes;

import javax.swing.Action;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.nodes.AbstractNode;
import org.openide.util.Utilities;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class ResourcesRootNode extends AbstractNode {
    private MavenProject project;
    
    ResourcesRootNode(MavenProject mavproject, boolean testResource) {
        super(new ResourcesRootChildren(mavproject, testResource));
        setName(testResource ? "TestResources" : "Resources"); //NOI18N
        setDisplayName(testResource ? "Test Resources" : "Resources");
        // can do so, since we depend on it..
        setIconBase("org/mevenide/netbeans/project/resources/defaultFolder"); //NOI18N
        project = mavproject;
    }
    
    public Action[] getActions( boolean context ) {
        return super.getActions(context);
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/resources.gif"),
                        0, 0);
        return retValue;
    }
    
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        retValue = Utilities.mergeImages(retValue,
                      Utilities.loadImage("org/mevenide/netbeans/project/resources/resources.gif"),
                      0, 0);
        return retValue;
    }
    
}

