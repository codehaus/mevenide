/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.build.AbstractMavenBuildTask;
import org.codehaus.mevenide.idea.helper.BuildContext;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class StopProcessAction extends AbstractBuildAction {

    protected void doUpdate(Presentation presentation, Project project, BuildContext buildContext) {
        AbstractMavenBuildTask mavenBuildTask = buildContext.getBuildTask();
        if (mavenBuildTask == null || mavenBuildTask.isStopped()) {
            presentation.setEnabled(false);
        } else {
            presentation.setEnabled(true);
        }
    }

    protected void doPerform(Project project, BuildContext buildContext) {
        AbstractMavenBuildTask mavenBuildTask = buildContext.getBuildTask();
        if (mavenBuildTask != null) {
            buildContext.setBuildCancelled(true);
            mavenBuildTask.cancel();
        }
    }
}
