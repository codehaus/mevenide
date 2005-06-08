package org.mevenide.idea.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public abstract class IDEUtils {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(IDEUtils.class);

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
}
