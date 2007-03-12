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
import com.intellij.openapi.projectRoots.ProjectJdkTable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.maven.embedder.MavenEmbedder;

import org.codehaus.mevenide.idea.build.IBuildEnvironment;
import org.codehaus.mevenide.idea.build.IMavenBuildLogger;
import org.codehaus.mevenide.idea.build.IMavenConfiguration;
import org.codehaus.mevenide.idea.build.MavenBuildException;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class IdeaBuildEnvironment implements IBuildEnvironment {
    private static final Logger LOG = Logger.getLogger(IdeaBuildEnvironment.class);
    private boolean useMavenEmbedder;
    private MavenEmbedder mavenEmbedder;
    private IMavenBuildLogger logger;
    private List<String> goals = new ArrayList<String>();
    private String pomFile;
    private String workingDir;
    private Project project;
    private IMavenConfiguration mavenConfiguration;

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

    public IMavenConfiguration getMavenBuildSettings() {
        return mavenConfiguration;    // To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMavenBuildSettings(IMavenConfiguration mavenConfiguration) {
        this.mavenConfiguration = mavenConfiguration;
    }

    public String getPathToJdk() {
        String javaExecutable;    // if JAVA_HOME is specified use it as executable
        String javaHome = System.getenv("JAVA_HOME");

        if (!StringUtils.isBlank(javaHome)) {
            javaExecutable = javaHome + System.getProperty("file.separator") + "bin"
                             + System.getProperty("file.separator");
            LOG.info("Using JAVA_HOME variable. Java executable is in directory: " + javaExecutable);

            // if JAVA_HOME is not specified try to use the internal JDK from IDEA
        } else {
            ProjectJdkTable projectJdkTable = ProjectJdkTable.getInstance();

            javaExecutable = projectJdkTable.getInternalJdk().getBinPath() + System.getProperty("file.separator");
            LOG.info("JAVA_HOME not specified. Using internal IDEA JDK with bin path: " + javaExecutable);
            javaHome = projectJdkTable.getInternalJdk().getHomePath();
        }

        if (StringUtils.isBlank(javaHome)) {
            throw new MavenBuildException("Unable to locate JDK home directory!" + System.getProperty("line.separator")
                                          + "Either set the home directory in the configuration dialogs"
                                          + System.getProperty("line.separator")
                                          + "or set the JAVA_HOME environment variable on your system!");
        }

        if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_WINDOWS_2000 || SystemUtils.IS_OS_WINDOWS_95
                || SystemUtils.IS_OS_WINDOWS_98 || SystemUtils.IS_OS_WINDOWS_ME || SystemUtils.IS_OS_WINDOWS_NT
                || SystemUtils.IS_OS_WINDOWS_XP) {
            if (!StringUtils.isBlank(javaExecutable)) {
                javaExecutable = javaExecutable + "java.exe";
            }
        } else if (SystemUtils.IS_OS_UNIX) {
            if (!StringUtils.isBlank(javaExecutable)) {
                javaExecutable = javaExecutable + "java";
            }
        }

        return javaExecutable;
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
