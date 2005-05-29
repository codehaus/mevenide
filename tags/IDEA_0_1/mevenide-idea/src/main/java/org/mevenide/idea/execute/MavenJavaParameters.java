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

import com.intellij.execution.CantRunException;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import org.mevenide.idea.MavenHomeNotDefinedException;
import org.mevenide.idea.PomNotDefinedException;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.util.goals.GoalsHelper;

import java.io.File;

/**
 * @author Arik
 */
public class MavenJavaParameters extends JavaParameters
{
    private static final String ENDORSED_DIR_NAME = "lib/endorsed";
    private static final String FOREHEAD_MAIN_CLASS = "com.werken.forehead.Forehead";
    private static final String FOREHEAD_CONF_FILE = "bin/forehead.conf";
    private static final String FOREHEAD_JAR_FILE = "lib/forehead-1.0-beta-5.jar";
    public static final String COMPILE_REGEXP =
    RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS;

    public MavenJavaParameters(final Module pModule, final String[] pGoals)
            throws MavenHomeNotDefinedException, PomNotDefinedException, CantRunException {

        //
        //make sure the user has set the Maven home location
        //
        final File mavenHome = MavenManager.getInstance().getMavenHome();
        if (mavenHome == null)
            throw new MavenHomeNotDefinedException();

        //
        //make sure selected module has a POM file
        //
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        final File pomFile = moduleSettings.getPomFile();
        if (pomFile == null || !pomFile.exists())
            throw new PomNotDefinedException();

        //
        //make sure selected module has a valid JDK set
        //
        final ProjectJdk jdk = moduleSettings.getJdk();
        if (jdk == null)
            throw CantRunException.noJdkForModule(pModule);

        //
        //important locations for the command line
        //
        final File foreheadConf = new File(mavenHome, FOREHEAD_CONF_FILE);
        final File javaEndorsed = new File(jdk.getHomeDirectory().getPath(), ENDORSED_DIR_NAME);
        final File mavenEndorsed = new File(mavenHome, ENDORSED_DIR_NAME);
        final String endorsedDirs =
                javaEndorsed.getAbsolutePath() + File.pathSeparatorChar + mavenEndorsed.getAbsolutePath();
        final File foreheadJar = new File(mavenHome, FOREHEAD_JAR_FILE);

        //
        //build the command line to execute
        //
        setWorkingDirectory(pomFile.getParentFile());
        setJdk(moduleSettings.getJdk());
        setMainClass(FOREHEAD_MAIN_CLASS);
        getVMParametersList().defineProperty("maven.home", mavenHome.getAbsolutePath());
        getVMParametersList().defineProperty("tools.jar", jdk.getToolsPath());
        getVMParametersList().defineProperty("forehead.conf.file", foreheadConf.getAbsolutePath());
        getVMParametersList().defineProperty("java.endorsed.dirs", endorsedDirs);
        getVMParametersList().add("-Xmx256m");//TODO: add as configuration entry in MavenPlugin!
        getClassPath().add(foreheadJar.getAbsolutePath());

        for(final String goal : pGoals)
            if(goal.endsWith(GoalsHelper.DEFAULT_GOAL_NAME))
                getProgramParametersList().add(GoalsHelper.getPluginName(goal));
            else
                getProgramParametersList().add(goal);
    }
}
