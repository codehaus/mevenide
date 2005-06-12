package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.repository.ArtifactDownloadManager;
import org.mevenide.idea.repository.ArtifactNotFoundException;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.synchronize.AbstractProblemInfo;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.inspections.AbstractModuleInspector;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * @author Arik
 */
public class DependencyNotDownloadedInspector extends AbstractModuleInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependencyNotDownloadedInspector.class);

    public DependencyNotDownloadedInspector() {
        super(RES.get("dep.missing.inspector.name"),
              RES.get("dep.missing.inspector.desc"));
    }

    public ProblemInfo[] inspect(Module pModule) {

        //
        //make sure this module is "mavenized"
        //
        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final IQueryContext ctx = settings.getQueryContext();
        if (ctx == null)
            return new ProblemInfo[0];

        //
        //find the local repository
        //
        final VirtualFile localRepo = RepositoryUtils.getLocalRepository(pModule);
        if (localRepo == null)
            return new ProblemInfo[] {new LocalRepoNotFoundProblemInfo(pModule)};

        //
        //buffer for the set of problems we'll encounter
        //
        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>();

        //
        //iterate over the project dependencies, and check each one if it
        //exists in the local repository
        //
        final Dependency[] deps = RepositoryUtils.getModulePomDependencies(pModule);
        for (Dependency dep : deps) {
            if(dep.getType() == null)
                dep.setType("jar");

            if (!dep.isAddedToClasspath())
                continue;

            if(!RepositoryUtils.isArtifactInstalled(localRepo, dep))
                problems.add(new DependencyNotDownloadedProblemInfo(pModule, localRepo, dep));
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    private class LocalRepoNotFoundProblemInfo extends AbstractProblemInfo {
        public LocalRepoNotFoundProblemInfo(final Module pModule) {
            super(DependencyNotDownloadedInspector.this, pModule);
        }

        public String getDescription() {
            return RES.get("local.repo.undefined", module.getName());
        }

        public boolean isValid() {
            final ModuleSettings settings = ModuleSettings.getInstance(module);
            final IQueryContext ctx = settings.getQueryContext();
            if (ctx == null)
                return false;

            final String localRepo = ctx.getResolver().getResolvedValue("maven.repo.local");
            return localRepo != null && localRepo.trim().length() > 0;
        }
    }

    private class DependencyNotDownloadedProblemInfo extends AbstractProblemInfo {
        private final VirtualFile localRepo;
        private final Dependency dependency;

        public DependencyNotDownloadedProblemInfo(final Module pModule,
                                                  final VirtualFile pLocalRepo,
                                                  final Dependency pDependency) {
            super(DependencyNotDownloadedInspector.this, pModule);
            localRepo = pLocalRepo;
            dependency = pDependency;
        }

        public final Dependency getDependency() {
            return dependency;
        }

        public String getDescription() {
            String artifact = dependency.getArtifact();
            if (artifact.endsWith(".null"))
                artifact = artifact.replace(".null", ".jar");

            if(!isValid())
                return RES.get("dep.missing.problem.desc", artifact);
            else
                return RES.get("dep.missing.problem.solved", artifact);
        }

        public boolean isValid() {
            return !RepositoryUtils.isArtifactInstalled(localRepo, dependency);
        }

        @Override
        public boolean canBeFixed() {
            return true;
        }

        @Override
        public void fix() {
            final Downloader downloader = new Downloader();
            final Application app = ApplicationManager.getApplication();
            app.runProcessWithProgressSynchronously(downloader,
                                                    "Downloading...",
                                                    true,
                                                    module.getProject());

            if(downloader.getError() != null)
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
}
