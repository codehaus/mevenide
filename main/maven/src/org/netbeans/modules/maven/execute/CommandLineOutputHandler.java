/* ==========================================================================
 * Copyright 2007 Mevenide Team
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
package org.netbeans.modules.maven.execute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * handling of output coming from maven commandline builds
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class CommandLineOutputHandler extends AbstractOutputHandler {

    private static final RequestProcessor PROCESSOR = new RequestProcessor("Maven ComandLine Output Redirection", 5); //NOI18N
    private InputOutput inputOutput;
    private Pattern linePattern = Pattern.compile("\\[(DEBUG|INFO|WARN|ERROR|FATAL)\\] (.*)"); //NOI18N
    private Pattern startPattern = Pattern.compile("\\[INFO\\] \\[(.*):(.*)\\]"); //NOI18N
    private OutputWriter stdOut;
    //    private ProgressHandle handle;
    private String currentTag;
    Task outTask;
    private MavenEmbedderLogger logger;
    private Input inp;

    CommandLineOutputHandler() {
    }

    public CommandLineOutputHandler(InputOutput io, Project proj, ProgressHandle hand, RunConfig config) {
        this();
        inputOutput = io;
        stdOut = inputOutput.getOut();
        logger = new Logger();
        initProcessorList(proj, config);
    }

    void setStdOut(InputStream inStr) {
        outTask = PROCESSOR.post(new Output(inStr));
    }

    void setStdIn(OutputStream in) {
        inp = new Input(in, inputOutput);
        PROCESSOR.post(inp);
    }

    void waitFor() {
        inp.stopInput();
//        if (inTask != null) {
//            inTask.waitFinished();
//        }
        if (outTask != null) {
            outTask.waitFinished();
        }
    }

    private class Output implements Runnable {

        private static final String SEC_MOJO_EXEC = "mojo-execute"; //NOI18N
        private BufferedReader str;
        private boolean skipLF = false;

        public Output(InputStream instream) {
            str = new BufferedReader(new InputStreamReader(instream));
        }

        private String readLine() throws IOException {
            char[] char1 = new char[1];
            boolean isReady = true;
            StringBuffer buf = new StringBuffer();
            while (isReady) {
                int ret = str.read(char1);
                if (ret != 1) {
                     if (ret == -1 && buf.length() == 0) {
                         return null;
                     }
                    return buf.toString();
                }
                if (skipLF) {
                    skipLF = false;
                    if (char1[0] == '\n') {
                        continue;
                    }
                }
                if (char1[0] == '\n') {
                    return buf.toString();
                }
                if (char1[0] == '\r') {
                    skipLF = true;
                    return buf.toString();
                }
                buf.append(char1[0]);
                isReady = str.ready();
                if (!isReady) {
                    synchronized (this) {
                        try {
                            wait(500);
                        } catch (InterruptedException ex) {
                            Exceptions.printStackTrace(ex);
                        } finally {
                            if (!str.ready()) {
                                break;
                            }
                            isReady = true;
                        }
                    }

                }
            }
            return "&^#INCOMPLINE:" + buf.toString();

        }

        public void run() {
            try {

                String line = readLine();
                while (line != null) {
                    if (line.startsWith("&^#INCOMPLINE:")) {
                        stdOut.print(line.substring("&^#INCOMPLINE:".length()));
                        line = readLine();
                        continue;
                    }
                    if (line.startsWith("[INFO] Final Memory:")) { //NOI18N
                        // previous value [INFO] --------------- is too early, the compilation errors don't get processed in this case.
                        //heuristics..
                        if (currentTag != null) {
                            CommandLineOutputHandler.this.processEnd(getEventId(SEC_MOJO_EXEC, currentTag), stdOut);
                        }
                        currentTag = null;
                    }
                    Matcher match = startPattern.matcher(line);
                    if (match.matches()) {
                        String tag = match.group(1) + ":" + match.group(2); //NOi18N
                        if (currentTag != null) {
                            CommandLineOutputHandler.this.processEnd(getEventId(SEC_MOJO_EXEC, currentTag), stdOut);
                        }
                        CommandLineOutputHandler.this.processStart(getEventId(SEC_MOJO_EXEC, tag), stdOut);
                        currentTag = tag;
                    } else {
                        match = linePattern.matcher(line);
                        if (match.matches()) {
                            String level = match.group(1);
                            processLine(match.group(2), stdOut, "INFO".equals(level) ? "" : level); //NOI18N
                        } else {
                            // oh well..
                            processLine(line, stdOut, ""); //NOI18N
                        }
                    }
                    line = readLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    str.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    static class Input implements Runnable {

        private InputOutput inputOutput;
        private OutputStream str;
        private boolean stopIn = false;

        public Input(OutputStream out, InputOutput inputOutput) {
            str = out;
            this.inputOutput = inputOutput;
        }

        public void stopInput() {
            stopIn = true;
        }

        public void run() {
            Reader in = inputOutput.getIn();
            try {
                while (true) {
                    int read = in.read();
                    if (read != -1) {
                        str.write(read);
                        str.flush();
                    } else {
                        str.close();
                        return;
                    }
                    if (stopIn) {
                        return;
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    str.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    MavenEmbedderLogger getLogger() {
        return logger;
    }

    private class Logger implements MavenEmbedderLogger {

        private Logger() {
        }

        public void debug(String arg0) {
            inputOutput.getOut().println(arg0);
        }

        public void debug(String arg0, Throwable arg1) {
            inputOutput.getOut().println(arg0);
        }

        public boolean isDebugEnabled() {
            return true;
        }

        public void info(String arg0) {
            inputOutput.getOut().println(arg0);
        }

        public void info(String arg0, Throwable arg1) {
            inputOutput.getOut().println(arg0);
        }

        public boolean isInfoEnabled() {
            return true;
        }

        public void warn(String arg0) {
            inputOutput.getOut().println(arg0);
        }

        public void warn(String arg0, Throwable arg1) {
            inputOutput.getOut().println(arg0);
        }

        public boolean isWarnEnabled() {
            return true;
        }

        public void error(String arg0) {
            inputOutput.getErr().println(arg0);
        }

        public void error(String arg0, Throwable arg1) {
            inputOutput.getErr().println(arg0);
        }

        public boolean isErrorEnabled() {
            return true;
        }

        public void fatalError(String arg0) {
            inputOutput.getErr().println(arg0);
        }

        public void fatalError(String arg0, Throwable arg1) {
            inputOutput.getErr().println(arg0);
        }

        public boolean isFatalErrorEnabled() {
            return true;
        }

        public void setThreshold(int arg0) {
        }

        public int getThreshold() {
            return MavenEmbedderLogger.LEVEL_DEBUG;
        }

        public void close() {
        }
    }
}
