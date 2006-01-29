package org.mevenide.idea.project.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.GoalContainer;

/**
 * @author Arik
 */
public abstract class GoalNode<GoalType extends Goal> extends DefaultMutableTreeNode {
    public GoalNode(final GoalType pGoal) {
        super(pGoal, false);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public GoalContainer getContainer() {
        final GoalType goal = getGoal();
        if (goal == null)
            return null;

        return goal.getContainer();
    }

    public final GoalType getGoal() {
        return getUserObject();
    }

    @Override
    public GoalType getUserObject() {
        //noinspection UNCHECKED_WARNING
        return (GoalType) super.getUserObject();
    }
}
