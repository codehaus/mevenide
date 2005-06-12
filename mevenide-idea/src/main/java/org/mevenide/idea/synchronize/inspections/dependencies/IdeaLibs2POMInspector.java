package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.synchronize.AbstractProblemInfo;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.inspections.AbstractModuleInspector;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * This inspector checks that all dependencies defined in the POM are also defined in the IDEA project.
 *
 * <p>Problems reported by this inspector will fix the IDEA project to mirror the POM.</p>
 *
 * @author Arik
 */
public class IdeaLibs2POMInspector extends AbstractModuleInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(IdeaLibs2POMInspector.class);

    public IdeaLibs2POMInspector() {
        super(RES.get("idea2pom.inspector.name"),
              RES.get("idea2pom.inspector.desc"));
    }

    public ProblemInfo[] inspect(Module pModule) {
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
        final Dependency[] deps = RepositoryUtils.getModulePomDependencies(pModule);
        for (Dependency dep : deps) {
            if (dep.getType() == null)
                dep.setType("jar");

            if (!dep.isAddedToClasspath())
                continue;

            final String relPath = RepositoryUtils.getDependencyRelativePath(dep);
            final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
            if (depFile == null)
                continue;

            if (!isDependencyUsed(pModule, depFile))
                problems.add(new DependencyMissingInIdeaProblem(pModule, dep));
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    private static boolean isDependencyUsed(final Module pModule,
                                            final VirtualFile pFile) {
        final String depFilePath = pFile.getPath();
        final ModuleRootManager rootMgr = ModuleRootManager.getInstance(pModule);
        final OrderEntry[] orderEntries = rootMgr.getOrderEntries();
        for (OrderEntry entry : orderEntries) {
            final VirtualFile[] files;
            if(entry instanceof LibraryOrderEntry)
                files = ((LibraryOrderEntry) entry).getLibrary().getFiles(OrderRootType.CLASSES);
            else if(entry instanceof JdkOrderEntry)
                continue;
            else
                files = entry.getFiles(OrderRootType.CLASSES);

            for (VirtualFile file : files) {
                String libFilePath = file.getPath();
                if (libFilePath.endsWith("!/"))
                    libFilePath = libFilePath.substring(0, libFilePath.length() - 2);

                if (pFile.equals(file) || depFilePath.equals(libFilePath))
                    return true;
            }
        }

        return false;
    }

    private class DependencyMissingInIdeaProblem extends AbstractProblemInfo {
        private final Module module;
        private final Dependency dependency;

        public DependencyMissingInIdeaProblem(final Module pModule,
                                              final Dependency pDependency) {
            super(IdeaLibs2POMInspector.this, pModule);
            module = pModule;
            dependency = pDependency;
        }

        public String getDescription() {
            final String relativePath = RepositoryUtils.getDependencyRelativePath(dependency);
            if (isValid())
                return RES.get("dep.missing.from.idea.problem",
                               relativePath,
                               module.getName());
            else
                return RES.get("dep.missing.from.idea.problem.solved",
                               relativePath,
                               module.getName());
        }

        public boolean isValid() {
            final String relPath = RepositoryUtils.getDependencyRelativePath(dependency);
            final VirtualFile localRepo = RepositoryUtils.getLocalRepository(module);
            final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
            if (depFile == null)
                return true;

            return !isDependencyUsed(module, depFile);
        }

        @Override
        public boolean canBeFixed() {
            return true;
        }

        @Override
        public void fix() {
            final Runnable runnable = new Runnable() {
                public void run() {
                    final LibraryTablesRegistrar libTableMgr = LibraryTablesRegistrar.getInstance();
                    final LibraryTable libTable = libTableMgr.getLibraryTable(module.getProject());

                    Library lib = libTable.getLibraryByName(dependency.getArtifact());
                    if(lib == null)
                        lib = libTable.createLibrary(dependency.getArtifact());

                    final Library.ModifiableModel model = lib.getModifiableModel();

                    final String relPath = RepositoryUtils.getDependencyRelativePath(dependency);
                    final VirtualFile localRepo = RepositoryUtils.getLocalRepository(module);
                    final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
                    if (depFile == null) {
                        UIUtils.showError(module, RES.get("local.repo.undefined", module.getName()));
                        return;
                    }

                    model.addRoot(depFile, OrderRootType.CLASSES);
                    model.commit();

                    final ModuleRootManager modRootMgr = ModuleRootManager.getInstance(module);
                    final ModifiableRootModel moduleRootModel = modRootMgr.getModifiableModel();
                    moduleRootModel.addLibraryEntry(lib);
                    moduleRootModel.commit();
                }
            };

            IDEUtils.runCommand(module, runnable);
        }

        public Module getModule() {
            return module;
        }

        public Dependency getDependency() {
            return dependency;
        }
    }
}
