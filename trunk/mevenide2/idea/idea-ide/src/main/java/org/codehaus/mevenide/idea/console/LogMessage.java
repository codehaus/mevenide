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
 * A container for log message information.
 *
 * @author bkate
 */
public class LogMessage {

    // the data
    private String message;
    private int severity = INFO;

    // different levels of logging
    public static final int OFF   = 5;
    public static final int FATAL = 4;
    public static final int ERROR = 3;
    public static final int WARN  = 2;
    public static final int INFO  = 1;
    public static final int DEBUG = 0;


    /**
     * Constructor that takes the message text. Defaults to INFO log level.
     *
     * @param text The log message text.
     */
    public LogMessage(String text) {
        message = text;
    }


    /**
     * Constructor that takes the message text and a logging level.
     *
     * @param text The log message text.
     * @param level The level of the log message.
     */
    public LogMessage(String text, int level) {
        message = text;
        severity = level;
    }


    /**
     * Gets the string message.
     *
     * @return The message text.
     */
    public String getMessage() {
        return message;
    }


    /**
     * Gets the log message level.
     *
     * @return The logging level of this message.
     */
    public int getLogLevel() {
        return severity;
    }


    /**
     * Converts a logging level to a string identifier.
     *
     * @param level The logging level to convert.
     *
     * @return The logging level as a string (i.e. INFO, DEBUG, etc...).
     */
    public String levelToString(int level) {

        switch(level) {

            case FATAL:
                return "FATAL";

            case ERROR:
                return "ERROR";

            case WARN:
                return "WARN";

            case DEBUG:
                return "DEBUG";

            case INFO:
            default:
                return "INFO";
        }
    }


    /**
     * Gets the message as a human readable string.
     *
     * @return The message as a string.
     */
    public String toString() {
        return "[" + levelToString(severity) + "] - " + message;
    }

}
