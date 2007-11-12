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



package org.codehaus.mevenide.idea.build;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class LogHelper {
    private static final Logger LOG = Logger.getLogger(LogHelper.class);

    public static void notifyListeners(List<LogListener> listeners, String prependString, String message,
                                       Throwable throwable, int outputType) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(prependString).append(message).append(System.getProperty("line.separator"));
        notify(listeners, throwable, buffer, outputType, false);
    }

    public static void notifyListeners(List<LogListener> listeners, String message, Throwable throwable,
                                       int outputType) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(message).append(System.getProperty("line.separator"));
        notify(listeners, throwable, buffer, outputType, false);
    }

    public static void notifyListeners(List<LogListener> listeners, String message, Throwable throwable,
                                       int outputType, boolean isRaiseFatalError) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(message).append(System.getProperty("line.separator"));
        notify(listeners, throwable, buffer, outputType, isRaiseFatalError);
    }

    private static void notify(List<LogListener> listeners, Throwable throwable, StringBuffer buffer, int outputType,
                               boolean isRaisFatalError) {
        String stackTrace = null;

        if (throwable != null) {
            Throwable cause = ExceptionUtils.getCause(throwable);

            stackTrace = ExceptionUtils.getStackTrace((cause != null)
                    ? ExceptionUtils.getCause(throwable)
                    : throwable);
            LOG.error(stackTrace);
        }

        for (LogListener listener : listeners) {
            listener.printMessage(buffer.toString(), stackTrace, outputType);
        }

        if (isRaisFatalError) {
            for (LogListener listener : listeners) {
                listener.raiseFatalErrorMessage(buffer.toString(), stackTrace);
            }
        }
    }
}
