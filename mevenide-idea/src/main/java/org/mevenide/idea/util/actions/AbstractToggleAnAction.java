package org.mevenide.idea.util.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractToggleAnAction extends ToggleAction {
    public AbstractToggleAnAction() {
    }

    public AbstractToggleAnAction(final String text) {
        super(text);
    }

    public AbstractToggleAnAction(final String text,
                                  final String description,
                                  final Icon icon) {
        super(text, description, icon);
    }

    protected Project getProject(final AnActionEvent pEvent) {
        return (Project) pEvent.getDataContext().getData(DataConstants.PROJECT);
    }
}
