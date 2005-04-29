/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
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
