/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.netbeans.project.exec;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.output.OutputProcessor;
import org.mevenide.netbeans.project.output.OutputVisitor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;

import org.openide.util.MapFormat;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
import org.openide.execution.NbProcessDescriptor;
import org.mevenide.netbeans.project.output.JavaOutputListenerProvider;
import org.mevenide.netbeans.project.output.TestOutputListenerProvider;
import org.mevenide.netbeans.project.output.AnnouncementOutputListenerProvider;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenExecutor implements Runnable {
    private static final Log logger = LogFactory.getLog(MavenExecutor.class);
    
    public static final String FORMAT_MAVEN_HOME = "MAVEN_HOME"; //NOI18N
    public static final String FORMAT_GOAL = "goal"; //NOI18N
    public static final String FORMAT_OFFLINE = "offline"; //NOI18N
    public static final String FORMAT_NOBANNER = "nobanner"; //NOI18N
    public static final String FORMAT_DEBUG = "debug"; //NOI18N
    public static final String FORMAT_EXCEPTIONS = "exceptions"; //NOI18N
    public static final String FORMAT_NONVERBOSE = "nonverbose"; //NOI18N
    public static final String FORMAT_DOWNLOADMETER = "downloadmeter"; //NOI18N
    
    // -- default value
    private String goal = "dist"; //NOI18N 
    private boolean offline = false;
    private boolean nobanner = false;
    private boolean debug = false;
    private boolean exceptions = false;
    private boolean nonverbose = false;
    private String meter = "silent"; //NOI18N
    
    private static final long serialVersionUID = 7564737833872873L;
    private NbProcessDescriptor descriptor;
    private MavenProject project;
    private Process proces;
    private String format;
    private InputOutput io;
    private Set processors;
    
    private static RequestProcessor PROCESSOR = new RequestProcessor("maven execution", 3);
    
    public MavenExecutor(MavenProject proj, String gl, Set procs) {
        project = proj;
        goal = gl;
        processors = procs;
        StringBuffer mavenExeFmt = new StringBuffer();
        if (Utilities.isWindows()) {
            mavenExeFmt.append("\"{");
        } else {
            mavenExeFmt.append("{");
        }
        mavenExeFmt.append(FORMAT_MAVEN_HOME);
        mavenExeFmt.append("}/bin/");
        if (Utilities.isWindows()) {
            mavenExeFmt.append("maven.bat\"");
        } else {
            mavenExeFmt.append("maven");
        }
        mavenExeFmt.append(" {");
        mavenExeFmt.append(FORMAT_NOBANNER);
        mavenExeFmt.append("} {");
        mavenExeFmt.append(FORMAT_OFFLINE);
        mavenExeFmt.append("} {");
        mavenExeFmt.append(FORMAT_DEBUG);
        mavenExeFmt.append("} {");
        mavenExeFmt.append(FORMAT_EXCEPTIONS);
        mavenExeFmt.append("} {");
        mavenExeFmt.append(FORMAT_NONVERBOSE);
        mavenExeFmt.append("} {");
        mavenExeFmt.append(FORMAT_DOWNLOADMETER);
        mavenExeFmt.append("} {");
        mavenExeFmt.append(FORMAT_GOAL);
        mavenExeFmt.append("}");
//        String mavenExeFmt = "{" + FORMAT_MAVEN_HOME + "}/" + "bin/maven"; //NOI18N
//        if (Utilities.isWindows()) {
//            mavenExeFmt = "\"{" + FORMAT_MAVEN_HOME + "}/" + "bin/maven.bat\""; //NOI18N
//        }
//        format = mavenExeFmt + " {" + FORMAT_NOBANNER + "} {" + FORMAT_OFFLINE + "} {" + FORMAT_GOAL + "}"; //NOI18N
        format = mavenExeFmt.toString();
    }
    
    public void setOffline(boolean offline) {
        this.offline = offline;
    }
    
    public void setNoBanner(boolean nb) {
        nobanner = nb;
    }
    
    public void setDebug(boolean deb) {
        debug = deb;
    }
    
    public void setExceptions(boolean exc) {
        exceptions = exc;
    }
    
    public void setNonverbose(boolean nv) {
        nonverbose = nv;
    }
    
    public void setDownloadMeter(String met) {
        meter = met;
    }
    
    public Process createProcess() throws IOException {
        File execDir = FileUtil.toFile(project.getProjectDirectory());
        HashMap formats = new HashMap(5);
        formats.put(FORMAT_GOAL, goal);
        formats.put(FORMAT_MAVEN_HOME, project.getLocFinder().getMavenHome());
        formats.put(FORMAT_OFFLINE, offline ? "--offline" : ""); //NOI18N
        formats.put(FORMAT_NOBANNER, nobanner ? "--nobanner" : ""); //NOI18N
        formats.put(FORMAT_DEBUG, debug ? "-X" : ""); //NOI18N
        formats.put(FORMAT_EXCEPTIONS, exceptions ? "--exception" : ""); //NOI18N
        formats.put(FORMAT_NONVERBOSE, nonverbose ? "--quiet" : ""); //NOI18N
        if (!offline) {
            formats.put(FORMAT_DOWNLOADMETER, "default".equals(meter) ? "" : "-Dmaven.download.meter=" + meter); //NOI18N
        } else {
            formats.put(FORMAT_DOWNLOADMETER, ""); //NOI18N
        }
        String procString = MapFormat.format(format, formats);
        Process proc = Runtime.getRuntime().exec(procString, null, execDir);
        return proc;
    }
    
    private InputOutput createInputOutput() {
        InputOutput newio = IOProvider.getDefault().getIO("Maven", false);
        newio.setErrSeparated(false);
        try {
            newio.getOut().reset();
        } catch (IOException exc) {
            logger.error("Cannot reset InputOutput", exc);
        }
        return newio;
    }    
    
    public InputOutput getInputOutput() {
        if (io == null) {
            io = createInputOutput();
        }
        return io;
    }
    
    public void setCustomInputOutput(InputOutput inout) {
        io = inout;
    }
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        try {
            proces = createProcess();
            InputOutput ioput = getInputOutput();
            OutputProcessor[] providers = new OutputProcessor[processors.size()];
            providers = (OutputProcessor[])processors.toArray(providers);
            Output out = new Output(proces.getInputStream(), ioput.getOut(),  providers);
            Output err = new Output(proces.getErrorStream(), ioput.getErr(),  providers);
            Task outTask = PROCESSOR.post(out);
            Task errTask = PROCESSOR.post(err);
            proces.waitFor();
            outTask.waitFinished();
            errTask.waitFinished();
            if (out.wasSuccessfull()) {
                // out.wasSuccessfull() is not happening when limiting output..
                List succActions = new ArrayList();
                succActions.addAll(out.getSuccessActions() != null ? out.getSuccessActions() : Collections.EMPTY_LIST);
                succActions.addAll(err.getSuccessActions() != null ? err.getSuccessActions() : Collections.EMPTY_LIST);
                if (succActions.size() > 0) {
                    Action action = null;
                    if (succActions.size() == 1) {
                        action = (Action)succActions.iterator().next();
                    } else {
                        int maxPriotity = -1;
                        Iterator it = succActions.iterator();
                        while (it.hasNext()) {
                            Action act = (Action)it.next();
                            Integer priority = (Integer)act.getValue("Priority");
                            if (priority.intValue() > maxPriotity) {
                                action = act;
                                maxPriotity = priority.intValue();
                            }
                        }
                    }
                    String question = (String)action.getValue("Question");
                    String title = (String)action.getValue(Action.SHORT_DESCRIPTION);
                    if (question != null) {
                        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(question,
                        title == null ? "" : title,
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.QUESTION_MESSAGE);
                        Object returned = DialogDisplayer.getDefault().notify(desc);
                        if (NotifyDescriptor.OK_OPTION.equals(returned)) {
                            action.actionPerformed(new ActionEvent(MavenExecutor.this, ActionEvent.ACTION_PERFORMED, "Performed"));
                        }

                    } else {
                        //TODO
                        System.out.println("size is not 1");
                    }
                    
                }
            }
            
            
        } catch (IOException io) {
            ErrorManager.getDefault().notify(io);
        } catch (InterruptedException exc) {
            ErrorManager.getDefault().notify(exc);
        }
    }
    
    private static class Output implements Runnable {
        private InputStream str;
        private OutputWriter writer;
        private OutputProcessor[] providers;
        private List successActions;
        private boolean success = false;
        private boolean failed = false;
        public Output(InputStream instream, OutputWriter out, OutputProcessor[] provs) {
            str = instream;
            writer = out;
            providers = provs;
            successActions = new ArrayList();
        }
        
        public void run() {
            BufferedReader read = new BufferedReader(new InputStreamReader(str), 50);
            String line; 
            OutputVisitor visitor = new OutputVisitor();
            try {
                while ((line = read.readLine()) != null) {
                    if (line.equals("BUILD SUCCESSFUL")) {
                        success = true;
                    }
                    if (line.equals("BUILD FAILED")) {
                        failed = true;
                    }
                    visitor.resetVisitor();
                    if (providers != null) {
                        for (int i = 0; i < providers.length; i++) {
                            providers[i].processLine(line,visitor);
                        }
                    }
                    if (visitor.getFilteredLine() != null) {
                        line = visitor.getFilteredLine();
                    }
                    if (visitor.getOutputListener() == null) {
                        writer.println(line);
                    } else {
                        writer.println(line, visitor.getOutputListener());
                    }
                    if (visitor.getSuccessAction() != null) {
                        successActions.add(visitor.getSuccessAction());
                    }
                    writer.flush();
                }
                read.close();
            } catch (IOException io) {
                    logger.error(io);
            } finally {
                try {
                    read.close();
                    writer.close();
                } catch (IOException ioexc) {
                    logger.error(ioexc);
                }
            }
        }
        
        public List getSuccessActions() {
            return successActions;
        }
        
        public boolean wasSuccessfull() {
            return success;
        }
        
        public boolean wasFailed() {
            return failed;
        }
    }
}
