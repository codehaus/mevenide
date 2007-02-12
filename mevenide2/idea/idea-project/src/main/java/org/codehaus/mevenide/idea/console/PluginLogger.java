/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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
