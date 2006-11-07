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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessorFactory;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.codehaus.mevenide.netbeans.embedder.ProgressTransferListener;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.execution.ExecutorTask;
import org.openide.util.Lookup;
import org.openide.util.io.NullOutputStream;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * handling of output coming from maven builds.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class OutputHandler implements EventMonitor, TransferListener, MavenEmbedderLogger {
    
    private boolean failed = false;
    
    private InputOutput inputOutput;
    
    private OutputWriter stdOut, stdErr;
    
    private StreamBridge out, err;
    
    
    private int threshold = MavenEmbedderLogger.LEVEL_INFO;
    
    private HashMap processors;
    private OutputVisitor visitor;

    private ProgressHandle handle;

    private boolean doCancel = false;

    private ExecutorTask task;
    
    private Set currentProcessors;
    
    private ProgressTransferListener downloadProgress;
    
    OutputHandler() {
        processors = new HashMap();
        currentProcessors = new HashSet();
        visitor = new OutputVisitor();
        downloadProgress = new ProgressTransferListener();
    }
    
    /**
     * @deprecated for tests only..
     */
    void setup(HashMap procs, OutputWriter std, OutputWriter err) {
        processors = procs;
        stdErr = err;
        stdOut = std;
    }
    
    public OutputHandler(InputOutput io, NbMavenProject proj, ProgressHandle hand)    {
        this();
        inputOutput = io;
        handle = hand;
        stdOut = inputOutput.getOut();
        stdErr = inputOutput.getErr();
        
        // get the registered processors.
        Lookup.Result result  = Lookup.getDefault().lookup(new Lookup.Template(OutputProcessorFactory.class));
        Iterator it = result.allInstances().iterator();
        while (it.hasNext()) {
            OutputProcessorFactory factory = (OutputProcessorFactory)it.next();
            Set procs = factory.createProcessorsSet(proj);
            Iterator it2 = procs.iterator();
            while (it2.hasNext()) {
                OutputProcessor proc = (OutputProcessor)it2.next();
                String[] regs = proc.getRegisteredOutputSequences();
                for (int i = 0; i < regs.length; i++) {
                    String str = regs[i];
                    Set set = (Set) processors.get(str);
                    if (set == null) {
                        set = new HashSet();
                        processors.put(str, set);
                    }
                    set.add(proc);
                }
            }
        }
    }
    
    public void errorEvent(String eventName, String target, long l, Throwable throwable) {
//        if (throwable != null) {
//            processMultiLine(throwable.getLocalizedMessage(), stdErr, "ERROR2");
//        }
//        if (throwable instanceof MojoExecutionException) {
//            MojoExecutionException exc = (MojoExecutionException)throwable;
//            processMultiLine(exc.getLongMessage(), stdErr, "ERROR");
//        } else if (throwable instanceof MojoFailureException) {
//            MojoFailureException exc = (MojoFailureException)throwable;
//            processMultiLine(exc.getLongMessage(), stdErr, "ERROR");
//        }
        processFail(getEventId(eventName, target), stdErr);
//        if (throwable instanceof BuildFailureException) {
//            stdErr.println("");
//            stdErr.println("BUILD FAILED.");
//            stdErr.println("");
//        }
        Set set = (Set) processors.get(getEventId(eventName, target));
        if (set != null) {
            Set retain = new HashSet();
            retain.addAll(set);
            retain.retainAll(currentProcessors);
            Set remove = new HashSet();
            remove.addAll(set);
            remove.removeAll(retain);
            currentProcessors.removeAll(remove);
        }
    }
    
    public void transferProgress(TransferEvent transferEvent, byte[] b, int i)    {
        downloadProgress.transferProgress(transferEvent, b, i);
    }
    
    private String getEventId(String eventName, String target) {
        if ("project-execute".equals(eventName)) {
            return eventName;
        }
        return eventName + "#" + target;
    }
    
    public void startEvent(String eventName, String target, long l)    {
        Set set = (Set) processors.get(getEventId(eventName, target));
        if (set != null) {
            currentProcessors.addAll(set);
        }
        processStart(getEventId(eventName, target), stdOut);
        if (handle != null) {
            // null in case of tests
            handle.progress(target);
        }
    }
    
    public void endEvent(String eventName, String target, long l)    {
        processEnd(getEventId(eventName, target), stdOut);
        Set set = (Set) processors.get(getEventId(eventName, target));
        if (set != null) {
            //TODO a bulletproof way would be to keep a list of currently started
            // sections and compare to the list of getRegisteredOutputSequences fo each of the
            // processors in set..
            currentProcessors.removeAll(set);
        }
        if (doCancel) {
            assert task != null;
            task.stop();
        }
    }
    
    public void transferStarted(TransferEvent transferEvent)    {
        downloadProgress.transferStarted(transferEvent);
    }
    
    public void transferInitiated(TransferEvent transferEvent)    {
        downloadProgress.transferInitiated(transferEvent);
    }
    
    public void transferError(TransferEvent transferEvent)    {
        downloadProgress.transferError(transferEvent);
    }
    
    public void transferCompleted(TransferEvent transferEvent)    {
        downloadProgress.transferCompleted(transferEvent);
    }
    
    public void debug(String string) {
        if (isDebugEnabled()) {
            processMultiLine(string, stdOut, "DEBUG");
        }
    }
    
    public void debug(String string, Throwable throwable) {
        if (isDebugEnabled()) {
            processMultiLine(string, stdOut, "DEBUG");
            throwable.printStackTrace(stdOut);
        }
    }
    
    public boolean isDebugEnabled()    {
        return threshold == MavenEmbedderLogger.LEVEL_DEBUG;
    }
    
    public void info(String string)    {
        processMultiLine(string, stdOut, "INFO");
    }
    
    public void info(String string, Throwable throwable)    {
        processMultiLine( string, stdOut, "INFO");
        throwable.printStackTrace(stdOut);
    }
    
    public boolean isInfoEnabled()    {
        return true;
    }
    
    public void warn(String string)    {
        processMultiLine(string, stdOut, "WARN");
    }
    
    public void warn(String string, Throwable throwable)    {
        processMultiLine(string, stdOut, "WARN");
        throwable.printStackTrace(stdOut);
    }
    
    public boolean isWarnEnabled()    {
        return true;
    }
    
    public void error(String string)    {
        processMultiLine(string, stdErr, "ERROR");
    }
    
    public void error(String string, Throwable throwable)    {
        processMultiLine(string, stdErr, "ERROR");
        throwable.printStackTrace(stdErr);
    }
    
    public boolean isErrorEnabled()    {
        return true;
    }
    
    public void fatalError(String string)    {
        processMultiLine(string, stdErr, "FATAL");
    }
    
    public void fatalError(String string, Throwable throwable)    {
        processMultiLine(string, stdErr, "FATAL");
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
 
    private void processMultiLine(String input, OutputWriter writer, String levelText) {
        if (input == null) {
            return;
        }
        String[] strs = input.split(System.getProperty("line.separator"));
        for (int i = 0; i < strs.length; i++) {
            processLine(strs[i], writer, levelText);
        }
    }
    
    private void processLine(String input, OutputWriter writer, String levelText) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.processLine(input, visitor);
        }
        if (!visitor.isLineSkipped()) {
            String line = visitor.getLine() == null ? input : visitor.getLine();
            if (visitor.getOutputListener() != null) {
                try {
                    writer.println((levelText.length() == 0 ? "" : ("[" + levelText + "]")) + line, visitor.getOutputListener(), visitor.isImportant());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                writer.println((levelText.length() == 0 ? "" : ("[" + levelText + "]")) + line);
            }
        }
    }
    
    private void processStart(String id, OutputWriter writer) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.sequenceStart(id, visitor);
        }
        if (visitor.getLine() == null) {
            return;
        }
        if (visitor.getOutputListener() != null) {
            try {
                writer.println(visitor.getLine(), visitor.getOutputListener(), visitor.isImportant());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            writer.println(visitor.getLine());
        }
    }
    
    private void processEnd(String id, OutputWriter writer) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.sequenceEnd(id, visitor);
        }
        if (visitor.getLine() == null) {
            return;
        }
        if (visitor.getOutputListener() != null) {
            try {
                writer.println(visitor.getLine(), visitor.getOutputListener(), visitor.isImportant());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            writer.println(visitor.getLine());
        }
    }
    
    private void processFail(String id, OutputWriter writer) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.sequenceFail(id, visitor);
        }
        if (visitor.getLine() == null) {
            return;
        }
        if (visitor.getOutputListener() != null) {
            try {
                writer.println(visitor.getLine(), visitor.getOutputListener(), visitor.isImportant());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            writer.println(visitor.getLine());
        }
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
            processMultiLine(buff.toString(), writer, "");
            buff.setLength(0);
        }
        
    }
    
}
