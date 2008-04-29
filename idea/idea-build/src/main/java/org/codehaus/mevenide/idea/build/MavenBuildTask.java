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



package org.codehaus.mevenide.idea.build;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.util.CommonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildTask extends AbstractMavenBuildTask implements Runnable {
    private static final Logger LOG = Logger.getLogger(MavenBuildTask.class);
    private Process mavenProcess;

    public MavenBuildTask(IBuildEnvironment buildEnvironment) {
        super(buildEnvironment);
    }

    public void run() {
        MavenBuildFormattedLogger logger = (MavenBuildFormattedLogger) buildEnvironment.getLogger();

        try {
            List<String> executionCommand = BuildHelper.createMavenExecutionCommand(buildEnvironment);

            LOG.info("Running " + executionCommand);

            String[] cmdArray = new String[executionCommand.size()];
            Runtime rt = Runtime.getRuntime();
            ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();

            if (indicator != null) {
                indicator.setText(buildEnvironment.getGoals().toString());
                indicator.setIndeterminate(true);
            }

            LOG.info("Working directory is: " + buildEnvironment.getWorkingDir());
            mavenProcess = rt.exec(executionCommand.toArray(cmdArray), null,
                                   new File(buildEnvironment.getWorkingDir()));

            StringBuffer command = new StringBuffer();

            for (String anExecutionCommand : executionCommand) {
                command.append(anExecutionCommand);
                command.append(" ");
            }

            logger.info(command + System.getProperty("line.separator") + System.getProperty("line.separator"),
                        LogListener.OUTPUT_TYPE_SYSTEM);
            logger.setOutputType(LogListener.OUTPUT_TYPE_NORMAL);

            BufferedReader procout = new BufferedReader(new InputStreamReader(mavenProcess.getInputStream()));
            String line;

            while ((line = procout.readLine()) != null) {
                if (indicator.isCanceled()) {
                    cancel();
                }

                logger.info(line);
            }

            if (logger.isOutputPaused()) {
                logger.setOutputPaused(false);
                logger.flushBuffer();
            }

            int exitValue = stop();

            if (isCancelled()) {
                logger.info(System.getProperty("line.separator") + BuildConstants.MESSAGE_PROCESS_ABORTED
                            + ((mavenProcess != null)
                               ? mavenProcess.exitValue()
                               : "-1"), LogListener.OUTPUT_TYPE_SYSTEM);
            } else {
                if (exitValue == 0) {
                    logger.info(System.getProperty("line.separator") + BuildConstants.MESSAGE_PROCESS_FINISHED
                                + mavenProcess.exitValue(), LogListener.OUTPUT_TYPE_SYSTEM);

                    // Terminated by user
                } else {
                    logger.error(System.getProperty("line.separator")
                                 + BuildConstants.MESSAGE_PROCESS_ABNORMALLY_TERMINATED + ((mavenProcess != null)
                            ? mavenProcess.exitValue()
                            : "-1"), LogListener.OUTPUT_TYPE_SYSTEM);
                }
            }

            // User hit cancel button on progress dialog
        } catch (MavenBuildCancelledException ex) {
            int exitValue = stop();

            logger.info(System.getProperty("line.separator") + BuildConstants.MESSAGE_PROCESS_ABORTED + exitValue,
                        LogListener.OUTPUT_TYPE_SYSTEM);

            // catch MavenBuildExceptions
        } catch (MavenBuildException ex) {
            int exitValue = stop();

            logger.info(System.getProperty("line.separator") + BuildConstants.MESSAGE_PROCESS_ABNORMALLY_TERMINATED
                        + exitValue, LogListener.OUTPUT_TYPE_SYSTEM);
            LOG.error(ex.getMessage(), ex);
            logger.fatalError(ex.getMessage(), ex, LogListener.OUTPUT_TYPE_SYSTEM);

            // other exceptions
        } catch (Exception e) {
            logger.error(e.getMessage(), e, LogListener.OUTPUT_TYPE_SYSTEM);
            logger.error(System.getProperty("line.separator") + BuildConstants.MESSAGE_PROCESS_ABNORMALLY_TERMINATED
                         + ((mavenProcess != null)
                            ? mavenProcess.exitValue()
                            : "-1"), LogListener.OUTPUT_TYPE_SYSTEM);
        }
    }

    protected int stop() {
        int exitValue = CommonUtils.destroyProcess(mavenProcess);

        setStopped(true);

        return exitValue;
    }

    public String getCaption() {
        return BuildConstants.MESSAGE_USING_EXTERNAL_MAVEN;
    }
}
