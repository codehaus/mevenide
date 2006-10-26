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
package org.codehaus.mevenide.netbeans.runjar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.runjar.MavenRunJar;
import org.codehaus.plexus.util.cli.Commandline;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

public class MavenRunJarImpl implements MavenRunJar {
    
    private static final RequestProcessor PROCESSOR = new RequestProcessor("Netbeans-Run-Plugin IO redirection", 5);
    
    public MavenRunJarImpl() {
    }
    
    public int runJarProject(MavenProject project, Log log, String finalName,
            File jarLocation, File workDirectory, String executable, String parameters,
            String jvmParameters, String debugJvmParameters, boolean waitForFinish) throws MojoFailureException, MojoExecutionException 
    {
        File jarArtifact = new File(jarLocation, finalName + ".jar");
        if (jarArtifact == null || !jarArtifact.exists()) {
            throw new MojoExecutionException("Badly configured, need existing jar at " + jarArtifact);
        }
        InputOutput io = IOProvider.getDefault().getIO("Run " + jarArtifact.getName(), true);
        io.select();
        Wrapper wrapper = new Wrapper(io, jarArtifact, executable, parameters, jvmParameters, debugJvmParameters, 
                                      workDirectory, jarLocation, finalName, project, log);
//        System.out.println("class=" + io.getClass());
//        System.out.println("classloader=" + io.getClass().getClassLoader().getClass());
//        System.out.println("executor engine=" + ExecutionEngine.getDefault().getClass());
        ExecutorTask task =  ExecutionEngine.getDefault().execute("Run " + jarArtifact.getName(), wrapper, io);
        try {
            synchronized (wrapper.semaphor) {
                wrapper.semaphor.wait();
            }
        } catch (InterruptedException ex) {
            //do nothing.
        }
        if (waitForFinish) {
            return task.result();
        }
        // if immediately exiting, cannot figure ut exit code..
        // do we need the immediately exiting stuff?
        return 0;
    }
    
    private class Wrapper implements Runnable {
        private InputOutput io;
        private File jarArtifact;
        private String executable;
        private String parameters;
        private String jvmParameters;
        private String debugJvmParameters;
        private File workDirectory;
        private File jarLocation;
        private String finalName;
        private MavenProject project;
        private Log log;
        Object semaphor = new Object();
        
        public Wrapper(InputOutput io, File jarArtifact, String executable, String parameters, String jvmParameters, String debugJvmParameters,
                File workDirectory, File jarLocation, String finalName, MavenProject project, Log log) {
            this.io = io;
            this.jarArtifact = jarArtifact;
            this.executable = executable;
            this.parameters = parameters;
            this.jvmParameters = jvmParameters;
            this.debugJvmParameters = debugJvmParameters;
            this.workDirectory = workDirectory;
            this.jarLocation = jarLocation;
            this.finalName = finalName;
            this.project = project;
            this.log = log;
        }
        
        public void run() {
            StringBuffer cmd = new StringBuffer();
            cmd.append(executable);
            cmd.append(" ");
            if (jvmParameters != null) {
                cmd.append(jvmParameters);
                cmd.append(" ");
            }
            if (debugJvmParameters != null) {
                cmd.append(debugJvmParameters);
                cmd.append(" ");
            }
            cmd.append("-jar ");
            cmd.append(jarArtifact.getName());
            cmd.append(" ");
            if (parameters != null) {
                cmd.append(parameters);
                cmd.append(" ");
            }
            String[] cmds;
            try {
                cmds = Commandline.translateCommandline(cmd.toString());
                log.info("Executing \"" + cmd + "\" in directory " + workDirectory);
                // IF we get the jdk 1.5 support only, make sure this uses ProcessBuilder
                Process proc = Runtime.getRuntime().exec(cmds, null, workDirectory);
                synchronized (semaphor) {
                    semaphor.notifyAll();
                }
                Output out = new Output(proc.getInputStream(), io.getOut());
                Output err = new Output(proc.getErrorStream(), io.getErr());
                Task outTask = PROCESSOR.post(out);
                Task errTask = PROCESSOR.post(err);
                int exit = proc.waitFor();
                outTask.waitFinished();
                out.closeWriter();
                errTask.waitFinished();
                err.closeWriter();
            } catch (IOException ex) {
                ex.printStackTrace();
//            System.out.println("IO");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
//            System.out.println("INT");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static class Output implements Runnable {
        private InputStreamReader str;
        private OutputWriter writer;
        public Output(InputStream instream, OutputWriter out) {
            str = new InputStreamReader(instream);
            writer = out;
        }
        
        public void run() {
            try {
                int chr = str.read();
                while (chr != -1) {
                    if (chr == (int)'\n') {
                        writer.println();
                    } else {
                        writer.write(chr);
                    }
                    chr = str.read();
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
        
        public void closeWriter() {
            writer.close();
        }
    }
}
