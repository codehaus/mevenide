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



package org.codehaus.mevenide.idea.console;

/**
 * A simple logging interface that resembles that of log4j.
 *
 * @author bkate
 */
public interface PluginLogger {

    /**
     * Logs a debugging message to the logger.
     *
     * @param message The message to be logged.
     */
    public void debug(String message);

    /**
     * Logs a debugging message to the logger.
     *
     * @param message The message to be logged.
     * @param error An error that occured to prompt the message.
     */
    public void debug(String message, Throwable error);

    /**
     * Logs an info message to the logger.
     *
     * @param message The message to be logged.
     */
    public void info(String message);

    /**
     * Logs an info message to the logger.
     *
     * @param message The message to be logged.
     * @param error An error that occured to prompt the message.
     */
    public void info(String message, Throwable error);

    /**
     * Logs a warning message to the logger.
     *
     * @param message The message to be logged.
     */
    public void warn(String message);

    /**
     * Logs a warning message to the logger.
     *
     * @param message The message to be logged.
     * @param error An error that occured to prompt the message.
     */
    public void warn(String message, Throwable error);

    /**
     * Logs an error message to the logger.
     *
     * @param message The message to be logged.
     */
    public void error(String message);

    /**
     * Logs an error message to the logger.
     *
     * @param message The message to be logged.
     * @param error An error that occured to prompt the message.
     */
    public void error(String message, Throwable error);

    /**
     * Logs a fatal message to the logger.
     *
     * @param message The message to be logged.
     */
    public void fatal(String message);

    /**
     * Logs a fatal message to the logger.
     *
     * @param message The message to be logged.
     * @param error An error that occured to prompt the message.
     */
    public void fatal(String message, Throwable error);
}
