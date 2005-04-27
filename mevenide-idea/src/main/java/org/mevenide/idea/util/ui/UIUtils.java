package org.mevenide.idea.util.ui;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.module.Module;

/**
 * @author Arik
 */
public abstract class UIUtils {

    public static final String ERROR_TITLE = "Error";

    public static void showError(final String pMessage) {
        Messages.showErrorDialog(pMessage, ERROR_TITLE);
    }

    public static void showError(final Module pModule, final String pMessage) {
        showError(pModule.getProject(), pMessage);
    }

    public static void showError(final Project pProject, final String pMessage) {
        Messages.showErrorDialog(pProject, pMessage, ERROR_TITLE);
    }

    public static void showError(final Throwable pCause) {
        Messages.showErrorDialog(pCause.getMessage(), ERROR_TITLE);
    }

    public static void showError(final Module pModule, final Throwable pCause) {
        showError(pModule.getProject(), pCause);
    }

    public static void showError(final Project pProject, final Throwable pCause) {
        Messages.showErrorDialog(pProject, pCause.getMessage(), ERROR_TITLE);
    }
}
