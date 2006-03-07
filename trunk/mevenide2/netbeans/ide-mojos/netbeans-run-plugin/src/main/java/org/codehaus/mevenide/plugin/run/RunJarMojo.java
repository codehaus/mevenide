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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * goal for running the project's jar artifact through maven in netbeans.
 * runs java -jar <jar>, assuming the assembly's jar-with-dependencies to be run before this one.
 * also assuming a Main-Class: entry in the manifest.
 *
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal run-jar
 * @requiresDependencyResolution runtime
 * @requiresProject
 */
public class RunJarMojo extends AbstractRunMojo implements Runnable {
    
    /**
     * wait for the process to finish.
     * @parameter
     */
    private boolean wait = true;
    
    /**
     * The main class to execute
     * @parameter expression="${nb.mainClass}"
     * @required
     */
    private String mainClass;
    
    /**
     * the java executable to use for starting the process
     * @parameter expression = "java"
     */
    private String executable;
    
    /**
     * @parameter
     */
    private List parameters;

    /**
     * @parameter
     */
    private List jvmParameters;
    
    /**
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File workDirectory;
    
    /**
     * @parameter expression="${project.build.directory}/artifactForNbRun-jar-with-dependencies.jar"
     * @required
     */
    private File jarArtifact;
    
    
    /**
     * maven project
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    private InputOutput io;
    
    private static final RequestProcessor PROCESSOR = new RequestProcessor("Netbeans-Run-Plugin IO redirection", 5);

    
    public void execute() throws MojoExecutionException {
        if (jarArtifact == null || !jarArtifact.exists()) {
            throw new MojoExecutionException("Badly configured, need existing jar at " + jarArtifact);
        }
        io = IOProvider.getDefault().getIO("Run", true);
        io.select();
        System.out.println("class=" + io.getClass());
        //XXX
        io.getOut().println("XXX");
        
        ExecutorTask tsk = ExecutionEngine.getDefault().execute("Run " + jarArtifact.getName(), this, io);
        if (wait) {
            int result = tsk.result();
            getLog().warn("Exited with return code=" + result);
        }
    }
    
    public void run() {
        List cmd = new ArrayList();
        cmd.add(executable);
        if (jvmParameters != null) {
            Iterator it = jvmParameters.iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                if (elem != null) {
                    cmd.add(elem);
                }
            }
        }
        if (parameters != null) {
            Iterator it = parameters.iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                if (elem != null) {
                    cmd.add(elem);
                }
            }
        }
        cmd.add("-cp");
        cmd.add(jarArtifact.getAbsolutePath());
        cmd.add(mainClass);
        String[] cmds = (String[])cmd.toArray(new String[cmd.size()]);
        Process proc;
        for (int i = 0; i < cmds.length; i++) {
            System.out.println("cmd=" + cmds[i]);
        }
        System.out.println("workdir=" + workDirectory);
        try {
            // IF we get the jdk 1.5 support only, make sure this uses ProcessBuilder 
            proc = Runtime.getRuntime().exec(cmds, null, workDirectory);
            Output out = new Output(proc.getInputStream(), io.getOut());
            Output err = new Output(proc.getErrorStream(), io.getErr());
            Task outTask = PROCESSOR.post(out);
            Task errTask = PROCESSOR.post(err);
            int exit = proc.waitFor();
            outTask.waitFinished();
            if (exit != 0) {
                getLog().info("Exit with return code " + exit);
                io.getOut().println("Exit code="+ exit);
            }
            out.closeWriter();
            errTask.waitFinished();
            err.closeWriter();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("IO");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.out.println("INT");
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
            System.out.println("run started..");
            try {
                int chr = str.read();
                while (chr != -1) {
                    writer.print(chr);
                    System.out.print((char)chr);
                    chr = str.read();
                }
                writer.println("Hello world");
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
