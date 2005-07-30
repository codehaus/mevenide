package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;

/**
 * @author Arik
 */
public interface ModuleProblemInspector extends ProblemInspector {
    /**
     * Inspect the specified module and report any problems found.
     *
     * @return problems - differences between the IDEA project and the POM
     */
    ProblemInfo[] inspect(String pPomUrl, Module pModule);
}
