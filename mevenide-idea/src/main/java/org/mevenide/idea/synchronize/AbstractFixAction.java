package org.mevenide.idea.synchronize;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import javax.swing.*;
import org.mevenide.idea.util.actions.AbstractAnAction;

/**
 * @author Arik
 */
public abstract class AbstractFixAction<ProblemType extends ProblemInfo> extends AbstractAnAction {
    protected final ProblemType problem;

    protected AbstractFixAction(final ProblemType pProblem) {
        problem = pProblem;
    }

    protected AbstractFixAction(final String pText, final ProblemType pProblem) {
        super(pText);
        problem = pProblem;
    }

    protected AbstractFixAction(final String pText,
                                final String pDescription,
                                final Icon pIcon,
                                final ProblemType pProblem) {
        super(pText, pDescription, pIcon);
        problem = pProblem;
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        if (pEvent == null)
            return;

        final Presentation p = pEvent.getPresentation();
        if (p == null)
            return;

        p.setEnabled(problem.isValid());
    }
}
