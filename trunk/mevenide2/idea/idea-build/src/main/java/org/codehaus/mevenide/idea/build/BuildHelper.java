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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import org.codehaus.mevenide.idea.build.util.BuildConstants;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class BuildHelper {
    static final Logger LOG = Logger.getLogger(BuildHelper.class);

    /**
     * Determines, whether the given path to the maven settings file is the default file.
     *
     * @param settingsFile path to settings file.
     *
     * @return true in case the settings file is the default one, false otherwise.
     */
    private static boolean isDefaultSettingsFile(String settingsFile) {
        return settingsFile.equals(System.getProperty("user.home") + System.getProperty("file.separator") + ".m2"
                                   + System.getProperty("file.separator")
                                   + BuildConstants.FILENAME_MAVEN_SETTINGS_FILE);
    }

    public static List<String> createMavenExecutionCommand(IBuildEnvironment buildEnvironment)
            throws MavenBuildException {
        String mavenHome;
        String classpathSeparator = null;

        if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_WINDOWS_2000 || SystemUtils.IS_OS_WINDOWS_95
                || SystemUtils.IS_OS_WINDOWS_98 || SystemUtils.IS_OS_WINDOWS_ME || SystemUtils.IS_OS_WINDOWS_NT
                || SystemUtils.IS_OS_WINDOWS_XP) {
            classpathSeparator = ";";
        } else if (SystemUtils.IS_OS_UNIX) {
            classpathSeparator = ":";
        }

        mavenHome = buildEnvironment.getMavenBuildSettings().getMavenHome();
        mavenHome = getMavenHome(mavenHome);

        String classpathEntries = getMavenClasspathEntries(mavenHome, classpathSeparator);
        String vmOptions = buildEnvironment.getMavenBuildSettings().getVmOptions();
        String mavenSystemOptions = StringUtils.defaultString(System.getenv("MAVEN_OPTS"));
        List<String> cmdList = new ArrayList<String>();

        cmdList.add(buildEnvironment.getPathToJdk());

        if (!StringUtils.isBlank(vmOptions)) {
            StringTokenizer tokenizer = new StringTokenizer(vmOptions);

            while (tokenizer.hasMoreTokens()) {
                cmdList.add(tokenizer.nextToken());
            }
        }

        if (!StringUtils.isBlank(mavenSystemOptions)) {
            StringTokenizer tokenizer = new StringTokenizer(mavenSystemOptions);

            while (tokenizer.hasMoreTokens()) {
                cmdList.add(tokenizer.nextToken());
            }
        }

        cmdList.add("-classpath");
        cmdList.add(classpathEntries);
        cmdList.add("-Dclassworlds.conf=" + mavenHome + System.getProperty("file.separator") + "bin"
                    + System.getProperty("file.separator") + "m2.conf");
        cmdList.add("-Dmaven.home=" + mavenHome);
        cmdList.add("org.codehaus.classworlds.Launcher");

        if (!StringUtils.isBlank(buildEnvironment.getMavenBuildSettings().getMavenCommandLineParams())) {
            cmdList.add(buildEnvironment.getMavenBuildSettings().getMavenCommandLineParams());
        }

        if (!StringUtils.isBlank(buildEnvironment.getMavenBuildSettings().getMavenSettingsFile())
                &&!isDefaultSettingsFile(buildEnvironment.getMavenBuildSettings().getMavenSettingsFile())) {
            cmdList.add("-s");
            cmdList.add(buildEnvironment.getMavenBuildSettings().getMavenSettingsFile());
        }

        // Todo: Insert Maven Options when running external maven
/*
        if (StringUtils.isNotEmpty(buildEnvironment.getMavenBuildSettings().getMavenOptions().toString())) {
            String[] mavenOptions;

            mavenOptions = StringUtils.split(buildEnvironment.getMavenBuildSettings().getMavenOptions().toString());

            for (String mavenOption : mavenOptions) {
                cmdList.add(mavenOption);
            }
        }
*/

        cmdList.add("-f");
        cmdList.add(buildEnvironment.getPomFile());

        List<String> goals = buildEnvironment.getGoals();

        for (String goal : goals) {
            cmdList.add(goal);
        }

        return cmdList;
    }

    private static String getMavenClasspathEntries(String mavenHome, String classpathSeparator) {
        File mavenHomeBootAsFile = new File(mavenHome + System.getProperty("file.separator") + "core"
                                       + System.getProperty("file.separator") + "boot");
        String classpathEntries = new String();

        if (mavenHomeBootAsFile.isDirectory()) {
            File[] files = mavenHomeBootAsFile.listFiles();

            for (File file : files) {
                if (file.getName().startsWith("classworlds-")) {
                    classpathEntries = classpathEntries + file.getAbsolutePath() + classpathSeparator;
                }
            }

            classpathEntries = StringUtils.chop(classpathEntries);
        }

        LOG.info("Classpath for Maven-2 call is: " + classpathEntries);

        return classpathEntries;
    }

    private static String getMavenHome(String mavenHome) throws MavenBuildException {
        String mavenSystemHome = System.getenv("M2_HOME");

        if (StringUtils.isBlank(mavenHome)) {
            LOG.info("Maven-2 Home not specified. Trying M2_HOME env variable!");

            if (!StringUtils.isBlank(mavenSystemHome)) {
                LOG.info("M2_HOME property found: " + mavenSystemHome);
                mavenHome = mavenSystemHome;
            } else {
                throw new MavenBuildException("Unable to execute Maven-2. No Maven-2 home directory specified!"
                                              + System.getProperty("line.separator")
                                              + "Either set the home directory in the configuration dialogs"
                                              + System.getProperty("line.separator")
                                              + "or set the M2_HOME environment variable on your system!");
            }
        }

        File mavenHomeAsFile = new File(mavenHome);
        File mavenConfFile = new File(mavenHome + System.getProperty("file.separator") + "bin"
                                      + System.getProperty("file.separator") + "m2.conf");

        if (!mavenConfFile.exists()) {
            throw new MavenBuildException("Specified Maven-2 home directory " + mavenHome + " is not a"
                                          + System.getProperty("line.separator") + "Maven-2 installation directory!");
        }

        try {
            mavenHome = mavenHomeAsFile.getCanonicalPath();
            LOG.debug("Maven home as canonical path: " + mavenHome);
            LOG.debug("Maven home as absolute path: " + mavenHomeAsFile.getAbsolutePath());
            LOG.debug("Maven home as path: " + mavenHomeAsFile.getPath());
        } catch (IOException e) {
            throw new MavenBuildException(e);
        }

        return mavenHome;
    }
}
