package org.mevenide.idea.project.goals;

import java.util.EventObject;

/**
 * @author Arik
 */
public class PomPluginGoalEvent extends EventObject {
    private final String pomUrl;
    private final PluginGoal addedGoal;
    private final PluginGoal removedGoal;

    public PomPluginGoalEvent(final PomPluginGoalsManager pSource,
                              final String pPomUrl,
                              final PluginGoal pAddedGoal,
                              final PluginGoal pRemovedGoal) {
        super(pSource);
        pomUrl = pPomUrl;
        addedGoal = pAddedGoal;
        removedGoal = pRemovedGoal;
    }

    @Override
    public PomPluginGoalsManager getSource() {
        return (PomPluginGoalsManager) super.getSource();
    }

    public String getPomUrl() {
        return pomUrl;
    }

    public PluginGoal getAddedGoal() {
        return addedGoal;
    }

    public PluginGoal getRemovedGoal() {
        return removedGoal;
    }
}
