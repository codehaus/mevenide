package org.mevenide.idea.util.components;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

/**
 * @author Arik
 */
public abstract class AbstractProjectComponent extends AbstractIdeaComponent
    implements ProjectComponent {
    /**
     * The component's project.
     */
    protected final Project project;

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the component's project
     */
    protected AbstractProjectComponent(final Project pProject) {
        project = pProject;
    }

    /**
     * Does nothing. Implemented for convenience, so subclasses wouldn't have to implement
     * if they have no functionality in this method.
     */
    public void projectOpened() {
    }

    /**
     * Does nothing. Implemented for convenience, so subclasses wouldn't have to implement
     * if they have no functionality in this method.
     */
    public void projectClosed() {
    }
}
