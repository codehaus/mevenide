/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
import org.codehaus.plexus.util.StringUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Utilities;
import org.openide.windows.InputOutput;

/**
 * support for executing maven, externally on the command line.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenCommandLineExecutor extends AbstractMavenExecutor {
    
    private ProgressHandle handle;
    private CommandLineOutputHandler out;
    private Process process;
    
    private Logger LOGGER = Logger.getLogger(MavenCommandLineExecutor.class.getName());
    
    
    public MavenCommandLineExecutor(RunConfig conf) {
        super(conf);
        handle = ProgressHandleFactory.createHandle(conf.getTaskDisplayName(), this);
    }
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        InputOutput ioput = getInputOutput();
        actionStatesAtStart();
        handle.start();
        processInitialMessage();
        try {
            out = new CommandLineOutputHandler(ioput, config.getProject(), handle, config);
            if (config.getProject() != null) {
                try {
                    checkDebuggerListening(config, out);
                } catch (MojoExecutionException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(), ex);
                } catch (MojoFailureException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(), ex);
                }
            }
            //TODO we might need to copy and update settings file..
            File userLoc = new File(System.getProperty("user.home"), ".m2"); //NOI18N
            File userSettingsPath = new File(userLoc, "settings.xml");//NOI18N
            
            
            File workingDir = config.getExecutionDirectory();
            List<String> cmdLine = createMavenExecutionCommand(config);
            ProcessBuilder builder = new ProcessBuilder(cmdLine);
            builder.redirectErrorStream(true);
            builder.directory(workingDir);
            //TODO set the JDK of choice in env
//            builder.environment();
            ioput.getOut().println("WARNING: You are running Maven builds externally, some UI functionality will not be available."); //NOI18N - to be shown in log.
            ioput.getOut().println("Executing:" + StringUtils.join(builder.command().iterator(), " "));//NOI18N - to be shown in log.
            process = builder.start();
            out.setStdOut(process.getInputStream());
            out.setStdErr(process.getInputStream());
            out.setStdIn(process.getOutputStream());
            process.waitFor();
            out.waitFor();
        } catch (IOException x) {
            //TODO
            LOGGER.log(Level.INFO , x.getMessage(), x);
        } catch (InterruptedException x) {
            //TODO
            LOGGER.log(Level.INFO , x.getMessage(), x);
        } catch (ThreadDeath death) {
            if (process != null) {
                process.destroy();
            }
            throw death;
        } finally {
            out.buildFinished();
            handle.finish();
            ioput.getOut().close();
            ioput.getErr().close();
            actionStatesAtFinish();
            markFreeTab();
        }
    }
    
    public boolean cancel() {
        if (process != null) {
            process.destroy();
            process = null;
        }
        return true;
    }
        
    private static List<String> createMavenExecutionCommand(RunConfig config) {
        File mavenHome = MavenExecutionSettings.getDefault().getCommandLinePath();
        assert mavenHome != null;
        
        //Do we care?
        String mavenOpts = System.getenv("MAVEN_OPTS") == null ? "" : System.getenv("MAVEN_OPTS");//NOI18N
        List<String> toRet = new ArrayList<String>();
        String ex = Utilities.isWindows() ? "mvn.bat" : "mvn"; //NOI18N
        File bin = new File(mavenHome, "bin" + File.separator + ex);//NOI18N
        toRet.add(bin.getAbsolutePath());
        
        for (Object key : config.getProperties().keySet()) {
            String val = config.getProperties().getProperty((String)key);
            toRet.add("-D" + key + "=" + val);//NOI18N
        }

        if (config.isOffline() != null && config.isOffline().booleanValue()) {
            toRet.add("--offline");//NOI18N
        }
        if (!config.isRecursive()) {
            toRet.add("--non-recursive");//NOI18N
        }
        if (config.isShowDebug()) {
            toRet.add("--debug");//NOI18N
        }
        if (config.isShowError()) {
            toRet.add("--errors");//NOI18N
        }
        if (!MavenExecutionSettings.getDefault().isUsePluginRegistry()) {
            toRet.add("--no-plugin-registry");//NOI18N
        }
        String checksum = MavenExecutionSettings.getDefault().getChecksumPolicy();
        if (checksum != null) {
            if (MavenExecutionRequest.CHECKSUM_POLICY_FAIL.equals(checksum)) {
                toRet.add("--strict-checksums");//NOI18N
            }
            if (MavenExecutionRequest.CHECKSUM_POLICY_WARN.equals(checksum)) {
                toRet.add("--lax-checksums");//NOI18N
            }
        }
        if (config.isUpdateSnapshots()) {
            toRet.add("--update-snapshots");//NOI18N
        }
        
        String profiles = "";//NOI18N
        for (Object profile : config.getActivatedProfiles()) {
            profiles = profiles + "," + profile;//NOI18N
        }
        if (profiles.length() > 0) {
            profiles = profiles.substring(1);
            toRet.add("-P" + profiles);//NOI18N
        }
        
        for (String goal : config.getGoals()) {
            toRet.add(goal);
        }

        return toRet;
    }
    
}
