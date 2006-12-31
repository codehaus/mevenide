package org.mevenide.idea.project.goals;

/**
 * @author Arik
 */
public class DefaultPluginGoalContainer extends AbstractGoalContainer<PluginGoal>
        implements PluginGoalContainer {
    @Override
    public String toString() {
        return "Plugin " + getId();
    }
}
