package org.mevenide.idea.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.repository.Artifact;

/**
 * @author Arik
 */
public abstract class IDEUtils {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(IDEUtils.class);

    public static void runCommand(final Module pModule,
                                  final Runnable pRunnable) {
        runCommand(pModule.getProject(), pRunnable);
    }

    public static void runCommand(final Project pProject,
                                  final Runnable pRunnable) {

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                CommandProcessor.getInstance().executeCommand(
                    pProject,
                    pRunnable,
                    "Maven Command",
                    "POM");
            }
        });
    }

    public static ProgressIndicator getProgressIndicator() {
        final ProgressManager mgr = ProgressManager.getInstance();
        if (!mgr.hasProgressIndicator()) {
            LOG.warn("No progress indicator.");
            return null;
        }
        else
            return mgr.getProgressIndicator();
    }

    public static Library findLibrary(final Project pProject, final Artifact pDependency) {
        final LibraryTablesRegistrar libTableMgr = LibraryTablesRegistrar.getInstance();
        final LibraryTable libTable = libTableMgr.getLibraryTable(pProject);

        String libName = pDependency.toString();
        Library lib = libTable.getLibraryByName(libName);
        if (lib == null) {
            String type = pDependency.getType();
            if (type == null || type.trim().length() == 0)
                type = "jar";

            if (libName.endsWith("." + type)) {
                final int newLength = libName.length() - 1 - type.length();
                libName = libName.substring(0, newLength);
                lib = libTable.getLibraryByName(libName);
            }
        }

        return lib;
    }
}
