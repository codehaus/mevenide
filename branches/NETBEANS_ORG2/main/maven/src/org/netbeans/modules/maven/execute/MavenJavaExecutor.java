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

package org.netbeans.modules.maven.execute;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.maven.api.execute.RunConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.maven.cli.CLIReportingUtils;
import org.apache.maven.embedder.ConfigurationValidationResult;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.errors.DefaultCoreErrorReporter;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.embedder.exec.MyLifecycleExecutor;
import org.netbeans.modules.maven.options.MavenExecutionSettings;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.maven.api.execute.ExecutionContext;
import org.netbeans.modules.maven.api.execute.ExecutionResultChecker;
import org.netbeans.modules.maven.api.execute.LateBoundPrerequisitesChecker;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 * support for executing maven, from the ide using embedder
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJavaExecutor extends AbstractMavenExecutor {
    private static final String BASEDIR = "basedir";
    private static final String PROFILE_PRIVATE = "netbeans-private";
    private static final String PROFILE_PUBLIC = "netbeans-public";
    
    private AggregateProgressHandle handle;
    private JavaOutputHandler out;
    
    private Logger LOGGER = Logger.getLogger(MavenJavaExecutor.class.getName());
    
    private volatile boolean finishing = false;
    
    
    public MavenJavaExecutor(RunConfig conf) {
        super(conf);
        handle = AggregateProgressFactory.createHandle(conf.getTaskDisplayName(), new ProgressContributor[0], this, null);
        ProgressContributor backupContrib = AggregateProgressFactory.createProgressContributor("backup"); //NOI18N
        handle.addContributor(backupContrib);
    }
    
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        finishing = false;
        RunConfig clonedConfig = new BeanRunConfig(this.config);
        // check the prerequisites
        InputOutput ioput = getInputOutput();
        if (clonedConfig.getProject() != null) {
            ProgressHandle ph = ProgressHandleFactory.createSystemHandle("Additional maven build processing");
            ph.start();
            ExecutionContext exCon = ActionToGoalUtils.ACCESSOR.createContext(ioput, ph);
            try {
                Lookup.Result<LateBoundPrerequisitesChecker> result = clonedConfig.getProject().getLookup().lookup(new Lookup.Template<LateBoundPrerequisitesChecker>(LateBoundPrerequisitesChecker.class));
                for (LateBoundPrerequisitesChecker elem : result.allInstances()) {
                    if (!elem.checkRunConfig(clonedConfig, exCon)) {
                        return;
                    }
                }
            } finally {
                ph.finish();
            }
        }
        
        final Properties origanalProperties = clonedConfig.getProperties();
        actionStatesAtStart();
        String basedir = System.getProperty(BASEDIR);//NOI18N
        handle.start();
        ioput.getOut().println("WARNING: You are running embedded Maven builds, some build may fail due to incompatibilities with latest Maven release."); //NOI18N - to be shown in log.
        try {
            ioput.getOut().println("         To set Maven instance to use for building, click here.", //NOI18N - to be shown in log.
                    new OutputListener() {
                public void outputLineSelected(OutputEvent ev) {}
                public void outputLineAction(OutputEvent ev) {
                    OptionsDisplayer.getDefault().open(OptionsDisplayer.ADVANCED + "/Maven"); //NOI18N - the id is the name of instance in layers.
                }
                public void outputLineCleared(OutputEvent ev) {}
            });
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        processInitialMessage();
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();
        int executionResult = -10;
        try {
            MavenEmbedder embedder;
            ProgressTransferListener.setAggregateHandle(handle);
            out = new JavaOutputHandler(ioput, clonedConfig.getProject(), handle, clonedConfig);
            IOBridge.pushSystemInOutErr(out);
            boolean debug = clonedConfig.isShowDebug();
            req.setShowErrors(debug || clonedConfig.isShowError());
            if (debug) {
                req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_DEBUG);
                out.setThreshold(MavenEmbedderLogger.LEVEL_DEBUG);
            } else {
                req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_INFO);
                out.setThreshold(MavenEmbedderLogger.LEVEL_INFO);
            }
            
            File userSettingsPath = MavenEmbedder.DEFAULT_USER_SETTINGS_FILE;
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);//NOI18N
            DefaultConfiguration settConfig = new DefaultConfiguration();
            settConfig.setGlobalSettingsFile(globalSettingsPath);
            if (userSettingsPath.exists()) {
                settConfig.setUserSettingsFile(userSettingsPath);
            }
            ConfigurationValidationResult setres = MavenEmbedder.validateConfiguration(settConfig);
            if (!setres.isValid()) {
                if (setres.getUserSettingsException() != null) {
                    CLIReportingUtils.showError("Error reading user settings: ", setres.getUserSettingsException(), req.isShowErrors(), new DefaultCoreErrorReporter(), out);//NOI18N - part of maven output
                }
                if (setres.getUserSettingsException() != null) {
                    CLIReportingUtils.showError("Error reading global settings: ", setres.getGlobalSettingsException(), req.isShowErrors(), new DefaultCoreErrorReporter(), out);//NOI18N - part of maven output
                }
                return;
            }
            
            embedder = EmbedderFactory.createExecuteEmbedder(out);
            super.buildPlan.setEmbedder(embedder);
