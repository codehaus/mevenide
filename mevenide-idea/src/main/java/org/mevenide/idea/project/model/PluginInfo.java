package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public interface PluginInfo {
    String getName();

    String getVersion();

    GoalInfo[] getGoals();
}
