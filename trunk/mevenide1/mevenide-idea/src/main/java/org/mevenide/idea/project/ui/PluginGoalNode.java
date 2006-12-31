package org.mevenide.idea.project.ui;

import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PluginGoalContainer;

/**
 * @author Arik
 */
public class PluginGoalNode extends GoalNode<PluginGoal> {
    public PluginGoalNode(final PluginGoal pGoal) {
        super(pGoal);
    }

    @Override
    public PluginGoalContainer getContainer() {
        return (PluginGoalContainer) super.getContainer();
    }
}
