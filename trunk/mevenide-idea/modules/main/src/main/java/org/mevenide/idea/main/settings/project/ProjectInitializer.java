package org.mevenide.idea.main.settings.project;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.main.windows.execution.ExecutionToolWindow;
import org.mevenide.idea.main.windows.goals.GoalsToolWindow;

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
        GoalsToolWindow.register(project);
        ExecutionToolWindow.register(project);
    }

    public void initComponent() {
    }

    public void projectClosed() {
        GoalsToolWindow.unregister(project);
        ExecutionToolWindow.unregister(project);
    }

    public void disposeComponent() {
    }
}