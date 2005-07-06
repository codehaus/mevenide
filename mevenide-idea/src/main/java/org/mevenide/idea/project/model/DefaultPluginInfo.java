package org.mevenide.idea.project.model;

/**
 * @author Arik
 */
public class DefaultPluginInfo implements PluginInfo {
    private String id;
    private String artifactId;
    private String groupId;
    private String version;
    private String name;
    private String description;
    private GoalInfo[] goals;

    public String getId() {
        if (id != null && id.trim().length() > 0)
            return id;

        return groupId + ":" + artifactId;
    }

    public void setId(final String pId) {
        id = pId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String pArtifactId) {
        artifactId = pArtifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String pGroupId) {
        groupId = pGroupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String pVersion) {
        version = pVersion;
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

    public GoalInfo[] getGoals() {
        return goals;
    }

    public void setGoals(final GoalInfo[] pGoals) {
        goals = pGoals;
    }

    public GoalInfo getGoal(String pName) {
        for (GoalInfo goalInfo : goals) {
            if (goalInfo.getName().equals(pName))
                return goalInfo;
        }

        return null;
    }
}
