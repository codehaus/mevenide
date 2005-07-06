package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public class DefaultGoalInfo implements GoalInfo {
    private PluginInfo plugin;
    private String name;
    private String description;
    private GoalInfo[] prereqs;

    public PluginInfo getPlugin() {
        return plugin;
    }

    public void setPlugin(final PluginInfo pPluginName) {
        plugin = pPluginName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        name = pName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String pDescription) {
        description = pDescription;
    }

    public GoalInfo[] getPrereqs() {
        return prereqs;
    }

    public void setPrereqs(final GoalInfo[] pPrereqs) {
        prereqs = pPrereqs;
    }
}
