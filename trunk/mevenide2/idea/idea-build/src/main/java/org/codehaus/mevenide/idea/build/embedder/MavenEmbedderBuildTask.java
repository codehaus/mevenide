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
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.monitor.event.DefaultEventMonitor;

import org.codehaus.mevenide.idea.build.AbstractMavenBuildTask;
import org.codehaus.mevenide.idea.build.IBuildEnvironment;
import org.codehaus.mevenide.idea.build.LogListener;
import org.codehaus.mevenide.idea.build.util.BuildConstants;

import java.io.File;

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
        MavenEmbedder maven;
        MavenEmbedderBuildLogger logger = (MavenEmbedderBuildLogger) buildEnvironment.getLogger();

        try {
            maven = buildEnvironment.getMavenEmbedder();

            File pomFile = new File(buildEnvironment.getPomFile());
            MavenExecutionRequest req = new DefaultMavenExecutionRequest();

            req.setPomFile(pomFile.getPath());
            req.setBasedir(new File(buildEnvironment.getWorkingDir()));
            req.setLocalRepositoryPath(buildEnvironment.getMavenBuildSettings().getMavenRepository());

            String mavenSettingsFile =
                StringUtils.defaultString(buildEnvironment.getMavenBuildSettings().getMavenSettingsFile());

            req.setSettings(maven.buildSettings(new File(mavenSettingsFile),
                    new File(System.getProperty("user.dir") + "/settings.xml"), false));

            // req.setProperties(props);
            req.setRecursive(true);
            req.setShowErrors(true);
            req.setFailureBehavior("fail-fast");
            req.setGoals(buildEnvironment.getGoals());
            req.addEventMonitor(new DefaultEventMonitor(new PlexusLoggerAdapter(maven.getLogger())));
            req.addEventMonitor(
                new MavenEmbedderBuildEventMonitor(ProgressManager.getInstance().getProgressIndicator(), this));
            maven.execute(req);
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
