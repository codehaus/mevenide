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
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileUtil;

import org.openide.util.MapFormat;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;


public class MavenExecutor implements Runnable {
    private static final Log logger = LogFactory.getLog(MavenExecutor.class);
    
    public static final String FORMAT_MAVEN_HOME = "MAVEN_HOME"; //NOI18N
    public static final String FORMAT_GOAL = "goal"; //NOI18N
    public static final String FORMAT_OFFLINE = "offline"; //NOI18N
    public static final String FORMAT_NOBANNER = "nobanner"; //NOI18N
    
    // -- default value
    private String goal = "dist"; //NOI18N 
    private boolean offline = false;
    private boolean nobanner = false;
    
    private static final long serialVersionUID = 7564737833872873L;
    private NbProcessDescriptor descriptor;
    private MavenProject project;
    private Process proces;
    private String format;
    private InputOutput io;
    
    private static RequestProcessor PROCESSOR = new RequestProcessor("maven execution", 3);
    
    public MavenExecutor(MavenProject proj, String gl)
    {
        project = proj;
        goal = gl;
        String mavenExeFmt = "{" + FORMAT_MAVEN_HOME + "}/" + "bin/maven"; //NOI18N
        if (Utilities.isWindows()) {
            mavenExeFmt = "\"{" + FORMAT_MAVEN_HOME + "}/" + "bin/maven.bat\""; //NOI18N
        }
        format = mavenExeFmt + " {" + FORMAT_NOBANNER + "} {" + FORMAT_OFFLINE + "} {" + FORMAT_GOAL + "}"; //NOI18N
    }
    
    public void setOffline(boolean offline)
    {
        this.offline = offline;
    }
    
    public void setNoBanner(boolean nb)
    {
        nobanner = nb;
    }    
    
    public Process createProcess() throws IOException
    {
        File execDir = FileUtil.toFile(project.getProjectDirectory());
        HashMap formats = new HashMap(5);
        formats.put(FORMAT_GOAL, goal);
        formats.put(FORMAT_MAVEN_HOME, project.getLocFinder().getMavenHome());
        formats.put(FORMAT_OFFLINE, offline ? "--offline" : ""); //NOI18N
        formats.put(FORMAT_NOBANNER, nobanner ? "--nobanner" : ""); //NOI18N
        String procString = MapFormat.format(format, formats);
        Process proc = Runtime.getRuntime().exec(procString, null, execDir);
        InputOutput ioput = getInputOutput();
        PROCESSOR.post(new Output(proc.getInputStream(), ioput.getOut()));
        PROCESSOR.post(new Output(proc.getErrorStream(), ioput.getErr()));
        return proc;
    }
    
    private InputOutput createInputOutput() {
        InputOutput io = IOProvider.getDefault().getIO("Maven", false);
        io.setErrSeparated(false);
        try {
            io.getOut().reset();
        } catch (IOException exc) {
            logger.error("Cannot reset InputOutput", exc);
        }
        return io;
    }    
    
    public InputOutput getInputOutput() {
        if (io == null) {
            io = createInputOutput();
        }
        return io;
    }    
     
//    public int result() {
//        return proces.exitValue();
//    }
//    
//    public void stop() {
//        proces.destroy();
//    }
//
//    public ExecutorTask execute() {
//        return new WrapperTask();
//    }

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
    
    private class Output implements Runnable {
        private InputStream str;
        private OutputWriter writer;
        public Output(InputStream instream, OutputWriter out) {
            str = instream;
            writer = out;
        }
        
        public void run() {
            BufferedReader read = new BufferedReader(new InputStreamReader(str));
            String line; 
            try {
                while ((line = read.readLine()) != null) {
                    writer.println(line);
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
    
//    private class WrapperTask extends ExecutorTask {
//        
//        public  WrapperTask() {
//            super(MavenExecutor.this);
//        }
//        public InputOutput getInputOutput() {
//            return MavenExecutor.this.getInputOutput();
//        }
//        
//        public int result() {
//            return MavenExecutor.this.result();
//        }
//        
//        public void stop() {
//            MavenExecutor.this.stop();
//        }
//        
//    }
}
