package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;

/**
 * @author Arik
 */
public abstract class AbstractProblemInfo implements ProblemInfo {
    protected final ProblemInspector inspector;
    protected final Module module;

    protected AbstractProblemInfo(final ProblemInspector pInspector) {
        this(pInspector, null);
    }

    protected AbstractProblemInfo(final ProblemInspector pInspector, final Module pModule) {
        inspector = pInspector;
        module = pModule;
    }

    public Module getModule() {
        return module;
    }

    public final ProblemInspector getInspector() {
        return inspector;
    }

    public boolean canBeFixed() {
        return false;
    }

    public void fix() {
        throw new UnsupportedOperationException("This problem cannot be fixed automatically.");
    }
}
