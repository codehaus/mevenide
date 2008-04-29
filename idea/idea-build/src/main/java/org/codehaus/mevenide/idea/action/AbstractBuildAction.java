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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.component.MavenBuildProjectComponent;
import org.codehaus.mevenide.idea.helper.BuildContext;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public abstract class AbstractBuildAction extends AnAction {
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        if ( project != null ) {
            BuildContext buildContext = MavenBuildProjectComponent.getInstance(project).getBuildContext();
            if ( buildContext != null) {
                doUpdate (presentation, project, buildContext);
                return;
            }
        }
        presentation.setEnabled(false);
    }

    public void actionPerformed(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        if ( project != null ) {
            BuildContext buildContext = MavenBuildProjectComponent.getInstance(project).getBuildContext();
            if ( buildContext != null) {
                doPerform (project, buildContext);
            }
        }
    }

    protected abstract void doUpdate(Presentation presentation, Project project, BuildContext buildContext);
    protected abstract void doPerform(Project project, BuildContext buildContext);
}
