package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public class DefaultPluginInfo implements PluginInfo {
    private String name;
    private String version;
    private GoalInfo[] goals;

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        name = pName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String pVersion) {
        version = pVersion;
    }

    public GoalInfo[] getGoals() {
        return goals;
    }

    public void setGoals(final GoalInfo[] pGoals) {
        goals = pGoals;
    }
}
