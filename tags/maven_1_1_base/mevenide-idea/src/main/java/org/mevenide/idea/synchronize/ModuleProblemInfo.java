package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;

/**
 * @author Arik Kfir
 */
public interface ModuleProblemInfo extends ProblemInfo {
    Module getModule();
}
