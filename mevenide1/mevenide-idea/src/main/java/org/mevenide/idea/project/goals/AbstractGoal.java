package org.mevenide.idea.project.goals;

/**
 * @author Arik
 */
public abstract class AbstractGoal<ContainerType extends GoalContainer> {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private ContainerType plugin;
    private String name;
    private String description;
    private String[] prereqs = EMPTY_STRING_ARRAY;

    public ContainerType getContainer() {
        return plugin;
    }

    public void setContainer(final ContainerType pPluginName) {
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

    public boolean equals(final Object pValue) {
        if (this == pValue) return true;
        if (pValue == null || getClass() != pValue.getClass()) return false;

        final AbstractGoal that = (AbstractGoal) pValue;

        if (!name.equals(that.name)) return false;
        return plugin.equals(that.plugin);
    }

    public int hashCode() {
        int result;
        result = plugin.hashCode();
        result = 29 * result + name.hashCode();
        return result;
    }
}
