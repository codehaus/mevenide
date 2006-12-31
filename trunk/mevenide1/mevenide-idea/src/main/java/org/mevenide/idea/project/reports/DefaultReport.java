package org.mevenide.idea.project.reports;

import org.mevenide.idea.project.goals.PluginGoalContainer;

/**
 * @author Arik
 */
public class DefaultReport implements Report {
    private PluginGoalContainer plugin;
    private String id;
    private String name;
    private String description;

    public PluginGoalContainer getPlugin() {
        return plugin;
    }

    public void setPlugin(final PluginGoalContainer pPlugin) {
        plugin = pPlugin;
    }

    public String getId() {
        return id;
    }

    public void setId(final String pId) {
        id = pId;
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
}
