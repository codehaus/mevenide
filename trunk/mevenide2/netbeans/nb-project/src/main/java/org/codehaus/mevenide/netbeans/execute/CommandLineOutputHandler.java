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
package org.codehaus.mevenide.netbeans.execute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.netbeans.api.progress.ProgressHandle;
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

    CommandLineOutputHandler() {
    }

    public CommandLineOutputHandler(InputOutput io, NbMavenProject proj, ProgressHandle hand, RunConfig config) {
        this();
        inputOutput = io;
        stdOut = inputOutput.getOut();
        logger = new Logger();
        initProcessorList(proj, config);
    }

    void setStdOut(InputStream inStr) {
        outTask = PROCESSOR.post(new Output(inStr));
    }

    void setStdErr(InputStream inStr) {
    //ignore for now..
    }

    void setStdIn(OutputStream in) {
    //TODO
    }

    void waitFor() {
        if (outTask != null) {
            outTask.waitFinished();
        }
    }

    private class Output implements Runnable {

        private static final String SEC_MOJO_EXEC = "mojo-execute"; //NOI18N
        private BufferedReader str;

        public Output(InputStream instream) {
            str = new BufferedReader(new InputStreamReader(instream));
        }

        public void run() {
            try {
                String line = str.readLine();
                while (line != null) {
                    if (line.startsWith("[INFO] ---------------------------------")) { //NOI18N
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
                    line = str.readLine();
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
    }
}
