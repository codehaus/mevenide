package org.mevenide.idea.synchronize.inspections;

import org.mevenide.idea.synchronize.ModuleProblemInspector;

/**
 * @author Arik
 */
public abstract class AbstractModuleInspector extends AbstractInspector implements ModuleProblemInspector {

    protected AbstractModuleInspector(final String pName) {
        super(pName);
    }

    protected AbstractModuleInspector(final String pName, final String pDescription) {
        super(pName, pDescription);
    }
}
