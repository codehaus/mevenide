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
package org.mevenide.idea.project;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.toolwindows.goals.GoalsToolWindowUI;
import org.mevenide.idea.toolwindows.execution.ExecutionToolWindowUI;

/**
 * @author Arik
 */
public class ProjectInitializer implements ProjectComponent {
    private final Project project;

    public ProjectInitializer(final Project pProject) {
        project = pProject;
    }

    public String getComponentName() {
        return ProjectInitializer.class.getName();
    }

    public void projectOpened() {
        GoalsToolWindowUI.register(project);
        ExecutionToolWindowUI.register(project);
    }

    public void initComponent() {
    }

    public void projectClosed() {
        GoalsToolWindowUI.unregister(project);
        ExecutionToolWindowUI.unregister(project);
    }

    public void disposeComponent() {
    }
}