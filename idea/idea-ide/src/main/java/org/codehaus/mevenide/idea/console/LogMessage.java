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
 * A container for log message information.
 *
 * @author bkate
 */
public class LogMessage {

    // the data
    private String message;
    private int severity = INFO;

    // different levels of logging
    public static final int OFF = 5;
    public static final int FATAL = 4;
    public static final int ERROR = 3;
    public static final int WARN = 2;
    public static final int INFO = 1;
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
        switch (level) {
            case FATAL :
                return "FATAL";

            case ERROR :
                return "ERROR";

            case WARN :
                return "WARN";

            case DEBUG :
                return "DEBUG";

            case INFO :
            default :
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
