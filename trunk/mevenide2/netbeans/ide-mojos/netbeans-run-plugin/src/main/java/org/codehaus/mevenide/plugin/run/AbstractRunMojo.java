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
package org.codehaus.mevenide.plugin.run;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Commandline;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

public abstract class AbstractRunMojo extends AbstractMojo implements Runnable {
    /**
     * the java executable to use for starting the process
     * @parameter expression = "java"
     */
    private String executable;
    
    /**
     * @parameter expression="${netbeans.jar.run.params}"
     */
    private String parameters;

    /**
     * @parameter expression="${netbeans.jar.run.jvmparams}"
     */
    private String jvmParameters;

    /**
     * @parameter expression="${netbeans.jar.run.debugparams}"
     */
    private String debugJvmParameters;

    /**
     * @parameter expression="${project.build.directory}/executable-netbeans"
     */
    private File workDirectory;
    
    /**
     * @parameter expression="${project.build.directory}/executable-netbeans"
     * @required
     */
    private File jarLocation;
    
    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * maven project
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    private InputOutput io;
    
    private static final RequestProcessor PROCESSOR = new RequestProcessor("Netbeans-Run-Plugin IO redirection", 5);

    private File jarArtifact;
    
    private Object semaphor = new Object();

    protected ExecutorTask startExecutorTask() throws MojoExecutionException {
        jarArtifact = new File(jarLocation, finalName + ".jar");
        if (jarArtifact == null || !jarArtifact.exists()) {
            throw new MojoExecutionException("Badly configured, need existing jar at " + jarArtifact);
        }
        io = IOProvider.getDefault().getIO("Run " + jarArtifact.getName(), true);
        io.select();
//        System.out.println("class=" + io.getClass());
//        System.out.println("classloader=" + io.getClass().getClassLoader().getClass());
//        System.out.println("executor engine=" + ExecutionEngine.getDefault().getClass());
        ExecutorTask task =  ExecutionEngine.getDefault().execute("Run " + jarArtifact.getName(), this, io);
        try {
            synchronized (semaphor) {
                semaphor.wait();
            }
        } catch (InterruptedException ex) {
            //do nothing.
        }
        return  task;
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
            getLog().info("Executing \"" + cmd + "\" in directory " + workDirectory);
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
