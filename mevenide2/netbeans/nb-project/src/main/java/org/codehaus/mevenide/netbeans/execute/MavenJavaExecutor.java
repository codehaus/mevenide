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
import java.util.Enumeration;
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
import org.openide.ErrorManager;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

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
        String basedir = System.getProperty("basedir");
        try {
            MavenEmbedder embedder;
            OutputHandler out = new OutputHandler(ioput, config.getProject());
            embedder = EmbedderFactory.createExecuteEmbedder(out);
            try {
                checkDebuggerListening(config, out);
            } catch (MojoExecutionException ex) {
                ex.printStackTrace();
            } catch (MojoFailureException ex) {
                ex.printStackTrace();
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
            req.setProperties(config.getProperties());
            req.setBasedir(config.getExecutionDirectory());
            req.setPomFile(new File(config.getExecutionDirectory(), "pom.xml").getAbsolutePath());
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
            embedder.execute(req);
        } catch (MavenEmbedderException ex) {
//            ex.printStackTrace();
        } catch (MavenExecutionException ex) {
//            ex.printStackTrace();
        } catch (SettingsConfigurationException ex) {
//                ex.printStackTrace();
        } catch (ThreadDeath death) {
            cancel();
            throw death;
        } finally {
            ioput.getOut().close();
            ioput.getErr().close();
            //SUREFIRE-94/MEVENIDE-412 the surefire plugin sets basedir environment variable, which breaks ant integration
            // in netbeans.
            if (basedir == null) {
                System.clearProperty("basedir");
            } else {
                System.setProperty("basedir", basedir);
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
    
}
