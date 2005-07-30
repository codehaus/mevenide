package org.mevenide.idea.project.ui;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.GoalContainer;

/**
 * @author Arik
 */
public abstract class GoalContainerNode<ContainerType extends GoalContainer,GoalType extends Goal>
        extends DefaultMutableTreeNode {
    protected GoalContainerNode(final ContainerType pGoalContainer) {
        super(pGoalContainer, true);
    }

    @Override
    public Enumeration<GoalNode<GoalType>> children() {
        //noinspection UNCHECKED_WARNING
        return super.children();
    }

    @Override
    public GoalNode<GoalType> getChildAt(int index) {
        return (GoalNode<GoalType>) super.getChildAt(index);
    }

    @Override
    public GoalNode<GoalType> getFirstChild() {
        return (GoalNode<GoalType>) super.getFirstChild();
    }

    @Override
    public GoalNode<GoalType> getLastChild() {
        return (GoalNode<GoalType>) super.getLastChild();
    }

    @Override
    public GoalNode<GoalType> getChildAfter(TreeNode aChild) {
        return (GoalNode<GoalType>) super.getChildAfter(aChild);
    }

    @Override
    public GoalNode<GoalType> getChildBefore(TreeNode aChild) {
        return (GoalNode<GoalType>) super.getChildBefore(aChild);
    }

    @Override
    public ContainerType getUserObject() {
        //noinspection UNCHECKED_WARNING
        return (ContainerType) super.getUserObject();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
