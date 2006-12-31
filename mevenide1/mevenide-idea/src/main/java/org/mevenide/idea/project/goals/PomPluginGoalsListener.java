package org.mevenide.idea.project.goals;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface PomPluginGoalsListener extends EventListener {
    void pomPluginGoalAdded(PomPluginGoalEvent pEvent);

    void pomPluginGoalRemoved(PomPluginGoalEvent pEvent);
}
