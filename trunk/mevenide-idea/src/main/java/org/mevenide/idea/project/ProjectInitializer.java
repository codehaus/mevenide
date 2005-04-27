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