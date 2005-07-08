package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;

/**
 * @author Arik Kfir
 */
public interface ModuleProblemInfo extends ProblemInfo {
    /**
     * Returns the module this problem pertains to. Can return {@code null} if this problem relates
     * to the entire project rather than a specific module.
     *
     * @return module, or {@code null} if in project level
     */
    Module getModule();
}
