package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.FileProblemInfo;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemoveLibraryFromModuleAction extends AbstractFixAction<FileProblemInfo> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RemoveLibraryFromModuleAction.class);

    private final Runnable libraryRemover = new RemoveFromIdeaRunnable();

    /**
     * The module in which the problem was discovered.
     */
    private final Module module;

    public RemoveLibraryFromModuleAction(final FileProblemInfo pProblem,
                                         final Module pModule) {
        super(RES.get("remove.lib.from.idea.action.name", pProblem.getFile().getPath()),
              RES.get("remove.lib.from.idea.action.desc",
                      pProblem.getFile().getPresentableName(),
                      pModule.getName()),
              Icons.FIX_PROBLEMS,
              pProblem);
        module = pModule;
    }

    public void actionPerformed(AnActionEvent e) {
        IDEUtils.runCommand(module, libraryRemover);
    }

    private class RemoveFromIdeaRunnable implements Runnable {
        public void run() {
            final ModuleRootManager rootMgr = ModuleRootManager.getInstance(module);
            final ModifiableRootModel model = rootMgr.getModifiableModel();

            final OrderEntry[] entries = model.getOrderEntries();
            for (OrderEntry entry : entries) {
                if (entry instanceof LibraryOrderEntry) {
                    final LibraryOrderEntry libEntry = (LibraryOrderEntry) entry;
                    final Library lib = libEntry.getLibrary();

                    final VirtualFile[] files;
                    if (lib == null)
                        files = libEntry.getFiles(OrderRootType.CLASSES);
                    else
                        files = lib.getFiles(OrderRootType.CLASSES);

                    for (VirtualFile file : files)
                        if (file.equals(problem.getFile()))
                            model.removeOrderEntry(libEntry);
                }
            }

            if (model.isChanged())
                model.commit();
        }
    }
}
