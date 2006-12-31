package org.mevenide.idea.project.discovery;

/**
 * @author Arik
 */
public class GoalInfo {

    private final PluginInfo plugin;
    private final String name;
    private final String description;
    private final GoalInfo[] prereqs;

    public GoalInfo(final PluginInfo pPlugin,
                    final String pName,
                    final String pDescription,
                    final GoalInfo[] pPrereqs) {
        plugin = pPlugin;
        name = pName;
        description = pDescription;
        prereqs = pPrereqs;
    }

    public PluginInfo getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GoalInfo[] getPrereqs() {
        return prereqs;
    }
}
