package org.mevenide.idea.project.goals;

/**
 * @author Arik
 */
public interface PluginGoalContainer extends GoalContainer {
    String getId();

    String getArtifactId();

    String getGroupId();

    String getName();

    String getVersion();

    String getDescription();

    PluginGoal[] getGoals();

    PluginGoal getGoal(String pName);
}
