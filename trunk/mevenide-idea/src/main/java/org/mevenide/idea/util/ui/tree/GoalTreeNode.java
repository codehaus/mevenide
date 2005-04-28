package org.mevenide.idea.util.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class GoalTreeNode extends DefaultMutableTreeNode {
    private final String description;
    private final String[] prereqs;

    public GoalTreeNode(final String pGoal) {
        this(pGoal, null, null);
    }

    public GoalTreeNode(final String pGoal, final String pDescription) {
        this(pGoal, pDescription, null);
    }

    public GoalTreeNode(final String pGoal, final String[] pPrereqs) {
        this(pGoal, null, pPrereqs);
    }

    public GoalTreeNode(final String pGoal, final String pDescription, final String[] pPrereqs) {
        super(pGoal);

        //TODO: load this message from res
        if(pGoal == null || pGoal.trim().length() == 0)
            throw new IllegalArgumentException("Goal name cannot be empty.");

        if(pDescription == null || pDescription.trim().length() == 0 || pDescription.equalsIgnoreCase("null"))
            description = null;
        else
            description = pDescription;

        if(pPrereqs == null)
            prereqs = new String[0];
        else
            prereqs = pPrereqs;
    }

    public String getUserObject() {
        return (String) super.getUserObject();
    }

    public String getGoal() {
        return getUserObject();
    }

    public String getDescription() {
        return description;
    }

    public String[] getPrereqs() {
        return prereqs;
    }

    public String toString() {
        if (description == null || description.trim().length() == 0)
            return super.toString();
        else
            return super.toString() + " - " + description;
    }
}
