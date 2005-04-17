package org.mevenide.idea.settings.global;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface GlobalSettingsListener extends EventListener {

    void mavenHomeChanged(MavenHomeChangedEvent pEvent);
}
