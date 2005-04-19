package org.mevenide.idea.main.windows.goals;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class GoalTreeNode extends DefaultMutableTreeNode {
    public GoalTreeNode(final String pGoal) {
        super(pGoal);
    }

    public String getGoal() {
        return (String) userObject;
    }
}
