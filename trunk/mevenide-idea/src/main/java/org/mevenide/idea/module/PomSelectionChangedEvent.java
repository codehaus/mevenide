package org.mevenide.idea.module;

import java.util.EventObject;

/**
 * @author Arik
 */
public class PomSelectionChangedEvent extends EventObject {

    public PomSelectionChangedEvent(final ModuleSettings pModuleSettings) {
        super(pModuleSettings);
    }

    public ModuleSettings getModuleSettings() {
        return (ModuleSettings) source;
    }
}
