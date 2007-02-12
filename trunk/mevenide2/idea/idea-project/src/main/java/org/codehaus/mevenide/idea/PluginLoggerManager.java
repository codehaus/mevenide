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

package org.codehaus.mevenide.idea;


import org.codehaus.mevenide.idea.console.LogMessage;
import org.codehaus.mevenide.idea.console.PluginLogger;
import org.codehaus.mevenide.idea.console.LoggerListener;
import org.codehaus.mevenide.idea.configuration.ConfigurationBean;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.PrintStream;

import org.apache.maven.embedder.MavenEmbedderLogger;
import com.intellij.openapi.project.Project;


/**
 * A class that manages logging within the Maven Reloaded plugin.
 *
 * @author bkate
 */
public class PluginLoggerManager {

    // the plugin config for this project
    private ConfigurationBean config;

    // loggers
    private Map<String, PluginLogger> pluginLoggers;
    private MavenEmbedderLogger embedderLogger;

    // listeners to log messages
    private Set<LoggerListener> listeners = new HashSet<LoggerListener>();

    // different log types to manage
    private enum LogType {EMBEDDER, PLUGIN};

    // map of instances sorted by project
    private static final Map<Project, PluginLoggerManager> instances = new ConcurrentHashMap<Project, PluginLoggerManager>();


    /**
     * Private constructor supports the factory pattern, use getInstance() to obtain a manager.
     */
    private PluginLoggerManager(Project proj) {

        config = PluginConfigurationManager.getInstance(proj).getConfig();

        pluginLoggers = new HashMap<String, PluginLogger>();
        embedderLogger = new MavenEmbedderLoggerImpl();
    }


    /**
     * Gets the singleton instance of the manager.
     *
     * @return The PluginLoggerManager that is managing the plugin pluginLoggers.
     */
    public static PluginLoggerManager getInstance(Project proj) {

        if (!instances.containsKey(proj)) {
            instances.put(proj, new PluginLoggerManager(proj));
        }

        return instances.get(proj);
    }


    /**
     * Releases the instance that is associated with the Project passed in.
     *
     * @param proj The Project that is associated with the instance being released.
     */
    public static void releaseInstance(Project proj) {
        instances.remove(proj);
    }


    /**
     * Gets the plugin logger responsible for handling the logging of a specific class type.
     *
     * @param loggingClass The class that is doing the logging.
     *
     * @return A logger that handles log messages for the given class.
     */
    public PluginLogger getPluginLogger(Class loggingClass) {
        return getPluginLogger(loggingClass.getName());
    }


    /**
     * Gets a plugin logger by name.
     *
     * @param name The logger name.
     *
     * @return The PluginLogger that has the requested name.
     */
    public PluginLogger getPluginLogger(String name) {

        // get the logger if it already exists
        if (pluginLoggers.containsKey(name)) {
            return pluginLoggers.get(name);
        }

        // new logger
        PluginLoggerImpl logger = new PluginLoggerImpl(name);

        pluginLoggers.put(name, logger);

        return logger;
    }


    /**
     * Gets the maven embedder logger in use.
     *
     * @return The logger that can be used to log messages from within the maven embedder.
     */
    public MavenEmbedderLogger getEmbedderLogger() {
        return embedderLogger;
    }


    /**
     * Adds a log listener.
     *
     * @param listener A LoggerListener that can handle log messages.
     */
    public void addListener(LoggerListener listener) {
        listeners.add(listener);
    }


    /**
     * Removes a log listener.
     *
     * @param listener A LoggerListener that will no longer handle log messages.
     */
    public void removeListener(LoggerListener listener) {
        listeners.remove(listener);
    }


    /**
     * Notifies all log listeners that there is a new log message.
     *
     * @param message The message to be logged.
     * @param log The log type, EMBEDDER or PLUGIN.
     */
    private void notifyListeners(LogMessage message, LogType log) {

        for (LoggerListener listener : listeners) {

            switch(log) {

                case EMBEDDER:
                    listener.logEmbedderMessage(message);
                    break;

                case PLUGIN:
                default:
                    listener.logPluginMessage(message);
                    break;
            }
        }
    }


    /**
     * An implementation of the PluginLogger interface that works with the log listeners to export messages.
     */
    private class PluginLoggerImpl implements PluginLogger {

        // name of the logger
        private String loggerName;


        /**
         * Default constructor that takes the name of the logger.
         *
         * @param name The unique name of this logger.
         */
        public PluginLoggerImpl(String name) {
            loggerName = name;
        }


