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



package org.codehaus.mevenide.idea.helper;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.progress.ProgressIndicator;

import org.apache.maven.embedder.MavenEmbedder;

import org.codehaus.mevenide.idea.build.AbstractMavenBuildTask;
import org.codehaus.mevenide.idea.build.IMavenBuildLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class BuildContext {
    private ActionContext actionContext;
    private boolean useMavenEmbedder;
    private boolean buildCancelled = false;
    private ConsoleView consoleView;
    private AbstractMavenBuildTask buildTask;
    private IMavenBuildLogger logger;
    private List<String> goals = new ArrayList<String>();
    private String pomFile;
    private String workingDir;
    private ProgressIndicator progressIndicator;
    private MavenEmbedder mavenEmbedder;

    public BuildContext() {}

    public MavenEmbedder getMavenEmbedder() {
        return mavenEmbedder;
    }

    public void setMavenEmbedder(MavenEmbedder mavenEmbedder) {
        this.mavenEmbedder = mavenEmbedder;
    }

    public AbstractMavenBuildTask getBuildTask() {
        return buildTask;
    }

    public void setBuildTask(AbstractMavenBuildTask buildTask) {
        this.buildTask = buildTask;
    }

    public boolean isBuildCancelled() {
        return buildCancelled;
    }

    public void setBuildCancelled(boolean buildCancelled) {
        this.buildCancelled = buildCancelled;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public List<String> getGoals() {
        return goals;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getPomFile() {
        return pomFile;
    }

    /**
     * Method description
     *
     * @param goals Document me!
     */
    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    /**
     * Method description
     *
     * @param pomFile Document me!
     */
    public void setPomFile(String pomFile) {
        this.pomFile = pomFile;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public IMavenBuildLogger getLogger() {
        return logger;
    }

    public void setLogger(IMavenBuildLogger logger) {
        this.logger = logger;
    }

    public ActionContext getActionContext() {
        return actionContext;
    }

    public void setActionContext(ActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public boolean isUseMavenEmbedder() {
        return useMavenEmbedder;
    }

    public void setUseMavenEmbedder(boolean useMavenEmbedder) {
        this.useMavenEmbedder = useMavenEmbedder;
    }

    public ConsoleView getConsoleView() {
        return consoleView;
    }

    public void setConsoleView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }
}
