/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea.execute;

import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import org.mevenide.idea.global.MavenHomeUndefinedException;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.util.FileUtils;

/**
 * @author Arik
 */
public class MavenJavaParameters extends JavaParameters {
    private static final String JRE_ENDORSED_DIR_NAME = "lib/endorsed";
    private static final String JDK_ENDORSED_DIR_NAME = "jre/lib/endorsed";
    private static final String MAVEN_ENDORSED_DIR_NAME = "lib/endorsed";
    private static final String FOREHEAD_MAIN_CLASS = "com.werken.forehead.Forehead";
    private static final String FOREHEAD_CONF_FILE = "bin/forehead.conf";
    private static final String FOREHEAD_JAR_FILE = "lib/forehead-1.0-beta-5.jar";
    public static final String COMPILE_REGEXP =
            RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS;

    public MavenJavaParameters(final VirtualFile pPomFile,
                               final ProjectJdk pJvm,
                               final Goal... pGoals) throws MavenHomeUndefinedException {
        this(pPomFile, pJvm, extractGoalNames(pGoals));
    }

    public MavenJavaParameters(final VirtualFile pPomFile,
                               final ProjectJdk pJvm,
                               final String... pGoals) throws MavenHomeUndefinedException {

        final ParametersList vmArgs = getVMParametersList();
        final ParametersList params = getProgramParametersList();
        final MavenManager mavenMgr = MavenManager.getInstance();

        //
        //make sure the user has set the Maven home location
        //
        final VirtualFile mavenHome = mavenMgr.getMavenHome();
        if (mavenHome == null)
            throw new MavenHomeUndefinedException();

        //
        //important locations for the command line
        //
        final VirtualFile foreheadConf = mavenHome.findFileByRelativePath(FOREHEAD_CONF_FILE);
        final VirtualFile jdkHome = pJvm.getHomeDirectory();
        VirtualFile javaEndorsed = jdkHome.findFileByRelativePath(JRE_ENDORSED_DIR_NAME);
        if (javaEndorsed == null)
            javaEndorsed = jdkHome.findFileByRelativePath(JDK_ENDORSED_DIR_NAME);

        final VirtualFile mavenEndorsed = mavenHome.findFileByRelativePath(MAVEN_ENDORSED_DIR_NAME);
        final VirtualFile foreheadJar = mavenHome.findFileByRelativePath(FOREHEAD_JAR_FILE);

        final StringBuilder endorsedBuf = new StringBuilder();
        if (javaEndorsed != null) {
            endorsedBuf.append(FileUtils.getAbsolutePath(javaEndorsed));
            endorsedBuf.append(File.pathSeparatorChar);
        }
        if (mavenEndorsed != null)
            endorsedBuf.append(FileUtils.getAbsolutePath(mavenEndorsed));
        final String endorsedDirs = endorsedBuf.toString();

        //
        //setup commandline
        //
        final VirtualFile dir = pPomFile.getParent();
        if (dir == null)
            throw new IllegalArgumentException("POM file has no directory.");
        setWorkingDirectory(FileUtils.getAbsolutePath(dir));
        setJdk(pJvm);
        setMainClass(FOREHEAD_MAIN_CLASS);

        //maven-related JVM arguments
        vmArgs.defineProperty("maven.home", FileUtils.getAbsolutePath(mavenHome));
        vmArgs.defineProperty("tools.jar", pJvm.getToolsPath());
        vmArgs.defineProperty("forehead.conf.file", FileUtils.getAbsolutePath(foreheadConf));
        if (endorsedDirs != null && endorsedDirs.trim().length() > 0)
            vmArgs.defineProperty("java.endorsed.dirs", endorsedDirs);

        //user specified JVM arguments
        final String mavenOptions = mavenMgr.getMavenOptions();
        if (mavenOptions != null && mavenOptions.trim().length() > 0)
            vmArgs.add(mavenOptions);

        //only forehead is needed in the classpath
        getClassPath().add(FileUtils.getAbsolutePath(foreheadJar));

        //set offline mode, if needed
        if (mavenMgr.isOffline())
            params.add("-o");

        //suppress banner
        params.add("-b");
        params.add("-p", pPomFile.getName());

        //
        //specify the goals to execute
        //
        for (final String goal : pGoals)
            params.add(goal);
    }

    private static String[] extractGoalNames(final Goal... pGoals) {
        final String[] names = new String[pGoals.length];
        for (int i = 0; i < pGoals.length; i++)
            names[i] = pGoals[i].getName();

        return names;
    }
}
