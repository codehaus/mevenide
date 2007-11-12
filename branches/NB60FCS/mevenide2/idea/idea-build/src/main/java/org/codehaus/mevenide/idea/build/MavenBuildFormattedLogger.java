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

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildFormattedLogger extends MavenBuildLogger {
    public void debug(String message, int outputType) {
        debug(message, null, outputType);
    }

    public void info(String message, int outputType) {
        info(message, null, outputType);
    }

    public void warn(String message, int outputType) {
        warn(message, null, outputType);
    }

    public void error(String message, int outputType) {
        error(message, null, outputType);
    }

    public void fatalError(String message, int outputType) {
        fatalError(message, null, outputType);
    }

    public void debug(String string, Throwable throwable, int outputType) {
        if (isDebugEnabled()) {
            setOutputType(outputType);
            logMessage(string, throwable);
        }
    }

    public void info(String string, Throwable throwable, int outputType) {
        if (isInfoEnabled()) {
            setOutputType(outputType);
            logMessage(string, throwable);
        }
    }

    public void warn(String string, Throwable throwable, int outputType) {
        if (isWarnEnabled()) {
            setOutputType(outputType);
            logMessage(string, throwable);
        }
    }

    public void error(String string, Throwable throwable, int outputType) {
        if (isErrorEnabled()) {
            setOutputType(outputType);
            logMessage(string, throwable);
        }
    }

    public void fatalError(String string, Throwable throwable, int outputType) {
        if (isFatalErrorEnabled()) {
            setOutputType(outputType);
            logMessageAndShowError(string, throwable);
        }
    }
}
