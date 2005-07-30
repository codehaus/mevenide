package org.mevenide.idea.synchronize;

import com.intellij.openapi.project.Project;

/**
 * @author Arik Kfir
 */
public interface ProjectProblemInspector extends ProblemInspector {
    /**
     * Inspect the specified module and report any problems found.
     *
     * @return problems - differences between the IDEA project and the POM
     */
    ProblemInfo[] inspect(String pPomUrl, Project pProject);
}