//mkleint: not relevant anymore, we don't ship the repository, rely on central repo instead.
//            File repoRoot = InstalledFileLocator.getDefault().locate("m2-repository", null, false);//NOI18N
//            //TODO we should get completely rid of this..
//            Profile myProfile = new Profile();
//            if (repoRoot != null) {
//                //can happen when users don't install the repository module.
//                myProfile.setId(PROFILE_PUBLIC);//NOI18N
//                Repository repo = new Repository();
//                repo.setUrl("file://" + repoRoot.getAbsolutePath());//NOI18N
//                repo.setId("netbeansIDE-repo-internal");//NOI18N
//                RepositoryPolicy snap = new RepositoryPolicy();
//                snap.setEnabled(false);
//                repo.setSnapshots(snap);
//                repo.setName("NetBeans IDE internal Repository hosting plugins that are executable in NetBeans IDE only.");//NOI18N
//                myProfile.addPluginRepository(repo);
//                Activation act = new Activation();
//                ActivationProperty prop = new ActivationProperty();
//                prop.setName("netbeans.execution");//NOI18N
//                prop.setValue("true");//NOI18N
//                act.setProperty(prop);
//                myProfile.setActivation(act);
//            }
            
            //TODO we need to reenact the custom dynamic profile.
//            Settings settings = embedder.buildSettings( userSettingsPath,
//                    globalSettingsPath,
//                    MavenExecutionSettings.getDefault().getPluginUpdatePolicy());
//            if (repoRoot != null) {
//                settings.addProfile(myProfile);
//            }
//            settings.setUsePluginRegistry(MavenExecutionSettings.getDefault().isUsePluginRegistry());
//            //MEVENIDE-407
//            if (settings.getLocalRepository() == null) {
//                settings.setLocalRepository(new File(userLoc, "repository").getAbsolutePath());//NOI18N
//            }
//            if (MavenExecutionSettings.getDefault().isSynchronizeProxy()) {
//            }
            
            req.addActiveProfiles(clonedConfig.getActivatedProfiles());
            
            // TODO remove explicit activation
            req.addActiveProfile(PROFILE_PUBLIC).addActiveProfile(PROFILE_PRIVATE);
            //            req.activateDefaultEventMonitor();
            if (clonedConfig.isOffline() != null) {
                req.setOffline(clonedConfig.isOffline().booleanValue());
            }
            req.setInteractiveMode(clonedConfig.isInteractive());
//TODO            req.setSettings(settings);
            req.setGoals(clonedConfig.getGoals());
            //mavenCLI adds all System.getProperties() in there as well..
            Properties props = new Properties();
            EmbedderFactory.fillEnvVars(props);
            props.putAll(excludeNetBeansProperties(System.getProperties()));
            props.putAll(clonedConfig.getProperties());
            props.setProperty("netbeans.execution", "true");//NOI18N
            
            req.setProperties(props);
            req.setBaseDirectory(clonedConfig.getExecutionDirectory());
            File pom = new File(clonedConfig.getExecutionDirectory(), "pom.xml");//NOI18N
            if (pom.exists()) {
                req.setPomFile(pom.getAbsolutePath());
            }
