package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.util.PomUtils;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class AddAsDependencyAction extends AbstractBrowserAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddAsDependencyAction.class);

    public AddAsDependencyAction(final RepositoryBrowser pBrowser) {
        super(pBrowser,
              RES.get("add.dep.action.text"),
              RES.get("add.dep.action.desc"),
              Icons.ADD_DEPENDENCY);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            pEvent.getPresentation().setEnabled(false);
        else {
            final RepoPathElement[] selectedItems = getSelectedItems(RepoPathElement.LEVEL_VERSION);
            pEvent.getPresentation().setEnabled(selectedItems.length > 0);
        }
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            return;

        final String pomUrl = PomUtils.selectPom(project,
                                                 "Select POM",
                                                 "Please select the project you wish to add the dependency for:");
        if (pomUrl == null || pomUrl.trim().length() == 0)
            return;

        final PomModelManager modelMgr = PomModelManager.getInstance(project);
        final PsiProject psi = modelMgr.getPsiProject(pomUrl);
        final PsiDependencies deps = psi.getDependencies();
        final RepoPathElement[] artifacts = getSelectedItems(RepoPathElement.LEVEL_VERSION);
        for (RepoPathElement pathElement : artifacts) {
            final int row = deps.appendRow();
            deps.setGroupId(row, pathElement.getGroupId());
            deps.setArtifactId(row, pathElement.getArtifactId());
            deps.setType(row, pathElement.getType());
            deps.setVersion(row, pathElement.getVersion());
        }
    }
}
