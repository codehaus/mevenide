/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.jbuilder;

import javax.swing.Icon;

import com.borland.primetime.ide.BrowserIcons;
import com.borland.primetime.node.LightweightNode;
import com.borland.primetime.node.Project;

/**
 * @author Serge Huber
 * @version 1.0
 */
public class MavenGoalNode extends LightweightNode {

    private String description;
    private String fullyQualifiedGoalName;

    // public static final Icon ICON = new ImageIcon(MavenGoalNode.class.getResource("maven.gif"));

    public MavenGoalNode (Project project, MavenFileNode mavenFileNode,
                          String name,
                          String description,
                          String fullyQualifiedGoalName) {
        super(project, mavenFileNode, name);
        this.description = description;
        this.fullyQualifiedGoalName = fullyQualifiedGoalName;
    }

    public Icon getDisplayIcon () {
        return BrowserIcons.ICON_ANT_TARGET;
        // return ICON;
    }

    public String getDescription () {
        return description;
    }

    public MavenFileNode getMavenNode () {
        return (MavenFileNode)super.getParent();
    }

    public String getFullyQualifiedGoalName () {
        return fullyQualifiedGoalName;
    }

    public boolean isPersistent () {
        return false;
    }

}
