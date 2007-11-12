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
    private boolean buildCancelled = false;
    private AbstractMavenBuildTask buildTask;
    private IMavenBuildLogger logger;
    private List<String> goals = new ArrayList<String>();
    private String pomFile;
    private String workingDir;

    public BuildContext() {}

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

    public List<String> getGoals() {
        return goals;
    }

    public String getPomFile() {
        return pomFile;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

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
}
