package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.actionSystem.AnAction;
import java.util.concurrent.atomic.AtomicReference;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public abstract class AbstractProblemInfo implements ProblemInfo {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AbstractProblemInfo.class);

    protected final ProblemInspector inspector;
    protected final Module module;
    protected final String description;
    private final AtomicReference<AnAction[]> fixActions = new AtomicReference<AnAction[]>(new AnAction[0]);

    protected AbstractProblemInfo(final ProblemInspector pInspector) {
        this(pInspector, null);
    }

    protected AbstractProblemInfo(final ProblemInspector pInspector, final Module pModule) {
        this(pInspector, pModule, RES.get("default.problem.desc"));
    }

    protected AbstractProblemInfo(final ProblemInspector pInspector,
                                  final Module pModule,
                                  final String pDescription) {
        inspector = pInspector;
        module = pModule;
        description = pDescription;
    }

    public Module getModule() {
        return module;
    }

    public final ProblemInspector getInspector() {
        return inspector;
    }

    public String getDescription() {
        return description;
    }

    public AnAction[] getFixActions() {
        return fixActions.get();
    }

    protected final void addFixAction(final AnAction pFixAction) {
        final AnAction[] currentActions = fixActions.get();
        final AnAction[] newActions = new AnAction[currentActions.length + 1];
        System.arraycopy(currentActions, 0, newActions, 0, currentActions.length);
        newActions[newActions.length - 1] = pFixAction;
        fixActions.set(newActions);
    }

    protected final void setFixActions(final AnAction[] pFixActions) {
        fixActions.set(pFixActions);
    }
}
