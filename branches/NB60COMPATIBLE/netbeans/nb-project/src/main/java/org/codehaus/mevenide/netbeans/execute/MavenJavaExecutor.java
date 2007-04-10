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
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
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
import org.codehaus.mevenide.netbeans.debug.JPDAStart;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener;
import org.codehaus.mevenide.netbeans.embedder.exec.MyLifecycleExecutor;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.FileOwnerQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.windows.InputOutput;

/**
 * support for executing maven, from the ide using embedder
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJavaExecutor extends AbstractMavenExecutor {
    
    private AggregateProgressHandle handle;
    private OutputHandler out;
    
    private Logger LOGGER = Logger.getLogger(MavenJavaExecutor.class.getName());
    
    
    public MavenJavaExecutor(RunConfig conf) {
        super(conf);
        String name = conf.getProject() != null ? "Build " + conf.getProject().getOriginalMavenProject().getArtifactId() :
                                                  "Execute Maven";  
        handle = AggregateProgressFactory.createHandle(name, new ProgressContributor[0], this, null);
        ProgressContributor backupContrib = AggregateProgressFactory.createProgressContributor("backup");
        handle.addContributor(backupContrib);
    }
    
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        InputOutput ioput = getInputOutput();
        actionStatesAtStart();
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
            actionStatesAtFinish();
            markFreeTab(ioput);
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
            //TODO add a timeout for this and do a hard kill otherwise
        }
        return true;
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
}
