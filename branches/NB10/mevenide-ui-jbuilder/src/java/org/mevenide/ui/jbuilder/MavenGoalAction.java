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

import com.borland.primetime.build.BuildAction;
import com.borland.primetime.build.BuildProcess;
import com.borland.primetime.build.Phase;
import com.borland.primetime.ide.Browser;
import com.borland.primetime.ide.BrowserIcons;
import com.borland.primetime.node.Node;
import com.borland.primetime.vfs.Url;

/**
 * @author Serge Huber
 * @version 1.0
 */
class MavenGoalAction extends BuildAction {

    MavenGoalAction (MavenGoalNode mavenGoalNode) {
        super(mavenGoalNode.getFullyQualifiedGoalName(),
              '%',
              mavenGoalNode.getFullyQualifiedGoalName(),
              BrowserIcons.ICON_ANT_TARGET,
              mavenGoalNode.getDescription());
        this.mavenGoalNode = mavenGoalNode;
    }

    public String getName () {
        return mavenGoalNode.getFullyQualifiedGoalName();
    }

    public String toString () {
        return mavenGoalNode.getFullyQualifiedGoalName();
    }

    public String[] getTargets () {
        return (new String[] {
                Phase.MAKE_PHASE
        });
    }

    public Node[] getNodes () {
        return (new Node[] {
                mavenGoalNode
        });
    }

    public String getKey () {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(MavenBuilder.class.getName());
        stringbuffer.append(';');
        Url url = mavenGoalNode.getProject().getUrl().getParent();
        String mavenFileRelativePath = url.getRelativePath(mavenGoalNode.
            getMavenNode().getUrl());
        stringbuffer.append(mavenFileRelativePath);
        stringbuffer.append(';');
        stringbuffer.append(mavenGoalNode.getFullyQualifiedGoalName());
        return stringbuffer.toString();
    }

    public void actionPerformed (Browser browser) {
        // Let's make the goal.
        BuildProcess buildProcess = new BuildProcess(getNodes());
        buildProcess.build(true, getTargets());
    }

    private MavenGoalNode mavenGoalNode;
}
