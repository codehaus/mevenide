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


package org.codehaus.mevenide.idea.build.embedder;

import org.codehaus.mevenide.idea.build.LogHelper;
import org.codehaus.mevenide.idea.build.LogListener;
import org.codehaus.mevenide.idea.build.MavenBuildFormattedLogger;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenEmbedderBuildLogger extends MavenBuildFormattedLogger {
    private List<LogListener> listeners = new ArrayList<LogListener>();
    private PrintStream stdout;
    private PrintStream stderr;


    public MavenEmbedderBuildLogger() {
        // preserve old stdout/stderr streams in case they might be useful
        stdout = System.out;
        stderr = System.err;
        PrintStream newStdout = new PrintStream(
             new FilteredStream(
               new ByteArrayOutputStream()));
        System.setOut(new PrintStream(newStdout, true));
        System.setErr(new PrintStream(newStdout, true));
    }

    public void restoreStreams() {
      System.setOut(stdout);
      System.setErr(stderr);
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
            LogHelper.notifyListeners(listeners, "[DEBUG] ", string, throwable,
                LogListener.OUTPUT_TYPE_NORMAL);
        }
    }

    public void info(String string, Throwable throwable) {
        if (isInfoEnabled()) {
            LogHelper
                .notifyListeners(listeners, "[INFO] ", string, throwable,
                    LogListener.OUTPUT_TYPE_NORMAL);
        }
    }

    public void warn(String string, Throwable throwable) {
        if (isWarnEnabled()) {
            LogHelper.notifyListeners(listeners, "[WARNING] ", string, throwable,
                LogListener.OUTPUT_TYPE_NORMAL);
        }
    }

    public void error(String string, Throwable throwable) {
        if (isErrorEnabled()) {
            LogHelper.notifyListeners(listeners, "[ERROR] ", string, throwable,
                LogListener.OUTPUT_TYPE_NORMAL);
        }
    }

    public void fatalError(String string, Throwable throwable) {
        if (isFatalErrorEnabled()) {
            LogHelper.notifyListeners(listeners, "[FATAL ERROR] ", string, throwable,
                LogListener.OUTPUT_TYPE_NORMAL);
        }
    }

    class FilteredStream extends FilterOutputStream {
        StringBuffer bufferedString = new StringBuffer();

        public FilteredStream(OutputStream aStream) {
            super(aStream);
        }

        public void write(byte b[], int off, int len) throws IOException {
            String aString = new String(b, off, len);
            if ((aString.length() >= 1 && aString.endsWith(System.getProperty("line.separator")))) {
                if (!aString.equalsIgnoreCase(System.getProperty("line.separator"))) {
                    bufferedString.append(aString);
                }
                LogHelper.notifyListeners(listeners, "[INFO] ", bufferedString.toString(), null,
                    LogListener.OUTPUT_TYPE_NORMAL);
                bufferedString = new StringBuffer();
            } else {
                bufferedString.append(aString);
            }
        }
    }
}
