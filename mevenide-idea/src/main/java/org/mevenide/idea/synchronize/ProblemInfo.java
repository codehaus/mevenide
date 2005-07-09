package org.mevenide.idea.synchronize;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;

/**
 * @author Arik
 */
public interface ProblemInfo {
    /**
     * Returns the project context in which the inspection was performed.
     *
     * @return IDEA project
     */
    Project getProject();

    /**
     * The url of the POM that was inspected.
     *
     * @return pom url (can be used with pom managers like {@link org.mevenide.idea.project.PomManager})
     */
    String getPomUrl();

    /**
     * Returns the description of the problem.
     *
     * @return string
     */
    String getDescription();

    /**
     * Returns the inspector that discovered this problem. This is used mainly for
     * categorization in the problems tree.
     *
     * @return the problem inspector (should never return {@code null)
     */
    ProblemInspector getInspector();

    /**
     * This method should check if the problem still exists.
     *
     * <p>When displaying multiple problems to the user, and the user fixes some, other
     * problems might not be relevant anymore. Therefor, after each fix, this method will
     * be called to all other problems to check if they are still relevant.</p>
     *
     * @return {@code true} if this problem is still relevant, {@code false} otherwise
     */
    boolean isValid();

    /**
     * Returns the list of actions that can be applied/executed to fix this problem.
     *
     * <p>Returning an empty array, or {@code null} means that this problem cannot be
     * automatically solved and the user needs to intervene.</p>
     *
     * @return can return an actions array, {@code null} or an empty array
     * @todo how about returning a list of a new Fixable interface?
     */
    AnAction[] getFixActions();

}
