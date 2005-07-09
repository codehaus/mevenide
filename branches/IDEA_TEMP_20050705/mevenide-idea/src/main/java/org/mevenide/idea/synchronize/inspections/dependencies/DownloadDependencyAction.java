package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ArtifactProblemInfo;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
class DownloadDependencyAction extends AbstractFixAction<ArtifactProblemInfo> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DownloadDependencyAction.class);

    /**
     * A runnable that performs the actual download, and displays a message if an
     * error occurs.
     */
    private final Downloader downloader = new Downloader();

    public DownloadDependencyAction(final ArtifactProblemInfo pProblemInfo) {
        super(RES.get("dep.missing.fix.download.title"),
              RES.get("dep.missing.fix.download.desc", pProblemInfo.getArtifact()),
              Icons.DOWNLOAD,
              pProblemInfo);
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Application app = ApplicationManager.getApplication();
        app.runProcessWithProgressSynchronously(downloader,
                                                "Downloading...",
                                                true,
                                                problem.getProject());

        if (downloader.getError() != null)
            UIUtils.showError(problem.getProject(), downloader.getError());
    }

    private class Downloader implements Runnable {
        private Throwable error = null;

        public void run() {
            error = null;
            try {
                final PomRepoManager mgr = PomRepoManager.getInstance(problem.getProject());
                mgr.download(problem.getPomUrl(), problem.getArtifact());
            }
            catch (Exception e) {
                error = e;
            }
        }

        public Throwable getError() {
            return error;
        }
    }
}
