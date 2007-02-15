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

import org.apache.maven.embedder.AbstractMavenEmbedderLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildLogger extends AbstractMavenEmbedderLogger implements IMavenBuildLogger {
    private int threshold = IMavenBuildLogger.LEVEL_INFO;
    private List<LogListener> listeners = new ArrayList<LogListener>();
    private boolean outputPaused = false;
    private StringBuffer buffer = new StringBuffer();
    private String name;
    private int outputType;

    public int getOutputType() {
        return outputType;
    }

    public void setOutputType(int outputType) {
        this.outputType = outputType;
    }

    public boolean isOutputPaused() {
        return outputPaused;
    }

    public void setOutputPaused(boolean outputPaused) {
        this.outputPaused = outputPaused;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }

    public void debug(String message) {
        debug(message, null);
    }

    public boolean isDebugEnabled() {
        return threshold <= IMavenBuildLogger.LEVEL_DEBUG;
    }

    public void info(String message) {
        info(message, null);
    }

    public boolean isInfoEnabled() {
        return threshold <= IMavenBuildLogger.LEVEL_INFO;
    }

    public void warn(String message) {
        warn(message, null);
    }

    public boolean isWarnEnabled() {
        return threshold <= IMavenBuildLogger.LEVEL_WARN;
    }

    public void error(String message) {
        error(message, null);
    }

    public boolean isErrorEnabled() {
        return threshold <= IMavenBuildLogger.LEVEL_ERROR;
    }

    public void fatalError(String message) {
        fatalError(message, null);
    }

    public boolean isFatalErrorEnabled() {
        return threshold <= IMavenBuildLogger.LEVEL_FATAL;
    }

    public void addListener(LogListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(LogListener listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    public void debug(String string, Throwable throwable) {
        if (isDebugEnabled()) {
            logMessage(string, throwable);
        }
    }

    public void info(String string, Throwable throwable) {
        if (isInfoEnabled()) {
            logMessage(string, throwable);
        }
    }

    public void warn(String string, Throwable throwable) {
        if (isWarnEnabled()) {
            logMessage(string, throwable);
        }
    }

    public void error(String string, Throwable throwable) {
        if (isErrorEnabled()) {
            logMessage(string, throwable);
        }
    }

    public void fatalError(String string, Throwable throwable) {
        if (isFatalErrorEnabled()) {
            logMessage(string, throwable);
        }
    }

    public void flushBuffer() {
        if (buffer.length() > 0) {
            LogHelper.notifyListeners(listeners, buffer.toString(), null, outputType);
        }

        buffer = new StringBuffer();
    }

    protected void logMessage(String string, Throwable throwable) {
        String message;

        if (!isOutputPaused()) {
            if (buffer.length() > 0) {
                message = buffer.toString();
                message = message + string;
                buffer = new StringBuffer();
            } else {
                message = string;
            }

            LogHelper.notifyListeners(listeners, message, throwable, outputType);
        } else {
            buffer.append(string).append(System.getProperty("line.separator"));
        }
    }

    protected void logMessageAndShowError(String string, Throwable throwable) {
        String message;

        if (!isOutputPaused()) {
            if (buffer.length() > 0) {
                message = buffer.toString();
                message = message + string;
                buffer = new StringBuffer();
            } else {
                message = string;
            }

            LogHelper.notifyListeners(listeners, message, throwable, outputType, true);
        } else {
            buffer.append(string).append(System.getProperty("line.separator"));
        }
    }

    protected boolean isValidThreshold(int threshold) {
        if (threshold == IMavenBuildLogger.LEVEL_DEBUG) {
            return true;
        }

        if (threshold == IMavenBuildLogger.LEVEL_INFO) {
            return true;
        }

        if (threshold == IMavenBuildLogger.LEVEL_WARN) {
            return true;
        }

        if (threshold == IMavenBuildLogger.LEVEL_ERROR) {
            return true;
        }

        if (threshold == IMavenBuildLogger.LEVEL_FATAL) {
            return true;
        }

        if (threshold == IMavenBuildLogger.LEVEL_DISABLED) {
            return true;
        }

        return false;
    }
}
