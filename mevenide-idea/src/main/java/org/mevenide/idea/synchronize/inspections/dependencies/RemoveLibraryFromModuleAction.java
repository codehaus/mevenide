package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemoveLibraryFromModuleAction extends AbstractFixAction {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(RemoveLibraryFromModuleAction.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RemoveLibraryFromModuleAction.class);

    private final Runnable libraryRemover = new RemoveFromIdeaRunnable();
    private final Module module;
    private final VirtualFile libraryFile;
    private final String libraryFilePath;

    public RemoveLibraryFromModuleAction(final ProblemInfo pProblem,
                                         final Module pModule,
                                         final VirtualFile pLibraryFile) {
        super(RES.get("remove.lib.from.idea.action.name", pLibraryFile.getPath()),
              RES.get("remove.lib.from.idea.action.desc", pLibraryFile.getPath(), pModule.getName()),
              Icons.FIX_PROBLEMS,
              pProblem);
        module = pModule;
        libraryFile = pLibraryFile;
        libraryFilePath = FileUtils.fixPath(libraryFile);
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
                    if(lib == null)
                        files = libEntry.getFiles(OrderRootType.CLASSES);
                    else
                        files = lib.getFiles(OrderRootType.CLASSES);

                    for (VirtualFile file : files) {
                        final String path = FileUtils.fixPath(file);
                        if(file.equals(libraryFile) || libraryFilePath.equals(path))
                            model.removeOrderEntry(libEntry);
                    }
                }
            }

            if(model.isChanged())
                model.commit();
        }
    }
}
