package org.mevenide.idea.project.ui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import javax.swing.tree.DefaultMutableTreeNode;
import org.mevenide.idea.project.actions.ExecuteGoalAction;
import org.mevenide.idea.project.model.GoalInfo;

/**
 * @author Arik
 */
public class GoalNode extends DefaultMutableTreeNode implements PopupMenuNode {
    private final DefaultActionGroup popupActions = new DefaultActionGroup();

    public GoalNode(final GoalInfo pGoal) {
        super(pGoal, false);
        popupActions.add(new ExecuteGoalAction());
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public GoalInfo getUserObject() {
        return (GoalInfo) super.getUserObject();
    }

    public ActionGroup getPopupActions() {
        return popupActions;
    }
}
