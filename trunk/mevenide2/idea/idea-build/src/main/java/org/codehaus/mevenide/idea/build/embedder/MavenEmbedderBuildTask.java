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
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.settings.Settings;
import org.codehaus.mevenide.idea.build.AbstractMavenBuildTask;
import org.codehaus.mevenide.idea.build.IBuildEnvironment;
import org.codehaus.mevenide.idea.build.LogListener;
import org.codehaus.mevenide.idea.build.MavenConfiguration;
import org.codehaus.mevenide.idea.build.util.BuildConstants;

import java.io.File;
import java.util.Properties;

/**
 * Describe what this class does.
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
        MavenConfiguration mavenOption = buildEnvironment.getMavenBuildSettings().getMavenConfiguration();
        try {
            mavenEmbedder = buildEnvironment.getMavenEmbedder();

            File pomFile = new File(buildEnvironment.getPomFile());
            MavenExecutionRequest req = new DefaultMavenExecutionRequest();

            if (pomFile.exists()) {
                req.setPomFile(pomFile.getAbsolutePath());
            }
            req.setBasedir(new File(buildEnvironment.getWorkingDir()));
            req.setLocalRepositoryPath(buildEnvironment.getMavenBuildSettings().getMavenRepository());

            String mavenSettingsFile =
                    StringUtils.defaultString(buildEnvironment.getMavenBuildSettings().getMavenSettingsFile());
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            Settings settings = mavenEmbedder.buildSettings(new File(mavenSettingsFile), userSettingsPath, false);
            settings.setOffline(mavenOption.isWorkOffline());
            req.setSettings(settings);
            req.setLocalRepositoryPath(mavenEmbedder.getLocalRepositoryPath(settings));
            req.setRecursive(!mavenOption.isNonRecursive());
            req.setShowErrors(mavenOption.isProduceExceptionErrorMessages());
      //      req.setFailureBehavior("fail-never");
            req.setGoals(buildEnvironment.getGoals());
            Properties props = new Properties();
            props.putAll(System.getProperties());
      //      props.putAll(config.getProperties());
            props.setProperty("idea.execution", "true");

            if (mavenOption.isSkipTests()) {
                props.setProperty("test", "skip");
            }
            req.setProperties(props);
            MavenEmbedderLogger mavenEmbedderLogger = mavenEmbedder.getLogger();

            req.setLoggingLevel(mavenOption.getOutputLevel());
            mavenEmbedderLogger.setThreshold(mavenOption.getOutputLevel());

            req.addEventMonitor(new DefaultEventMonitor(new PlexusLoggerAdapter(mavenEmbedderLogger)));
            req.addEventMonitor(
                    new MavenEmbedderBuildEventMonitor(ProgressManager.getInstance().getProgressIndicator(), this));
   //         req.setGlobalChecksumPolicy(mavenOption.);
            req.setStartTime(new java.util.Date());
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
