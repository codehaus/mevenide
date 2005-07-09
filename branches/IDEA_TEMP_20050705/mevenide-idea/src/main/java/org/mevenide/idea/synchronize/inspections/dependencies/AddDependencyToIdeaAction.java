package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.Artifact;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ModuleArtifactProblemInfo;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class AddDependencyToIdeaAction extends AbstractFixAction<ModuleArtifactProblemInfo> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddDependencyToIdeaAction.class);

    private final Runnable dependencyAdder = new AddDependencyToIdeaRunnable();

    public AddDependencyToIdeaAction(final ModuleArtifactProblemInfo pProblem) {
        super(RES.get("add.dep2idea.action.name", pProblem.getArtifact()),
              RES.get("add.dep2idea.action.desc", pProblem.getArtifact()),
              Icons.FIX_PROBLEMS,
              pProblem);
    }

    public void actionPerformed(AnActionEvent e) {
        IDEUtils.runCommand(problem.getModule(), dependencyAdder);
    }

    private class AddDependencyToIdeaRunnable implements Runnable {
        protected final LibraryTablesRegistrar libTableMgr = LibraryTablesRegistrar.getInstance();

        public void run() {
            final Project project = problem.getProject();
            final Module module = problem.getModule();
            final LibraryTable libTable = libTableMgr.getLibraryTable(project);

            //
            //if a library with the name of the dependency already exists,
            //use it. Otherwise, create a new one
            //
            final Artifact artifact = problem.getArtifact();
            Library lib = IDEUtils.findLibrary(project, artifact);
            if (lib == null)
                lib = libTable.createLibrary(artifact.toString());

            //
            //get the library's modifiable model
            //
            final Library.ModifiableModel model = lib.getModifiableModel();

            //
            //calculate the dependency's location in the local repository
            //
            final PomRepoManager repoMgr = PomRepoManager.getInstance(project);
            final VirtualFile file = repoMgr.findFile(problem.getPomUrl(), artifact);
            if (file == null || !file.isValid()) {
                final String msg = RES.get("dep.missing.problem.desc", artifact);
                UIUtils.showError(project, msg);
                return;
            }

            //
            //add the dependency jar into the library, and commit the change
            //
            model.addRoot(file, OrderRootType.CLASSES);
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