//TODO??            req.setLocalRepositoryPath(embedder.getSettings().getLocalRepository());
            req.addEventMonitor(out);
            req.setTransferListener(new ProgressTransferListener());
            //            req.setReactorActive(true);
            
            req.setReactorFailureBehavior(MavenExecutionSettings.getDefault().getFailureBehaviour());
            req.setStartTime(new Date());
            req.setGlobalChecksumPolicy(MavenExecutionSettings.getDefault().getChecksumPolicy());
            
            req.setUpdateSnapshots(clonedConfig.isUpdateSnapshots());
            req.setRecursive(clonedConfig.isRecursive());
            MavenExecutionResult res = embedder.execute(req);
            CLIReportingUtils.logResult(req, res, out);
            if (res.hasExceptions()) {
                //TODO something?
                executionResult = -1;
            } else {
                executionResult = 0;
            }
//        } catch (MavenExecutionException ex) {
//            LOGGER.log(Level.FINE, ex.getMessage(), ex);
//        } catch (SettingsConfigurationException ex) {
//            LOGGER.log(Level.FINE, ex.getMessage(), ex);
        } catch (RuntimeException re) {
            CLIReportingUtils.showError("Runtime Exception thrown during execution", re, req.isShowErrors(), req.getErrorReporter(), out);//NOI18N - part of maven output
            LOGGER.log(Level.FINE, re.getMessage(), re);
            executionResult = -2;
        } catch (ThreadDeath death) {
//            cancel();
            CLIReportingUtils.showError("Killed.", new Exception(""), false, req.getErrorReporter(), out); //NOI18N - part of maven output
            shutdownOutput(ioput);
            ioput = null;
            executionResult = ExecutionContext.EXECUTION_ABORTED;
            throw death;
        } finally {
            finishing = true; //#103460
            ProgressHandle ph = ProgressHandleFactory.createSystemHandle( "Additional maven build processing");
            ph.start();
            try { //defend against badly written extensions..
                out.buildFinished();
                Lookup.Result<ExecutionResultChecker> result = clonedConfig.getProject().getLookup().lookup(new Lookup.Template<ExecutionResultChecker>(ExecutionResultChecker.class));
               ExecutionContext exCon = ActionToGoalUtils.ACCESSOR.createContext(ioput, ph);
                for (ExecutionResultChecker elem : result.allInstances()) {
                    elem.executionResult(clonedConfig, exCon, executionResult);
                }
            }
            finally {
                ph.finish();
                shutdownOutput(ioput);
                handle.finish();
                handle = null;
                ProgressTransferListener.clearAggregateHandle();
                //SUREFIRE-94/MEVENIDE-412 the surefire plugin sets basedir environment variable, which breaks ant integration
                // in netbeans.
                if (basedir == null) {
                    System.getProperties().remove(BASEDIR);
                } else {
                    System.setProperty( BASEDIR,basedir);
                }
                //MEVENIDE-623 re add original Properties
                clonedConfig.setProperties(origanalProperties);

                actionStatesAtFinish();
                EmbedderFactory.resetProjectEmbedder();
                final List<File> fireList = MyLifecycleExecutor.getAffectedProjects();
                RequestProcessor.getDefault().post(new Runnable() { //#103460
                    public void run() {
                        for (File elem: fireList) {
                            if (elem == null) {
                                // during archetype creation?
                                continue;
                            }
                            FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(elem));
                            if (fo != null) {
                                //TODO have the firing based on open projects only..
                                NbMavenProject.fireMavenProjectReload(FileOwnerQuery.getOwner(fo));
                            }
                        }
                    }
                });
                doRemoveAllShutdownHooks();
            }
        }
    }
    
    private void shutdownOutput(InputOutput ioput) {
        if (ioput == null) {
            return;
        }
        ioput.getOut().close();
        ioput.getErr().close();
        markFreeTab();
        IOBridge.restoreSystemInOutErr();
    }
    
    /** Number of milliseconds to wait before forcibly halting a runaway process. */
    private static final int STOP_TIMEOUT = 3000;    
    
    public boolean cancel() {
        if (out != null) {
            out.requestCancel(task);
            // Try stopping at a safe point.
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(MavenJavaExecutor.class, "MSG_stopping"));
            // But if that doesn't do it, double-check later...
            // Yes Thread.stop() is deprecated; that is why we try to avoid using it.
            RequestProcessor.getDefault().create(new Runnable() {
                public void run() {
                    if (!finishing) {
                        task.stop();
                    }
                }
            }).schedule(STOP_TIMEOUT);
        }
        return true;
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
                "Error removing shutdown hook originated from Maven build. " + ex.getMessage(), //NOI18N
                ex);
        }
    }
}