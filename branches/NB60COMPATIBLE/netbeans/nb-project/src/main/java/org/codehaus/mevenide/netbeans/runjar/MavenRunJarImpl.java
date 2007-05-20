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
import org.codehaus.mevenide.netbeans.api.output.OutputUtils;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.plexus.util.cli.Commandline;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

public class MavenRunJarImpl implements MavenRunJar {
    
    private static final RequestProcessor PROCESSOR = new RequestProcessor("NetBeans-Run-Plugin IO redirection", 5);
    
    public MavenRunJarImpl() {
    }
    
    public int runJarProject(MavenProject project, Log log, String finalName,
            File jarLocation, File workDirectory, String executable, String parameters,
            String jvmParameters, String debugJvmParameters, boolean waitForFinish) throws MojoFailureException, MojoExecutionException 
    {
        if (!jarLocation.exists()) {
            File alternate = new File(jarLocation.getAbsolutePath() + ".dir");
            if (alternate.exists()) {
                //MEVENIDE-523
                jarLocation = alternate;
            }
        }
        if (!workDirectory.exists()) {
            File alternate = new File(workDirectory.getAbsolutePath() + ".dir");
            if (alternate.exists()) {
                //MEVENIDE-523
                workDirectory = alternate;
            }
        }
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
        ExecutorTask task = null;
        try {
            task =  ExecutionEngine.getDefault().execute("Run " + jarArtifact.getName(), wrapper, io);
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
        } catch (ThreadDeath td) {
            if (task != null) {
                task.stop();
            }
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
            Output out = null;
            Output err = null;
            Process proc = null;
            try {
                cmds = Commandline.translateCommandline(cmd.toString());
                log.info("Executing \"" + cmd + "\" in directory " + workDirectory);
                // IF we get the jdk 1.5 support only, make sure this uses ProcessBuilder
                proc = Runtime.getRuntime().exec(cmds, null, workDirectory);
                synchronized (semaphor) {
                    semaphor.notifyAll();
                }
                File fil = FileUtil.normalizeFile(project.getFile());
                FileObject fo = FileUtil.toFileObject(fil);
                ClassPath classpath = null;
                if (fo != null) {
                    Project prj = FileOwnerQuery.getOwner(fo);
                    ClassPathProviderImpl cpp = prj.getLookup().lookup(ClassPathProviderImpl.class);
                    classpath =  ClassPathSupport.createProxyClassPath(cpp.getProjectClassPaths(ClassPath.EXECUTE));
                }
                
                out = new Output(proc.getInputStream(), io.getOut(), classpath);
                err = new Output(proc.getErrorStream(), io.getErr(), classpath);
                Task outTask = PROCESSOR.post(out);
                Task errTask = PROCESSOR.post(err);
                int exit = proc.waitFor();
                outTask.waitFinished();
                errTask.waitFinished();
            } catch (IOException ex) {
//                ex.printStackTrace();
//            System.out.println("IO");
            } catch (InterruptedException ex) {
//                ex.printStackTrace();
                if (proc != null) {
                    proc.destroy();
                }
//            System.out.println("INT");
            } catch (Exception ex) {
//                ex.printStackTrace();
            } catch (ThreadDeath de) {
                if (proc != null) {
                    proc.destroy();
                }
//                ex.printStackTrace();
            } finally {
                if (out != null)  {
                    out.closeWriter();
                }
                if (err != null) { 
                    err.closeWriter();
                }
                
            }
        }
    }
    
    private static class Output implements Runnable {
        private InputStreamReader str;
        private OutputWriter writer;
        private ClassPath cp;
        public Output(InputStream instream, OutputWriter out, ClassPath cp) {
            str = new InputStreamReader(instream);
            writer = out;
            this.cp = cp;
        }
        
        public void run() {
            try {
                long stamp = System.currentTimeMillis();
                int chr = str.read();
                StringBuffer buf = new StringBuffer();
                while (chr != -1) {
                    if (chr == (int)'\n') {
                        if (buf.length() > 0 && buf.charAt(buf.length() - 1) == '\r') {
                            // should fix issues on windows..
                            buf.setLength(buf.length() - 1);
                        }
                        String line = buf.toString();
                        if (cp != null) {
                            OutputListener ol = OutputUtils.matchStackTraceLine(line, cp);
                            if (ol != null) {
                                writer.println(line, ol);
                            } else {
                                writer.println(line);
                            }
                        } else {
                            writer.println(line);
                        }
                                
                        buf.setLength(0);
                        stamp = System.currentTimeMillis();
                    } else {
                        buf.append((char)chr);
                    }
                    while (true) {
                        if (str.ready()) {
                            chr = str.read();
                            break;
                        } else {
                            if (System.currentTimeMillis() - stamp > 700) {
                                writer.print(buf.toString());
                                buf.setLength(0);
                                chr = str.read();
                                stamp = System.currentTimeMillis();
                                break;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }
                        }
                    }
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
