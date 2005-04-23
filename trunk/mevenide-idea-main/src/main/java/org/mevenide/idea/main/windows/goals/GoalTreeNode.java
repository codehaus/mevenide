package org.mevenide.idea.main.windows.goals;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class GoalTreeNode extends DefaultMutableTreeNode {

    private final String description;

    public GoalTreeNode(final String pGoal) {
        this(pGoal, null);
    }

    public GoalTreeNode(final String pGoal, final String pDescription) {
        super(pGoal);
        description = pDescription;
    }

    public String getGoal() {
        return (String) userObject;
    }

    public String toString() {
        if(description == null || description.trim().length() == 0)
            return super.toString();
        else
            return super.toString() + " - " + description;
    }
}
