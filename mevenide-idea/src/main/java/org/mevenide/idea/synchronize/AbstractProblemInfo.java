package org.mevenide.idea.synchronize;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
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

    /**
     * The inspector that discovered this problem.
     */
    protected final ProblemInspector inspector;

    /**
     * The project context.
     */
    protected final Project project;

    /**
     * The url of the POM that was inspected.
     */
    protected final String pomUrl;

    /**
     * Default description of the problem. If the description changes on state, you can override
     * {@link #getDescription()}.
     */
    protected final String description;

    /**
     * Default fix actions. If the available actions change on state, you can override {@link
     * #getFixActions()}.
     */
    private final AtomicReference<AnAction[]> fixActions = new AtomicReference<AnAction[]>(
            new AnAction[0]);

    /**
     * Creates an instance with the default problem description.
     *
     * @param pInspector inspector that discovered the problem.
     */
    protected AbstractProblemInfo(final ProblemInspector pInspector,
                                  final Project pProject,
                                  final String pPomUrl) {
        this(pInspector, pProject, pPomUrl, RES.get("default.problem.desc"));
    }

    /**
     * Creates an instance with the given description.
     *
     * @param pInspector   inspector that discovered the problem.
     * @param pDescription problem description
     */
    protected AbstractProblemInfo(final ProblemInspector pInspector,
                                  final Project pProject,
                                  final String pPomUrl,
                                  final String pDescription) {
        inspector = pInspector;
        project = pProject;
        pomUrl = pPomUrl;
        description = pDescription;
    }

    public final ProblemInspector getInspector() {
        return inspector;
    }

    public Project getProject() {
        return project;
    }

    public String getPomUrl() {
        return pomUrl;
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
