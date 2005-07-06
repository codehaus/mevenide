package org.mevenide.idea.project.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.mevenide.idea.project.model.GoalInfo;

/**
 * @author Arik
 */
public class GoalNode extends DefaultMutableTreeNode {
    public GoalNode(final GoalInfo pGoal) {
        super(pGoal, false);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public GoalInfo getUserObject() {
        return (GoalInfo) super.getUserObject();
    }
}
