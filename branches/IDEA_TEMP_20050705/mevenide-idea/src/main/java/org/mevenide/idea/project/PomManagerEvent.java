package org.mevenide.idea.project;

import java.util.EventObject;

/**
 * Called when a POM file is added, removed or changed in the {@link PomManager}.
 *
 * @author Arik
 */
public class PomManagerEvent extends EventObject {
    /**
     * A pointer to the POM file (might not be valid).
     */
    private final String url;

    /**
     * Creates an instance for the given POM manager and file.
     *
     * @param pSource the POM manager generating the event
     * @param pUrl    the POM file that has changed
     */
    public PomManagerEvent(final PomManager pSource,
                           final String pUrl) {
        super(pSource);
        url = pUrl;
    }

    /**
     * Returns the POM manager that generated the event.
     *
     * @return POM manager
     */
    @Override
    public PomManager getSource() {
        return (PomManager) super.getSource();
    }

    /**
     * Returns a pointer to the POM file that changed. Note that the pointer might not be valid (use
     * {@link com.intellij.openapi.vfs.pointers.VirtualFilePointer#isValid()}).
     *
     * @return pointer
     */
    public String getUrl() {
        return url;
    }
}
