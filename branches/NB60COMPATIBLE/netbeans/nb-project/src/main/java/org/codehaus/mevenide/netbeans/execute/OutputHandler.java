/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.monitor.event.EventMonitor;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.exec.MyLifecycleExecutor;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.openide.execution.ExecutorTask;
import org.openide.util.io.NullOutputStream;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * handling of output coming from maven builds.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class OutputHandler extends AbstractOutputHandler implements EventMonitor, MavenEmbedderLogger {
    private static final String SEC_MOJO_EXEC = "mojo-execute";//NOI18N
    private static final String SEC_PRJ_EXEC = "project-execute";//NOI18N
    private static final String SEC_REAC_EXEC = "reactor-execute";//NOI18N
    
    private InputOutput inputOutput;
    
    private OutputWriter stdOut, stdErr;
    
    private StreamBridge out, err;
    
    
    private int threshold = MavenEmbedderLogger.LEVEL_INFO;
    

    private AggregateProgressHandle handle;

    private boolean doCancel = false;

    private ExecutorTask task;
    
    
    private List<ProgressContributor> progress = new ArrayList<ProgressContributor>();
    private boolean isReactor = false;
    private ProgressContributor cont;
    private int total = 10;
    private int count = 0;
    
    OutputHandler() {
    }
    
    /**
     * @deprecated for tests only..
     */
    void setup(HashMap procs, OutputWriter std, OutputWriter err) {
        processors = procs;
        stdErr = err;
        stdOut = std;
    }
    
    public OutputHandler(InputOutput io, NbMavenProject proj, AggregateProgressHandle hand)    {
        this();
        inputOutput = io;
        handle = hand;
        stdOut = inputOutput.getOut();
        stdErr = inputOutput.getErr();
        
        initProcessorList(proj);
    }
    
    public void errorEvent(String eventName, String target, long l, Throwable throwable) {
        processFail(getEventId(eventName, target), stdErr);
    }
    
    public void startEvent(String eventName, String target, long l)    {
        processStart(getEventId(eventName, target), stdOut);
        if (handle != null) {
            if (SEC_REAC_EXEC.equals(eventName)) { //NOI18N
                isReactor = true;
            }
            if (isReactor && SEC_PRJ_EXEC.equals(eventName)) { //NOI18N
                isReactor = false;
                int bufferSize = MyLifecycleExecutor.getAffectedProjects().size();
                for (int i = 0; i < bufferSize; i++) {
                    ProgressContributor contr = AggregateProgressFactory. createProgressContributor("project" + i); //NOI18N
                    handle.addContributor(contr);
                    progress.add(contr);
                }
            }
            if (SEC_PRJ_EXEC.equals(eventName)) { //NOI18N
                if (progress.size() > 0) {
                    cont = progress.remove(0);
                    cont.start(1);
                } else {
                    cont = AggregateProgressFactory. createProgressContributor("project"); //NOI18N
                }
                // instead of one, possibly try to guess the number of steps in project build..
                count = 0;
                cont.start(total);
            }
            if (SEC_MOJO_EXEC.equals(eventName)) {
                count = count + 1;
                if (count < total) {
                    cont.progress(target, count);
                }
            }
        }
        if (cont != null) {
            cont.progress(target);
        }
    }
    
    public void endEvent(String eventName, String target, long l)    {
        processEnd(getEventId(eventName, target), stdOut);
        if (SEC_PRJ_EXEC.equals(eventName) &&  cont != null) { //NOI18N
            total = count;
            cont.finish();
        }
        if (doCancel) {
            assert task != null;
            task.stop();
        }
    }
    
    public void debug(String string) {
        if (isDebugEnabled()) {
            processMultiLine(string, stdOut, "DEBUG");//NOI18N
        }
    }
    
    public void debug(String string, Throwable throwable) {
        if (isDebugEnabled()) {
            processMultiLine(string, stdOut, "DEBUG");//NOI18N
            throwable.printStackTrace(stdOut);
        }
    }
    
    public boolean isDebugEnabled()    {
        return threshold == MavenEmbedderLogger.LEVEL_DEBUG;
    }
    
    public void info(String string)    {
        processMultiLine(string, stdOut, /*"INFO"*/ "");//NOI18N
    }
    
    public void info(String string, Throwable throwable)    {
        processMultiLine( string, stdOut, /*"INFO"*/ "");//NOI18N
        throwable.printStackTrace(stdOut);
    }
    
    public boolean isInfoEnabled()    {
        return true;
    }
    
    public void warn(String string)    {
        processMultiLine(string, stdOut, "WARN");//NOI18N
    }
    
    public void warn(String string, Throwable throwable)    {
        processMultiLine(string, stdOut, "WARN");//NOI18N
        throwable.printStackTrace(stdOut);
    }
    
    public boolean isWarnEnabled()    {
        return true;
    }
    
    public void error(String string)    {
        processMultiLine(string, stdErr, "ERROR");//NOI18N
    }
    
    public void error(String string, Throwable throwable)    {
        processMultiLine(string, stdErr, "ERROR");//NOI18N
        throwable.printStackTrace(stdErr);
    }
    
    public boolean isErrorEnabled()    {
        return true;
    }
    
    public void fatalError(String string)    {
        processMultiLine(string, stdErr, "FATAL");//NOI18N
    }
    
    public void fatalError(String string, Throwable throwable)    {
        processMultiLine(string, stdErr, "FATAL");//NOI18N
        throwable.printStackTrace(stdErr);
    }
    
    public boolean isFatalErrorEnabled()    {
        return true;
    }
    
    public void setThreshold(int i)    {
        threshold = i;
    }
    
    public int getThreshold()    {
        return threshold;
    }
 
    
    PrintStream getErr() {
        if (err == null) {
            err =  new StreamBridge(stdErr);
        }
        return err;
    }

    InputStream getIn() {
        return null;
    }

    PrintStream getOut() {
        if (out == null) {
            out = new StreamBridge(stdOut);
        }
        return out;
    }

    void requestCancel(ExecutorTask task) {
        doCancel = true;
        this.task = task;
    }
    
    private class StreamBridge extends PrintStream {
        StringBuffer buff = new StringBuffer();
        private OutputWriter writer;
        public StreamBridge(OutputWriter wr) {
            super(new NullOutputStream());
            writer = wr;
        }
        
        public void flush() {
            if (buff.length() > 0) {
                doPrint();
            }
        }
        
        public void print(long l) {
            buff.append(l);
        }
        
        public void print(char[] s) {
            buff.append(s);
        }
        
        public void print(int i) {
            buff.append(i);
        }
        
        public void print(boolean b) {
            buff.append(b);
        }
        
        public void print(char c) {
            buff.append(c);
        }
        
        public void print(float f) {
            buff.append(f);
        }
        
        public void print(double d) {
            buff.append(d);
        }
        
        public void print(Object obj) {
            buff.append(obj.toString());
        }
        
        public void print(String s) {
            buff.append(s);
        }
        
        public void println(double x) {
            buff.append(x);
            doPrint();
        }
        
        public void println(Object x) {
            buff.append(x.toString());
            doPrint();
        }
        
        public void println(float x) {
            buff.append(x);
            doPrint();
        }
        
        public void println(int x) {
            buff.append(x);
            doPrint();
        }

        public void println(char x) {
            buff.append(x);
            doPrint();
        }
        
        public void println(boolean x) {
            buff.append(x);
            doPrint();
        }
        
        public void println(String x) {
            buff.append(x);
            doPrint();
        }
        
        public void println(char[] x) {
            buff.append(x);
            doPrint();
        }
        
        public void println() {
            doPrint();
        }
        
        public void println(long x) {
            buff.append(x);
            doPrint();
        }
        
        public void write(int b) {
            buff.append((char)b);
        }
        
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }
        
        public void write(byte[] b, int off, int len) {
            ByteArrayInputStream bais = new ByteArrayInputStream(b, off, len);
            Reader read = new InputStreamReader(bais);
            try {
                while (read.ready()) {
                    buff.append((char)read.read());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        private void doPrint() {
            processMultiLine(buff.toString(), writer, "");//NOI18N
            buff.setLength(0);
        }
        
    }
    
}
