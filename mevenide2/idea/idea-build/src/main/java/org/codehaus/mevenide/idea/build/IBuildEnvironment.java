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

import com.intellij.openapi.project.Project;

import org.apache.maven.embedder.MavenEmbedder;

import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public interface IBuildEnvironment {
    public MavenEmbedder getMavenEmbedder();

    public void setMavenEmbedder(MavenEmbedder mavenEmbedder);

    public Project getProject();

    public void setProject(Project project);

    public IMavenBuildSettings getMavenBuildSettings();

    public void setMavenBuildSettings(IMavenBuildSettings mavenBuildSettings);

    public String getPathToJdk();

    public boolean isUseMavenEmbedder();

    public void setUseMavenEmbedder(boolean useMavenEmbedder);

    public IMavenBuildLogger getLogger();

    public void setLogger(IMavenBuildLogger logger);

    public List<String> getGoals();

    public void setGoals(List<String> goals);

    public String getPomFile();

    public void setPomFile(String pomFile);

    public String getWorkingDir();

    public void setWorkingDir(String workingDir);
}
