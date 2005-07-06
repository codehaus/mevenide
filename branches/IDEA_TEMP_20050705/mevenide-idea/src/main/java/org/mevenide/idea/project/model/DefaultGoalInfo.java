package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public class DefaultGoalInfo implements GoalInfo {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private PluginInfo plugin;
    private String name;
    private String description;
    private String[] prereqs = EMPTY_STRING_ARRAY;

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

    public String[] getPrereqs() {
        return prereqs;
    }

    public void setPrereqs(final String[] pPrereqs) {
        prereqs = pPrereqs;
    }
}
