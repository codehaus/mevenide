package org.mevenide.idea.module;

import java.util.EventObject;

/**
 * @author Arik
 */
public class ModuleFavoriteGoalsChangedEvent extends EventObject {

    public ModuleFavoriteGoalsChangedEvent(final ModuleSettings pSource) {
        super(pSource);
    }

    @Override public ModuleSettings getSource() {
        return (ModuleSettings) super.getSource();
    }
}
