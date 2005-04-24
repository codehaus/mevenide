package org.mevenide.idea.common.settings.module;

import java.util.EventObject;

/**
 * @author Arik
 */
public class ModuleGoalsChangedEvent extends EventObject {

    public ModuleGoalsChangedEvent(final ModuleSettingsModel pSource) {
        super(pSource);
    }

    public ModuleSettingsModel getModuleSettings() {
        return (ModuleSettingsModel) source;
    }
}