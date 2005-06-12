package org.mevenide.idea.synchronize.inspections.dependencies;

import org.mevenide.idea.synchronize.inspections.AbstractModuleInspector;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.Res;
import com.intellij.openapi.module.Module;

/**
 * This inspector checks that all libraries defined in the IDEA project are
 * also defined in the POM.
 *
 * <p>It will search for libraries that are missing from the POM - meaning
 * that problems reported by this inspector will fix the POM to mirror the
 * IDEA project.</p>
 *
 * @author Arik
 */
public class POM2IdeaLibsInspector extends AbstractModuleInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(IdeaLibs2POMInspector.class);

    public POM2IdeaLibsInspector() {
        super(RES.get("pom2idea.inspector.name"),
              RES.get("pom2idea.inspector.desc"));
    }

    public ProblemInfo[] inspect(Module pModule) {
        return new ProblemInfo[0];
    }
}
