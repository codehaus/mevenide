package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleUtils;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.synchronize.AbstractProblemInfo;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.inspections.AbstractModuleInspector;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.MavenUtils;

/**
 * @author Arik
 */
public class DependencyDiffInspector extends AbstractModuleInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependencyDiffInspector.class);

    public DependencyDiffInspector() {
        super(RES.get("dep.diff.inspector.name"),
              RES.get("dep.diff.inspector.desc"));
    }

    public ProblemInfo[] inspect(Module pModule) {

        //
        //buffer for the set of problems we'll encounter
        //
        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>();

        //
        //find dependencies defined in POM and not in the IDEA module
        //
        findDepsMissingFromIdea(problems, pModule);

        //
        //find libraries defined in IDEA module and missing from POM
        //
        findLibsMissingFromPom(problems, pModule);

        //
        //return the problems found
        //
        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    protected final void findLibsMissingFromPom(final Set<ProblemInfo> pProblemBuffer,
                                                final Module pModule) {
        //
        //find the local repository
        //
        final VirtualFile localRepo = RepositoryUtils.getLocalRepository(pModule);
        if (localRepo == null)
            return;

        final Dependency[] deps = ModuleUtils.getModulePomDependencies(pModule);
        final Map<VirtualFile, String> depFiles = new HashMap<VirtualFile, String>(deps.length);
        for (Dependency dep : deps) {
            if (!dep.isAddedToClasspath())
                continue;

            final String relPath = RepositoryUtils.getDependencyRelativePath(dep);
            final VirtualFile file = localRepo.findFileByRelativePath(relPath);
            if (file == null || !file.isValid())
                continue;

            depFiles.put(file, FileUtils.fixPath(file));
        }

        //
        //find module's classpath files
        //
        final VirtualFile[] files = ModuleUtils.getModuleClasspath(pModule);
        for (VirtualFile file : files) {
            final String filePath = FileUtils.fixPath(file);
            boolean found = false;
            for (Map.Entry<VirtualFile, String> entry : depFiles.entrySet()) {
                final VirtualFile depFile = entry.getKey();
                final String depFilePath = entry.getValue();
                if (depFile.equals(file) || depFilePath.equals(filePath)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                pProblemBuffer.add(
                    new LibraryMissingFromPomProblem(pModule, file));
        }
    }

    protected final void findDepsMissingFromIdea(final Set<ProblemInfo> pProblemBuffer,
                                                 final Module pModule) {
        //
        //find the local repository
        //
        final VirtualFile localRepo = RepositoryUtils.getLocalRepository(pModule);
        if (localRepo == null)
            return;

        //
        //iterate over the project dependencies, and check each one if it
        //exists in the local repository
        //
        final Dependency[] deps = ModuleUtils.getModulePomDependencies(pModule);
        for (Dependency dep : deps) {
            if (!dep.isAddedToClasspath())
                continue;

            final String relPath = RepositoryUtils.getDependencyRelativePath(dep);
            final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
            if (depFile == null)
                continue;

            if (!ModuleUtils.isFileInClasspath(pModule, depFile))
                pProblemBuffer.add(
                    new DependencyMissingInIdeaProblem(pModule, dep));
        }
    }

    private class DependencyMissingInIdeaProblem extends AbstractProblemInfo {
        private final Module module;
        private final Dependency dependency;

        public DependencyMissingInIdeaProblem(final Module pModule,
                                              final Dependency pDependency) {
            super(DependencyDiffInspector.this,
                  pModule,
                  RES.get("dep.missing.from.idea.problem",
                          RepositoryUtils.getDependencyRelativePath(pDependency),
                          pModule.getName()));

            module = pModule;
            dependency = pDependency;

            addFixAction(new AddDependencyToIdeaAction(this, module, dependency));
            addFixAction(new RemoveDependencyFromPomAction(this, module, dependency));
        }

        public boolean isValid() {
            if (!MavenUtils.isDependencyDeclared(module, dependency))
                return false;

            final String relPath = RepositoryUtils.getDependencyRelativePath(dependency);
            final VirtualFile localRepo = RepositoryUtils.getLocalRepository(module);
            final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
            if (depFile == null)
                return true;

            return !ModuleUtils.isFileInClasspath(module, depFile);
        }
    }

    private class LibraryMissingFromPomProblem extends AbstractProblemInfo {
        private final Module module;
        private final VirtualFile libraryFile;
        private final String libraryFilePath;

        public LibraryMissingFromPomProblem(final Module pModule,
                                            final VirtualFile pLibraryFile) {
            super(DependencyDiffInspector.this,
                  pModule,
                  RES.get("lib.missing.from.pom.problem",
                          FileUtils.fixPath(pLibraryFile),
                          pModule.getName()));
            module = pModule;
            libraryFile = pLibraryFile;
            libraryFilePath = FileUtils.fixPath(libraryFile);

            //
            //find the local repository - if the file is not under the
            //local repo, we cannot derive the group and artifact ids,
            //and therefor we cannot fix the problem (return empty array)
            //
            final VirtualFile localRepo = RepositoryUtils.getLocalRepository(module);
            final File ioLibraryFile = VfsUtil.virtualToIoFile(libraryFile);
            final File ioLocalRepo = VfsUtil.virtualToIoFile(localRepo);

            if (localRepo != null && VfsUtil.isAncestor(ioLocalRepo,
                                                        ioLibraryFile,
                                                        true)) {
                addFixAction(new AddLibraryToPomAction(this, module, libraryFile));
                addFixAction(new RemoveLibraryFromModuleAction(this,
                                                               module,
                                                               libraryFile));
            }
        }

        public boolean isValid() {
            if (!ModuleUtils.isFileInClasspath(module, libraryFile))
                return false;

            //
            //find the local repository
            //
            final VirtualFile localRepo = RepositoryUtils.getLocalRepository(module);
            if (localRepo == null)
                return true;

            //
            //search for the file in the POM dependencies - if found, then
            //the problem is no longer relevant, and return false. Otherwise
            //return true
            //
            final Dependency[] deps = ModuleUtils.getModulePomDependencies(module);
            for (Dependency dep : deps) {
                if (!dep.isAddedToClasspath())
                    continue;

                final String relPath = RepositoryUtils.getDependencyRelativePath(dep);
                final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
                if (depFile == null || !depFile.isValid())
                    continue;

                final String depFilePath = FileUtils.fixPath(depFile);

                if (libraryFile.equals(depFile) || libraryFilePath.equals(depFilePath))
                    return false;
            }

            return true;
        }
    }
}
