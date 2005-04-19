package org.mevenide.idea.common.settings.global;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface GlobalSettingsListener extends EventListener {
    void mavenHomeChanged(MavenHomeChangedEvent pEvent);
}
