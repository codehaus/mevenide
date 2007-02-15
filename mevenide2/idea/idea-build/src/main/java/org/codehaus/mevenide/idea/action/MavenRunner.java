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



package org.codehaus.mevenide.idea.action;

import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

import org.apache.log4j.Logger;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;

import org.codehaus.mevenide.idea.build.AbstractMavenBuildManager;
import org.codehaus.mevenide.idea.build.BuildException;
import org.codehaus.mevenide.idea.build.IBuildEnvironment;
import org.codehaus.mevenide.idea.build.IMavenBuildLogger;
import org.codehaus.mevenide.idea.build.MavenBuildFormattedLogger;
import org.codehaus.mevenide.idea.build.MavenBuildManager;
import org.codehaus.mevenide.idea.build.embedder.MavenEmbedderBuildLogger;
import org.codehaus.mevenide.idea.build.embedder.MavenEmbedderBuildManager;
import org.codehaus.mevenide.idea.common.util.BuildUtils;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.helper.IdeaBuildEnvironment;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.io.IOException;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenRunner {
    private static final Logger LOG = Logger.getLogger(MavenRunner.class);
    private static final String COMPILE_REGEXP_SOURCE = RegexpFilter.FILE_PATH_MACROS + ":\\["
                                                        + RegexpFilter.LINE_MACROS + "," + RegexpFilter.COLUMN_MACROS
                                                        + "]";
    private static final String COMPILE_REGEXP_JAR = RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS;
    private ActionContext context;
    private BuildContext buildContext;
    private boolean useEmbedder;

    /**
     * Constructs ...
     *
     * @param buildContext Document me!
     */
    public MavenRunner(BuildContext buildContext) {
        this.buildContext = buildContext;
        this.context = buildContext.getActionContext();

        ToolWindowManager manager = ToolWindowManager.getInstance(context.getPluginProject());

        if (manager.getToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID) != null) {
            manager.unregisterToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID);
        }
    }

    /**
     * Method description
     *
     * @throws IdeaMavenPluginException in case the execution fails.
     */
    public void execute() throws IdeaMavenPluginException {
        try {
            useEmbedder = context.getProjectPluginSettings().isUseMavenEmbedder();

            if (useEmbedder) {
                startEmbeddedMavenExecution();
            } else {
                startMavenExecution();
            }
        } catch (Exception e1) {
            throw new IdeaMavenPluginException(e1);
        }
    }

    private void startEmbeddedMavenExecution() throws BuildException {
        IBuildEnvironment buildEnvironment = createBuildEnvironment();
        MavenEmbedder embedder = createMavenEmbedder();

        buildContext.setMavenEmbedder(embedder);
        buildContext.setLogger((MavenEmbedderBuildLogger) buildContext.getMavenEmbedder().getLogger());
        ActionUtils.createAndShowOutputConsole(buildContext);
        buildEnvironment.setLogger((MavenEmbedderBuildLogger) buildContext.getMavenEmbedder().getLogger());
        buildEnvironment.setMavenEmbedder(embedder);

        AbstractMavenBuildManager buildManager = new MavenEmbedderBuildManager(buildEnvironment);

        buildContext.setBuildTask(buildManager.execute());
    }

    private void startMavenExecution() throws BuildException, IOException {
        IBuildEnvironment buildEnvironment = createBuildEnvironment();
        IMavenBuildLogger logger = new MavenBuildFormattedLogger();

        buildContext.setUseMavenEmbedder(useEmbedder);
        buildContext.setLogger(logger);
        ActionUtils.createAndShowOutputConsole(buildContext);
        buildEnvironment.setLogger(logger);

        AbstractMavenBuildManager buildManager = new MavenBuildManager(buildEnvironment);

        buildContext.setBuildTask(buildManager.execute());
    }

    private IBuildEnvironment createBuildEnvironment() {
        Project project = context.getPluginProject();
        ConsoleView view = createConsoleView(project);

        // Todo: Set the project correctly
        context.setLastExecutedMavenProject("bla");
        buildContext.setUseMavenEmbedder(useEmbedder);
        buildContext.setConsoleView(view);

        IBuildEnvironment buildEnvironment = new IdeaBuildEnvironment();

        buildEnvironment.setProject(context.getPluginProject());
        buildEnvironment.setMavenBuildSettings(BuildUtils.createMavenBuildSettings(context.getProjectPluginSettings()));
        buildEnvironment.setGoals(buildContext.getGoals());
        buildEnvironment.setUseMavenEmbedder(useEmbedder);
        buildEnvironment.setPomFile(buildContext.getPomFile());
        buildEnvironment.setWorkingDir(buildContext.getWorkingDir());

        return buildEnvironment;
    }

    private ConsoleView createConsoleView(Project project) {
        ConsoleView view;
        TextConsoleBuilder builder;
        TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();

        builder = factory.createBuilder(project);

        Filter[] filters = new Filter[] {new ExceptionFilter(project), new RegexpFilter(project, COMPILE_REGEXP_SOURCE),
                                         new RegexpFilter(project, COMPILE_REGEXP_JAR)};

        builder.addFilter(filters[0]);
        builder.addFilter(filters[1]);
        builder.addFilter(filters[2]);
        view = builder.getConsole();
        context.getGuiContext().setOutputConsoleView(view);
        view.clear();

        return view;
    }

    private MavenEmbedder createMavenEmbedder() {
        MavenEmbedder maven = new MavenEmbedder();
        ClassLoader classLoader = getClass().getClassLoader();

        // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
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
}
