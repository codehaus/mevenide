package org.mevenide.idea.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arik
 */
public abstract class ModuleUtils {
    public static boolean isFileInClasspath(final Module pModule,
                                            final VirtualFile pFile) {
        final String depFilePath = pFile.getPath();
        final VirtualFile[] files = getModuleClasspath(pModule);
        for (VirtualFile file : files) {
            final String libFilePath = FileUtils.fixPath(file);
            if (pFile.equals(file) || depFilePath.equals(libFilePath))
                return true;
        }
        return false;
    }

    public static VirtualFile[] getModuleClasspath(final Module pModule) {
        return getModuleClasspath(pModule, false);
    }

    public static VirtualFile[] getModuleClasspath(final Module pModule,
                                                   final boolean pIncludeJdk) {
        final ModuleRootManager rootMgr = ModuleRootManager.getInstance(pModule);
        final ModifiableRootModel model = rootMgr.getModifiableModel();

        final Set<VirtualFile> files = new HashSet<VirtualFile>();
        final OrderEntry[] entries = model.getOrderEntries();
        for (OrderEntry entry : entries) {
            if (entry instanceof LibraryOrderEntry) {
                final LibraryOrderEntry libEntry = (LibraryOrderEntry) entry;
                final Library lib = libEntry.getLibrary();
                if (lib == null)
                    Collections.addAll(files, libEntry.getFiles(OrderRootType.CLASSES));
                else
                    Collections.addAll(files, lib.getFiles(OrderRootType.CLASSES));
            }
            else if (pIncludeJdk || !(entry instanceof JdkOrderEntry))
                Collections.addAll(files, entry.getFiles(OrderRootType.CLASSES));
        }

        return files.toArray(new VirtualFile[files.size()]);
    }
}
