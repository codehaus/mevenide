package org.mevenide.idea.synchronize;

/**
 * @author Arik
 */
public interface ProblemInspector {
    /**
     * Returns the name of this inspector. Used in the user interface to diffrenciate
     * various inspectors.
     *
     * @return string
     */
    String getName();

    /**
     * Describes this inspector. Shown to the user for additional info.
     *
     * @return string
     */
    String getDescription();
}
