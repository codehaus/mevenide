package org.mevenide.idea.util.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Arik Kfir
 */
public abstract class AbstractAnActionGroup extends ActionGroup {
    protected AbstractAnActionGroup() {
    }

    protected AbstractAnActionGroup(String shortName, boolean popup) {
        super(shortName, popup);
    }

    public abstract AnAction[] getChildren(final AnActionEvent pEvent);
}
