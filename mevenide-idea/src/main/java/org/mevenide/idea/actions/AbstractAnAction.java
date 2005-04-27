package org.mevenide.idea.actions;

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

    protected AbstractAnAction(final String pText) {
        super(pText);
    }

    protected AbstractAnAction(final String pText,
                               final String pDescription,
                               final Icon pIcon) {
        super(pText, pDescription, pIcon);
    }

    protected Project getProject(final AnActionEvent pEvent) {
        return (Project) pEvent.getDataContext().getData(DataConstants.PROJECT);
    }
}
