package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.download.ArtifactDownloadManager;
import org.mevenide.idea.repository.download.ArtifactNotFoundException;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */ class DownloadDependencyAction extends AbstractFixAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DownloadDependencyAction.class);

    private final Module module;
    private final Dependency dependency;
    private final Downloader downloader = new Downloader();

    public DownloadDependencyAction(final ProblemInfo pProblemInfo,
                                    final Module pModule,
                                    final Dependency pDependency) {
        super(RES.get("dep.missing.fix.download.title"),
              RES.get("dep.missing.fix.download.desc", pDependency.getArtifact()),
              Icons.DOWNLOAD,
              pProblemInfo);

        module = pModule;
        dependency = pDependency;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Application app = ApplicationManager.getApplication();
        app.runProcessWithProgressSynchronously(downloader,
                                                "Downloading...",
                                                true,
                                                module.getProject());

        if (downloader.getError() != null)
            UIUtils.showError(module, downloader.getError());
    }

    private class Downloader implements Runnable {
        private final AtomicReference<Throwable> error = new AtomicReference<Throwable>();

        public void run() {
            error.set(null);
            final ArtifactDownloadManager downloadMgr = ArtifactDownloadManager.getInstance();
            try {
                downloadMgr.downloadArtifact(module, dependency);
            }
            catch (ArtifactNotFoundException e) {
                error.set(e);
            }
        }

        public Throwable getError() {
            return error.get();
        }
    }
}
