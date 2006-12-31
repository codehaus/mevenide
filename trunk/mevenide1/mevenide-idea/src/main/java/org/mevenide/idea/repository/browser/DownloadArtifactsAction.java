package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.ArtifactNotFoundException;
import org.mevenide.idea.repository.PomRepoManager;
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
    private static final String DLG_TITLE = RES.get("download.dlg.title");
    private static final String DLG_LABEL = RES.get("download.dlg.label");

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
        final PomRepoManager mgr = PomRepoManager.getInstance(project);
        final String repoUrl = mgr.selectDestinationRepo(DLG_TITLE, DLG_LABEL);
        if (repoUrl == null)
            return;

        //
        //prepare list of path elements to download
        //
        final RepoPathElement[] pathElements = getSelectedItems();
        final Runnable downloader = new Runnable() {
            public void run() {
                for (RepoPathElement element : pathElements) {
                    try {
                        //TODO: show returned VirtualFile(s) in Results pane
                        mgr.download(repoUrl, element);
                    }
                    catch (ArtifactNotFoundException e) {
                        //TODO: accumulate errors and display once
                        UIUtils.showError(project, e);
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
}
