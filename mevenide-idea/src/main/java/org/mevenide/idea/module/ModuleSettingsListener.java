package org.mevenide.idea.module;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface ModuleSettingsListener extends EventListener {

    void modulePomSelectionChanged(PomSelectionChangedEvent pEvent);

}
