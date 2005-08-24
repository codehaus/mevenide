package org.mevenide.idea.common.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractAnAction extends AnAction {

    protected AbstractAnAction() {
    }

    protected AbstractAnAction(String text) {
        super(text);
    }

    protected AbstractAnAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    protected Project getProject(AnActionEvent pEvent) {
        return (Project) pEvent.getDataContext().getData(DataConstants.PROJECT);
    }
}
