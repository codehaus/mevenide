package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.FileProblemInfo;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class AddLibraryToPomAction extends AbstractFixAction<FileProblemInfo> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddDependencyToIdeaAction.class);

    public AddLibraryToPomAction(final FileProblemInfo pProblem, final Module pModule) {
        super(RES.get("add.lib2pom.action.name", pProblem.getFile().getPath()),
              RES.get("add.lib2pom.action.desc",
                      pProblem.getFile().getPresentableUrl(),
                      pModule.getName()),
              Icons.FIX_PROBLEMS,
              pProblem);
    }

    /**
     * @todo would be nice to show a dialog to let the user verify the new dependency details
     */
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile typeDir = problem.getFile().getParent();
        if (typeDir == null)
            return;

        final VirtualFile groupDir = typeDir.getParent();
        if (groupDir == null)
            return;

        final String groupId = groupDir.getName();
        final String artifactId;
        final String version;

        String type = typeDir.getName();
        if (type.endsWith("s"))
            type = type.substring(0, type.length() - 1);

        final String fileName = problem.getFile().getName();
        final int hyphenIndex = fileName.lastIndexOf('-');
        if (hyphenIndex < 0) {
            UIUtils.showError(problem.getProject(),
                              "Could not derive dependency details from file '" + fileName + "' - please enter the dependency manually.");
            return;
        }
        final int dotIndex = fileName.lastIndexOf('.');
        if (hyphenIndex < 0) {
            UIUtils.showError(problem.getProject(),
                              "Could not derive dependency details from file '" + fileName + "' - please enter the dependency manually.");
            return;
        }
        artifactId = fileName.substring(0, hyphenIndex);
        version = fileName.substring(hyphenIndex + 1, dotIndex);
        String extension = problem.getFile().getExtension();
        if (extension == null || extension.equalsIgnoreCase(type))
            extension = null;

        final PomModelManager modelMgr = PomModelManager.getInstance(problem.getProject());
        final PsiProject psi = modelMgr.getPsiProject(problem.getPomUrl());
        if(psi == null)
            return;
        
        final PsiDependencies deps = psi.getDependencies();
        final int row = deps.appendRow();
        deps.setGroupId(row, groupId);
        deps.setArtifactId(row, artifactId);
        deps.setType(row, type);
        deps.setVersion(row, version);
        deps.setExtension(row, extension);
    }
}
