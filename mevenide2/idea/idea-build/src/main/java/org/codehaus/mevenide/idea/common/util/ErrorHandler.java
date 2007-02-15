/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.common.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class ErrorHandler {
    private static final Logger LOG = Logger.getLogger(ErrorHandler.class);

    /**
     * Processes an error by showing an error dialog and logging the error message including
     * stacktrace.
     *
     * @param project The project.
     * @param error   The error.
     */
    public static void processAndShowError(Project project, Throwable error) {
        LOG.error(error.getMessage(), error);
        Messages.showErrorDialog(project, (ExceptionUtils.getRootCause(error) != null)
                                          ? ExceptionUtils.getFullStackTrace(ExceptionUtils.getRootCause(error))
                                          : ExceptionUtils.getFullStackTrace(
                                          error), "Maven 2 Integration plugin error");
    }

    /**
     * Processes an error by showing an error dialog and logging the error message.
     *
     * @param project        The project.
     * @param error          The error.
     * @param showStacktrace true, if the stacktrace should be show in the error dialog, otherwise
     */
    public static void processAndShowError(Project project, Throwable error, boolean showStacktrace) {
        LOG.error(error.getMessage(), error);
        Messages.showErrorDialog(project, showStacktrace
                                          ? (ExceptionUtils.getRootCause(error) != null)
                ? ExceptionUtils.getFullStackTrace(ExceptionUtils.getRootCause(error))
                : ExceptionUtils.getFullStackTrace(error)
                                          : error.getMessage(), "Maven-2 Integration plugin error");
    }

    /**
     * Processes an error by showing an error dialog and logging the error message.
     *
     * @param message The error message.
     */
    public static void showErrorMessage(String message) {
        LOG.error(message);

//      Messages.showErrorDialog(component, message, "Maven-2 Integration plugin error");
    }
}
