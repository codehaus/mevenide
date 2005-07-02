package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.util.RepositoryUtils;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class AddDependencyToIdeaAction extends AbstractFixAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddDependencyToIdeaAction.class);

    private final Runnable dependencyAdder = new AddDependencyToIdeaRunnable();
    private final Module module;
    private final Dependency dependency;

    public AddDependencyToIdeaAction(final ProblemInfo pProblem,
                                     final Module pModule,
                                     final Dependency pDependency) {
        super(RES.get("add.dep2idea.action.name", pDependency.getArtifact()),
              RES.get("add.dep2idea.action.desc", pDependency.getArtifact()),
              Icons.FIX_PROBLEMS,
              pProblem);
        module = pModule;
        dependency = pDependency;
    }

    public void actionPerformed(AnActionEvent e) {
        IDEUtils.runCommand(module, dependencyAdder);
    }

    private class AddDependencyToIdeaRunnable implements Runnable {
        protected final LibraryTablesRegistrar libTableMgr = LibraryTablesRegistrar.getInstance();

        public void run() {
            final Project project = module.getProject();
            final LibraryTable libTable = libTableMgr.getLibraryTable(project);

            //
            //if a library with the name of the dependency already exists,
            //use it. Otherwise, create a new one
            //
            Library lib = IDEUtils.findLibrary(project, dependency);
            if (lib == null)
                lib = libTable.createLibrary(dependency.getArtifact());

            //
            //get the library's modifiable model
            //
            final Library.ModifiableModel model = lib.getModifiableModel();

            //
            //find the local repository for the module
            //
            final VirtualFile localRepo = RepositoryUtils.getLocalRepository(module);

            //
            //calculate the dependency's location in the local repository
            //
            final String relPath = RepositoryUtils.getDependencyRelativePath(dependency);
            final VirtualFile depFile = localRepo.findFileByRelativePath(relPath);
            if (depFile == null) {
                final String msg = RES.get("dep.missing.problem.desc",
                                           dependency.getArtifact());
                UIUtils.showError(module, msg);
                return;
            }

            //
            //add the dependency jar into the library, and commit the change
            //
            model.addRoot(depFile, OrderRootType.CLASSES);
            model.commit();

            //
            //add the library to the module and commit the change
            //
            final ModuleRootManager modRootMgr = ModuleRootManager.getInstance(module);
            final ModifiableRootModel moduleRootModel = modRootMgr.getModifiableModel();
            moduleRootModel.addLibraryEntry(lib);
            moduleRootModel.commit();
        }
    }
}
