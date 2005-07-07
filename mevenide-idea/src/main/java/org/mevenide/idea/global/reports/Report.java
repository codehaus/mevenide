package org.mevenide.idea.global.reports;

import org.mevenide.idea.project.goals.PluginGoalContainer;

/**
 * @author Arik
 */
public interface Report {
    PluginGoalContainer getPlugin();

    String getId();

    String getName();

    String getDescription();

}
