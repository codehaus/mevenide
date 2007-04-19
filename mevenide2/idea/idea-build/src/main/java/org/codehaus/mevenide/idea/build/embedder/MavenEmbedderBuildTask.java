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


package org.codehaus.mevenide.idea.build.embedder;

import com.intellij.openapi.progress.ProgressManager;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.settings.Settings;
import org.codehaus.mevenide.idea.build.AbstractMavenBuildTask;
import org.codehaus.mevenide.idea.build.IBuildEnvironment;
import org.codehaus.mevenide.idea.build.LogListener;
import org.codehaus.mevenide.idea.build.IMavenBuildConfiguration;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.model.MavenConfiguration;

import java.io.File;
import java.util.Properties;

/**
 * Implements the execution of a Maven build process using the maven embedder component.
 * The embedder runs in his own process so nothing is blocked from executioning.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenEmbedderBuildTask extends AbstractMavenBuildTask {
    public MavenEmbedderBuildTask(IBuildEnvironment buildEnvironment) {
        super(buildEnvironment);
    }

    public void run() {
        MavenEmbedder mavenEmbedder;
        MavenEmbedderBuildLogger logger = (MavenEmbedderBuildLogger) buildEnvironment.getLogger();
        MavenConfiguration mavenConfig = buildEnvironment.getMavenBuildConfiguration().getMavenConfiguration();
        IMavenBuildConfiguration mavenBuildConfig = buildEnvironment.getMavenBuildConfiguration();
        try {
            mavenEmbedder = buildEnvironment.getMavenEmbedder();

            File pomFile = new File(buildEnvironment.getPomFile());
            MavenExecutionRequest req = new DefaultMavenExecutionRequest();

            if (pomFile.exists()) {
                req.setPomFile(pomFile.getAbsolutePath());
            }
            req.setBasedir(new File(buildEnvironment.getWorkingDir()));

            String mavenSettingsFile =
                    StringUtils.defaultString(mavenBuildConfig.getMavenSettingsFile());
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            Settings settings = mavenEmbedder
                    .buildSettings(new File(mavenSettingsFile), userSettingsPath, mavenConfig.isPluginUpdatePolicy());
            settings.setOffline(mavenConfig.isWorkOffline());
            //MEVENIDE-407
            if (settings.getLocalRepository() == null) {
                if (!StringUtils.isEmpty(mavenConfig.getLocalRepository())) {
                    settings.setLocalRepository(new File(mavenConfig.getLocalRepository()).getAbsolutePath());
                } else {
                    settings.setLocalRepository(new File(userLoc, "repository").getAbsolutePath());
                }
            }

            req.setSettings(settings);
            req.setLocalRepositoryPath(mavenEmbedder.getLocalRepositoryPath(settings));
            req.setRecursive(!mavenConfig.isNonRecursive());
            req.setShowErrors(mavenConfig.isProduceExceptionErrorMessages());
            req.setGoals(buildEnvironment.getGoals());
            req.setFailureBehavior(mavenConfig.getFailureBehavior());
            req.setGlobalChecksumPolicy(
                    (StringUtils.isEmpty(mavenConfig.getChecksumPolicy()) ? null : mavenConfig.getChecksumPolicy()));
            Properties props = new Properties();
            // add all system properties
            props.putAll(System.getProperties());
            // add all configured user properties
            props.putAll(mavenBuildConfig.getMavenProperties());
            props.setProperty("idea.execution", "true");

            if (mavenBuildConfig.isSkipTests()) {
                props.setProperty("test", "skip");
            }

            req.setProperties(props);
            MavenEmbedderLogger mavenEmbedderLogger = mavenEmbedder.getLogger();

            req.setLoggingLevel(mavenConfig.getOutputLevel());
            mavenEmbedderLogger.setThreshold(mavenConfig.getOutputLevel());
            DefaultEventMonitor eventMonitor = new DefaultEventMonitor(new PlexusLoggerAdapter(mavenEmbedderLogger));
            req.addEventMonitor(eventMonitor);
            req.addEventMonitor(
                    new MavenEmbedderBuildEventMonitor(ProgressManager.getInstance().getProgressIndicator(), this));
            req.setStartTime(new java.util.Date());
            logger.debug(mavenConfig.toString());
            mavenEmbedder.execute(req);
            stop();

            if (logger.isOutputPaused()) {
                logger.setOutputPaused(false);
                logger.flushBuffer();
            }

            if (isCancelled()) {
                logger.info(System.getProperty("line.separator") + BuildConstants.MESSAGE_EMBEDDED_MAVENBUILD_ABORTED,
                        LogListener.OUTPUT_TYPE_SYSTEM);
            }
        } catch (Exception e) {
            stop();

            if (logger.isOutputPaused()) {
                logger.setOutputPaused(false);
                logger.flushBuffer();
            }

            if (isCancelled()) {
                logger.info(System.getProperty("line.separator") + BuildConstants.MESSAGE_EMBEDDED_MAVENBUILD_ABORTED,
                        LogListener.OUTPUT_TYPE_SYSTEM);
            }
        }
    }
}
