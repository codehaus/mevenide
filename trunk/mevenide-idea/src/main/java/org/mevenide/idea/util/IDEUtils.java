package org.mevenide.idea.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;

/**
 * @author Arik
 */
public abstract class IDEUtils {

    public static void runCommand(final Project pProject,
                                  final Runnable pRunnable) {

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                CommandProcessor.getInstance().executeCommand(
                        pProject,
                        pRunnable,
                        "Add Dependency",
                        "POM");
            }
        });
    }
}
