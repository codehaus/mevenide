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
import org.mevenide.idea.MavenHomeUndefinedException;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.goals.GoalsHelper;

/**
 * @author Arik
 */
public class MavenJavaParameters extends JavaParameters {
    private static final String ENDORSED_DIR_NAME = "lib/endorsed";
    private static final String FOREHEAD_MAIN_CLASS = "com.werken.forehead.Forehead";
    private static final String FOREHEAD_CONF_FILE = "bin/forehead.conf";
    private static final String FOREHEAD_JAR_FILE = "lib/forehead-1.0-beta-5.jar";
    public static final String COMPILE_REGEXP =
            RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS;

    public MavenJavaParameters(final VirtualFile pWorkingDir,
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
        final VirtualFile javaEndorsed = jdkHome.findFileByRelativePath(ENDORSED_DIR_NAME);
        final VirtualFile mavenEndorsed = mavenHome.findFileByRelativePath(ENDORSED_DIR_NAME);
        final VirtualFile foreheadJar = mavenHome.findFileByRelativePath(FOREHEAD_JAR_FILE);
        final String endorsedDirs =
                FileUtils.getAbsolutePath(javaEndorsed) +
                        File.pathSeparatorChar +
                        FileUtils.getAbsolutePath(mavenEndorsed);

        //
        //setup commandline
        //
        setWorkingDirectory(FileUtils.getAbsolutePath(pWorkingDir));
        setJdk(pJvm);
        setMainClass(FOREHEAD_MAIN_CLASS);

        //maven-related JVM arguments
        vmArgs.defineProperty("maven.home", FileUtils.getAbsolutePath(mavenHome));
        vmArgs.defineProperty("tools.jar", pJvm.getToolsPath());
        vmArgs.defineProperty("forehead.conf.file", FileUtils.getAbsolutePath(foreheadConf));
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

        //
        //specify the goals to execute
        //
        for (final String goal : pGoals)
            if (goal.endsWith(GoalsHelper.DEFAULT_GOAL_NAME))
                params.add(GoalsHelper.getPluginName(goal));
            else
                params.add(goal);
    }
}
