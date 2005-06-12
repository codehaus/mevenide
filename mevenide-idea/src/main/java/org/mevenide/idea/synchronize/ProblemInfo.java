package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;

/**
 * @author Arik
 */
public interface ProblemInfo {
    /**
     * Returns the description of the problem.
     *
     * @return string
     */
    String getDescription();

    /**
     * Returns the module this problem pertains to. Can return {@code null}
     * if this problem relates to the entire project rather than a specific
     * module.
     *
     * @return module, or {@code null} if in project level
     */
    Module getModule();

    /**
     * Returns the inspector that discovered this problem. This is used
     * mainly for categorization in the problems tree.
     *
     * @return the problem inspector (should never return {@code null)
     */
    ProblemInspector getInspector();

    /**
     * This method should check if the problem still exists.
     *
     * <p>When displaying multiple problems to the user, and the user fixes
     * some, other problems might not be relevant anymore. Therefor,
     * after each fix, this method will be called to all other problems
     * to check if they are still relevant.</p>
     *
     * @return {@code true} if this problem is still relevant, {@code false} otherwise
     */
    boolean isValid();

    /**
     * Returns {@code true} if this problem can be automatically fixed, or
     * {@code false} if the user has to fix it manually.
     *
     * <p>If this method returns {@code false}, the {@link #fix()} method
     * will never be called.</p>
     *
     * @return boolean
     */
    boolean canBeFixed();

    /**
     * Fixes the problem.
     * @todo perhaps this should be replaced to return a "getFixActions" method, since a problem might have multiple solutions
     */
    void fix();

}
