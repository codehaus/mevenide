package org.mevenide.idea.project.discovery;

/**
 * @author Arik
 */
public class PluginInfo {

    private final String name;
    private final String version;
    private GoalInfo[] goals;

    public PluginInfo(final String pName, final String pVersion) {
        name = pName;
        version = pVersion;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public GoalInfo[] getGoals() {
        return goals;
    }
}
