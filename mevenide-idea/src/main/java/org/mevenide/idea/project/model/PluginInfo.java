package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public interface PluginInfo {
    String getId();

    String getArtifactId();

    String getGroupId();

    String getName();

    String getVersion();

    GoalInfo getGoal(String pName);

    GoalInfo[] getGoals();

    String getDescription();
}
