package org.mevenide.idea.synchronize.inspections;

import org.mevenide.idea.synchronize.ProblemInspector;

/**
 * @author Arik
 */
public abstract class AbstractInspector implements ProblemInspector {

    private final String name;
    private final String description;

    protected AbstractInspector(final String pName) {
        this(pName, null);
    }

    protected AbstractInspector(final String pName, final String pDescription) {
        name = pName;
        description = pDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
