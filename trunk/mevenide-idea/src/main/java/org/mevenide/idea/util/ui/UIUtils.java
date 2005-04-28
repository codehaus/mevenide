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

    }

    public static void showError(final Module pModule,
                                 final String pMessage,
                                 final Throwable pCause) {
        showError(pModule.getProject(), pMessage, pCause);
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

    public static void showError(final Project pProject,
                                 final String pMessage,
                                 final Throwable pCause) {
        Messages.showErrorDialog(pProject, pMessage + " (" + pCause.getMessage() + ")", ERROR_TITLE);
    }

    public static String buildMessage(final Throwable pCause, final String pDefault) {
        if (pCause == null)
            return pDefault;

        final String msg = pCause.getMessage();
        if (msg == null || msg.trim().length() == 0)
            return pDefault;

        return msg;
    }
}
