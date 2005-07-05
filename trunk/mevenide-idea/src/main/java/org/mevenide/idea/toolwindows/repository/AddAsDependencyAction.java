package org.mevenide.idea.toolwindows.repository;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.module.ModuleUtils;
import org.mevenide.idea.psi.PsiPomManager;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.repository.browser.AbstractBrowserAction;
import org.mevenide.idea.repository.browser.RepositoryBrowser;
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

    /**
     * Used to render modules in a list when selecting from multiple modules.
     */
    private static final ModuleToStringAspect MODULE_TO_STRING_ASPECT = new ModuleToStringAspect();

    public AddAsDependencyAction(final RepositoryBrowser pBrowser) {
        super(pBrowser,
              RES.get("add.dep.text"),
              RES.get("add.dep.desc"),
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

        final Module module = ModuleUtils.selectMavenModule(project, MODULE_TO_STRING_ASPECT);
        if (module == null)
            return;

        final ModuleSettings mavenSettings = ModuleSettings.getInstance(module);
        final VirtualFile pomFile = mavenSettings.getPomVirtualFile();
        if (pomFile == null)
            return;

        final PsiProject psi = PsiPomManager.getInstance(module).getPsiProject();
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

    private static class ModuleToStringAspect implements SelectFromListDialog.ToStringAspect {
        public String getToStirng(Object obj) {
            final Module module = (Module) obj;
            final ModuleSettings mavenSettings = ModuleSettings.getInstance(module);
            final VirtualFile pomFile = mavenSettings.getPomVirtualFile();
            final String loc = pomFile == null ? "(unknown POM file)" : pomFile.getPath();
            return module.getName() + " - " + loc;
        }
    }
}
