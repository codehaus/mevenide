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


package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.log4j.Logger;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.codehaus.mevenide.idea.build.*;
import org.codehaus.mevenide.idea.build.embedder.MavenEmbedderBuildLogger;
import org.codehaus.mevenide.idea.build.embedder.MavenEmbedderBuildTask;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.common.util.BuildUtils;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectOutputForm;
import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.helper.GuiContext;
import org.codehaus.mevenide.idea.helper.IdeaBuildEnvironment;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class MavenRunner {
    private static final Logger LOG = Logger.getLogger(MavenRunner.class);
    private final Project project;
    private BuildContext buildContext;
    private GuiContext guiContext;
    private IMavenBuildConfiguration pluginSettings;

    public MavenRunner(Project project) {
        this.project = project;
        buildContext = MavenBuildProjectComponent.getInstance(project).getBuildContext();
        guiContext = MavenBuildProjectComponent.getInstance(project).getGuiContext();
        pluginSettings = MavenBuildProjectComponent.getInstance(project).getProjectPluginSettings();
    }

    public void execute() throws IdeaMavenPluginException {
        FileDocumentManager.getInstance().saveAllDocuments();

        try {
            final AbstractMavenBuildTask buildTask;
            if (pluginSettings.isUseMavenEmbedder()) {
                buildTask = createInternalBuildTask();
            } else {
                buildTask = createExternalBuildTask();
            }
            attachConsole(buildTask);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(project,
                    BuildConstants.MESSAGE_EXECUTING_MAVEN + " - " + buildTask.getCaption(),
                    buildTask, null, null, getBackgroundOption());

        } catch (Exception e) {
            throw new IdeaMavenPluginException(e);
        }
    }

    private PerformInBackgroundOption getBackgroundOption() {
        return new PerformInBackgroundOption() {
            public boolean shouldStartInBackground() {
                return pluginSettings.isRunMavenInBackground();
            }

            public void processSentToBackground() {
                pluginSettings.setRunMavenInBackground(true);
            }

            public void processRestoredToForeground() {
                pluginSettings.setRunMavenInBackground(false);
            }
        };
    }

    private AbstractMavenBuildTask createInternalBuildTask() throws BuildException {
        MavenEmbedder embedder = createMavenEmbedder();
        IBuildEnvironment buildEnvironment = createBuildEnvironment();
        buildEnvironment.setLogger((MavenEmbedderBuildLogger) embedder.getLogger());
        buildEnvironment.setMavenEmbedder(embedder);
        return new MavenEmbedderBuildTask(buildEnvironment);
    }

    private AbstractMavenBuildTask createExternalBuildTask() throws BuildException, IOException {
        IBuildEnvironment buildEnvironment = createBuildEnvironment();
        buildEnvironment.setLogger(new MavenBuildFormattedLogger());
        return new MavenBuildTask(buildEnvironment);
    }

    private IBuildEnvironment createBuildEnvironment() {
        IBuildEnvironment buildEnvironment = new IdeaBuildEnvironment();

        buildEnvironment.setProject(project);
        buildEnvironment.setMavenBuildConfiguration(BuildUtils.createMavenBuildSettings(pluginSettings));
        buildEnvironment.setGoals(buildContext.getGoals());
        buildEnvironment.setUseMavenEmbedder(pluginSettings.isUseMavenEmbedder());
        buildEnvironment.setPomFile(buildContext.getPomFile());
        buildEnvironment.setWorkingDir(buildContext.getWorkingDir());

        return buildEnvironment;
    }

    private MavenEmbedder createMavenEmbedder() {
        MavenEmbedder maven = new MavenEmbedder();
        ClassLoader classLoader = this.getClass().getClassLoader();

        MavenEmbedderBuildLogger logger = new MavenEmbedderBuildLogger();

        logger.setThreshold(IMavenBuildLogger.LEVEL_INFO);
        maven.setLogger(logger);
        maven.setClassLoader(classLoader);
        try {
            maven.start();
        } catch (MavenEmbedderException e) {
            LOG.error(e);
        }
        return maven;
    }

    private void attachConsole(AbstractMavenBuildTask buildTask) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow outputToolWindow = manager.getToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID);

        if (outputToolWindow == null) {
            outputToolWindow = manager.registerToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID, createOutputPanel(),
                    ToolWindowAnchor.BOTTOM);
            outputToolWindow.show(null);
        }

        buildContext.setBuildTask(buildTask);
        IMavenBuildLogger buildLogger = buildTask.getLogger();
        buildContext.setLogger(buildLogger);
        ((MavenBuildProjectOutputForm) guiContext.getMavenOutputWindowForm()).setLogger(buildLogger);
    }

    private JComponent createOutputPanel() {
        MavenBuildProjectOutputForm oldForm = (MavenBuildProjectOutputForm) guiContext.getMavenOutputWindowForm();
        if(oldForm!=null){
            oldForm.dispose();
        }
        MavenBuildProjectOutputForm form = new MavenBuildProjectOutputForm(project);
        guiContext.setMavenOutputWindowForm(form);
        return form.getRootComponent();
    }

    public static void setupBuildContext(Project project, VirtualFile pomFile, List<String> sortedGoalList) {
        BuildContext buildContext = new BuildContext();

        buildContext.setPomFile(pomFile.getPath());

        if (pomFile.getParent() != null) {
            buildContext.setWorkingDir(pomFile.getParent().getPath());
        }

        buildContext.setGoals(sortedGoalList);

        MavenBuildProjectComponent.getInstance(project).setBuildContext(buildContext);
    }

    public static void run(Project project) {
        MavenRunner runner = new MavenRunner(project);
        try {
            runner.execute();
        } catch (IdeaMavenPluginException e) {
            ErrorHandler.processAndShowError(project, e, false);
        }
    }
}
