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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.maven.BuildFailureException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessorFactory;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.codehaus.mevenide.netbeans.embedder.ProgressTransferListener;
import org.openide.util.Lookup;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * handling of output coming from maven builds.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class OutputHandler implements EventMonitor, TransferListener, MavenEmbedderLogger {
    
    private boolean failed = false;
    
    private InputOutput inputOutput;
    
    private OutputWriter stdOut;
    
    private OutputWriter stdErr;
    
    private int threshold = MavenEmbedderLogger.LEVEL_INFO;
    
    private HashMap processors;
    private OutputVisitor visitor;
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
    
    public OutputHandler(InputOutput io, NbMavenProject proj)    {
        this();
        inputOutput = io;
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
        processMultiLine(throwable.getLocalizedMessage(), stdErr);
        if (throwable instanceof MojoExecutionException) {
            MojoExecutionException exc = (MojoExecutionException)throwable;
            processMultiLine(exc.getLongMessage(), stdErr);
        }
        if (throwable instanceof MojoFailureException) {
            MojoFailureException exc = (MojoFailureException)throwable;
            processMultiLine(exc.getLongMessage(), stdErr);
        }
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
//        processMultiLine(string, stdOut);
    }
    
    public void debug(String string, Throwable throwable) {
//        processMultiLine(string, stdOut);
    }
    
    public boolean isDebugEnabled()    {
        return false;
    }
    
    public void info(String string)    {
        processMultiLine("[INFO]" + string, stdOut);
    }
    
    public void info(String string, Throwable throwable)    {
        processMultiLine("[INFO]" + string, stdOut);
    }
    
    public boolean isInfoEnabled()    {
        return true;
    }
    
    public void warn(String string)    {
        processMultiLine("[WARN]" + string, stdOut);
    }
    
    public void warn(String string, Throwable throwable)    {
        processMultiLine("[WARN]" + string, stdOut);
    }
    
    public boolean isWarnEnabled()    {
        return true;
    }
    
    public void error(String string)    {
        processMultiLine("[ERROR]" + string, stdErr);
    }
    
    public void error(String string, Throwable throwable)    {
        StringWriter sw = new StringWriter();
        PrintWriter wr = new PrintWriter(sw);
        wr.write("[ERROR]" + string + "\n");
        throwable.printStackTrace(wr);
        wr.close();
        processMultiLine(sw.toString(), stdErr);
    }
    
    public boolean isErrorEnabled()    {
        return true;
    }
    
    public void fatalError(String string)    {
        processMultiLine("[FATAL]" + string, stdErr);
    }
    
    public void fatalError(String string, Throwable throwable)    {
        processMultiLine("[FATAL]" + string + "\n" + throwable.toString(), stdErr);
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
 
    private void processMultiLine(String input, OutputWriter writer) {
        if (input == null) {
            return;
        }
        String[] strs = input.split("\n");
        for (int i = 0; i < strs.length; i++) {
            processLine(strs[i], writer);
        }
    }
    
    private void processLine(String input, OutputWriter writer) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.processLine(input, visitor);
        }
        String line = visitor.getLine() == null ? input : visitor.getLine();
        if (visitor.getOutputListener() != null) {
            try {
                writer.println(line, visitor.getOutputListener(), visitor.isImportant());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            writer.println(line);
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
    
}
