package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public interface GoalInfo {
    PluginInfo getPlugin();

    String getName();

    String getDescription();

    GoalInfo[] getPrereqs();
}
