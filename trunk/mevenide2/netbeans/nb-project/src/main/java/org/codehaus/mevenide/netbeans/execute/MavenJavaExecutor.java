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

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import org.apache.maven.BuildFailureException;
import org.apache.maven.SettingsConfigurationException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.model.Plugin;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.registry.MavenPluginRegistryBuilder;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.reactor.MavenExecutionException;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.openide.ErrorManager;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInstall;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;



/**
 * support for executing maven, from the ide using embedder
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJavaExecutor implements Runnable, Cancellable {
    
    private static final RequestProcessor PROCESSOR = new RequestProcessor("Maven2 execution", 3);
    
    private RunConfig config;
    private InputOutput io;
    
    public MavenJavaExecutor(RunConfig conf) {
        config = conf;
    }
    
    private InputOutput createInputOutput() {
        InputOutput newio = IOProvider.getDefault().getIO(config.getExecutionName(), false);
        newio.setErrSeparated(false);
        try {
            newio.getOut().reset();
        } catch (IOException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return newio;
    }
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        InputOutput ioput = getInputOutput();
        try {
            MavenEmbedder embedder;
            OutputHandler out = new OutputHandler(ioput, config.getProject());
            if (config.getClassLoader() != null) {
                embedder = EmbedderFactory.createExecuteEmbedder(out, config.getClassLoader());
            } else {
                embedder = EmbedderFactory.createExecuteEmbedder(out);
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
                                                        false,
                                                        false,
                                                        true, //use plugin registry
                                                        Boolean.FALSE);
            settings.addProfile(myProfile);
            MavenExecutionRequest req = new DefaultMavenExecutionRequest();
            // need to set some profiles or get NPE!
            req.addActiveProfiles(Collections.EMPTY_LIST).addInactiveProfiles(Collections.EMPTY_LIST);
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
//            req.activateDefaultEventMonitor();
            req.setSettings(settings);
            req.setGoals(config.getGoals());
            req.setProperties(config.getProperties());
            req.setBasedir(config.getExecutionDirectory());
            req.setPomFile(new File(config.getExecutionDirectory(), "pom.xml").getAbsolutePath());
            req.setLocalRepositoryPath(settings.getLocalRepository() != null ? settings.getLocalRepository() : new File(userLoc, "repository").getAbsolutePath());
            req.addEventMonitor(out);
            req.setTransferListener(out);
//            req.setReactorActive(true);

            req.setFailureBehavior(MavenExecutionRequest.REACTOR_FAIL_FAST);
            req.setStartTime(new Date());
            req.setGlobalChecksumPolicy(MavenExecutionRequest.CHECKSUM_POLICY_WARN);
            req.setShowErrors(true);
            req.setLoggingLevel(MavenExecutionRequest.LOGGING_LEVEL_WARN);
            req.setUpdateSnapshots(false);
            embedder.execute(req);
        } catch (MavenEmbedderException ex) {
            ex.printStackTrace();
        } catch (MavenExecutionException ex) {
            ex.printStackTrace();
        } catch (SettingsConfigurationException ex) {
                ex.printStackTrace();
        } catch (ThreadDeath death) {
            cancel();
            throw death;
        } finally {
            ioput.getOut().close();
            ioput.getErr().close();
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
    
}
