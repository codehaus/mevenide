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

import com.intellij.openapi.project.Project;
import org.apache.maven.embedder.MavenEmbedder;
import org.codehaus.mevenide.idea.build.IBuildEnvironment;
import org.codehaus.mevenide.idea.build.IMavenBuildLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class IdeaBuildEnvironment implements IBuildEnvironment {
    private boolean useMavenEmbedder;
    private MavenEmbedder mavenEmbedder;
    private IMavenBuildLogger logger;
    private List<String> goals = new ArrayList<String>();
    private String pomFile;
    private String workingDir;
    private Project project;
    private org.codehaus.mevenide.idea.build.IMavenBuildConfiguration mavenConfiguration;

    public MavenEmbedder getMavenEmbedder() {
        return mavenEmbedder;
    }

    public void setMavenEmbedder(MavenEmbedder mavenEmbedder) {
        this.mavenEmbedder = mavenEmbedder;
    }

    public Project getProject() {
        return project;    // To change body of implemented methods use File | Settings | File Templates.
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public org.codehaus.mevenide.idea.build.IMavenBuildConfiguration getMavenBuildConfiguration() {
        return mavenConfiguration;    // To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMavenBuildConfiguration(
            org.codehaus.mevenide.idea.build.IMavenBuildConfiguration mavenConfiguration) {
        this.mavenConfiguration = mavenConfiguration;
    }

    public boolean isUseMavenEmbedder() {
        return useMavenEmbedder;
    }

    public void setUseMavenEmbedder(boolean useMavenEmbedder) {
        this.useMavenEmbedder = useMavenEmbedder;
    }

    public IMavenBuildLogger getLogger() {
        return logger;
    }

    public void setLogger(IMavenBuildLogger logger) {
        this.logger = logger;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public String getPomFile() {
        return pomFile;
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
}
