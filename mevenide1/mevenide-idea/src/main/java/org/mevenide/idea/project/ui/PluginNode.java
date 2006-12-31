package org.mevenide.idea.project.ui;

import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PluginGoalContainer;

/**
 * @author Arik
 */
public class PluginNode extends GoalContainerNode<PluginGoalContainer, PluginGoal> {
    public PluginNode(final PluginGoalContainer pGoalContainer) {
        super(pGoalContainer);
    }
}
