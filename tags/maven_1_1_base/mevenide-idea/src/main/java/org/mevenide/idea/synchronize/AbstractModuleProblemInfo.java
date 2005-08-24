package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;

/**
 * @author Arik Kfir
 */
public abstract class AbstractModuleProblemInfo extends AbstractProblemInfo {
    protected final Module module;

    protected AbstractModuleProblemInfo(final ProblemInspector pInspector,
                                        final Module pModule,
                                        final String pPomUrl) {
        super(pInspector, pModule.getProject(), pPomUrl);
        module = pModule;
    }

    protected AbstractModuleProblemInfo(final ProblemInspector pInspector,
                                        final String pPomUrl,
                                        final String pDescription,
                                        final Module pModule) {
        super(pInspector, pModule.getProject(), pPomUrl, pDescription);
        module = pModule;
    }

    public Module getModule() {
        return module;
    }
}
