package org.mevenide.idea.common.settings.global;

import org.mevenide.idea.common.ModelObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Arik
 */
public interface GlobalSettingsModel extends ModelObject {
    /**
     * Returns the maven home.
     *
     * @return File instance
     */
    File getMavenHome();

    /**
     * Sets the maven home to be used by the plugin.
     *
     * <p>Makes sure the maven home exists, if it is not null. Otherwise, fires a MavenHomeChanged
     * event.</p>
     *
     * @param pMavenHome the maven home
     *
     * @throws IllegalArgumentException if not null, and doesn't exist
     */
    void setMavenHome(File pMavenHome) throws FileNotFoundException;

    /**
     * Adds a global settings listener.
     *
     * @param pListener the listener to add
     */
    void addGlobalSettingsListener(GlobalSettingsListener pListener);

    /**
     * Removes the specified listener from the listener list.
     *
     * @param pListener the listener to remove
     */
    void removeGlobalSettingsListener(GlobalSettingsListener pListener);

}
