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

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.maven.SettingsConfigurationException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reactor.MavenExecutionException;
import org.apache.maven.settings.Activation;
import org.apache.maven.settings.ActivationProperty;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.debug.JPDAStart;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener;
import org.codehaus.mevenide.netbeans.embedder.exec.MyLifecycleExecutor;
import org.codehaus.mevenide.netbeans.execute.ui.RunGoalsPanel;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
    private StopAction stop;
    private AggregateProgressHandle handle;
    private OutputHandler out;
    
    private Logger LOGGER = Logger.getLogger(MavenJavaExecutor.class.getName());
    /**
     * All tabs which were used for some process which has now ended.
     * These are closed when you start a fresh process.
     * Map from tab to tab display name.
     */
    private static final Map freeTabs = new WeakHashMap();
    private ExecutorTask task;
    
    
    public MavenJavaExecutor(RunConfig conf) {
        config = conf;
        String name = conf.getProject() != null ? "Build " + conf.getProject().getOriginalMavenProject().getArtifactId() :
                                                  "Execute Maven";  
        handle = AggregateProgressFactory.createHandle(name, new ProgressContributor[0], this, null);
        ProgressContributor backupContrib = AggregateProgressFactory.createProgressContributor("backup");
        handle.addContributor(backupContrib);
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
                    stop = (StopAction)vals.next();
                    rerun.setConfig(config);
                    rerunDebug.setConfig(config);
                    stop.setExecutor(this);
                    try {
                        io.getOut().reset();
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
            stop = new StopAction();
            Action[] actions = new Action[] {
                rerun, 
                rerunDebug,
                stop
            };
            io = IOProvider.getDefault().getIO(config.getExecutionName(), actions);
            rerun.setConfig(config);
            rerunDebug.setConfig(config);
            stop.setExecutor(this);
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
        stop.setEnabled(true);
        String basedir = System.getProperty("basedir");
        handle.start();
        try {
            MavenEmbedder embedder;
            ProgressTransferListener.setAggregateHandle(handle);
            out = new OutputHandler(ioput, config.getProject(), handle);
            IOBridge.pushSystemInOutErr(out);
            embedder = EmbedderFactory.createExecuteEmbedder(out);
            if (config.getProject() != null) {
                try {
                    checkDebuggerListening(config, out);
                } catch (MojoExecutionException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(), ex);
                } catch (MojoFailureException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(), ex);
                }
            }
            File repoRoot = InstalledFileLocator.getDefault().locate("m2-repository", null, false);
            //TODO we should get completely rid of this..
            Profile myProfile = new Profile();
            if (repoRoot != null) {
                //can happen when users don't install the repository module.
                myProfile.setId("netbeans-public");
                Repository repo = new Repository();
                repo.setUrl("file://" + repoRoot.getAbsolutePath());
                repo.setId("netbeansIDE-repo-internal");
                RepositoryPolicy snap = new RepositoryPolicy();
                snap.setEnabled(false);
                repo.setSnapshots(snap);
                repo.setName("NetBeans IDE internal Repository hosting plugins that are executable in NetBeans IDE only.");
                myProfile.addPluginRepository(repo);
                Activation act = new Activation();
                ActivationProperty prop = new ActivationProperty();
                prop.setName("netbeans.execution");
                prop.setValue("true");
                act.setProperty(prop);
                myProfile.setActivation(act);
            }
            
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            
            Settings settings = embedder.buildSettings( userSettingsPath,
                    globalSettingsPath,
                    MavenExecutionSettings.getDefault().getPluginUpdatePolicy());
            if (repoRoot != null) {
                settings.addProfile(myProfile);
            }
            settings.setUsePluginRegistry(MavenExecutionSettings.getDefault().isUsePluginRegistry());
            //MEVENIDE-407
            if (settings.getLocalRepository() == null) {
                settings.setLocalRepository(new File(userLoc, "repository").getAbsolutePath());
            }
            if (MavenExecutionSettings.getDefault().isSynchronizeProxy()) {
                //TODO
            }
            MavenExecutionRequest req = new DefaultMavenExecutionRequest();
            req.addActiveProfiles(config.getActivatedProfiles());
			// TODO remove explicit activation
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            //            req.activateDefaultEventMonitor();
            if (config.isOffline() != null) {
                settings.setOffline(config.isOffline().booleanValue());
            } else {
                config.setOffline(Boolean.valueOf(settings.isOffline()));
            }
            req.setSettings(settings);
            req.setGoals(config.getGoals());
            //mavenCLI adds all System.getProperties() in there as well..
            Properties props = new Properties();
            props.putAll(System.getProperties());
            props.putAll(config.getProperties());
            props.setProperty("netbeans.execution", "true");
            
            req.setProperties(props);
            req.setBasedir(config.getExecutionDirectory());
            File pom = new File(config.getExecutionDirectory(), "pom.xml");
            if (pom.exists()) {
                req.setPomFile(pom.getAbsolutePath());
            }
            req.setLocalRepositoryPath(embedder.getLocalRepositoryPath(settings));
            req.addEventMonitor(out);
            req.setTransferListener(new ProgressTransferListener());
            //            req.setReactorActive(true);
            
            req.setFailureBehavior(MavenExecutionSettings.getDefault().getFailureBehaviour());
            req.setStartTime(new Date());
            req.setGlobalChecksumPolicy(MavenExecutionSettings.getDefault().getChecksumPolicy());
            
            boolean debug = config.isShowDebug();
            req.setShowErrors(debug || config.isShowError());
            if (debug) {
                req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_DEBUG);
                out.setThreshold(MavenEmbedderLogger.LEVEL_DEBUG);
            } else {
                req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_INFO);
                out.setThreshold(MavenEmbedderLogger.LEVEL_INFO);
            }
            req.setUpdateSnapshots(config.isUpdateSnapshots());
            req.setRecursive(config.isRecursive());
            embedder.execute(req);
        } catch (MavenExecutionException ex) {
            LOGGER.log(Level.FINE, ex.getMessage(), ex);
        } catch (SettingsConfigurationException ex) {
            LOGGER.log(Level.FINE, ex.getMessage(), ex);
        } catch (ThreadDeath death) {
//            cancel();
            throw death;
        } finally {
            handle.finish();
            ProgressTransferListener.clearAggregateHandle();
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
            stop.setEnabled(false);
            synchronized (freeTabs) {
                Collection col = new ArrayList();
                col.add(config.getExecutionName());
                col.add(rerun);
                col.add(rerunDebug);
                col.add(stop);
                freeTabs.put(ioput, col);
            }
            EmbedderFactory.resetProjectEmbedder();
            List<File> fireList = MyLifecycleExecutor.getAffectedProjects();
            for (File elem: fireList) {
                if (elem == null) {
                    // during archetype creation?
                    continue;
                }
                FileObject fo = FileUtil.toFileObject(elem);
                if (fo != null) {
                    //TODO have the firing based on open projects only..
                    ProjectURLWatcher.fireMavenProjectReload(FileOwnerQuery.getOwner(fo));
                }
            }
            doRemoveAllShutdownHooks();
        }
    }
    
    public boolean cancel() {
        if (out != null) {
            out.requestCancel(task);
        }
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

    public void setTask(ExecutorTask task) {
        this.task = task;
    }

    /**
     * a brute force hack to workaround a severe issue with Maven Plugins 
     * registering shutdown hooks. The plugin's ThreadGroup was long gone and the
     * hooks thread was not capable of being run, halting the shutdown of the IDE
     * indefinitely.
     * The workaround involves use of reflection to get the list of shutdown hooks
     *  and remove/run all that appeared in the current threadgroup. The chances are
     * these stem from the plugins. (Use of shutdown hooks in the plugins should be punished
     * without mercy)
     */ 
    private void doRemoveAllShutdownHooks() {
        try     {
            java.lang.Class shutdown = java.lang.Class.forName("java.lang.Shutdown"); //NOI18N
            java.lang.reflect.Field fld = shutdown.getDeclaredField("hooks"); //NOI18N
            if (fld != null) {
                fld.setAccessible(true);
                Collection set = (Collection) fld.get(null);
                if (set != null) {
                    // objects are Shutdown.WrappedHook instances
                    for (Object wr : new ArrayList(set)) {
                        if (wr instanceof Runnable) {
                            // we'return in 1.6 and later.. it's all Runnables, not Threads..
                            // not possible to distinguish the maven shutdown hooks from the rest..
                            // but should not cause any trouble anymore..
                            break;
                        }
                         Field hookFld = wr.getClass().getDeclaredField("hook"); //NOI18N
                         hookFld.setAccessible(true);
                         Thread hook = (Thread) hookFld.get(wr);
                         if (hook.getThreadGroup() != null && hook.getThreadGroup() == Thread.currentThread().getThreadGroup()) {
                             hook.start();
                             try {
                                 hook.join();
                             } catch (InterruptedException e) {
                                 
                             } finally {
                                 Runtime.getRuntime().removeShutdownHook(hook);
                             }
                         }
                    }
                }
            }
        }
        catch (Exception ex) {
            LOGGER.log(Level.INFO,
                "Error removing shutdown hook originated from Maven build. " + ex.getMessage(), 
                ex);
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
            putValue(Action.NAME, debug ? "Re-run with different parameters" : "Re-run the goals.");
            putValue(Action.SHORT_DESCRIPTION, debug ? "Re-run with different parameters": "Re-run the goals.");
            setEnabled(false);
            
        }
        
        void setConfig(RunConfig config) {
            this.config = config;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (debug) {
                RunGoalsPanel pnl = new RunGoalsPanel();
                DialogDescriptor dd = new DialogDescriptor(pnl, "Run Maven");
                pnl.readConfig(config);
                Object retValue = DialogDisplayer.getDefault().notify(dd);
                if (retValue == DialogDescriptor.OK_OPTION) {
                    BeanRunConfig newConfig = new BeanRunConfig();
                    newConfig.setExecutionDirectory(config.getExecutionDirectory());
                    newConfig.setExecutionName(config.getExecutionName());
                    newConfig.setProject(config.getProject());
                    pnl.applyValues(newConfig);
                    RunUtils.executeMaven("Maven", newConfig);
                }
            } else {
                RunConfig newConfig = config;
                RunUtils.executeMaven("Maven", newConfig);
            }
            //TODO the waiting on tasks won't work..
        }
    }
    
    static class StopAction extends AbstractAction {
        private MavenJavaExecutor exec;
        StopAction() {
            putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/stop.gif")));
            putValue(Action.NAME, "Stop execution");
            putValue(Action.SHORT_DESCRIPTION, "Stop the currently executing build");
            setEnabled(false);
        }
        
        void setExecutor(MavenJavaExecutor ex) {
            exec = ex;
        }
        public void actionPerformed(ActionEvent e) {
            exec.cancel();
        }
    }
}
