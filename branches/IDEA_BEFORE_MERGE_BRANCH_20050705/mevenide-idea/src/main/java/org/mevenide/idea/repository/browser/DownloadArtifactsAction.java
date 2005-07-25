package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import java.io.IOException;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.module.ModuleUtils;
import org.mevenide.idea.repository.download.ArtifactDownloadManager;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class DownloadArtifactsAction extends AbstractBrowserAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DownloadArtifactsAction.class);
    private static final ModuleToStringAspect MODULE_TO_STRING_ASPECT = new ModuleToStringAspect();

    public DownloadArtifactsAction(final RepositoryBrowser pBrowser) {
        super(pBrowser,
              RES.get("download.action.text"),
              RES.get("download.action.desc"),
              Icons.DOWNLOAD);
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        final Module selectedModule = ModuleUtils.selectMavenModule(
                project,
                MODULE_TO_STRING_ASPECT);

        if (selectedModule == null)
            return;

        //
        //prepare list of path elements to download
        //
        final RepoPathElement[] pathElements = getSelectedItems();
        final Runnable downloader = new Runnable() {
            public void run() {
                final ArtifactDownloadManager downloadMgr = ArtifactDownloadManager.getInstance();
                for (RepoPathElement element : pathElements) {
                    try {
                        downloadMgr.downloadArtifact(selectedModule, element);
                    }
                    catch (IOException e) {
                        //TODO: accumulate errors and display once
                        UIUtils.showError(selectedModule, e);
                    }
                }
            }
        };

        final Application app = ApplicationManager.getApplication();
        app.runProcessWithProgressSynchronously(downloader,
                                                "Downloading...",
                                                true,
                                                project);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            pEvent.getPresentation().setEnabled(false);
        else {
            final int selectedItemsCount = browser.getSelectedItemsCount();
            pEvent.getPresentation().setEnabled(selectedItemsCount > 0);
        }
    }

    private static class ModuleToStringAspect implements SelectFromListDialog.ToStringAspect {
        public String getToStirng(Object obj) {
            final Module module = (Module) obj;
            final ILocationFinder finder = new ModuleLocationFinder(module);
            final String repo = finder.getMavenLocalRepository();
            return module.getName() + " - Local repository at " + repo;
        }
    }
}
