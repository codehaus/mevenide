package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ArtifactProblemInfo;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemoveDependencyFromPomAction extends AbstractFixAction<ArtifactProblemInfo> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RemoveDependencyFromPomAction.class);

    public RemoveDependencyFromPomAction(final ArtifactProblemInfo pProblemInfo) {
        super(RES.get("remove.dep.from.pom.action.name", pProblemInfo.getArtifact()),
              RES.get("remove.dep.from.pom.action.desc", pProblemInfo.getArtifact()),
              Icons.FIX_PROBLEMS,
              pProblemInfo);
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = problem.getProject();
        final PomModelManager modelMgr = PomModelManager.getInstance(project);
        final PsiProject psi = modelMgr.getPsiProject(problem.getPomUrl());
        final PsiDependencies deps = psi.getDependencies();
        final int row = deps.findRow(problem.getArtifact());
        if (row >= 0)
            deps.deleteRows(row);
    }
}