        /** {@inheritDoc} */
        public void debug(String message) {
            debug(message, null);
        }


        /** {@inheritDoc} */
        public void debug(String message, Throwable error) {

            if (isLogEnabled(LogMessage.DEBUG)) {
                notifyListeners(new LogMessage(format(message, error), LogMessage.DEBUG), LogType.PLUGIN);
            }
        }


        /** {@inheritDoc} */
        public void info(String message) {
            info(message, null);
        }


        /** {@inheritDoc} */
        public void info(String message, Throwable error) {

            if (isLogEnabled(LogMessage.INFO)) {
                notifyListeners(new LogMessage(format(message, error), LogMessage.INFO), LogType.PLUGIN);
            }
        }


        /** {@inheritDoc} */
        public void warn(String message) {
            warn(message, null);
        }


        /** {@inheritDoc} */
        public void warn(String message, Throwable error) {

            if (isLogEnabled(LogMessage.WARN)) {
                notifyListeners(new LogMessage(format(message, error), LogMessage.WARN), LogType.PLUGIN);
            }
        }


        /** {@inheritDoc} */
        public void error(String message) {
            error(message, null);
        }


        /** {@inheritDoc} */
        public void error(String message, Throwable error) {

            if (isLogEnabled(LogMessage.ERROR)) {
                notifyListeners(new LogMessage(format(message, error), LogMessage.ERROR), LogType.PLUGIN);
            }
        }


        /** {@inheritDoc} */
        public void fatal(String message) {
            fatal(message, null);
        }


        /** {@inheritDoc} */
        public void fatal(String message, Throwable error) {

            if (isLogEnabled(LogMessage.FATAL)) {
                notifyListeners(new LogMessage(format(message, error), LogMessage.FATAL), LogType.PLUGIN);
            }
        }


        /**
         * Determines if logging is enabled at the given level.
         *
         * @param level The level desired.
         *
         * @return True if the current logging level will support the level passed in, false otherwise.
         */
        private boolean isLogEnabled(int level) {
            return (level >= config.getLogLevel());
        }


        /**
         * Formats a log message to have the name of the logger prepended.
         *
         * @param message The original message.
         * @param t An error that can be included in the message.
         *
         * @return The message prepended with the logger name.
         */
        private String format(String message, Throwable t) {

            // no error, just return the message
            if (t == null) {
                return message;
            }

            // do some trickiness to get a stack trace printed...
            LogPrintStream stream = new LogPrintStream();

            t.printStackTrace(stream);

            // append the stack trace to the message
            return loggerName + ": " + message + "\n" + stream.getStreamContents();
        }
    }


    /**
     * An implementation of Maven's embedder logger that logs messages to the logger listeners.
     */
    private class MavenEmbedderLoggerImpl implements MavenEmbedderLogger {

        /** {@inheritDoc} */
        public void debug(String message) {
            debug(message, null);
        }


        /** {@inheritDoc} */
        public void debug(String message, Throwable throwable) {

            if (isLogEnabled(LogMessage.DEBUG)) {
                notifyListeners(new LogMessage(format(message, throwable), LogMessage.DEBUG), LogType.EMBEDDER);
            }
        }


        /** {@inheritDoc} */
        public boolean isDebugEnabled() {
            return isLogEnabled(LogMessage.DEBUG);
        }


        /** {@inheritDoc} */
        public void info(String message) {
            info(message, null);
        }


        /** {@inheritDoc} */
        public void info(String message, Throwable throwable) {

            if (isLogEnabled(LogMessage.INFO)) {
                notifyListeners(new LogMessage(format(message, throwable), LogMessage.INFO), LogType.EMBEDDER);
            }
        }


        /** {@inheritDoc} */
        public boolean isInfoEnabled() {
            return isLogEnabled(LogMessage.INFO);
        }


        /** {@inheritDoc} */
        public void warn(String message) {
            warn(message, null);
        }


        /** {@inheritDoc} */
        public void warn(String message, Throwable throwable) {

            if (isLogEnabled(LogMessage.WARN)) {
                notifyListeners(new LogMessage(format(message, throwable), LogMessage.WARN), LogType.EMBEDDER);
            }
        }


        /** {@inheritDoc} */
        public boolean isWarnEnabled() {
            return isLogEnabled(LogMessage.WARN);
        }


        /** {@inheritDoc} */
        public void error(String message) {
            error(message, null);
        }


