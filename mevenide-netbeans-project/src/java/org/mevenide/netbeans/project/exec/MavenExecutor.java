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

import java.io.*;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

import org.openide.util.MapFormat;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
import org.openide.execution.NbProcessDescriptor;

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
    private OutputFilter outFilter;
    private OutputFilter errFilter;
    
    private static RequestProcessor PROCESSOR = new RequestProcessor("maven execution", 3);
    
    public MavenExecutor(MavenProject proj, String gl) {
        project = proj;
        goal = gl;
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
        InputOutput ioput = getInputOutput();
        OutputListenerProvider[] providers = new OutputListenerProvider[] {
            new TestOutputListenerProvider(project),
            new JavaOutputListenerProvider(project),
            new AnnouncementOutputListenerProvider(project)
        };
        PROCESSOR.post(new Output(proc.getInputStream(), ioput.getOut(), outFilter, providers));
        PROCESSOR.post(new Output(proc.getErrorStream(), ioput.getErr(), errFilter, providers));
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
    
    public void setFilterOutput(OutputFilter filter) {
        outFilter = filter;
    }
    
    public void setFilterError(OutputFilter filter) {
        errFilter = filter;
    }

    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        try {
            proces = createProcess();
            proces.waitFor();
        } catch (IOException io) {
            ErrorManager.getDefault().notify(io);
        } catch (InterruptedException exc) {
            ErrorManager.getDefault().notify(exc);
        }
    }
    
    private static class Output implements Runnable {
        private InputStream str;
        private OutputWriter writer;
        private OutputFilter filter;
        private OutputListenerProvider[] providers;
        public Output(InputStream instream, OutputWriter out, OutputFilter filt, OutputListenerProvider[] provs) {
            str = instream;
            writer = out;
            filter = filt;
            providers = provs;
        }
        
        public void run() {
            BufferedReader read = new BufferedReader(new InputStreamReader(str), 50);
            String line; 
            try {
                while ((line = read.readLine()) != null) {
                    if (filter != null) {
                        line = filter.filterLine(line);
                    }
                    if (line != null) {
                        OutputListener listener = null;
                        if (providers != null) {
                            for (int i = 0; i < providers.length; i++) {
                                listener = providers[i].recognizeLine(line);
                                if (listener != null) {
                                    break;
                                }
                            }
                        }
                        if (listener == null) {
                            writer.println(line);
                        } else {
                            writer.println(line, listener);
                        }
                        writer.flush();
                    }
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
    }
}
