package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.module.ModuleUtils;
import org.mevenide.idea.repository.util.RepositoryUtils;
import org.mevenide.idea.synchronize.AbstractProblemInfo;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.ProblemInspector;
import org.mevenide.idea.synchronize.inspections.AbstractModuleInspector;
import org.mevenide.idea.util.MavenUtils;

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
            return new ProblemInfo[0];

        //
        //buffer for the set of problems we'll encounter
        //
        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>();

        //
        //iterate over the project dependencies, and check each one if it
        //exists in the local repository
        //
        final Dependency[] deps = ModuleUtils.getModulePomDependencies(pModule);
        for (Dependency dep : deps) {
            if (dep.getType() == null)
                dep.setType("jar");

            if (!dep.isAddedToClasspath())
                continue;

            if (!RepositoryUtils.isArtifactInstalled(localRepo, dep))
                problems.add(new DependencyNotDownloadedProblemInfo(this,
                                                                    pModule,
                                                                    localRepo,
                                                                    dep));
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    private class DependencyNotDownloadedProblemInfo extends AbstractProblemInfo {
        private final VirtualFile localRepo;
        private final Dependency dependency;

        public DependencyNotDownloadedProblemInfo(final ProblemInspector pInspector,
                                                  final Module pModule,
                                                  final VirtualFile pLocalRepo,
                                                  final Dependency pDependency) {
            super(pInspector,
                  pModule,
                  RES.get("dep.missing.problem.desc", pDependency.getArtifact()));

            localRepo = pLocalRepo;
            dependency = pDependency;
            addFixAction(new DownloadDependencyAction(this,
                                                      module,
                                                      dependency));
            addFixAction(new RemoveDependencyFromPomAction(this,
                                                           module,
                                                           dependency));
        }

        public boolean isValid() {
            if (!MavenUtils.isDependencyDeclared(module, dependency))
                return true;

            return !RepositoryUtils.isArtifactInstalled(module.getProject(),
                                                        localRepo,
                                                        dependency,
                                                        true);
        }

    }
}