        /** {@inheritDoc} */
        public void error(String message, Throwable throwable) {

            if (isLogEnabled(LogMessage.ERROR)) {
                notifyListeners(new LogMessage(format(message, throwable), LogMessage.ERROR), LogType.EMBEDDER);
            }
        }


        /** {@inheritDoc} */
        public boolean isErrorEnabled() {
            return isLogEnabled(LogMessage.ERROR);
        }


        /** {@inheritDoc} */
        public void fatalError(String message) {
            fatalError(message, null);
        }


        /** {@inheritDoc} */
        public void fatalError(String message, Throwable throwable) {

            if (isLogEnabled(LogMessage.FATAL)) {
                notifyListeners(new LogMessage(format(message, throwable), LogMessage.FATAL), LogType.EMBEDDER);
            }
        }


        /** {@inheritDoc} */
        public boolean isFatalErrorEnabled() {
            return isLogEnabled(LogMessage.FATAL);
        }


        /**
         * {@inheritDoc}
         *
         * Does nothing in this implementation.
         */
        public void setThreshold(int threshold) {
        }


        /** {@inheritDoc} */
        public int getThreshold() {
            return config.getLogLevel();
        }


        /**
         * A method that formats a message (and possibly a throwable) into a single string.
         *
         * @param message The textual message.
         * @param t A Trhowable that may accompany the message (can be null).
         *
         * @return A single string that holds the contents of combined message.
         */
        private String format(String message, Throwable t) {

            // no error, just return the message
            if (t == null) {
                return message;
            }

            // do some trickiness to get a stack trace printed...
            LogPrintStream stream = new LogPrintStream();

            t.printStackTrace(stream);

            // append the stack trace to the message
            return message + "\n" + stream.getStreamContents();
        }


        /**
         * Determines if logging is enabled at the given level.
         *
         * @param level The level desired.
         *
         * @return True if the current logging level will support the level passed in, false otherwise.
         */
        private boolean isLogEnabled(int level) {
            return (level >= config.getLogLevel());
        }
    }


    /**
     * A class that fakes out a PrintStream to log most of its data to a StringBuffer. The contents can then be
     * accessed and used as a String. This is highly unorthodox, and probably a little unsafe...
     */
    private class LogPrintStream extends PrintStream {

        // the internal data structure
        private StringBuffer log = new StringBuffer();


        /**
         * Default constructor, all un-overridden functionality will fall through to System.out...
         */
        public LogPrintStream() {
            super(System.out);
        }


        /** {@inheritDoc} */
        public void print(boolean b) {
            print(Boolean.toString(b));
        }


        /** {@inheritDoc} */
        public void print(char c) {
            print(Character.toString(c));
        }


        /** {@inheritDoc} */
        public void print(int i) {
            print(Integer.toString(i));
        }


        /** {@inheritDoc} */
        public void print(long l) {
            print(Long.toString(l));
        }


        /** {@inheritDoc} */
        public void print(double v) {
            print(Double.toString(v));
        }


        /** {@inheritDoc} */
        public void print(float v) {
            print(Float.toString(v));
        }


        /** {@inheritDoc} */
        public void print(char[] chars) {
            print(String.valueOf(chars));
        }


        /** {@inheritDoc} */
        public void print(String string) {
            log.append(string);
        }


        /** {@inheritDoc} */
        public void print(Object object) {
            print(object.toString());
        }


        /** {@inheritDoc} */
        public void println() {
            log.append("\n");
        }


        /** {@inheritDoc} */
        public void println(boolean b) {
            print(b);
            println();
        }


        /** {@inheritDoc} */
        public void println(char c) {
            print(c);
            println();
        }


        /** {@inheritDoc} */
        public void println(int i) {
            print(i);
            println();
        }


        /** {@inheritDoc} */
        public void println(long l) {
            print(l);
            println();
        }


        /** {@inheritDoc} */
        public void println(float v) {
            print(v);
            println();
        }


        /** {@inheritDoc} */
        public void println(double v) {
            print(v);
            println();
        }


        /** {@inheritDoc} */
        public void println(char[] chars) {
            print(chars);
            println();
        }


        /** {@inheritDoc} */
        public void println(String string) {
            print(string);
            println();
        }


        /** {@inheritDoc} */
        public void println(Object object) {
            print(object);
            println();
        }


        /**
         * Gets the contents of the logger stream.
         *
         * @return The contents that were written to the stream.
         */
        public String getStreamContents() {
            return log.toString();
        }
    }

}

