package org.mevenide.idea.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @author Arik
 */
public class PomModelManager extends AbstractProjectComponent {
    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this component instance will be registered for
     */
    public PomModelManager(final Project pProject) {
        super(pProject);
    }


    public static PomModelManager getInstance() {
        return ApplicationManager.getApplication().getComponent(PomModelManager.class);
    }
}
