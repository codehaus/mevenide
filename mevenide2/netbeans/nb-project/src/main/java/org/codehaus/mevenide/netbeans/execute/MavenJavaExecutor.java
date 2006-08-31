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

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.maven.SettingsConfigurationException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reactor.MavenExecutionException;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.codehaus.mevenide.netbeans.debug.JPDAStart;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Cancellable;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * support for executing maven, from the ide using embedder
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJavaExecutor implements Runnable, Cancellable {
    
    private RunConfig config;
    private InputOutput io;
    private ReRunAction rerun;
    private ReRunAction rerunDebug;
    
    /**
     * All tabs which were used for some process which has now ended.
     * These are closed when you start a fresh process.
     * Map from tab to tab display name.
     */
    private static final Map freeTabs = new WeakHashMap();
    
    
    private MavenJavaExecutor(RunConfig conf) {
        config = conf;
    }
    
    /**
     *  execute maven build in netbeans execution engine.
     */
    public static ExecutorTask executeMaven(String runtimeName, RunConfig config) {
        MavenJavaExecutor exec = new MavenJavaExecutor(config);
        return executeMavenImpl(runtimeName, exec);
    }
    
    private static ExecutorTask executeMavenImpl(String runtimeName, MavenJavaExecutor exec) {
        return ExecutionEngine.getDefault().execute(runtimeName, exec, exec.getInputOutput());
    }
    
    private InputOutput createInputOutput() {
        synchronized (freeTabs) {
            Iterator it = freeTabs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                InputOutput free = (InputOutput)entry.getKey();
                Iterator vals = ((Collection)entry.getValue()).iterator();
                String freeName = (String)vals.next();
                if (io == null && freeName.equals(config.getExecutionName())) {
                    // Reuse it.
                    io = free;
                    rerun = (ReRunAction)vals.next();
                    rerunDebug = (ReRunAction)vals.next();
                    rerun.setConfig(config);
                    rerunDebug.setConfig(config);
                    try {
                        io.getOut().reset();
                        io.getErr().reset();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // useless: io.flushReader();
                } else {
                    // Discard it.
                    free.closeInputOutput();
                }
            }
            freeTabs.clear();
        }
        //                }
        if (io == null) {
            rerun = new ReRunAction(false);
            rerunDebug = new ReRunAction(true);
            Action[] actions = new Action[] {
                rerun, 
                rerunDebug
            };
            io = IOProvider.getDefault().getIO(config.getExecutionName(), actions);
            rerun.setConfig(config);
            rerunDebug.setConfig(config);
        }
        return io;
    }
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        InputOutput ioput = getInputOutput();
        rerun.setEnabled(false);
        rerunDebug.setEnabled(false);
        String basedir = System.getProperty("basedir");
        try {
            MavenEmbedder embedder;
            OutputHandler out = new OutputHandler(ioput, config.getProject());
            embedder = EmbedderFactory.createExecuteEmbedder(out);
            if (config.getProject() != null) {
                try {
                    checkDebuggerListening(config, out);
                } catch (MojoExecutionException ex) {
                    ex.printStackTrace();
                } catch (MojoFailureException ex) {
                    ex.printStackTrace();
                }
            }
            //            ArtifactRepository netbeansRepo = null;
            File repoRoot = InstalledFileLocator.getDefault().locate("m2-repository", null, false);
            //            netbeansRepo = embedder.createRepository("file://" + repoRoot.getAbsolutePath(), "netbeansIDE-repo-internal");
            Profile myProfile = new Profile();
            myProfile.setId("netbeans-public");
            Repository repo = new Repository();
            repo.setUrl("file://" + repoRoot.getAbsolutePath());
            repo.setId("netbeansIDE-repo-internal");
            RepositoryPolicy snap = new RepositoryPolicy();
            snap.setEnabled(false);
            repo.setSnapshots(snap);
            repo.setName("Netbeans IDE internal Repository hosting plugins that are executable in Netbeans IDE only.");
            myProfile.addPluginRepository(repo);
            
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            
            Settings settings = embedder.buildSettings( userSettingsPath,
                    globalSettingsPath,
                    MavenExecutionSettings.getDefault().getPluginUpdatePolicy());
            settings.addProfile(myProfile);
            settings.setUsePluginRegistry(MavenExecutionSettings.getDefault().isUsePluginRegistry());
            //MEVENIDE-407
            if (settings.getLocalRepository() == null) {
                settings.setLocalRepository(new File(userLoc, "repository").getAbsolutePath());
            }
            MavenExecutionRequest req = new DefaultMavenExecutionRequest();
            // need to set some profiles or get NPE!
            req.addActiveProfiles(Collections.EMPTY_LIST).addInactiveProfiles(Collections.EMPTY_LIST);
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            req.addActiveProfiles(config.getActiveteProfiles());
            //            req.activateDefaultEventMonitor();
            if (config.isOffline() != null) {
                settings.setOffline(config.isOffline().booleanValue());
            }
            req.setSettings(settings);
            req.setGoals(config.getGoals());
            //mavenCLI adds all System.getProperties() in there as well..
            Properties props = new Properties();
            props.putAll(System.getProperties());
            props.putAll(config.getProperties());
            req.setProperties(props);
            req.setBasedir(config.getExecutionDirectory());
            File pom = new File(config.getExecutionDirectory(), "pom.xml");
            if (pom.exists()) {
                req.setPomFile(pom.getAbsolutePath());
            }
            req.setLocalRepositoryPath(embedder.getLocalRepositoryPath(settings));
            req.addEventMonitor(out);
            req.setTransferListener(out);
            //            req.setReactorActive(true);
            
            req.setFailureBehavior(MavenExecutionSettings.getDefault().getFailureBehaviour());
            req.setStartTime(new Date());
            req.setGlobalChecksumPolicy(MavenExecutionSettings.getDefault().getChecksumPolicy());
            
            boolean debug = config.isShowDebug() != null
                    ? config.isShowDebug().booleanValue()
                    : MavenExecutionSettings.getDefault().isShowDebug();
            req.setShowErrors(debug || (config.isShowError() != null
                    ? config.isShowError().booleanValue()
                    : MavenExecutionSettings.getDefault().isShowErrors()));
            if (debug) {
                req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_DEBUG);
                out.setThreshold(MavenEmbedderLogger.LEVEL_DEBUG);
            } else {
                req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_INFO);
                out.setThreshold(MavenEmbedderLogger.LEVEL_INFO);
            }
            req.setUpdateSnapshots(false);
            IOBridge.pushSystemInOutErr(out);
            embedder.execute(req);
        } catch (MavenEmbedderException ex) {
            //            ex.printStackTrace();
            //            ErrorManager.getDefault().notify(ex);
        } catch (MavenExecutionException ex) {
            //            ex.printStackTrace();
            //            ErrorManager.getDefault().notify(ex);
        } catch (SettingsConfigurationException ex) {
            //                ex.printStackTrace();
        } catch (ThreadDeath death) {
            cancel();
            throw death;
        } finally {
            IOBridge.restoreSystemInOutErr();
            ioput.getOut().close();
            ioput.getErr().close();
            //SUREFIRE-94/MEVENIDE-412 the surefire plugin sets basedir environment variable, which breaks ant integration
            // in netbeans.
            if (basedir == null) {
                System.getProperties().remove("basedir");
            } else {
                System.setProperty("basedir", basedir);
            }
            rerun.setEnabled(true);
            rerunDebug.setEnabled(true);
            synchronized (freeTabs) {
                Collection col = new ArrayList();
                col.add(config.getExecutionName());
                col.add(rerun);
                col.add(rerunDebug);
                freeTabs.put(ioput, col);
            }
        }
    }
    
    public boolean cancel() {
        //        if (proces != null) {
        //            // this system out prints to output window..
        //            System.err.println("**User cancelled execution**");
        //            proces.destroy();
        //        }
        return true;
    }
    
    public InputOutput getInputOutput() {
        if (io == null) {
            io = createInputOutput();
        }
        return io;
    }
    
    private void checkDebuggerListening(RunConfig config, OutputHandler handler) throws MojoExecutionException, MojoFailureException {
        if ("true".equals(config.getProperties().getProperty("jpda.listen"))) {
            JPDAStart start = new JPDAStart();
            start.setName(config.getProject().getOriginalMavenProject().getArtifactId());
            start.setStopClassName(config.getProperties().getProperty("jpda.stopclass"));
            start.setLog(handler);
            String val = start.execute(config.getProject());
            Enumeration en = config.getProperties().propertyNames();
            while (en.hasMoreElements()) {
                String key = (String)en.nextElement();
                String value = config.getProperties().getProperty(key);
                StringBuffer buf = new StringBuffer(value);
                String replaceItem = "${jpda.address}";
                int index = buf.indexOf(replaceItem);
                while (index > -1) {
                    String newItem = val;
                    newItem = newItem == null ? "" : newItem;
                    buf.replace(index, index + replaceItem.length(), newItem);
                    index = buf.indexOf(replaceItem);
                }
                //                System.out.println("setting property=" + key + "=" + buf.toString());
                config.getProperties().setProperty(key, buf.toString());
            }
            config.getProperties().put("jpda.address", val);
        }
    }
    
    
    
    static class ReRunAction extends AbstractAction {
        private RunConfig config;
        private boolean debug;
        
        public ReRunAction(boolean debug) {
            this.debug  = debug;
            this.putValue(Action.SMALL_ICON, debug ? 
                new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/refreshdebug.png")) :
                new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/refresh.png")));
            putValue(Action.NAME, debug ? "Re-run with Debug messages on." : "Re-run the goals.");
            putValue(Action.SHORT_DESCRIPTION, debug ? "Re-run with Debug messages on.": "Re-run the goals.");
            setEnabled(false);
            
        }
        
        void setConfig(RunConfig config) {
            this.config = config;
        }
        
        public void actionPerformed(ActionEvent e) {
            RunConfig newConfig = !debug ? config : new ProxyRunConfig(this.config) {
                public Boolean isShowDebug() {
                    return Boolean.TRUE;
                }
            };
            MavenJavaExecutor.executeMaven("Maven", newConfig);
            //TODO the waiting on tasks won't work..
        }
        
    }
}
